package com.userobtain25.model.goout.populardeal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultBannerCoupon_ {@SerializedName("id")
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
    @SerializedName("resto_id")
    @Expose
    private String restoId;
    @SerializedName("resto_name")
    @Expose
    private String restoName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("resto_photo")
    @Expose
    private Object restoPhoto;

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

    public String getRestoId() {
        return restoId;
    }

    public void setRestoId(String restoId) {
        this.restoId = restoId;
    }

    public String getRestoName() {
        return restoName;
    }

    public void setRestoName(String restoName) {
        this.restoName = restoName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Object getRestoPhoto() {
        return restoPhoto;
    }

    public void setRestoPhoto(Object restoPhoto) {
        this.restoPhoto = restoPhoto;
    }

}
