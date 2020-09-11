package com.userobtain25.ui.home.goout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.account.ResultGreatOffer;
import com.userobtain25.model.account.ResultGreatOffers;
import com.userobtain25.model.goout.greatoffer.ResultGenerateNumberToUseCoupon;
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

public class GreatOfferActivity extends AppCompatActivity {

    protected ViewDialog viewDialog;
    LoginModel loginModel;
    String package_id;
    private RecyclerView recycler_view;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResultGreatOffer> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_great_offer);
        initView();
        initToolbar();
    }

    private void initView() {
        package_id = AppPreferences.getPackageId(this);
        loginModel = PrefUtils.getUser(GreatOfferActivity.this);
        viewDialog = new ViewDialog(GreatOfferActivity.this);
        viewDialog.setCancelable(false);
        contentHome_SwipeRefresh = findViewById(R.id.contentHome_SwipeRefresh);
        contentHome_SwipeRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        recycler_view = findViewById(R.id.viewProducts_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GreatOfferActivity.this);
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
                        Toast.makeText(GreatOfferActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Great Offers");
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
        private ArrayList<ResultGreatOffer> moviesList;

        public MyCustomAdapter(ArrayList<ResultGreatOffer> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_great_offer_list, parent, false);

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

            restro_id = datum.getRestaurantId();
            copon_id = datum.getId();
            holder.txtName.setText(datum.getName() + "");
            holder.txtDiscount.setText("Discount Value : " + datum.getDiscountValue() + " % ");
            holder.txtAmount.setText("Minimum Amount : " + datum.getMinimumAmount() + " ₹ ");
            if (datum.getMaximum_amount() != null) {
                holder.txtMaxAmount.setVisibility(View.VISIBLE);

                holder.txtMaxAmount.setText("Maximum Amount : " + datum.getMaximum_amount() + " ₹ ");

            } else {
                holder.txtMaxAmount.setVisibility(View.GONE);

            }
            if (datum.getRestoPhoto() != null) {
                Glide.with(GreatOfferActivity.this).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(GreatOfferActivity.this).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(GreatOfferActivity.this, Coupon_deailActivity.class);
                    i.putExtra("restro_id", datum.getRestaurantId());
                    startActivity(i);
                }
            });
            holder.l1Coupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginModel != null) {
                        if (package_id != null) {
                            UseCoupon(v);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GreatOfferActivity.this);
                            ViewGroup viewGroup = findViewById(android.R.id.content);
                            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.custom_package, viewGroup, false);
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

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GreatOfferActivity.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
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

                private void UseCoupon(final View view) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("user_id", loginModel.getSessionData().getId() + "");
                    hashMap.put("restaurant_id", restro_id + "");
                    hashMap.put("coupon_id", copon_id + "");
                    showProgressDialog();
                    Log.e("GAYA", hashMap + "");
                    Call<ResultGenerateNumberToUseCoupon> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).GenerateNumberToUseCoupon(hashMap);
                    marqueCall.enqueue(new Callback<ResultGenerateNumberToUseCoupon>() {
                        @Override
                        public void onResponse(@NonNull Call<ResultGenerateNumberToUseCoupon> call, @NonNull Response<ResultGenerateNumberToUseCoupon> response) {
                            ResultGenerateNumberToUseCoupon object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                            if (object != null && object.getError() == false) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GreatOfferActivity.this);
                                ViewGroup viewGroup = findViewById(android.R.id.content);
                                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_coupon, viewGroup, false);
                                builder.setView(dialogView);
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                TextView txtCode = alertDialog.findViewById(R.id.txtCode);
                                txtCode.setText(object.getResultGenerateNumberToUseCoupon().getCode());
                                dialogView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

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
                                    Toast.makeText(getApplicationContext(), jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResultGenerateNumberToUseCoupon> call, @NonNull Throwable t) {
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


            TextView txtAmount, txtDiscount, txtName, txtMaxAmount;
            ImageView img;
            LinearLayout l1Coupon;


            public MyViewHolder(View view) {
                super(view);


                txtName = view.findViewById(R.id.txtName);
                txtDiscount = view.findViewById(R.id.txtDiscount);
                txtAmount = view.findViewById(R.id.txtAmount);
                txtMaxAmount = view.findViewById(R.id.txtMaxAmount);
                img = view.findViewById(R.id.img);
                l1Coupon = view.findViewById(R.id.l1Coupon);


            }

        }

    }


}
