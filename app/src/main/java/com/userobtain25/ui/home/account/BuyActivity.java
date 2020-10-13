package com.userobtain25.ui.home.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.Constants;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyActivity extends AppCompatActivity {

    String package_id,package_name;
    Float price;
    AppCompatTextView txv_product_price, txt_buy_product;
    LoginModel loginModel;
    protected ViewDialog viewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        initView();
    }

    private void initView() {
        package_id = getIntent().getStringExtra("package_id");
        package_name = getIntent().getStringExtra("package_name");
        price = Float.valueOf(getIntent().getStringExtra("price"));
        loginModel = PrefUtils.getUser(BuyActivity.this);
        viewDialog = new ViewDialog(BuyActivity.this);
        viewDialog.setCancelable(false);
        txv_product_price = findViewById(R.id.txv_product_price);
        txt_buy_product = findViewById(R.id.txt_buy_product);
        txt_buy_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_buy_product.setEnabled(false);
                launchPaymentFlow();
            }
        });
    }

    private void launchPaymentFlow() {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        payUmoneyConfig.setPayUmoneyActivityTitle("Buy" + package_name);
        payUmoneyConfig.setDoneButtonText("Pay " + getResources().getString(R.string.Rupees) + price);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(String.valueOf(price))
                .setTxnId(System.currentTimeMillis() + "")
                .setPhone(Constants.MOBILE)
                .setProductName(package_name)
                .setFirstName(Constants.FIRST_NAME)
                .setEmail(Constants.EMAIL)
                .setsUrl(Constants.SURL)
                .setfUrl(Constants.FURL)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(Constants.DEBUG)
                .setKey(Constants.MERCHANT_KEY)
                .setMerchantId(Constants.MERCHANT_ID);

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            calculateHashInServer();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            txt_buy_product.setEnabled(true);
        }
    }

    private void calculateHashInServer() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("order_id",  "");
        hashMap.put("package_id", package_id + "");
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
                    Toast.makeText(BuyActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(BuyActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

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
    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        txt_buy_product.setEnabled(true);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {

            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }
    }

    private Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

}