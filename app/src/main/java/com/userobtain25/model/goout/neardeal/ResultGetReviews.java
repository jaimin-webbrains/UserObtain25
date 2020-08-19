package com.userobtain25.model.goout.neardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultGetReviews {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_GetReviews")
    @Expose
    private ArrayList<ResultGetReview> resultGetReviews = null;

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

    public ArrayList<ResultGetReview> getResultGetReviews() {
        return resultGetReviews;
    }

    public void setResultGetReviews(ArrayList<ResultGetReview> resultGetReviews) {
        this.resultGetReviews = resultGetReviews;
    }

}
