package com.userobtain25.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.account.ResultGetRestoInfoById;
import com.userobtain25.model.account.ResultGetRestoInfoById_;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.model.term.ResultGetTurms;
import com.userobtain25.ui.home.account.MyAccountFragment;
import com.userobtain25.ui.home.goout.HomeFragment;
import com.userobtain25.ui.home.login.LoginFragment;
import com.userobtain25.ui.home.search.SearchFragment;
import com.userobtain25.utils.AppPreferences;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    final String TAG = "GPS";
    protected ViewDialog viewDialog;
    Dialog dialog;
    String type, updateToken;
    LoginModel loginModel;
    GoogleApiClient gac;
    LocationRequest locationRequest;
    BottomNavigationView bottomNavigationView;
    private long UPDATE_INTERVAL = 2
            * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        type = getIntent().getStringExtra("type");
        //updateToken = getIntent().getStringExtra("updateToken");
        updateToken = AppPreferences.getToken(HomeActivity.this);

        loginModel = PrefUtils.getUser(HomeActivity.this);
        viewDialog = new ViewDialog(HomeActivity.this);
        viewDialog.setCancelable(false);

        isGooglePlayServicesAvailable();
        if (!isLocationEnabled())
            showAlert();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(HomeActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        loadFragment(new HomeFragment());
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if (loginModel != null) {
            bottomNavigationView.getMenu().getItem(2).setVisible(true);
            bottomNavigationView.getMenu().getItem(3).setVisible(false);
        } else {
            bottomNavigationView.getMenu().getItem(2).setVisible(false);
            bottomNavigationView.getMenu().getItem(3).setVisible(true);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();

                        break;
                    case R.id.nav_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.nav_account:
                        fragment = new MyAccountFragment();

                        break;
                    case R.id.nav_login:
                        fragment = new LoginFragment();

                        break;

                }
                return loadFragment(fragment);
            }
        });
        if (loginModel != null) {
            if (type.equals("0")) {
                GetTerm();
            } else if (type.equals("1")) {

            }
            GetToken();
        } else {

        }

    }

    private void GetToken() {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("id", loginModel.getSessionData().getId() + "");
        hashMap.put("type", "0" + "");
        hashMap.put("tocken", updateToken + "");
        Log.e("TAG", "Token_Pojo: " +hashMap);

        showProgressDialog();
        Call<SuccessModel> loginModelCall = RetrofitHelper.createService(RetrofitHelper.Service.class).RefreshToken(hashMap);
        loginModelCall.enqueue(new Callback<SuccessModel>() {

            @Override
            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                SuccessModel object = response.body();
                hideProgressDialog();

                if (object != null && object.getError() == false) {
                    Log.e("TAG", "Login_Response : " + new Gson().toJson(response.body()));


                   // Toast.makeText(HomeActivity.this, object.getMsg(), Toast.LENGTH_SHORT).show();


                } else if (object != null && object.getError() == true) {
                    Toast.makeText(HomeActivity.this, object.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Log.e("Login_Response", t.getMessage() + "");
            }
        });
    }
    private void GetTerm() {

        dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final AppCompatTextView txtName = dialog.findViewById(R.id.txtName);
        final Button btn_update = dialog.findViewById(R.id.btn_update);

        HashMap<String, String> hashMap = new HashMap<>();


        Log.e("GAYA", hashMap + "");
        showProgressDialog();
        Call<ResultGetTurms> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).GetTurms(hashMap);
        marqueCall.enqueue(new Callback<ResultGetTurms>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<ResultGetTurms> call, @NonNull Response<ResultGetTurms> response) {
                ResultGetTurms object = response.body();
                hideProgressDialog();
                Log.e("TAG", "Add_Shop : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    // txtName.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                    txtName.setText(object.getResultGetTurms().getDetail());

                } else if (object != null && object.getError() == true) {
                    Toast.makeText(HomeActivity.this, object.getMessage() + "", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(HomeActivity.this, jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultGetTurms> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }


        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) HomeActivity.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use HomeActivity.this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        updateUI(location);


        Log.e("Shilpa==>", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        if (location != null) {


            try {

                Geocoder geo = new Geocoder(HomeActivity.this, Locale.getDefault());


            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }

        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Log.d(TAG, "onConnected");

        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));

        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(HomeActivity.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d("DDD", connectionResult.toString());
    }


    public void updateUI(Location location) {
        Log.d(TAG, "updateUI");


        Log.e("Update = >", "API CALL");

        Log.e("LATLONG", location.getLatitude() + "" + "    " + location.getLongitude() + "");

    }


    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(HomeActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(HomeActivity.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "HomeActivity.this device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "HomeActivity.this device is supported.");
        return true;
    }


    @Override
    public void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        gac.disconnect();
        Log.e("lifecycle", "onStop invoked");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (loginModel != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            moveTaskToBack(true);
                            // finish();
                        }
                    }).create().show();

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent i = new Intent(HomeActivity.this, SelectionActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }
                    }).create().show();

        }
    }
}
