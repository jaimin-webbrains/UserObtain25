package com.userobtain25.model.goout.newarrival;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultNewArivals {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_NewArivals")
    @Expose
    private ArrayList<ResultNewArival> resultNewArivals = null;

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

    public ArrayList<ResultNewArival> getResultNewArivals() {
        return resultNewArivals;
    }

    public void setResultNewArivals(ArrayList<ResultNewArival> resultNewArivals) {
        this.resultNewArivals = resultNewArivals;
    }
}
