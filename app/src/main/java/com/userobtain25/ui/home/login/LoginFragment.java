package com.userobtain25.ui.home.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.ui.HomeActivity;
import com.userobtain25.ui.RegisterActivity;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment implements View.OnClickListener {

    protected ViewDialog viewDialog;
    EditText editText_Email, editText_Password;
    AppCompatButton btn_login;
    AppCompatTextView label_donthaveaccount, label_f_password;
    LoginModel loginModel;
    Dialog dialog;
    ImageView show_confirm;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        loginModel = PrefUtils.getUser(getActivity());
        viewDialog = new ViewDialog(getActivity());
        viewDialog.setCancelable(false);
        try {
            loginModel = PrefUtils.getUser(getActivity());
            if (loginModel.getSessionData() != null) {
                Intent i = new Intent(getActivity(), HomeActivity.class);
                i.putExtra("type", "1");
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editText_Email = rootView.findViewById(R.id.editText_Email);
        editText_Password = rootView.findViewById(R.id.editText_Password);
        btn_login = rootView.findViewById(R.id.btn_login);
        show_confirm = rootView.findViewById(R.id.show_confirm);
        label_f_password = rootView.findViewById(R.id.label_f_password);
        label_donthaveaccount = rootView.findViewById(R.id.label_donthaveaccount);
        btn_login.setOnClickListener(this);
        label_f_password.setOnClickListener(this);
        label_donthaveaccount.setOnClickListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        show_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass(v);
            }

            private void ShowHidePass(View v) {
                if (v.getId() == R.id.show_confirm) {

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
        });
        return rootView;
    }

    public void LoginCall() {

        final String email_id = editText_Email.getText().toString().trim();
        final String pass_word = editText_Password.getText().toString().trim();


        if (!email_id.matches(("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))) {
            editText_Email.setError("Invalid Email");
            editText_Email.requestFocus();
            return;
        } else if (pass_word.isEmpty()) {
            editText_Password.setError("Password Required");
            editText_Password.requestFocus();
            return;
        } else {
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put("email", email_id + "");
            hashMap.put("password", pass_word + "");

            showProgressDialog();
            Call<LoginModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).LoginModel(hashMap);
            loginModelCall.enqueue(new Callback<LoginModel>() {

                @Override
                public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                    LoginModel object = response.body();
                    hideProgressDialog();

                    if (object != null && object.getError() == false) {
                        Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));

                        PrefUtils.setUser(object, getActivity());


                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        i.putExtra("type", "0");
                        startActivity(i);


                    } else if (object != null && object.getError() == true) {
                        Toast.makeText(getActivity(), object.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                    hideProgressDialog();
                    t.printStackTrace();
                    Log.e("Login_Response", t.getMessage() + "");
                }
            });
        }
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                LoginCall();
                break;
            case R.id.label_donthaveaccount:
                Intent i = new Intent(getActivity(), RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.label_f_password:
                forgotPassword();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void forgotPassword() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editEmail = dialog.findViewById(R.id.editEmail);
        final Button btn_update = dialog.findViewById(R.id.btn_update);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = editEmail.getText().toString().trim();


                if (Email.isEmpty()) {
                    editEmail.setError("Old Password Required");
                    editEmail.requestFocus();
                    return;
                } else {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("email", Email + "");

                    Log.e("GAYA", hashMap + "");
                    showProgressDialog();
                    Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).forgetpassword(hashMap);
                    marqueCall.enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                            SuccessModel object = response.body();
                            hideProgressDialog();
                            if (object != null && object.getError() == false) {
                                Toast.makeText(getActivity(), object.getMsg() + "", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else if (object != null && object.getError() == true) {
                                Toast.makeText(getActivity(), object.getMsg() + "", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
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

                                try {

                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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

}