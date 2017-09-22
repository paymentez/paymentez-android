
package com.paymentez.examplestore.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("payment_date")
    @Expose
    private String paymentDate;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("authorization_code")
    @Expose
    private String authorizationCode;
    @SerializedName("installments")
    @Expose
    private Integer installments;
    @SerializedName("dev_reference")
    @Expose
    private String devReference;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("status_detail")
    @Expose
    private Integer statusDetail;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getDevReference() {
        return devReference;
    }

    public void setDevReference(String devReference) {
        this.devReference = devReference;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(Integer statusDetail) {
        this.statusDetail = statusDetail;
    }

}
