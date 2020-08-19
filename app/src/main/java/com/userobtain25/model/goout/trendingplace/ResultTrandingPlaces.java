package com.userobtain25.model.goout.trendingplace;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultTrandingPlaces {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_TrandingPlaces")
    @Expose
    private ArrayList<ResultTrandingPlace> resultTrandingPlaces = null;

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

    public ArrayList<ResultTrandingPlace> getResultTrandingPlaces() {
        return resultTrandingPlaces;
    }

    public void setResultTrandingPlaces(ArrayList<ResultTrandingPlace> resultTrandingPlaces) {
        this.resultTrandingPlaces = resultTrandingPlaces;
    }

}
