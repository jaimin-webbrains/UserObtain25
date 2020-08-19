package com.userobtain25.model.goout.greatoffer;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultUserCoupons {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_UserCoupons")
    @Expose
    private ArrayList<ResultUserCoupon> resultUserCoupons = null;

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

    public ArrayList<ResultUserCoupon> getResultUserCoupons() {
        return resultUserCoupons;
    }

    public void setResultUserCoupons(ArrayList<ResultUserCoupon> resultUserCoupons) {
        this.resultUserCoupons = resultUserCoupons;
    }
}