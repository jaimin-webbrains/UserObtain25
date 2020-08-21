package com.userobtain25.model.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultUserPackageInfo {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_UserPackageInfo")
    @Expose
    private ArrayList<ResultUserPackageInfo_> resultUserPackageInfo = null;

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

    public ArrayList<ResultUserPackageInfo_> getResultUserPackageInfo() {
        return resultUserPackageInfo;
    }

    public void setResultUserPackageInfo(ArrayList<ResultUserPackageInfo_> resultUserPackageInfo) {
        this.resultUserPackageInfo = resultUserPackageInfo;
    }
}
