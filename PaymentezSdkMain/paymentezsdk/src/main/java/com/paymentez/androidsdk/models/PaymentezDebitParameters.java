package com.paymentez.androidsdk.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mmucito on 12/05/16.
 */
public class PaymentezDebitParameters {

    private String uid = "";
    private String cardReference = "";
    private double productAmount = 0.0;
    private String productDescription = "";
    private String devReference = "";
    private double vat = 0.0;
    private String email = "";
    private double productDiscount = 0.0;
    private int installments = 1;
    private String buyerFiscalNumber = "";
    private String sellerId = "";
    private PaymentezShipping shipping = null;



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCardReference() {
        return cardReference;
    }

    public void setCardReference(String cardReference) {
        this.cardReference = cardReference;
    }

    public String getProductAmount() {

        return String.format( "%.2f", productAmount );
    }

    public void setProductAmount(double productAmount) {
        this.productAmount = productAmount;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getDevReference() {
        return devReference;
    }

    public void setDevReference(String devReference) {
        this.devReference = devReference;
    }

    public String getVat() {
        return String.format( "%.2f", vat );
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductDiscount() {
        return String.format( "%.2f", productDiscount );
    }

    public void setProductDiscount(double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public String getBuyerFiscalNumber() {
        return buyerFiscalNumber;
    }

    public void setBuyerFiscalNumber(String buyerFiscalNumber) {
        this.buyerFiscalNumber = buyerFiscalNumber;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public PaymentezShipping getShipping() {
        return shipping;
    }

    public void setShipping(PaymentezShipping shipping) {
        this.shipping = shipping;
    }


    public Map<String,String> toHashMap() {
        Map<String,String> paramsPost = new HashMap<>();


        paramsPost.put("uid", getUid());
        paramsPost.put("email", getEmail());
        paramsPost.put("card_reference", getCardReference());
        paramsPost.put("product_amount", getProductAmount());
        paramsPost.put("product_description", getProductDescription());
        paramsPost.put("dev_reference", getDevReference());



        if (Double.parseDouble(getVat()) > 0.0){
            paramsPost.put("vat", getVat());

        }


        if (getInstallments() > 1){
            paramsPost.put("installments", ""+getInstallments());
        }

        if (Double.parseDouble(getProductDiscount()) > 0.0){
            paramsPost.put("product_discount", getProductDiscount());

        }

        if(!getBuyerFiscalNumber().equals("")){
            paramsPost.put("buyer_fiscal_number", getBuyerFiscalNumber());
        }


        if(getShipping()!=null){
            paramsPost.putAll(getShipping().toHashMap());
        }



        return paramsPost;
    }
}
