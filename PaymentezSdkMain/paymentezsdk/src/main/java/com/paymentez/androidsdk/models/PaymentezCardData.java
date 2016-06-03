package com.paymentez.androidsdk.models;

/**
 * Created by mmucito on 31/05/16.
 */
public class PaymentezCardData {
    private String accountType;
    private String type;
    private String number;
    private String quotas;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQuotas() {
        return quotas;
    }

    public void setQuotas(String quotas) {
        this.quotas = quotas;
    }
}
