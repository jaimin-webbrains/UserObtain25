package com.userobtain25.ui.home.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.account.ResultGreatOffer;
import com.userobtain25.model.account.ResultGreatOffers;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.AppPreferences;
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

public class NotificationActivity extends AppCompatActivity {

    protected ViewDialog viewDialog;
    LoginModel loginModel;
    private RecyclerView recycler_view;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResultGreatOffer> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initToolbar();
        loginModel = PrefUtils.getUser(NotificationActivity.this);
        viewDialog = new ViewDialog(NotificationActivity.this);
        viewDialog.setCancelable(false);
        contentHome_SwipeRefresh = findViewById(R.id.contentHome_SwipeRefresh);
        contentHome_SwipeRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        recycler_view = findViewById(R.id.viewProducts_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);
        GetNotificationList();
        contentHome_SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contentHome_SwipeRefresh.setRefreshing(false);
                GetNotificationList();


            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Notifications");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void GetNotificationList() {
        HashMap<String, String> hashMap = new HashMap<>();

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultGreatOffers> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).GreatOffers(hashMap);
        marqueCall.enqueue(new Callback<ResultGreatOffers>() {
            @Override
            public void onResponse(@NonNull Call<ResultGreatOffers> call, @NonNull Response<ResultGreatOffers> response) {
                ResultGreatOffers object = response.body();
                hideProgressDialog();
                contentHome_SwipeRefresh.setRefreshing(false);
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayRestaurantCoupon_s = object.getResultGreatOffers();
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
                        Toast.makeText(NotificationActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultGreatOffers> call, @NonNull Throwable t) {
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
    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<ResultGreatOffer> moviesList;

        public MyCustomAdapter(ArrayList<ResultGreatOffer> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_notification_list, parent, false);

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


            final ResultGreatOffer datum = moviesList.get(position);


            holder.txtName.setText(datum.getName() + "");
            holder.txtDiscount.setText("Discount Value : " + datum.getDiscountValue() + " % ");
            holder.txtAmount.setText("Minimum Amount : " + datum.getMinimumAmount() + " ₹ ");
            if (datum.getMaximum_amount() != null) {
                holder.txtMaxAmount.setVisibility(View.VISIBLE);
                holder.vMax.setVisibility(View.VISIBLE);
                holder.txtMaxAmount.setText("Maximum Amount : " + datum.getMaximum_amount() + " ₹ ");

            } else {
                holder.txtMaxAmount.setVisibility(View.GONE);
                holder.vMax.setVisibility(View.GONE);
            }
            if (datum.getRestoPhoto() != null) {
                Glide.with(NotificationActivity.this).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(NotificationActivity.this).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView txtAmount,txtDiscount, txtName,txtMaxAmount;
            ImageView img;
            View vMax;

            public MyViewHolder(View view) {
                super(view);


                txtName = view.findViewById(R.id.txtName);
                txtDiscount = view.findViewById(R.id.txtDiscount);
                txtAmount = view.findViewById(R.id.txtAmount);
                img = view.findViewById(R.id.img);
                txtMaxAmount = view.findViewById(R.id.txtMaxAmount);
                vMax = view.findViewById(R.id.vMax);


            }

        }

    }

}