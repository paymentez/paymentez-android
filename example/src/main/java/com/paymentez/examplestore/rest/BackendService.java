package com.paymentez.examplestore.rest;

import com.paymentez.examplestore.rest.model.CreateChargeResponse;
import com.paymentez.examplestore.rest.model.DeleteCardResponse;
import com.paymentez.examplestore.rest.model.GetCardsResponse;
import com.paymentez.examplestore.rest.model.VerifyResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by mmucito on 13/09/17.
 */

public interface BackendService {

    @GET("/get-cards")
    Call<GetCardsResponse> getCards(@Query("uid") String uid);

    @FormUrlEncoded
    @POST("/create-charge")
    Call<CreateChargeResponse> createCharge(@Field("uid") String uid, @Field("session_id") String session_id,
                                            @Field("token") String token, @Field("amount") double amount,
                                            @Field("dev_reference") String dev_reference, @Field("description") String description);

    @FormUrlEncoded
    @POST("/delete-card")
    Call<DeleteCardResponse> deleteCard(@Field("uid") String uid, @Field("token") String token);

    @FormUrlEncoded
    @POST("/verify-transaction")
    Call<VerifyResponse> verifyTransaction(@Field("uid") String uid, @Field("transaction_id") String transaction_id,
                                           @Field("type") String type, @Field("value") String value);
}
