package com.paymentez.paymentezexample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.TextHttpResponseHandler;
import com.paymentez.androidsdk.PaymentezSDKClient;
import com.paymentez.androidsdk.models.PaymentezResponseDebitCard;
import com.paymentez.androidsdk.models.VerifyResponseHandler;
import com.paymentez.paymentezexample.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VerifyTransactionActivity extends AppCompatActivity {

    EditText textTransactionId;
    EditText textUid;
    EditText textVerificationCode;
    Button callApi;
    PaymentezSDKClient paymentezsdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_transaction);


        paymentezsdk = new PaymentezSDKClient(this, true, Constants.app_code, Constants.app_secret_key);




        textTransactionId = (EditText) findViewById(R.id.textTransactionId);
        textUid = (EditText) findViewById(R.id.textUid);
        textVerificationCode = (EditText) findViewById(R.id.textVerificationCode);

        callApi = (Button) findViewById(R.id.callApi);
        callApi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                if(textTransactionId.getText().toString().equals("") ||
                        textUid.getText().toString().equals("") ||
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

                    paymentezsdk.verifyWithCode(textTransactionId.getText().toString(), textUid.getText().toString(), textVerificationCode.getText().toString(), new VerifyResponseHandler() {



                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
                            pd2.dismiss();
                            try {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyTransactionActivity.this);

                                builder1.setMessage(jsonObject.toString(4));

                                builder1.setCancelable(false);
                                builder1.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, PaymentezResponseDebitCard paymentezResponse) {
                            pd2.dismiss();

                            if(!paymentezResponse.isSuccess()){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyTransactionActivity.this);

                                builder1.setMessage("Error: " + paymentezResponse.getErrorMessage());

                                builder1.setCancelable(false);
                                builder1.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                            }else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyTransactionActivity.this);

                                builder1.setMessage("Successfully Verified!" +
                                        "\nstatus: " + paymentezResponse.getStatus() +
                                        "\nstatus_detail: " + paymentezResponse.getStatusDetail() +
                                        "\ntransaction_id:" + paymentezResponse.getTransactionId());

                                builder1.setCancelable(false);
                                builder1.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();


                                System.out.println("TRANSACTION INFO");
                                System.out.println(paymentezResponse.getStatus());
                                System.out.println(paymentezResponse.getPaymentDate());
                                System.out.println(paymentezResponse.getAmount());
                                System.out.println(paymentezResponse.getTransactionId());
                                System.out.println(paymentezResponse.getStatusDetail());


                            }
                        }


                    });


                }

            }
        });
    }


}
