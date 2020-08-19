package com.userobtain25.ui.home.goout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.goout.trendingplace.ResultTrandingPlace;
import com.userobtain25.model.goout.trendingplace.ResultTrandingPlaces;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingActivity extends AppCompatActivity {
    private static DecimalFormat df = new DecimalFormat("0.000");
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    private RecyclerView recycler_view;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResultTrandingPlace> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_issue);
        initView();
        initToolbar();
    }

    private void initView() {
        loginModel = PrefUtils.getUser(TrendingActivity.this);
        viewDialog = new ViewDialog(TrendingActivity.this);
        viewDialog.setCancelable(false);
        contentHome_SwipeRefresh = findViewById(R.id.contentHome_SwipeRefresh);
        contentHome_SwipeRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        recycler_view = findViewById(R.id.viewProducts_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TrendingActivity.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);
        GreatOfferList();
        contentHome_SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contentHome_SwipeRefresh.setRefreshing(false);
                GreatOfferList();


            }
        });
    }

    private void GreatOfferList() {
        HashMap<String, String> hashMap = new HashMap<>();

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultTrandingPlaces> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).TrandingPlaces(hashMap);
        marqueCall.enqueue(new Callback<ResultTrandingPlaces>() {
            @Override
            public void onResponse(@NonNull Call<ResultTrandingPlaces> call, @NonNull Response<ResultTrandingPlaces> response) {
                ResultTrandingPlaces object = response.body();
                hideProgressDialog();
                contentHome_SwipeRefresh.setRefreshing(false);
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayRestaurantCoupon_s = object.getResultTrandingPlaces();
                    myCustomAdapter = new MyCustomAdapter(resultDisplayRestaurantCoupon_s);
                    recycler_view.setAdapter(myCustomAdapter);

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
                        Toast.makeText(TrendingActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultTrandingPlaces> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                contentHome_SwipeRefresh.setRefreshing(false);
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Trending Places");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        String restro_id, copon_id;
        private ArrayList<ResultTrandingPlace> moviesList;

        public MyCustomAdapter(ArrayList<ResultTrandingPlace> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_near_resro_list, parent, false);

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


            final ResultTrandingPlace datum = moviesList.get(position);

            restro_id = datum.getId();
            holder.txtName.setText(datum.getName() + "");
            // double input = Double.parseDouble(String.valueOf(datum.getNewRating()));
            // holder.txtDistance.setText("Distance :" + df.format(input) + " Km ");
            holder.txtDistance.setText("Distance :" + "2.3" + " Km ");
            holder.txtName.setText(datum.getName());
            if (datum.getNewRating() != null) {
                double rating = Double.parseDouble(datum.getNewRating() + "");
                int r_value = (int) Math.round(rating);
                holder.rating.setRating(Integer.parseInt(r_value + ""));

            } else {
                holder.rating.setRating(0);

            }
            if (datum.getRestoPhoto() != null) {
                Glide.with(TrendingActivity.this).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(TrendingActivity.this).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }
            holder.txtRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(TrendingActivity.this, RatingActivity.class);
                    i.putExtra("restro_id", datum.getId());
                    startActivity(i);


                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(TrendingActivity.this, Near_deal_deailActivity.class);
                    i.putExtra("restro_id", datum.getId());
                    startActivity(i);
                }
            });

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            ImageView img;
            TextView txtDistance, txtName, txtRating;
            SimpleRatingBar rating;


            public MyViewHolder(View view) {
                super(view);


                img = view.findViewById(R.id.img);
                txtName = view.findViewById(R.id.txtName);
                txtDistance = view.findViewById(R.id.txtDistance);
                txtRating = view.findViewById(R.id.txtRating);
                rating = view.findViewById(R.id.rating);


            }

        }

    }

}
