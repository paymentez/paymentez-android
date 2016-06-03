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

import com.paymentez.androidsdk.PaymentezSDKClient;
import com.paymentez.androidsdk.models.PaymentezResponseDebitCard;
import com.paymentez.paymentezexample.utils.Constants;

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
                    new CallApiVerifyWithCodeAsyncTask().execute( textTransactionId.getText().toString(), textUid.getText().toString(), textVerificationCode.getText().toString());
                }

            }
        });
    }


    private class CallApiVerifyWithCodeAsyncTask extends AsyncTask<String, Void, PaymentezResponseDebitCard> {

        ProgressDialog pd;

        @Override
        protected PaymentezResponseDebitCard doInBackground(String... params) {


            String transactionId = params[0];
            String uid = params[1];
            String verification_code = params[2];




            return paymentezsdk.verifyWithCode(transactionId, uid, verification_code);
        }
        protected void onPreExecute(){
            super.onPreExecute();
            pd = new ProgressDialog(VerifyTransactionActivity.this);
            pd.setMessage("");
            pd.show();
        }


        protected void onPostExecute(PaymentezResponseDebitCard paymentezResponse) {
            super.onPostExecute(paymentezResponse);
            if ((pd != null) && pd.isShowing()) {
                try {
                    pd.dismiss();
                }catch (Exception e){}
                pd = null;
            }

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

                builder1.setMessage("status: " + paymentezResponse.getStatus() +
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

    }
}
