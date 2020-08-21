package com.userobtain25.model.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultUserPackageInfo_ {
    @SerializedName("package_name")
    @Expose
    private String packageName;
    @SerializedName("package_starts")
    @Expose
    private String packageStarts;
    @SerializedName("package_ends")
    @Expose
    private String packageEnds;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageStarts() {
        return packageStarts;
    }

    public void setPackageStarts(String packageStarts) {
        this.packageStarts = packageStarts;
    }

    public String getPackageEnds() {
        return packageEnds;
    }

    public void setPackageEnds(String packageEnds) {
        this.packageEnds = packageEnds;
    }
}
