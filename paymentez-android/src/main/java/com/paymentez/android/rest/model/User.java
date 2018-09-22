
package com.paymentez.android.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("ip_address")
    @Expose
    private String ipAddress;

    @SerializedName("fiscal_number")
    @Expose
    private String fiscal_number;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getFiscal_number() {
        return fiscal_number;
    }

    public void setFiscal_number(String fiscal_number) {
        this.fiscal_number = fiscal_number;
    }
}
