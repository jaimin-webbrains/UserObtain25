package com.userobtain25.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultFrontLogin {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("user_photo")
    @Expose
    private Object userPhoto;
    @SerializedName("turms_and_condition")
    @Expose
    private Object turmsAndCondition;
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
    @SerializedName("paid")
    @Expose
    private Object paid;
    @SerializedName("package_id")
    @Expose
    private Object packageId;
    @SerializedName("order_id")
    @Expose
    private Object orderId;
    @SerializedName("paid_date")
    @Expose
    private Object paidDate;
    @SerializedName("user_tocken")
    @Expose
    private String userTocken;
    @SerializedName("login_time")
    @Expose
    private Integer loginTime;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Object userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Object getTurmsAndCondition() {
        return turmsAndCondition;
    }

    public void setTurmsAndCondition(Object turmsAndCondition) {
        this.turmsAndCondition = turmsAndCondition;
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

    public Object getPaid() {
        return paid;
    }

    public void setPaid(Object paid) {
        this.paid = paid;
    }

    public Object getPackageId() {
        return packageId;
    }

    public void setPackageId(Object packageId) {
        this.packageId = packageId;
    }

    public Object getOrderId() {
        return orderId;
    }

    public void setOrderId(Object orderId) {
        this.orderId = orderId;
    }

    public Object getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Object paidDate) {
        this.paidDate = paidDate;
    }

    public String getUserTocken() {
        return userTocken;
    }

    public void setUserTocken(String userTocken) {
        this.userTocken = userTocken;
    }

    public Integer getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Integer loginTime) {
        this.loginTime = loginTime;
    }
}