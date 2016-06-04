Paymentez SDK Android
===================


 Paymentez SDK Android is a library that allows developers to easily connect to the Paymentez CREDITCARDS API

----------

***REMOVED******REMOVED***Installation

***REMOVED******REMOVED******REMOVED***Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    compile 'com.paymentez.paymentezsdk:paymentezsdk:1.0.3'


***REMOVED******REMOVED******REMOVED*** ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the Paymentez bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.paymentez.** { *; }

***REMOVED******REMOVED*** Example
Try out the sample application

https://www.dropbox.com/s/m52m0u807ftw1jc/paymentez-example.apk?dl=0

***REMOVED******REMOVED***Documentation
***REMOVED******REMOVED******REMOVED***Init library
```java
PaymentezSDKClient paymentezsdk;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_cards);

	// @param mContext Context of the Main Activity
	// @param dev_environment false to use production environment
	// @param app_code Application identifier (provided by Paymentez).
	// @param app_secret_key Application Secret key (provided by Paymentez).
	paymentezsdk = new PaymentezSDKClient(this, true, "your_app_code", "your_app_secret_key");

}
```
***REMOVED******REMOVED******REMOVED***Show "Add Card" WebView
First, we'll assume that you're going to launch the scanner from a button, and that you've set the button's onClick handler. Then, add the method as:
```java
callApiAddWebView.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        paymentezsdk.addCardShowWebView(editTextUid.getText().toString(), editTextEmail.getText().toString(), AddCardActivity.this);
    }
});
```
***REMOVED******REMOVED******REMOVED***Show "Scan Card" View
First, we'll assume that you're going to launch the scanner from a button, and that you've set the button's onClick handler. Then, add the method as:
```java
callScanCard.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        paymentezsdk.scanCard();

    }
});
```
Next, we'll override onActivityResult() to get the scan result.
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PaymentezSDKClient.PAYMENTEZ_SCAN_CARD_REQUEST_CODE) {

        if (data != null && data.hasExtra(PaymentezSDKClient.PAYMENTEZ_EXTRA_SCAN_RESULT)) {

            PaymentezCard paymentezCard = data.getParcelableExtra(PaymentezSDKClient.PAYMENTEZ_EXTRA_SCAN_RESULT);

            if(paymentezCard!=null) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                String message = "card: " + paymentezCard.getCardNumber() +
                        "\ncard_holder: " + paymentezCard.getCardHolder();

                builder1.setMessage(message);

                builder1.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);
            builder1.setMessage("Scan was canceled.");

            builder1.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }
}
```
***REMOVED******REMOVED******REMOVED***List Cards
```java
paymentezsdk.listCards(uid, new ListCardsResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponseListCards response) {

        System.out.println("SUCCESS: " + response.isSuccess());
        System.out.println("CODE: " + response.getCode());

        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < response.getCards().size(); i++) {

            PaymentezCard card = response.getCards().get(i);
            values.add("name:" + card.getCardHolder() + "\ncard_reference:" + card.getCardReference());

            System.out.println("CARD INFO");
            System.out.println(card.getCardHolder());
            System.out.println(card.getCardReference());
            System.out.println(card.getExpiryYear());
            System.out.println(card.getTermination());
            System.out.println(card.getExpiryMonth());
            System.out.println(card.getType());
        }
    }


});
```

***REMOVED******REMOVED******REMOVED***Debit Card
```java
paymentezsdk.debitCard(debitParameters, new DebitCardResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponseDebitCard paymentezResponse) {                        
        if(!paymentezResponse.isSuccess()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

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
            if(paymentezResponse.getStatus().equals("failure") && paymentezResponse.shouldVerify()){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

                String message = "You must verify the transaction_id: " + paymentezResponse.getTransactionId();


                builder1.setMessage(message);

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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

                builder1.setMessage("status: " + paymentezResponse.getStatus() +
                        "\nstatus_detail: " + paymentezResponse.getStatusDetail() +
                        "\nshouldVerify: " + paymentezResponse.shouldVerify() +
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

                System.out.println("TRANSACTION card_data");
                System.out.println(paymentezResponse.getCardData().getAccountType());
                System.out.println(paymentezResponse.getCardData().getType());
                System.out.println(paymentezResponse.getCardData().getNumber());
                System.out.println(paymentezResponse.getCardData().getQuotas());

                System.out.println("TRANSACTION carrier_data");
                System.out.println(paymentezResponse.getCarrierData().getAuthorizationCode());
                System.out.println(paymentezResponse.getCarrierData().getAcquirerId());
                System.out.println(paymentezResponse.getCarrierData().getTerminalCode());
                System.out.println(paymentezResponse.getCarrierData().getUniqueCode());
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

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


});
```
***REMOVED******REMOVED******REMOVED***Delete Card
```java
paymentezsdk.deleteCard(uid, cardObject.getCardReference(), new TextHttpResponseHandler() {

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        pd2.dismiss();
        System.out.println("Failure: "+ responseString);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        pd2.dismiss();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);
        builder1.setMessage("Successfully Deleted!");
        builder1.setCancelable(false);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

        getCards();
    }


});
```
***REMOVED******REMOVED******REMOVED***Verify  Transaction
```java
paymentezsdk.verifyWithCode(textTransactionId.getText().toString(), textUid.getText().toString(), textVerificationCode.getText().toString(), new VerifyResponseHandler() {
    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){                            
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
```
***REMOVED******REMOVED******REMOVED***Add Card PCI
```java
PaymentezCard paymentezCard;
paymentezCard.setUid(editTextUid.getText().toString());
paymentezCard.setEmail(editTextEmail.getText().toString());
paymentezCard.setCardHolder("Holder Name");
paymentezCard.setCardNumber("4111111111111111");
paymentezCard.setExpiryMonth("11");
paymentezCard.setExpiryYear("2016");
paymentezCard.setCvc("444");
paymentezCard.setType("vi");

paymentezsdk.addCard(paymentezCard, new PaymentezResponseHandler() {
    @Override
    public void onSuccess(int statusCode, Header[] headers, PaymentezResponse paymentezResponse) {
        if(!paymentezResponse.isSuccess()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

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
            if(paymentezResponse.getStatus().equals("failure") && paymentezResponse.shouldVerify()){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                String message = "You must verify the transaction_id: " + paymentezResponse.getTransactionId();

                Log.i("Deba", message);

                builder1.setMessage(message);

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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

                String message = "status: " + paymentezResponse.getStatus() +
                        "\nshouldVerify: " + paymentezResponse.shouldVerify() +
                        "\ntransaction_id: " + paymentezResponse.getTransactionId();


                builder1.setMessage(message);

                builder1.setCancelable(false);
                builder1.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }



        }

    }


    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
        try {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCardActivity.this);

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



});
```