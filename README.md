Paymentez SDK Android
===================


 Paymentez SDK Android is a library that allows developers to easily connect to the Paymentez CREDITCARDS API

----------

##Installation

###Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    compile('com.paymentez.paymentezsdk:paymentezsdk:1.0.0') { transitive = true }


### ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the Paymentez bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.paymentez.** { *; }

## Example
Try out the sample application

https://www.dropbox.com/s/m52m0u807ftw1jc/paymentez-example.apk?dl=0

##Documentation
###init library
```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cards);

		// @param mContext Context of the Main Activity
		// @param dev_environment false to use production environment
		// @param app_code Application identifier (provided by Paymentez).
		// @param app_secret_key Application Secret key (provided by Paymentez).
		PaymentezSDKClient paymentezsdk = new PaymentezSDKClient(this, true, "your_app_code", "your_app_secret_key");

	}
```

###List Cards
```java
callApiListCards.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v) {
        uid = editTextUid.getText().toString();
        email = editTextEmail.getText().toString();


        if(uid.equals("")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);
            builder1.setMessage("uid is required");

            builder1.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }else {
            new CallApiListCardAsyncTask().execute(uid);
        }

    }
});


private class CallApiListCardAsyncTask extends AsyncTask<String, Void, PaymentezResponseListCards> {
    ProgressDialog pd;
    @Override
    protected PaymentezResponseListCards doInBackground(String... params) {
        String uid = params[0];
        return paymentezsdk.listCards(uid);
    }

    protected void onPreExecute(){
        super.onPreExecute();
        pd = new ProgressDialog(ListCardsActivity.this);
        pd.setMessage("");
        pd.show();
    }


    protected void onPostExecute(PaymentezResponseListCards responseListCards) {
        super.onPostExecute(responseListCards);
        if ((pd != null) && pd.isShowing()) {
            try {
                pd.dismiss();
            }catch (Exception e){}
            pd = null;
        }

        if(!responseListCards.isSuccess()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ListCardsActivity.this);

            builder1.setMessage("Error: " + responseListCards.getErrorMessage());

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

            System.out.println("SUCCESS: " + responseListCards.isSuccess());
            System.out.println("CODE: " + responseListCards.getCode());

            listCard = responseListCards.getCards();
            ArrayList<String> values = new ArrayList<>();

            for (int i = 0; i < responseListCards.getCards().size(); i++) {

                PaymentezCard card = responseListCards.getCards().get(i);
                values.add("name:" + card.getCardHolder() + "\ncard_reference:" + card.getCardReference());

                System.out.println("CARD INFO");
                System.out.println(card.getCardHolder());
                System.out.println(card.getCardReference());
                System.out.println(card.getExpiryYear());
                System.out.println(card.getTermination());
                System.out.println(card.getExpiryMonth());
                System.out.println(card.getType());

            }

            listAdapter = new ArrayAdapter<String>(ListCardsActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);
            listView.setAdapter(listAdapter);
        }

    }

}

```

