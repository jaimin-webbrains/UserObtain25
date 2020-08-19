package com.userobtain25.model.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultGreatOffers {


        @SerializedName("error")
        @Expose
        private Boolean error;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("result_GreatOffers")
        @Expose
        private ArrayList<ResultGreatOffer> resultGreatOffers = null;

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

        public ArrayList<ResultGreatOffer> getResultGreatOffers() {
            return resultGreatOffers;
        }

        public void setResultGreatOffers(ArrayList<ResultGreatOffer> resultGreatOffers) {
            this.resultGreatOffers = resultGreatOffers;
        }
}
