package com.userobtain25.ui.home.goout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.goout.neardeal.ResultGetReview;
import com.userobtain25.model.goout.neardeal.ResultGetReviews;
import com.userobtain25.model.login.LoginModel;
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

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {
    protected ViewDialog viewDialog;
    RecyclerView viewProducts_RecyclerView;
    AppCompatTextView btnSubmit;
    SimpleRatingBar ratingAdd;
    AppCompatEditText edtReview;
    LoginModel loginModel;
    String restro_id;
    private ArrayList<ResultGetReview> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private MyCustomAdapter myCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        initView();
        initToolbar();
    }

    private void initView() {
        restro_id = getIntent().getStringExtra("restro_id");
        loginModel = PrefUtils.getUser(RatingActivity.this);
        viewDialog = new ViewDialog(RatingActivity.this);
        viewDialog.setCancelable(false);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        ratingAdd = findViewById(R.id.ratingAdd);
        edtReview = findViewById(R.id.edtReview);
        viewProducts_RecyclerView = findViewById(R.id.viewProducts_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(RatingActivity.this);
        viewProducts_RecyclerView.setLayoutManager(layoutManager);
        viewProducts_RecyclerView.setHasFixedSize(true);
        GetRating();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Rating & Reviews");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                PostRating();
                break;


        }

    }

    private void PostRating() {
        final String editComment = edtReview.getText().toString().trim();
        final double rating = ratingAdd.getRating();


        if (editComment.isEmpty()) {
            edtReview.setError("Write Review..");
            edtReview.requestFocus();
            return;
        }
        if (ratingAdd.getRating() == 0) {
            Toast.makeText(this, "Please Add Rate", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("restaurant_id", restro_id + "");
            hashMap.put("user_id", loginModel.getSessionData().getId() + "");
            hashMap.put("rating", rating + "");
            hashMap.put("comments", editComment);


            Log.e("GAYA", hashMap + "");
            showProgressDialog();
            Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).PostUserRating(hashMap);
            marqueCall.enqueue(new Callback<SuccessModel>() {
                @Override
                public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                    SuccessModel object = response.body();
                    hideProgressDialog();
                    Log.e("TAG", "Add_Shop : " + new Gson().toJson(response.body()));
                    if (object != null && object.getError() == false) {
                        Toast.makeText(RatingActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();
                        GetRating();
                    } else if (object != null && object.getError() == true) {
                        Toast.makeText(RatingActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(RatingActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                        } catch (Exception e) {
                            Toast.makeText(RatingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    hideProgressDialog();
                    Log.e("ChatV_Response", t.getMessage() + "");
                }
            });
        }
    }

    private void GetRating() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("resto_id", restro_id + "");
        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultGetReviews> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).GetReviews(hashMap);
        marqueCall.enqueue(new Callback<ResultGetReviews>() {
            @Override
            public void onResponse(@NonNull Call<ResultGetReviews> call, @NonNull Response<ResultGetReviews> response) {
                ResultGetReviews object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayRestaurantCoupon_s = object.getResultGetReviews();
                    myCustomAdapter = new MyCustomAdapter(resultDisplayRestaurantCoupon_s);
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
                        Toast.makeText(RatingActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultGetReviews> call, @NonNull Throwable t) {
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

        private ArrayList<ResultGetReview> moviesList;

        public MyCustomAdapter(ArrayList<ResultGetReview> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layot_rating_list, parent, false);

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


            final ResultGetReview datum = moviesList.get(position);


            holder.txtName.setText(datum.getRestoName());
            holder.txtReview.setText(datum.getComments());
            if (datum.getRating() != null) {
                double rating = Double.parseDouble(datum.getRating() + "");
                int r_value = (int) Math.round(rating);
                holder.rating.setRating(Integer.parseInt(r_value + ""));

            } else {
                holder.rating.setRating(0);

            }

            if (datum.getRestoPhoto() != null) {
                Glide.with(RatingActivity.this).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.imgPerson);
            } else {
                Glide.with(RatingActivity.this).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.imgPerson);
            }

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            SimpleRatingBar rating;
            AppCompatTextView txtName, txtReview;
            CircularImageView imgPerson;

            public MyViewHolder(View view) {
                super(view);


                txtName = view.findViewById(R.id.txtName);
                txtReview = view.findViewById(R.id.txtReview);
                rating = view.findViewById(R.id.rating);
                imgPerson = view.findViewById(R.id.imgPerson);


            }

        }

    }
}