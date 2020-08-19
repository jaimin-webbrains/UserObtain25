package com.userobtain25.model.goout.neardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultNearestRestaurant {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("resto_photo")
    @Expose
    private Object restoPhoto;
    @SerializedName("NewRating")
    @Expose
    private Object newRating;
    @SerializedName("distance")
    @Expose
    private String distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Object getRestoPhoto() {
        return restoPhoto;
    }

    public void setRestoPhoto(Object restoPhoto) {
        this.restoPhoto = restoPhoto;
    }

    public Object getNewRating() {
        return newRating;
    }

    public void setNewRating(Object newRating) {
        this.newRating = newRating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
