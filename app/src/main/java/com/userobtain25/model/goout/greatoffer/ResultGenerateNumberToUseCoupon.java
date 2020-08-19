package com.userobtain25.model.goout.greatoffer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultGenerateNumberToUseCoupon {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result_GenerateNumberToUseCoupon")
    @Expose
    private ResultGenerateNumberToUseCoupon_ resultGenerateNumberToUseCoupon;

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

    public ResultGenerateNumberToUseCoupon_ getResultGenerateNumberToUseCoupon() {
        return resultGenerateNumberToUseCoupon;
    }

    public void setResultGenerateNumberToUseCoupon(ResultGenerateNumberToUseCoupon_ resultGenerateNumberToUseCoupon) {
        this.resultGenerateNumberToUseCoupon = resultGenerateNumberToUseCoupon;
    }

}
