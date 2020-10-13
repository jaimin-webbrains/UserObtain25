package com.userobtain25.ui.home.search;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.model.search.ResultSearchResto;
import com.userobtain25.model.search.ResultSearchResto_;
import com.userobtain25.ui.home.goout.Near_deal_deailActivity;
import com.userobtain25.ui.home.goout.RatingActivity;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {

    protected ViewDialog viewDialog;
    AppCompatEditText txt_search_restro;
    RecyclerView viewProducts_RecyclerView;
    LoginModel loginModel;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResultSearchResto_> resultSearchRestos = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        loginModel = PrefUtils.getUser(getActivity());
        viewDialog = new ViewDialog(getActivity());
        viewDialog.setCancelable(false);
        txt_search_restro = view.findViewById(R.id.txt_search_restro);
        viewProducts_RecyclerView = view.findViewById(R.id.viewProducts_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        viewProducts_RecyclerView.setLayoutManager(layoutManager);
        viewProducts_RecyclerView.setHasFixedSize(true);
        GetSearchResto();
        txt_search_restro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                GetSearchResto();
            }

        });
        return view;
    }

    private void GetSearchResto() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("restaurant_name", txt_search_restro.getText().toString() + "");
        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultSearchResto> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).SearchResto(hashMap);
        marqueCall.enqueue(new Callback<ResultSearchResto>() {
            @Override
            public void onResponse(@NonNull Call<ResultSearchResto> call, @NonNull Response<ResultSearchResto> response) {
                ResultSearchResto object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultSearchRestos = object.getResultSearchResto();
                    myCustomAdapter = new MyCustomAdapter(resultSearchRestos);
                    viewProducts_RecyclerView.setAdapter(myCustomAdapter);

                } else if (object != null && object.getError() == true) {
                    // Toast.makeText(getActivity(), object.getMessage(), Toast.LENGTH_SHORT).show();
                } else {


                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.e("TAG", "PO=> Error " + jObjError.getJSONObject("errors") + "");
                        Toast.makeText(getActivity(), jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultSearchResto> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<ResultSearchResto_> moviesList;

        public MyCustomAdapter(ArrayList<ResultSearchResto_> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_search_resro, parent, false);

            return new MyCustomAdapter.MyViewHolder(itemView);
        }

        public void clear() {
            int size = this.moviesList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.moviesList.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        @Override
        public void onBindViewHolder(MyCustomAdapter.MyViewHolder holder, final int position) {


            final ResultSearchResto_ datum = moviesList.get(position);

            holder.txtName.setText(datum.getName());
            if (datum.getNewRating() != null) {
                double rating = Double.parseDouble(datum.getNewRating() + "");
                int r_value = (int) Math.round(rating);
                holder.rating.setRating(Integer.parseInt(r_value + ""));

            } else {
                holder.rating.setRating(0);

            }
            if (datum.getRestoPhoto() != null) {
                Glide.with(getActivity()).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(getActivity()).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }
            holder.txtRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginModel != null) {
                        Intent i = new Intent(getActivity(), RatingActivity.class);
                        i.putExtra("restro_id", datum.getId());
                        startActivity(i);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.customview, viewGroup, false);
                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        dialogView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                            }
                        });
                    }

                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Near_deal_deailActivity.class);
                    i.putExtra("restro_id", datum.getId());
                    i.putExtra("type", "3");//0:search
                    startActivity(i);
                }
            });
        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }



        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtName, txtRating;
            SimpleRatingBar rating;
            ImageView img;


            public MyViewHolder(View view) {
                super(view);

                txtName = view.findViewById(R.id.txtName);
                txtRating = view.findViewById(R.id.txtRating);
                rating = view.findViewById(R.id.rating);
                img = view.findViewById(R.id.img);


            }

        }

    }
}