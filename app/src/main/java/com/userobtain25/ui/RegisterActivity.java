package com.userobtain25.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.utils.AppPreferences;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    protected ViewDialog viewDialog;
    AppCompatEditText editText_Address, editText_CPassword, editText_Password, editText_Food, editText_Adhar, editText_GST, editText_Mobile, editText_Email, editText_Person, editText_Name;
    AppCompatButton btn_signUp;
    String updateToken;
    ImageView show_password, show_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        updateToken = AppPreferences.getToken(RegisterActivity.this);

        viewDialog = new ViewDialog(RegisterActivity.this);
        viewDialog.setCancelable(false);
        editText_Address = findViewById(R.id.editText_Address);
        editText_CPassword = findViewById(R.id.editText_CPassword);
        editText_Password = findViewById(R.id.editText_Password);
        editText_Mobile = findViewById(R.id.editText_Mobile);
        editText_Email = findViewById(R.id.editText_Email);
        editText_Name = findViewById(R.id.editText_Name);
        show_password = findViewById(R.id.show_password);
        show_confirm = findViewById(R.id.show_confirm);
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(this);
        show_password.setOnClickListener(this);
        show_confirm.setOnClickListener(this);

    }


    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_signUp:
                SignUpCall();
                break;
            case R.id.show_password:
                ShowHidePass(v);
                break;
            case R.id.show_confirm:
                ShowHidePassC(v);
                break;
        }
    }

    private void ShowHidePassC(View v) {
        if (v.getId() == R.id.show_confirm) {

            if (editText_CPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_remove_red_eye_24);

                //Show Password
                editText_CPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);

                //Hide Password
                editText_CPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    private void ShowHidePass(View v) {
        if (v.getId() == R.id.show_password) {

            if (editText_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_remove_red_eye_24);

                //Show Password
                editText_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);

                //Hide Password
                editText_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }


    public void SignUpCall() {
        final String editTextName = editText_Name.getText().toString().trim();
        final String email = editText_Email.getText().toString().trim();
        final String mobile = editText_Mobile.getText().toString().trim();
        final String pass_word = editText_Password.getText().toString().trim();
        final String c_password = editText_CPassword.getText().toString().trim();
        final String address = editText_Address.getText().toString().trim();


        if (editTextName.isEmpty()) {
            editText_Name.setError("Restaurant Name Required");
            editText_Name.requestFocus();
            return;
        } else if (!email.matches(("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))) {
            editText_Email.setError("Invalid Email");
            editText_Email.requestFocus();
            return;
        } else if (mobile.isEmpty()) {
            editText_Mobile.setError("Mobile Required");
            editText_Mobile.requestFocus();
            return;
        } else if (pass_word.isEmpty()) {
            editText_Password.setError("Password Required");
            editText_Password.requestFocus();
            return;
        } else if (c_password.isEmpty()) {
            editText_CPassword.setError("Confirm Password Required");
            editText_CPassword.requestFocus();
            return;
        } else if (address.isEmpty()) {
            editText_Address.setError("Address Required");
            editText_Address.requestFocus();
            return;
        } else {
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("name", editTextName + "");
            hashMap.put("mobile", mobile + "");
            hashMap.put("email", email + "");
            hashMap.put("password", pass_word + "");
            hashMap.put("cpassword", c_password + "");
            hashMap.put("address", address + "");
            hashMap.put("user_tocken", updateToken + "");


            Log.e("HasMap =>", hashMap + "");
            showProgressDialog();
            Call<SuccessModel> viewBillingInfoModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).RegisterModel(hashMap);
            viewBillingInfoModelCall.enqueue(new Callback<SuccessModel>() {

                @Override
                public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                    SuccessModel object = response.body();

                    hideProgressDialog();
                    if (object != null && object.getError() == false) {
                        Log.e("TAG", "PO_Response : " + new Gson().toJson(response.body()));

                        Toast.makeText(RegisterActivity.this, object.getMsg() + "", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else if (object != null && object.getError() == true) {
                        Toast.makeText(RegisterActivity.this, object.getMsg(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }

                @Override
                public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                    hideProgressDialog();
                    t.printStackTrace();
                    Log.e("PO_Response", t.getMessage() + "");
                }
            });
        }

    }
}












