package com.userobtain25.model.goout.neardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultDisplayActiveRestaurantCoupon_ {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;
    @SerializedName("restaurant_id")
    @Expose
    private String restaurantId;
    @SerializedName("discount_value")
    @Expose
    private String discountValue;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("minimum_amount")
    @Expose
    private String minimumAmount;
    @SerializedName("maximum_amount")
    @Expose
    private String maximum_amount;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("action_taken_by_admin")
    @Expose
    private String actionTakenByAdmin;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("resto_photo")
    @Expose
    private Object restoPhoto;
    @SerializedName("counter")
    @Expose
    private Integer counter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getMaximum_amount() {
        return maximum_amount;
    }

    public void setMaximum_amount(String maximum_amount) {
        this.maximum_amount = maximum_amount;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getActionTakenByAdmin() {
        return actionTakenByAdmin;
    }

    public void setActionTakenByAdmin(String actionTakenByAdmin) {
        this.actionTakenByAdmin = actionTakenByAdmin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getRestoPhoto() {
        return restoPhoto;
    }

    public void setRestoPhoto(Object restoPhoto) {
        this.restoPhoto = restoPhoto;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
