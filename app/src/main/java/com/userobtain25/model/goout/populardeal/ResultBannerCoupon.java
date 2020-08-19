package com.userobtain25.model.goout.populardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultBannerCoupon {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_BannerCoupon")
    @Expose
    private ArrayList<ResultBannerCoupon_> resultBannerCoupon = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ResultBannerCoupon_> getResultBannerCoupon() {
        return resultBannerCoupon;
    }

    public void setResultBannerCoupon(ArrayList<ResultBannerCoupon_> resultBannerCoupon) {
        this.resultBannerCoupon = resultBannerCoupon;
    }

}
