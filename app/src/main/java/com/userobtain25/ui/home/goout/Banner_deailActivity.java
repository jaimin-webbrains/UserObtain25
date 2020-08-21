package com.userobtain25.ui.home.goout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.goout.populardeal.ResultBannerCoupon;
import com.userobtain25.model.goout.populardeal.ResultBannerCoupon_;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Banner_deailActivity extends AppCompatActivity {
    protected ViewDialog viewDialog;
    RecyclerView recyclerviewNear;
    LoginModel loginModel;
    LinearLayout l1Horizontal;
    String discount;
    Dialog dialog;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RestroAdapter restroAdapter;
    private ArrayList<ResultBannerCoupon_> resultDisplayActiveRestaurantCoupon_s = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_deatail);
        initView();
        initToolbar();
    }

    private void initView() {
        l1Horizontal = findViewById(R.id.l1Horizontal);
        discount = getIntent().getStringExtra("discount");


        loginModel = PrefUtils.getUser(Banner_deailActivity.this);
        viewDialog = new ViewDialog(Banner_deailActivity.this);
        recyclerviewNear = findViewById(R.id.recyclerviewNear);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Banner_deailActivity.this);
        recyclerviewNear.setLayoutManager(layoutManager);
        recyclerviewNear.setHasFixedSize(true);

        viewDialog.setCancelable(false);
        GetCouponList();
    }

    private void GetCouponList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("discount", discount + "");

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultBannerCoupon> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).BannerCoupon(hashMap);
        marqueCall.enqueue(new Callback<ResultBannerCoupon>() {
            @Override
            public void onResponse(@NonNull Call<ResultBannerCoupon> call, @NonNull Response<ResultBannerCoupon> response) {
                ResultBannerCoupon object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayActiveRestaurantCoupon_s = object.getResultBannerCoupon();
                    restroAdapter = new RestroAdapter(resultDisplayActiveRestaurantCoupon_s);
                    recyclerviewNear.setAdapter(restroAdapter);

                } else if (object != null && object.getError() == true) {
                    //Toast.makeText(Banner_deailActivity.this, object.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Banner_deailActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultBannerCoupon> call, @NonNull Throwable t) {
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Baner Detail");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public class RestroAdapter extends RecyclerView.Adapter<RestroAdapter.MyViewHolder> {
        String resro_id, member;
        private ArrayList<ResultBannerCoupon_> moviesList;

        public RestroAdapter(ArrayList<ResultBannerCoupon_> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public RestroAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_active_coupon_list, parent, false);

            return new RestroAdapter.MyViewHolder(itemView);
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
        public void onBindViewHolder(RestroAdapter.MyViewHolder holder, final int position) {


            final ResultBannerCoupon_ datum = moviesList.get(position);

            resro_id = datum.getRestaurantId();
            member = datum.getActive();
            holder.txtName.setText(datum.getRestoName() + "");
            holder.txtDiscount.setText("Discount Value : " + datum.getDiscountValue() + " % ");
            holder.txtAmount.setText("Minimum Amount : " + datum.getMinimumAmount() + " ₹ ");
            holder.imgpercent.setText(datum.getActive() + "");
            if (datum.getRestoPhoto() != null) {
                Glide.with(Banner_deailActivity.this).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(Banner_deailActivity.this).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }
            holder.l1Request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginModel != null) {
                        if (loginModel.getSessionData().getPackageId() != null) {
                            SendRequest();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Banner_deailActivity.this);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(Banner_deailActivity.this);
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
            });

        }

        private void SendRequest() {
            dialog = new Dialog(Banner_deailActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_send_request);
            dialog.setCancelable(true);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final EditText editN_m = dialog.findViewById(R.id.editN_m);
            final EditText editDate = dialog.findViewById(R.id.editDate);
            final EditText editTime = dialog.findViewById(R.id.editTime);
            final Button btn_update = dialog.findViewById(R.id.btn_update);
            editDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(Banner_deailActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    editDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();

                }
            });
            editTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(Banner_deailActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    editTime.setText(hourOfDay + ":" + minute);
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();

                }
            });

            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String member = editN_m.getText().toString().trim();
                    final String date = editDate.getText().toString().trim();
                    final String time = editTime.getText().toString().trim();


                    if (member.isEmpty()) {
                        editN_m.setError("Number Of Member Required");
                        editN_m.requestFocus();
                        return;
                    }
                    if (date.isEmpty()) {
                        editDate.setError("Date Required");
                        editDate.requestFocus();
                        return;
                    }
                    if (time.isEmpty()) {
                        editTime.setError("Time Required");
                        editTime.requestFocus();
                        return;
                    } else {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("user_id", loginModel.getSessionData().getId() + "");
                        hashMap.put("resto_id", resro_id + "");
                        hashMap.put("num_member", member + "");
                        hashMap.put("date_and_time", date + time + "");
                        showProgressDialog();
                        Log.e("GAYA", hashMap + "");
                        Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).UserRequest(hashMap);
                        marqueCall.enqueue(new Callback<SuccessModel>() {
                            @Override
                            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                                SuccessModel object = response.body();
                                hideProgressDialog();
                                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                if (object != null && object.getError() == false) {
                                    dialog.dismiss();
                                    // object.getResultGenerateNumberToUseCoupon().getCode();
                                    Toast.makeText(getApplicationContext(), object.getMsg(), Toast.LENGTH_SHORT).show();

                                } else if (object != null && object.getError() == true) {
                                    dialog.dismiss();
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
                            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                                t.printStackTrace();
                                hideProgressDialog();
                                Log.e("ChatV_Response", t.getMessage() + "");
                            }
                        });
                    }

                }
            });

            ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView imgpercent, txtAmount, txtDiscount, txtName;
            ImageView img;
            LinearLayout l1Request;


            public MyViewHolder(View view) {
                super(view);


                txtName = view.findViewById(R.id.txtName);
                txtDiscount = view.findViewById(R.id.txtDiscount);
                txtAmount = view.findViewById(R.id.txtAmount);
                imgpercent = view.findViewById(R.id.imgpercent);
                img = view.findViewById(R.id.img);
                l1Request = view.findViewById(R.id.l1Request);


            }

        }

    }

}
