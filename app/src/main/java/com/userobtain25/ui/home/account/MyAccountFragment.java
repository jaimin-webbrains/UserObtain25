package com.userobtain25.ui.home.account;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.api.video_model.ApiConfig;
import com.userobtain25.api.video_model.AppConfig;
import com.userobtain25.api.video_model.ServerResponse;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.account.ResultGetRestoInfoById;
import com.userobtain25.model.account.ResultGetRestoInfoById_;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.ui.LoginActivity;
import com.userobtain25.ui.SelectionActivity;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class MyAccountFragment extends Fragment implements View.OnClickListener {
    final static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_ALL = 1;
    public static Bitmap bitmap;
    protected ViewDialog viewDialog;
    Dialog dialog;
    AppCompatTextView txtName, txtPd, txtAd,txtLogout, txtPassword, txtNotification, txtAa;
    CircularImageView imgResro;
    LoginModel loginModel;
    private String mediaPathImage;
    private ResultGetRestoInfoById_ resultGetRestoInfoById_;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        loginModel = PrefUtils.getUser(getActivity());
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);
        txtName = rootView.findViewById(R.id.txtName);
        txtPd = rootView.findViewById(R.id.txtPd);
        txtAd = rootView.findViewById(R.id.txtAd);
        txtLogout = rootView.findViewById(R.id.txtLogout);
        txtPassword = rootView.findViewById(R.id.txtPassword);
        txtNotification = rootView.findViewById(R.id.txtNotification);
        txtAa = rootView.findViewById(R.id.txtAa);
        imgResro = rootView.findViewById(R.id.imgResro);
        txtPd.setOnClickListener(this);
        txtAa.setOnClickListener(this);
        txtNotification.setOnClickListener(this);
        txtPassword.setOnClickListener(this);
        txtAd.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        imgResro.setOnClickListener(this);
        GetUserInfo();
        return rootView;
    }

    private void GetUserInfo() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", loginModel.getSessionData().getId() + "");

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultGetRestoInfoById> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).GetUserInfoById(hashMap);
        marqueCall.enqueue(new Callback<ResultGetRestoInfoById>() {
            @Override
            public void onResponse(@NonNull Call<ResultGetRestoInfoById> call, @NonNull Response<ResultGetRestoInfoById> response) {
                ResultGetRestoInfoById object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {


                    resultGetRestoInfoById_ = object.getResultGetRestoInfoById();
                    txtName.setText(resultGetRestoInfoById_.getName());
                    if (resultGetRestoInfoById_.getUserPhoto() != null) {
                        Glide.with(getActivity()).
                                load(BuildConstants.User_Image + resultGetRestoInfoById_.getUserPhoto()).
                                into(imgResro);
                    } else {
                        Glide.with(getActivity()).
                                load(R.drawable.ic_profile_menu).
                                into(imgResro);
                    }

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
                        Toast.makeText(getActivity(), jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultGetRestoInfoById> call, @NonNull Throwable t) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPd:
                personalDetail();
                break;
            case R.id.txtAd:
                //buy package code
                break;
            case R.id.txtPassword:
                changePassword();
                break;
            case R.id.txtNotification:
                Intent i = new Intent(getActivity(), NotificationActivity.class);
                startActivity(i);

                break;
            case R.id.txtAa:
                Intent i1 = new Intent(getActivity(), AboutAppActivity.class);
                startActivity(i1);
                break;
            case R.id.imgResro:
                if (hasPermissions(getActivity(), PERMISSIONS)) {
                    SelectImage();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                }
                break;
            case R.id.txtLogout:
                PrefUtils.clearCurrentUser(getActivity());
                Intent intent = new Intent(getActivity(), SelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }
    }

    private void SelectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 0);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPathImage = cursor.getString(columnIndex);
                // Set the Image in ImageView for Previewing the Media
                if (imgResro.getDrawable() == null) {
                    //Image doesn´t exist.
                    Toast.makeText(getActivity(), "Please Add Thumbnal Image", Toast.LENGTH_SHORT).show();
                } else {
                    imgResro.setImageBitmap(BitmapFactory.decodeFile(mediaPathImage));
                    Map<String, RequestBody> map = new HashMap<>();
                    File file = null;


                    try {
                        file = new File(mediaPathImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    RequestBody requestBody = null;
                    try {
                        assert file != null;
                        requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + resultGetRestoInfoById_.getId());

                    viewDialog.show();

                    if (file != null) {
                        map.put("user_photo\"; filename=\"" + file.getName() + "\"", requestBody);
                        map.put("id", id);
                        Log.e("Params", map + "");
                        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
                        Call<ServerResponse> call = getResponse.upload("application/json", map);
                        call.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                ServerResponse serverResponse = response.body();
                                if (serverResponse != null && serverResponse.getError() == false) {
                                    Log.e("TAG", "Product : " + new Gson().toJson(response.body()));


                                    Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();


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


                                }
                                viewDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                viewDialog.dismiss();
                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    } else {

                    }

                }
                cursor.close();

            } else {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void changePassword() {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editOld_p = dialog.findViewById(R.id.editOld_p);
        final EditText editNew_p = dialog.findViewById(R.id.editNew_p);
        final EditText editC_p = dialog.findViewById(R.id.editC_p);
        final Button btn_update = dialog.findViewById(R.id.btn_update);
        editOld_p.setText(resultGetRestoInfoById_.getPassword() + "");


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editOld = editOld_p.getText().toString().trim();
                final String editNew = editNew_p.getText().toString().trim();
                final String editConfirm = editC_p.getText().toString().trim();


                if (editOld.isEmpty()) {
                    editOld_p.setError("Old Password Required");
                    editOld_p.requestFocus();
                    return;
                }
                if (editNew.isEmpty()) {
                    editNew_p.setError("New Password Required");
                    editNew_p.requestFocus();
                    return;
                }
                if (editConfirm.isEmpty()) {
                    editC_p.setError("Confirm Password Required");
                    editC_p.requestFocus();
                    return;
                } else {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("old_password", editOld + "");
                    hashMap.put("password", editNew + "");
                    hashMap.put("cpassword", editC_p + "");
                    hashMap.put("id", resultGetRestoInfoById_.getId());


                    Log.e("GAYA", hashMap + "");
                    showProgressDialog();
                    Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ChangePasswordUser(hashMap);
                    marqueCall.enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                            SuccessModel object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "Add_Shop : " + new Gson().toJson(response.body()));
                            if (object != null && object.getError() == false) {
                                Toast.makeText(getContext(), object.getMsg() + "", Toast.LENGTH_SHORT).show();
                                GetUserInfo();
                                dialog.dismiss();
                            } else if (object != null && object.getError() == true) {
                                Toast.makeText(getContext(), object.getMsg() + "", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void personalDetail() {
        dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_personal_detail);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final AppCompatEditText editText_Address = dialog.findViewById(R.id.editText_Address);
        final AppCompatEditText editText_Mobile = dialog.findViewById(R.id.editText_Mobile);
        final AppCompatEditText editText_Email = dialog.findViewById(R.id.editText_Email);
        final AppCompatEditText editText_Name = dialog.findViewById(R.id.editText_Name);
        final AppCompatButton btn_update = dialog.findViewById(R.id.btn_update);

        editText_Address.setText(resultGetRestoInfoById_.getAddress() + "");
        editText_Mobile.setText(resultGetRestoInfoById_.getMobile() + "");
        editText_Email.setText(resultGetRestoInfoById_.getEmail() + "");
        editText_Name.setText(resultGetRestoInfoById_.getName() + "");


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String editTextName = editText_Name.getText().toString().trim();
                final String email = editText_Email.getText().toString().trim();
                final String mobile = editText_Mobile.getText().toString().trim();
                final String address = editText_Address.getText().toString().trim();


                if (editTextName.isEmpty()) {
                    editText_Name.setError("Customer Name Required");
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
                } else if (address.isEmpty()) {
                    editText_Address.setError("Address Required");
                    editText_Address.requestFocus();
                    return;
                } else {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", editTextName + "");
                    hashMap.put("address", address + "");
                    hashMap.put("id", resultGetRestoInfoById_.getId() + "");


                    Log.e("GAYA", hashMap + "");
                    showProgressDialog();
                    Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).UpdateUserInfo(hashMap);
                    marqueCall.enqueue(new Callback<SuccessModel>() {
                        @Override
                        public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                            SuccessModel object = response.body();
                            hideProgressDialog();
                            Log.e("TAG", "Add_Shop : " + new Gson().toJson(response.body()));
                            if (object != null && object.getError() == false) {
                                Toast.makeText(getContext(), object.getMsg() + "", Toast.LENGTH_SHORT).show();
                                GetUserInfo();
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
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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