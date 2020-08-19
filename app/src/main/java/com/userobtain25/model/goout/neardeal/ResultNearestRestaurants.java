package com.userobtain25.model.goout.neardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultNearestRestaurants {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_nearest_restaurants")
    @Expose
    private ArrayList<ResultNearestRestaurant> resultNearestRestaurants = null;

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

    public ArrayList<ResultNearestRestaurant> getResultNearestRestaurants() {
        return resultNearestRestaurants;
    }

    public void setResultNearestRestaurants(ArrayList<ResultNearestRestaurant> resultNearestRestaurants) {
        this.resultNearestRestaurants = resultNearestRestaurants;
    }

}
