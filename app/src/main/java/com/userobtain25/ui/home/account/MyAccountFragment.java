package com.userobtain25.ui.home.account;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
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
import com.userobtain25.model.account.ResultUserPackageInfo;
import com.userobtain25.model.account.ResultUserPackageInfo_;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.ui.SelectionActivity;
import com.userobtain25.utils.AppPreferences;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;


public class MyAccountFragment extends Fragment implements View.OnClickListener {
    final static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_ALL = 1;
    private static final String IMAGE_DIRECTORY = "/WebBrains";
    public static Bitmap bitmap;
    protected ViewDialog viewDialog;
    Dialog dialog;
    AppCompatTextView txtName, txtPd, txtAd, txtLogout, txtPassword, txtNotification, txtAa, txtRv, txtSDate, txtEDate, txtOffer;
    View ViewAd, viewRv;
    LinearLayout l1Ad, l1Rv, l1Date;
    CircularImageView imgResro;
    LoginModel loginModel;
    String path;
    String package_id;
    String selectedImagePath;
    private ResultGetRestoInfoById_ resultGetRestoInfoById_;
    private ArrayList<ResultUserPackageInfo_> resultUserPackageInfo_s = new ArrayList<>();

    private Uri filePath;
    private int GALLERY = 1, CAMERA = 2;

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
        txtRv = rootView.findViewById(R.id.txtRv);
        txtSDate = rootView.findViewById(R.id.txtSDate);
        txtEDate = rootView.findViewById(R.id.txtEDate);
        txtOffer = rootView.findViewById(R.id.txtOffer);
        l1Date = rootView.findViewById(R.id.l1Date);
        l1Rv = rootView.findViewById(R.id.l1Rv);
        l1Ad = rootView.findViewById(R.id.l1Ad);
        viewRv = rootView.findViewById(R.id.viewRv);
        ViewAd = rootView.findViewById(R.id.ViewAd);
        imgResro = rootView.findViewById(R.id.imgResro);
        txtPd.setOnClickListener(this);
        txtAa.setOnClickListener(this);
        txtNotification.setOnClickListener(this);
        txtPassword.setOnClickListener(this);
        txtAd.setOnClickListener(this);
        txtRv.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        imgResro.setOnClickListener(this);

        GetUserInfo();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GetUserInfo();
    }

    private void GetPackageInfo() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", loginModel.getSessionData().getId() + "");

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultUserPackageInfo> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).UserPackageInfo(hashMap);
        marqueCall.enqueue(new Callback<ResultUserPackageInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResultUserPackageInfo> call, @NonNull Response<ResultUserPackageInfo> response) {
                ResultUserPackageInfo object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {


                    resultUserPackageInfo_s = object.getResultUserPackageInfo();
                    package_id = resultGetRestoInfoById_.getPackageId();
                    AppPreferences.setPackageId(getActivity(), package_id);
                    txtEDate.setText(resultUserPackageInfo_s.get(0).getPackageEnds());
                    txtSDate.setText(resultUserPackageInfo_s.get(0).getPackageStarts());
                    txtOffer.setText(resultUserPackageInfo_s.get(0).getPackageName());
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
            public void onFailure(@NonNull Call<ResultUserPackageInfo> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
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
                    if (resultGetRestoInfoById_.getPackageId() != null) {
                        l1Rv.setVisibility(View.VISIBLE);
                        l1Date.setVisibility(View.VISIBLE);
                        txtOffer.setVisibility(View.VISIBLE);
                        ViewAd.setVisibility(View.GONE);
                        l1Ad.setVisibility(View.GONE);
                        GetPackageInfo();
                    } else {
                        l1Rv.setVisibility(View.GONE);
                        viewRv.setVisibility(View.GONE);
                        l1Date.setVisibility(View.GONE);
                        txtOffer.setVisibility(View.GONE);
                        l1Ad.setVisibility(View.VISIBLE);
                    }
                    txtName.setText(resultGetRestoInfoById_.getName());
                    if (resultGetRestoInfoById_.getUserPhoto() != null) {
                        Glide.with(getActivity()).
                                load(BuildConstants.User_Image + resultGetRestoInfoById_.getUserPhoto()).
                                into(imgResro);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imgResro.setImageResource(R.drawable.new_profile);
                        }
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
                Intent i2 = new Intent(getActivity(), PackageActivity.class);
                startActivity(i2);
                break;
            case R.id.txtRv:
                Intent i3 = new Intent(getActivity(), RequestActivity.class);
                startActivity(i3);
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
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Add Photo!");
        String[] pictureDialogItems = {
                "Take Photo",
                "Choose from Gallery",
                "Cancel"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                takePhotoFromCamera();
                                break;
                            case 1:
                                choosePhotoFromGallary();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        filePath = data.getData();
        selectedImagePath = getPath(filePath);

        if (requestCode == GALLERY) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);


                imgResro.setImageBitmap(bitmap);
                Map<String, RequestBody> map = new HashMap<>();
                File file = null;


                try {
                    file = new File(selectedImagePath);

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

                showProgressDialog();

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
                                GetUserInfo();
                                hideProgressDialog();
                                Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();


                            } else if (serverResponse != null && serverResponse.getError() == true) {
                                Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
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
                            hideProgressDialog();
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            hideProgressDialog();
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imgResro.setImageBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File wallpaperDirectory = new File(
                    Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
            // have the object build the directory structure, if needed.
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }
            try {
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File f = new File(wallpaperDirectory, timeStamp + ".jpg");
                path = String.valueOf(f);
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                MediaScannerConnection.scanFile(getActivity(),
                        new String[]{f.getPath()},
                        new String[]{"image/jpeg"}, null);
                // loadImage(f);
                fo.close();
                Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Map<String, RequestBody> map = new HashMap<>();
            File fileCamera = null;


            try {
                fileCamera = new File(path);

            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBody requestBody = null;
            try {
                assert fileCamera != null;
                requestBody = RequestBody.create(MediaType.parse("*/*"), fileCamera);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + resultGetRestoInfoById_.getId());

            showProgressDialog();

            if (fileCamera != null) {
                map.put("user_photo\"; filename=\"" + fileCamera.getName() + "\"", requestBody);
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
                            GetUserInfo();
                            hideProgressDialog();
                            Toast.makeText(getActivity(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();


                        } else if (serverResponse != null && serverResponse.getError() == true) {
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
    }

    private String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
                null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
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
        final ImageView show_pass_btn = dialog.findViewById(R.id.show_pass_btn);
        final ImageView show_pass_new = dialog.findViewById(R.id.show_pass_new);
        final ImageView show_pass_confirm = dialog.findViewById(R.id.show_pass_confirm);
        final Button btn_update = dialog.findViewById(R.id.btn_update);
        if (resultGetRestoInfoById_.getPassword() != null) {
            editOld_p.setText(resultGetRestoInfoById_.getPassword() + "");
        }

        show_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass(v);
            }

            private void ShowHidePass(View v) {
                if (v.getId() == R.id.show_pass_btn) {

                    if (editOld_p.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_remove_red_eye_24);

                        //Show Password
                        editOld_p.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);

                        //Hide Password
                        editOld_p.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }
                }
            }
        });
        show_pass_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass(v);
            }

            private void ShowHidePass(View v) {
                if (v.getId() == R.id.show_pass_new) {

                    if (editNew_p.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_remove_red_eye_24);

                        //Show Password
                        editNew_p.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);

                        //Hide Password
                        editNew_p.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }
                }
            }
        });
        show_pass_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHidePass(v);
            }

            private void ShowHidePass(View v) {
                if (v.getId() == R.id.show_pass_confirm) {

                    if (editC_p.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_remove_red_eye_24);

                        //Show Password
                        editC_p.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        ((ImageView) (v)).setImageResource(R.drawable.ic_baseline_visibility_off_24);

                        //Hide Password
                        editC_p.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }
                }
            }
        });

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
                    hashMap.put("cpassword", editConfirm + "");
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