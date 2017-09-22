package com.paymentez.android.rest;

import com.paymentez.android.rest.model.CreateTokenRequest;
import com.paymentez.android.rest.model.CreateTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mmucito on 13/09/17.
 */

public interface PaymentezService {

    @POST("/v2/pci/create_token")
    Call<CreateTokenResponse> createToken(@Body CreateTokenRequest createTokenRequest);

}
