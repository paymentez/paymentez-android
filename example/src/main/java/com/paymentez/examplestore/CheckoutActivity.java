package com.paymentez.examplestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paymentez.android.Paymentez;
import com.paymentez.android.rest.model.ErrorResponse;
import com.paymentez.examplestore.rest.BackendService;
import com.paymentez.examplestore.rest.RetrofitFactory;
import com.paymentez.examplestore.rest.model.CreateChargeResponse;
import com.paymentez.examplestore.utils.Alert;
import com.paymentez.examplestore.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paymentez.examplestore.utils.MyCardAdapter.TEMPLATE_RESOURCE_MAP;

public class CheckoutActivity extends AppCompatActivity {

    LinearLayout buttonSelectPayment;
    ImageView imageViewCCImage;
    TextView textViewCCLastFour;
    Button buttonPlaceOrder;
    Context mContext;
    String CARD_TOKEN = "";
    int SELECT_CARD_REQUEST = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mContext = this;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        final BackendService backendService = RetrofitFactory.getClient().create(BackendService.class);


        imageViewCCImage = (ImageView) findViewById(R.id.imageViewCCImage);
        textViewCCLastFour = (TextView) findViewById(R.id.textViewCCLastFour);

        buttonPlaceOrder = (Button)findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(CARD_TOKEN == null || CARD_TOKEN.equals("")){
                    Alert.show(mContext,
                            "Error",
                            "You Need to Select a Credit Card!");
                }else{

                    final ProgressDialog pd = new ProgressDialog(mContext);
                    pd.setMessage("");
                    pd.show();

                    double ORDER_AMOUNT = 10.5;
                    String ORDER_ID = ""+System.currentTimeMillis();
                    String ORDER_DESCRIPTION = "ORDER ***REMOVED***" + ORDER_ID;
                    String DEV_REFERENCE = ORDER_ID;

                    backendService.createCharge(Constants.USER_ID, Paymentez.getSessionId(mContext),
                            CARD_TOKEN, ORDER_AMOUNT, DEV_REFERENCE, ORDER_DESCRIPTION).enqueue(new Callback<CreateChargeResponse>() {
                        @Override
                        public void onResponse(Call<CreateChargeResponse> call, Response<CreateChargeResponse> response) {
                            pd.dismiss();
                            CreateChargeResponse createChargeResponse = response.body();
                            if(response.isSuccessful() && createChargeResponse != null && createChargeResponse.getTransaction() != null) {
                                Alert.show(mContext,
                                        "Successful Charge",
                                        "status: " + createChargeResponse.getTransaction().getStatus() +
                                                "\nstatus_detail: " + createChargeResponse.getTransaction().getStatusDetail() +
                                                "\nmessage: " + createChargeResponse.getTransaction().getMessage() +
                                                "\ntransaction_id:" + createChargeResponse.getTransaction().getId());
                            }else {
                                Gson gson = new GsonBuilder().create();
                                try {
                                    ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                                    Alert.show(mContext,
                                            "Error",
                                            errorResponse.getError().getType());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CreateChargeResponse> call, Throwable e) {
                            pd.dismiss();
                            Alert.show(mContext,
                                    "Error",
                                    e.getLocalizedMessage());
                        }
                    });
                }
            }
        });

        buttonSelectPayment = (LinearLayout)findViewById(R.id.buttonSelectPayment);
        buttonSelectPayment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ListCardsActivity.class);
                startActivityForResult(intent, SELECT_CARD_REQUEST);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SELECT_CARD_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                CARD_TOKEN = data.getStringExtra("CARD_TOKEN");
                String CARD_TYPE = data.getStringExtra("CARD_TYPE");
                String CARD_LAST4 = data.getStringExtra("CARD_LAST4");

                if(CARD_LAST4 != null && !CARD_LAST4.equals("")){
                    textViewCCLastFour.setText("XXXX." + CARD_LAST4);

                    @DrawableRes int iconResourceId = TEMPLATE_RESOURCE_MAP.get(CARD_TYPE);
                    imageViewCCImage.setImageResource(iconResourceId);
                }

            }
        }
    }
}
