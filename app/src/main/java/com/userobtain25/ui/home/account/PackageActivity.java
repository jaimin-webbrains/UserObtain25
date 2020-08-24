package com.userobtain25.ui.home.account;

import android.graphics.Color;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.account.ResultPackage;
import com.userobtain25.model.account.ResultPackages;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageActivity extends AppCompatActivity {

    protected ViewDialog viewDialog;
    LoginModel loginModel;
    private RecyclerView recycler_view;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResultPackage> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;

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

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<ResultPackage> moviesList;

        public MyCustomAdapter(ArrayList<ResultPackage> moviesList) {
            this.moviesList = moviesList;
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


            final ResultPackage datum = moviesList.get(position);


            holder.txtName.setText(datum.getName() + "");
            holder.txtPrice.setText(datum.getPrice() + " ₹ ");
            holder.txtValidity.setText(datum.getValidity() + " Year ");
            holder.btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BuyPackage();
                }

                private void BuyPackage() {
                    Random random = new Random();
                    int randomNumber = random.nextInt((80 - 65) +1) + 65;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("order_id", randomNumber + "");
                    hashMap.put("package_id", datum.getId() + "");
                    hashMap.put("user_id", loginModel.getSessionData().getId() + "");


                    Log.e("GAYA", hashMap + "");
                    showProgressDialog();
                    Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).UpdatePaymentInfo(hashMap);
                    marqueCall.enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                            SuccessModel object = response.body();
                            hideProgressDialog();
                            if (object != null && object.getError() == false) {
                                Toast.makeText(PackageActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();
                                GetPackageList();
                                finish();
                            } else if (object != null && object.getError() == true) {
                                //Toast.makeText(PackageActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();

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
            });


        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView txtValidity, txtPrice, txtName;
            LinearLayout btnBuy;


            public MyViewHolder(View view) {
                super(view);


                txtName = view.findViewById(R.id.txtName);
                txtPrice = view.findViewById(R.id.txtPrice);
                txtValidity = view.findViewById(R.id.txtValidity);
                btnBuy = view.findViewById(R.id.btnBuy);


            }

        }

    }

}