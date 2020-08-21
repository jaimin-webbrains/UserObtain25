package com.userobtain25.model.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultPackages {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_Packages")
    @Expose
    private ArrayList<ResultPackage> resultPackages = null;

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

    public ArrayList<ResultPackage> getResultPackages() {
        return resultPackages;
    }

    public void setResultPackages(ArrayList<ResultPackage> resultPackages) {
        this.resultPackages = resultPackages;
    }
}
