package com.paymentez.androidsdk.models;

/**
 * Created by mmucito on 31/05/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class PaymentezCard implements Parcelable {

    private String cardReference;
    private String type;
    private String cardHolder;
    private String termination;
    private String expiryMonth;
    private String expiryYear;
    private String bin;
    private String cardNumber;
    private String cvc;
    private String uid;
    private String email;

    public PaymentezCard() {

    }

    public String getCardReference() {
        return cardReference;
    }

    public void setCardReference(String cardReference) {
        this.cardReference = cardReference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected PaymentezCard(Parcel in) {
        cardReference = in.readString();
        type = in.readString();
        cardHolder = in.readString();
        termination = in.readString();
        expiryMonth = in.readString();
        expiryYear = in.readString();
        bin = in.readString();
        cardNumber = in.readString();
        cvc = in.readString();
        uid = in.readString();
        email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardReference);
        dest.writeString(type);
        dest.writeString(cardHolder);
        dest.writeString(termination);
        dest.writeString(expiryMonth);
        dest.writeString(expiryYear);
        dest.writeString(bin);
        dest.writeString(cardNumber);
        dest.writeString(cvc);
        dest.writeString(uid);
        dest.writeString(email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PaymentezCard> CREATOR = new Parcelable.Creator<PaymentezCard>() {
        @Override
        public PaymentezCard createFromParcel(Parcel in) {
            return new PaymentezCard(in);
        }

        @Override
        public PaymentezCard[] newArray(int size) {
            return new PaymentezCard[size];
        }
    };
}