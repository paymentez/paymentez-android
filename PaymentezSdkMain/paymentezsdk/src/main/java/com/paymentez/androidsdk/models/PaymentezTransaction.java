package com.paymentez.androidsdk.models;

import java.util.Date;

/**
 * Created by mmucito on 12/05/16.
 */
public class PaymentezTransaction {

    private double amount;
    private Date paymentDate;
    private int status;
    private int statusDetail;
    private String transactionId;
    private String carrierData;
/*

    static func parseTransaction(data:AnyObject?) ->PaymentezTransaction
    {
        _ = data as! [String:AnyObject]
        let trx = PaymentezTransaction()
        trx.amount = data!["amount"] as? Double
        trx.status = data!["status"] as? Int
        trx.statusDetail = data!["status_detail"] as? Int
        trx.transactionId = data!["transaction_id"] as? String

        trx.carrierData = data!["carrier_data"] as? [String:AnyObject]

        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"

        trx.paymentDate = dateFormatter.dateFromString(data!["payment_date"] as! String)
        return trx

    }

    */


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(int statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCarrierData() {
        return carrierData;
    }

    public void setCarrierData(String carrierData) {
        this.carrierData = carrierData;
    }
}
