package com.paymentez.androidsdk.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mmucito on 24/05/16.
 */
public class PaymentezShipping {

    private String seller_id = "";
    private String shipping_street = "";
    private String shipping_house_number = "";
    private String shipping_city = "";
    private String shipping_zip = "";
    private String shipping_state = "";
    private String shipping_country = "";
    private String shipping_district = "";
    private String shipping_additional_address_info = "";

    public PaymentezShipping(){

    }

    public PaymentezShipping(String shipping_street, String shipping_house_number,
                             String shipping_city, String shipping_zip, String shipping_state,
                             String shipping_country, String shipping_district,
                             String shipping_additional_address_info) {
        super();
        this.shipping_street = shipping_street;
        this.shipping_house_number = shipping_house_number;
        this.shipping_city = shipping_city;
        this.shipping_zip = shipping_zip;
        this.shipping_state = shipping_state;
        this.shipping_country = shipping_country;
        this.shipping_district = shipping_district;
        this.shipping_additional_address_info = shipping_additional_address_info;
    }

    /**
     * @return the shipping_street
     */
    public String getShipping_street() {
        return shipping_street;
    }
    /**
     * @param shipping_street the shipping_street to set
     */
    public void setShipping_street(String shipping_street) {
        this.shipping_street = shipping_street;
    }
    /**
     * @return the shipping_house_number
     */
    public String getShipping_house_number() {
        return shipping_house_number;
    }
    /**
     * @param shipping_house_number the shipping_house_number to set
     */
    public void setShipping_house_number(String shipping_house_number) {
        this.shipping_house_number = shipping_house_number;
    }
    /**
     * @return the shipping_city
     */
    public String getShipping_city() {
        return shipping_city;
    }
    /**
     * @param shipping_city the shipping_city to set
     */
    public void setShipping_city(String shipping_city) {
        this.shipping_city = shipping_city;
    }
    /**
     * @return the shipping_zip
     */
    public String getShipping_zip() {
        return shipping_zip;
    }
    /**
     * @param shipping_zip the shipping_zip to set
     */
    public void setShipping_zip(String shipping_zip) {
        this.shipping_zip = shipping_zip;
    }
    /**
     * @return the shipping_state
     */
    public String getShipping_state() {
        return shipping_state;
    }
    /**
     * @param shipping_state the shipping_state to set
     */
    public void setShipping_state(String shipping_state) {
        this.shipping_state = shipping_state;
    }
    /**
     * @return the shipping_country
     */
    public String getShipping_country() {
        return shipping_country;
    }
    /**
     * @param shipping_country the shipping_country to set
     */
    public void setShipping_country(String shipping_country) {
        this.shipping_country = shipping_country;
    }
    /**
     * @return the shipping_district
     */
    public String getShipping_district() {
        return shipping_district;
    }
    /**
     * @param shipping_district the shipping_district to set
     */
    public void setShipping_district(String shipping_district) {
        this.shipping_district = shipping_district;
    }
    /**
     * @return the shipping_additional_address_info
     */
    public String getShipping_additional_address_info() {
        return shipping_additional_address_info;
    }
    /**
     * @param shipping_additional_address_info the shipping_additional_address_info to set
     */
    public void setShipping_additional_address_info(
            String shipping_additional_address_info) {
        this.shipping_additional_address_info = shipping_additional_address_info;
    }

    /* (non-Javadoc)
     * @see java.lang.Object***REMOVED***toString()
     */
    @Override
    public String toString() {
        return "PaymentezShipping [shipping_street=" + shipping_street
                + ", shipping_house_number=" + shipping_house_number
                + ", shipping_city=" + shipping_city + ", shipping_zip="
                + shipping_zip + ", shipping_state=" + shipping_state
                + ", shipping_country=" + shipping_country
                + ", shipping_district=" + shipping_district
                + ", shipping_additional_address_info="
                + shipping_additional_address_info + "]";
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public Map<String,String> toHashMap() {
        Map<String,String> paramsPost = new HashMap<>();

        if(!seller_id.equals(""))
            paramsPost.put("seller_id", seller_id);
        if(!shipping_street.equals(""))
            paramsPost.put("shipping_street", shipping_street);
        if(!shipping_house_number.equals(""))
            paramsPost.put("shipping_house_number", shipping_house_number);
        if(!shipping_zip.equals(""))
            paramsPost.put("shipping_zip", shipping_zip);
        if(!shipping_city.equals(""))
            paramsPost.put("shipping_city", shipping_city);
        if(!shipping_state.equals(""))
            paramsPost.put("shipping_state", shipping_state);
        if(!shipping_country.equals(""))
            paramsPost.put("shipping_country", shipping_country);
        if(!shipping_district.equals(""))
            paramsPost.put("shipping_district", shipping_district);
        if(!shipping_additional_address_info.equals(""))
            paramsPost.put("shipping_additional_address_info", shipping_additional_address_info);

        return paramsPost;
    }

}
