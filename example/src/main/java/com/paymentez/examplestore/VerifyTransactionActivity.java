package com.paymentez.examplestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paymentez.android.Paymentez;
import com.paymentez.android.rest.model.ErrorResponse;
import com.paymentez.examplestore.rest.BackendService;
import com.paymentez.examplestore.rest.RetrofitFactory;
import com.paymentez.examplestore.rest.model.DeleteCardResponse;
import com.paymentez.examplestore.rest.model.VerifyResponse;
import com.paymentez.examplestore.utils.Alert;
import com.paymentez.examplestore.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerifyTransactionActivity extends AppCompatActivity {

    EditText textTransactionId;
    EditText textVerificationCode;
    Button callApi;
    RadioButton radioCode;
    RadioButton radioAmount;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_transaction);
        mContext = this;

        final BackendService backendService = RetrofitFactory.getClient().create(BackendService.class);



        radioCode = (RadioButton) findViewById(R.id.radioCode);
        radioAmount = (RadioButton) findViewById(R.id.radioAmount);

        textTransactionId = (EditText) findViewById(R.id.textTransactionId);
        textVerificationCode = (EditText) findViewById(R.id.textVerificationCode);

        callApi = (Button) findViewById(R.id.callApi);
        callApi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                if(textTransactionId.getText().toString().equals("") ||
                        textVerificationCode.getText().toString().equals("")
                        ){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyTransactionActivity.this);
                    builder1.setMessage("all fields are required");

                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {

                    final ProgressDialog pd2 = new ProgressDialog(VerifyTransactionActivity.this);
                    pd2.setMessage("");
                    pd2.show();

                    String TYPE = "";
                    if(radioCode.isChecked()) {
                        TYPE = "BY_AUTH_CODE";
                    }else{
                        TYPE = "BY_AMOUNT";
                    }

                    backendService.verifyTransaction(Constants.USER_ID, textTransactionId.getText().toString(),
                            TYPE, textVerificationCode.getText().toString()).enqueue(new Callback<VerifyResponse>() {
                        @Override
                        public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                            pd2.dismiss();
                            VerifyResponse verifyResponse = response.body();
                            if(response.isSuccessful()) {
                                Alert.show(mContext,
                                        "Successfully Verified Transaction",
                                        "status: " + verifyResponse.getStatus());
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
                        public void onFailure(Call<VerifyResponse> call, Throwable e) {
                            pd2.dismiss();
                            Alert.show(mContext,
                                    "Error",
                                    e.getLocalizedMessage());
                        }
                    });





                }

            }
        });
    }


}
