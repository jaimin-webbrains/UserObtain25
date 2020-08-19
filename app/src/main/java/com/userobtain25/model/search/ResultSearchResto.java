package com.userobtain25.model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultSearchResto {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_SearchResto")
    @Expose
    private ArrayList<ResultSearchResto_> resultSearchResto = null;

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

    public ArrayList<ResultSearchResto_> getResultSearchResto() {
        return resultSearchResto;
    }

    public void setResultSearchResto(ArrayList<ResultSearchResto_> resultSearchResto) {
        this.resultSearchResto = resultSearchResto;
    }
}
