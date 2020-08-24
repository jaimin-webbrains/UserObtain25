package com.userobtain25.ui.home.goout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.userobtain25.R;
import com.userobtain25.api.BuildConstants;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.account.ResultGetRestoInfoById;
import com.userobtain25.model.account.ResultGetRestoInfoById_;
import com.userobtain25.model.goout.neardeal.ResultNearestRestaurant;
import com.userobtain25.model.goout.neardeal.ResultNearestRestaurants;
import com.userobtain25.model.goout.populardeal.ResultGetBanner;
import com.userobtain25.model.goout.populardeal.ResultGetBanners;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.ui.HomeActivity;
import com.userobtain25.ui.LoginActivity;
import com.userobtain25.ui.home.search.SearchFragment;
import com.userobtain25.utils.AppPreferences;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static DecimalFormat df = new DecimalFormat("0.000");
    protected ViewDialog viewDialog;
    View TODO;
    String  package_id;
    AppCompatTextView txtLocation, txtSearch, txtNodata, txtNodata1;
    LinearLayout txtGreat, txtTrending, txtarrival;
    RecyclerView recyclerviewNear, recyclerviewPopular;
    LoginModel loginModel;
    private ResultGetRestoInfoById_ resultGetRestoInfoById_;
    private MyCustomAdapter myCustomAdapter;
    private NearDealAdapter nearDealAdapter;
    private ArrayList<ResultGetBanner> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private ArrayList<ResultNearestRestaurant> resultNearestRestaurants = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        GetNearDeals();
        GetUserInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        loginModel = PrefUtils.getUser(getActivity());
        viewDialog = new ViewDialog(getActivity());
        viewDialog.setCancelable(false);

        txtLocation = view.findViewById(R.id.txtLocation);
        txtSearch = view.findViewById(R.id.txtSearch);
        txtGreat = view.findViewById(R.id.txtGreat);
        txtTrending = view.findViewById(R.id.txtTrending);
        txtarrival = view.findViewById(R.id.txtarrival);
        txtNodata = view.findViewById(R.id.txtNodata);
        txtNodata1 = view.findViewById(R.id.txtNodata1);
        txtarrival.setOnClickListener(this);
        txtGreat.setOnClickListener(this);
        txtTrending.setOnClickListener(this);
        recyclerviewNear = view.findViewById(R.id.recyclerviewNear);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerviewNear.setLayoutManager(layoutManager);
        recyclerviewNear.setHasFixedSize(true);
        recyclerviewPopular = view.findViewById(R.id.recyclerviewPopular);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerviewPopular.setLayoutManager(layoutManager1);
        recyclerviewPopular.setHasFixedSize(true);
        GetPoplarDeals();
        GetNearDeals();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String _Location = listAddresses.get(0).getAddressLine(0);
                    txtLocation.setText(_Location);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return view;
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
                    package_id = resultGetRestoInfoById_.getPackageId();
                    AppPreferences.setPackageId(getActivity(), package_id);


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

    private void GetNearDeals() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("latitude", AppPreferences.getLati(getActivity()) + "");
        hashMap.put("longitude", AppPreferences.getLongi(getActivity()) + "");
        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultNearestRestaurants> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ResultNearestRestaurants(hashMap);
        marqueCall.enqueue(new Callback<ResultNearestRestaurants>() {
            @Override
            public void onResponse(@NonNull Call<ResultNearestRestaurants> call, @NonNull Response<ResultNearestRestaurants> response) {
                ResultNearestRestaurants object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultNearestRestaurants = object.getResultNearestRestaurants();
                    nearDealAdapter = new NearDealAdapter(resultNearestRestaurants);
                    recyclerviewNear.setAdapter(nearDealAdapter);

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
            public void onFailure(@NonNull Call<ResultNearestRestaurants> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
    }

    private void GetPoplarDeals() {
        HashMap<String, String> hashMap = new HashMap<>();

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResultGetBanners> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).ResultGetBanners(hashMap);
        marqueCall.enqueue(new Callback<ResultGetBanners>() {
            @Override
            public void onResponse(@NonNull Call<ResultGetBanners> call, @NonNull Response<ResultGetBanners> response) {
                ResultGetBanners object = response.body();
                hideProgressDialog();
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayRestaurantCoupon_s = object.getResultGetBanners();
                    myCustomAdapter = new MyCustomAdapter(resultDisplayRestaurantCoupon_s);
                    recyclerviewPopular.setAdapter(myCustomAdapter);

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
            public void onFailure(@NonNull Call<ResultGetBanners> call, @NonNull Throwable t) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtarrival:
                Intent i = new Intent(getActivity(), NewArrivalActivity.class);
                startActivity(i);
                break;
            case R.id.txtTrending:
                Intent i1 = new Intent(getActivity(), TrendingActivity.class);
                startActivity(i1);
                break;
            case R.id.txtGreat:
                Intent i2 = new Intent(getActivity(), GreatOfferActivity.class);
                startActivity(i2);
                break;
            case R.id.txtSearch:
                Intent i3 = new Intent(getActivity(), SearchFragment.class);
                startActivity(i3);
                break;

        }

    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<ResultGetBanner> moviesList;

        public MyCustomAdapter(ArrayList<ResultGetBanner> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_slider_list, parent, false);

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


            final ResultGetBanner datum = moviesList.get(position);


            if (datum.getImageFileName() != null) {
                Glide.with(getActivity()).
                        load(BuildConstants.Slider_Image + datum.getImageFileName()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(getActivity()).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Banner_deailActivity.class);
                    i.putExtra("discount", datum.getDiscount());
                    startActivity(i);
                }
            });

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            ImageView img;


            public MyViewHolder(View view) {
                super(view);


                img = view.findViewById(R.id.img);


            }

        }

    }

    public class NearDealAdapter extends RecyclerView.Adapter<NearDealAdapter.MyViewHolder> {

        private ArrayList<ResultNearestRestaurant> moviesList;

        public NearDealAdapter(ArrayList<ResultNearestRestaurant> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public NearDealAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_near_resro_list, parent, false);

            return new NearDealAdapter.MyViewHolder(itemView);
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
        public void onBindViewHolder(NearDealAdapter.MyViewHolder holder, final int position) {


            final ResultNearestRestaurant datum = moviesList.get(position);


            double input = Double.parseDouble(String.valueOf(datum.getDistance()));
            holder.txtDistance.setText("Distance :" + df.format(input) + " Km ");
            holder.txtName.setText(datum.getName());
            if (datum.getNewRating() != null) {
                double rating = Double.parseDouble(datum.getNewRating() + "");
                int r_value = (int) Math.round(rating);
                holder.rating.setRating(Integer.parseInt(r_value + ""));

            } else {
                holder.rating.setRating(0);

            }

            if (datum.getRestoPhoto() != null) {
                Glide.with(getActivity()).
                        load(BuildConstants.Main_Image + datum.getRestoPhoto()).
                        asBitmap().
                        into(holder.img);
            } else {
                Glide.with(getActivity()).
                        load(R.drawable.mcdonalds).
                        asBitmap().
                        into(holder.img);
            }
            holder.txtRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginModel != null) {
                        Intent i = new Intent(getActivity(), RatingActivity.class);
                        i.putExtra("restro_id", datum.getId());
                        startActivity(i);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Near_deal_deailActivity.class);
                    i.putExtra("restro_id", datum.getId());
                    i.putExtra("type", "0");//0:near deal
                    startActivity(i);
                }
            });

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            ImageView img;
            TextView txtDistance, txtName, txtRating;
            SimpleRatingBar rating;

            public MyViewHolder(View view) {
                super(view);


                img = view.findViewById(R.id.img);
                txtName = view.findViewById(R.id.txtName);
                txtDistance = view.findViewById(R.id.txtDistance);
                txtRating = view.findViewById(R.id.txtRating);
                rating = view.findViewById(R.id.rating);


            }

        }

    }

}