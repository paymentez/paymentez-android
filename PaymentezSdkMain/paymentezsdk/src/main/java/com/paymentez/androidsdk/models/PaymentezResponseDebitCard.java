package com.paymentez.androidsdk.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by mmucito on 30/05/16.
 */
public class PaymentezResponseDebitCard extends PaymentezResponse{

    private String transactionId = "";
    private String status = "";
    private String statusDetail = "";
    private Date paymentDate;
    private double amount = 0.0;
    private PaymentezCarrierData carrierData;
    private PaymentezCardData cardData;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentezCarrierData getCarrierData() {
        return carrierData;
    }

    public void setCarrierData(PaymentezCarrierData carrierData) {
        this.carrierData = carrierData;
    }

    public PaymentezCardData getCardData() {
        return cardData;
    }

    public void setCardData(PaymentezCardData cardData) {
        this.cardData = cardData;
    }

    public boolean shouldVerify(){
        if(getStatusDetail().equals("1")){
            return true;
        }
        return false;
    }
}
