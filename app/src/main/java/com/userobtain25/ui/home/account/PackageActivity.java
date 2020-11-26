package com.userobtain25.ui.home.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.account.ResultPackage;
import com.userobtain25.model.account.ResultPackages;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.Constants;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = PackageActivity.class.getSimpleName();
    protected ViewDialog viewDialog;
    LoginModel loginModel;
    private RecyclerView recycler_view;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResultPackage> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;
    ResultPackage datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
        initView();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Package Detail");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        Checkout.preload(getApplicationContext());
        loginModel = PrefUtils.getUser(PackageActivity.this);
        viewDialog = new ViewDialog(PackageActivity.this);
        viewDialog.setCancelable(false);
        contentHome_SwipeRefresh = findViewById(R.id.contentHome_SwipeRefresh);
        contentHome_SwipeRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        recycler_view = findViewById(R.id.viewProducts_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PackageActivity.this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);
        GetPackageList();
        contentHome_SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contentHome_SwipeRefresh.setRefreshing(false);
                GetPackageList();


            }
        });
    }

    private void GetPackageList() {
        HashMap<String, String> hashMap = new HashMap<>();

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultPackages> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).Packages(hashMap);
        marqueCall.enqueue(new Callback<ResultPackages>() {
            @Override
            public void onResponse(@NonNull Call<ResultPackages> call, @NonNull Response<ResultPackages> response) {
                ResultPackages object = response.body();
                hideProgressDialog();
                contentHome_SwipeRefresh.setRefreshing(false);
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayRestaurantCoupon_s = object.getResultPackages();
                    myCustomAdapter = new MyCustomAdapter(resultDisplayRestaurantCoupon_s, PackageActivity.this);
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
                        Toast.makeText(PackageActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultPackages> call, @NonNull Throwable t) {
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

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(PackageActivity.this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            calculateHashInServer(razorpayPaymentID);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(PackageActivity.this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {
        private ArrayList<ResultPackage> moviesList;
        Activity context;

        public MyCustomAdapter(ArrayList<ResultPackage> moviesList, Activity context) {
            this.moviesList = moviesList;
            this.context = context;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_package, parent, false);

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


            datum = moviesList.get(position);


            holder.txtName.setText(datum.getName() + "");
            holder.txtPrice.setText(datum.getPrice() + " ₹ ");
            holder.txtValidity.setText(datum.getValidity() + " Year ");

            holder.btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BuyPackage();
                }

                private void BuyPackage() {
                    //final Activity activity = PackageActivity.this;
                    final Checkout co = new Checkout();
                    co.setKeyID("rzp_live_AuENMuNpZzNZcq");
                    Double i = Double.valueOf(datum.getPrice());
                    long j = (long) (i * 100);
                    try {
                        JSONObject options = new JSONObject();
                        options.put("name", datum.getName());
                        options.put("description", datum.getValidity() + " Year ");
                        //You can omit the image option to fetch the image from dashboard
                        //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                        options.put("currency", "INR");
                        options.put("amount", j + "");

                        JSONObject preFill = new JSONObject();
                        preFill.put("email", "mhfortuneadvertising@gmail.com");
                        preFill.put("contact", "7204342561");

                        options.put("prefill", preFill);

                        co.open(PackageActivity.this, options);
                    } catch (Exception e) {
                        Toast.makeText(PackageActivity.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                }


            });


        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView txtValidity, txtPrice, txtName;
            final LinearLayout btnBuy;


            public MyViewHolder(View view) {
                super(view);


                txtName = view.findViewById(R.id.txtName);
                txtPrice = view.findViewById(R.id.txtPrice);
                txtValidity = view.findViewById(R.id.txtValidity);
                btnBuy = view.findViewById(R.id.btnBuy);


            }

        }


    }

    private void calculateHashInServer(String razorpayPaymentID) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_id", razorpayPaymentID);
        hashMap.put("package_id", datum.getId() + "");
        hashMap.put("user_id", loginModel.getSessionData().getId() + "");


        Log.e("payment", hashMap + "");
        showProgressDialog();
        Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).UpdatePaymentInfo(hashMap);
        marqueCall.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                SuccessModel object = response.body();
                hideProgressDialog();
                if (object != null && object.getError() == false) {
                    Toast.makeText(PackageActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (object != null && object.getError() == true) {

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
                        Toast.makeText(PackageActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
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