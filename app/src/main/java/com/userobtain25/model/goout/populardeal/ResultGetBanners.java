package com.userobtain25.model.goout.populardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultGetBanners {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_GetBanners")
    @Expose
    private ArrayList<ResultGetBanner> resultGetBanners = null;

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

    public ArrayList<ResultGetBanner> getResultGetBanners() {
        return resultGetBanners;
    }

    public void setResultGetBanners(ArrayList<ResultGetBanner> resultGetBanners) {
        this.resultGetBanners = resultGetBanners;
    }

}
