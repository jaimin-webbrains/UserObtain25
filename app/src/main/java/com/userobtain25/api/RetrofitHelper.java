package com.userobtain25.api;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.account.ResponseAcceptedOrRejected;
import com.userobtain25.model.account.ResultGetRestoInfoById;
import com.userobtain25.model.account.ResultGreatOffers;
import com.userobtain25.model.account.ResultPackages;
import com.userobtain25.model.account.ResultUserPackageInfo;
import com.userobtain25.model.goout.greatoffer.ResultGenerateNumberToUseCoupon;
import com.userobtain25.model.goout.greatoffer.ResultUserCoupons;
import com.userobtain25.model.goout.neardeal.ResultDisplayActiveRestaurantCoupon;
import com.userobtain25.model.goout.neardeal.ResultDisplayRestoPhoto;
import com.userobtain25.model.goout.neardeal.ResultGetReviews;
import com.userobtain25.model.goout.neardeal.ResultNearestRestaurants;
import com.userobtain25.model.goout.newarrival.ResultNewArivals;
import com.userobtain25.model.goout.populardeal.ResultBannerCoupon;
import com.userobtain25.model.goout.populardeal.ResultGetBanners;
import com.userobtain25.model.goout.trendingplace.ResultTrandingPlaces;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.model.search.ResultSearchResto;
import com.userobtain25.model.term.ResultGetTurms;

import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * Created by Karan Brahmaxatriya on 20-Sept-18.
 */
public class RetrofitHelper {
    public static OkHttpClient okHttpClient;
    public static Retrofit retrofit, retrofitMatchScore;
    public static CookieManager cookieManager;

    public static OkHttpClient getOkHttpClientInstance() {
        if (okHttpClient != null) {
            return okHttpClient;
        }


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(2000, TimeUnit.SECONDS);
        httpClient.readTimeout(2000, TimeUnit.SECONDS);
        httpClient.writeTimeout(2000, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
              /*  if (!Connectivity.isConnected(Betx11Application.getContext())) {
                    throw new NoConnectivityException();
                }*/
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder(); // Add Device Detail

                Request request = requestBuilder.build();
                Response response = chain.proceed(request);
                String requestedHost = request.url().host();
                assert response.networkResponse() != null;
                String responseHost = response.networkResponse().request().url().host();
                if (!requestedHost.equalsIgnoreCase(responseHost)) {
                    throw new NoConnectivityException();
                }
                return response;
            }
        });
        httpClient.addInterceptor(logging);

        return httpClient.build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConstants.CURRENT_REST_URL)
                    .client(getOkHttpClientInstance())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit.create(serviceClass);
    }

    public static ArrayList<KeyValueModel> getKeyValueInputData(LinkedHashMap<String, String> hm) {
        ArrayList<KeyValueModel> modelList = new ArrayList<>();
        for (String key : hm.keySet()) {
            KeyValueModel obj = new KeyValueModel();
            obj.setKey(key);
            obj.setValue(hm.get(key));
            modelList.add(obj);
        }
        return modelList;
    }


    public interface Service {
        /*---------------------------------------GET METHOD------------------------------------------------------*/





        /*------------------------------------------POST METHOD---------------------------------------------------*/

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("user_register")
        Call<SuccessModel> RegisterModel(@FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("login_user")
        Call<LoginModel> LoginModel(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("forgetpassword")
        Call<SuccessModel> forgetpassword(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("GetTurms")
        Call<ResultGetTurms> GetTurms(@FieldMap HashMap<String, String> hashMap);

        //////////////my account///////////////////

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("GetUserInfoById")
        Call<ResultGetRestoInfoById> GetUserInfoById(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("UpdateUserInfo")
        Call<SuccessModel> UpdateUserInfo(@FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("GreatOffers")
        Call<ResultGreatOffers> GreatOffers(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("ChangePasswordUser")
        Call<SuccessModel> ChangePasswordUser(@FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("Packages")
        Call<ResultPackages> Packages(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("UpdatePaymentInfo")
        Call<SuccessModel> UpdatePaymentInfo(@FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("DisplayUserRequest")
        Call<ResponseAcceptedOrRejected> RestoDisplayUserRequestAcceptedOrRejected(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("UserPackageInfo")
        Call<ResultUserPackageInfo> UserPackageInfo(@FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("EditUserRequest")
        Call<SuccessModel> EditUserRequest(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("DeleteUserRequest")
        Call<SuccessModel> DeleteUserRequest(@FieldMap HashMap<String, String> hashMap);
        /////go out//////

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("GetBanners")
        Call<ResultGetBanners> ResultGetBanners(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("nearest_restaurants")
        Call<ResultNearestRestaurants> ResultNearestRestaurants(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("PostUserRating")
        Call<SuccessModel> PostUserRating(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("GetReviews")
        Call<ResultGetReviews> GetReviews(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("DisplayRestoPhoto")
        Call<ResultDisplayRestoPhoto> DisplayRestoPhoto(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("display_active_restaurant_coupon")
        Call<ResultDisplayActiveRestaurantCoupon> ActiveCoupanListModel(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("NewArivals")
        Call<ResultNewArivals> NewArivals(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("GenerateNumberToUseCoupon")
        Call<ResultGenerateNumberToUseCoupon> GenerateNumberToUseCoupon(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("RestoCoupons")
        Call<ResultUserCoupons> RestoCoupons(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("UserRequest")
        Call<SuccessModel> UserRequest(@FieldMap HashMap<String, String> hashMap);


        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("BannerCoupon")
        Call<ResultBannerCoupon>BannerCoupon(@FieldMap HashMap<String, String> hashMap);

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("TrandingPlaces")
        Call<ResultTrandingPlaces> TrandingPlaces(@FieldMap HashMap<String, String> hashMap);

        //////search//////////

        @FormUrlEncoded
        @Headers({"Accept: application/json"})
        @POST("SearchResto")
        Call<ResultSearchResto> SearchResto(@FieldMap HashMap<String, String> hashMap);
    }


}