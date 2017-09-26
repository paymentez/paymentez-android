Paymentez Android SDK
===================

 Paymentez Android SDK is a library that allows developers to easily connect to the Paymentez CREDITCARDS API

----------

## Installation

### Android Studio (or Gradle)

Add this line to your app's `build.gradle` inside the `dependencies` section:

    compile 'com.paymentez:paymentez-android:1.0.1'

### ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the Paymentez bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.paymentez.android.** { *; }

## Usage

### Using the CardMultilineWidget

You can add a widget to your apps that easily handles the UI states for collecting card data.

First, add the CardMultilineWidget to your layout.

```xml
<com.paymentez.android.view.CardMultilineWidget
        android:id="@+id/card_multiline_widget"
        android:layout_alignParentTop="true"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

You can customize the view with this tags:

```xml
app:shouldShowPostalCode="true"
app:shouldShowPaymentezLogo="true"
app:shouldShowCardHolderName="true"
app:shouldShowScanCard="true"
```

In order to use any of this tags, you'll need to enable the app XML namespace somewhere in the layout.

```xml
xmlns:app="http://schemas.android.com/apk/res-auto"
```

To get a `Card` object from the `CardMultilineWidget`, you ask the widget for its card.

```java
Card cardToSave = cardWidget.getCard();
if (cardToSave == null) {
    Alert.show(mContext,
        "Error",
        "Invalid Card Data");
    return;
}
```

If the returned `Card` is null, error states will show on the fields that need to be fixed. 

Once you have a non-null `Card` object from either widget, you can call [addCard](#addCard).

### Init library
You should initialize the library on your Application or in your first Activity. 

```java
/**
 * Init library
 *
 * @param test_mode false to use production environment
 * @param paymentez_client_app_code provided by Paymentez.
 * @param paymentez_client_app_key provided by Paymentez.
 */
Paymentez.setEnvironment(Constants.PAYMENTEZ_IS_TEST_MODE, Constants.PAYMENTEZ_CLIENT_APP_CODE, Constants.PAYMENTEZ_CLIENT_APP_KEY);


 // In case you have your own Fraud Risk Merchant Id
 //Paymentez.setRiskMerchantId(1000);
 // Note: for most of the devs, that's not necessary.
```

### addCard

addCard converts sensitive card data to a single-use token which you can safely pass to your server to charge the user. 

```java
paymentez.addCard(mContext, uid, email, cardToSave, new TokenCallback() {

    public void onSuccess(Card card) {
        
        if(card!=null){
            if(card.getStatus().equals("valid")){
                Alert.show(mContext,
                        "Card Successfully Added",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            }else if (card.getStatus().equals("review")) {
                Alert.show(mContext,
                        "Card Under Review",
                        "status: " + card.getStatus() + "\n" +
                                "Card Token: " + card.getToken() + "\n" +
                                "transaction_reference: " + card.getTransactionReference());

            } else {
                Alert.show(mContext,
                        "Error",
                        "status: " + card.getStatus() + "\n" +
                                "message: " + card.getMessage());
            }


        }

        //TODO: Create charge or Save Token to your backend
    }

    public void onError(PaymentezError error) {        
        Alert.show(mContext,
                "Error",
                "Type: " + error.getType() + "\n" +
                        "Help: " + error.getHelp() + "\n" +
                        "Description: " + error.getDescription());

        //TODO: Handle error
    }

});
```

The first argument to addCard is mContext (Context).
+ mContext. Context of the Current Activity

The second argument to addCard is uid (String).
+ uid Customer identifier. This is the identifier you use inside your application; you will receive it in notifications.

The third argument to addCard is email (String).
+ email Email of the customer

The fourth argument to addCard is a Card object. A Card contains the following fields:

+ number: card number as a string without any separators, e.g. '4242424242424242'.
+ holderName: cardholder name.
+ expMonth: integer representing the card's expiration month, e.g. 12.
+ expYear: integer representing the card's expiration year, e.g. 2013.
+ cvc: card security code as a string, e.g. '123'.
+ type: 

The fifth argument tokenCallback is a callback you provide to handle responses from Paymentez.
It should send the token to your server for processing onSuccess, and notify the user onError.

Here's a sample implementation of the token callback:
```java
paymentez.addCard(
    mContext, uid, email, cardToSave,
    new TokenCallback() {
        public void onSuccess(Card card) {
            // Send token to your own web service
            MyServer.chargeToken(card.getToken());
        }
        public void onError(PaymentezError error) {
            Toast.makeText(getContext(),
                error.getDescription(),
                Toast.LENGTH_LONG).show();
        }
    }
);
```

`addCard` is an asynchronous call – it returns immediately and invokes the callback on the UI thread when it receives a response from Paymentez's servers.

### getSessionId

The Session ID is a parameter Paymentez use for fraud purposes. 
Call this method if you want to Collect your user's Device Information.

```java
String session_id = Paymentez.getSessionId(mContext);
```

Once you have the Session ID, you can pass it to your server to charge the user.

### Client-side validation helpers

The Card object allows you to validate user input before you send the information to Paymentez.

#### validateNumber

Checks that the number is formatted correctly and passes the [Luhn check](http://en.wikipedia.org/wiki/Luhn_algorithm).

#### validateExpiryDate

Checks whether or not the expiration date represents an actual month in the future.

#### validateCVC

Checks whether or not the supplied number could be a valid verification code.

#### validateCard

Convenience method to validate card number, expiry date and CVC.

## Example apps

There is an example app included in the repository:

- PaymentezStore project is a full walk-through of building a shop activity, including connecting to a back end.

http://d20omjwo1khove.cloudfront.net/ccapi/paymentez-store-v1.0.apk

To build and run the example app, clone the repository and open the project.

### Getting started with the Android example app

Note: the app require an [Android SDK](https://developer.android.com/studio/index.html) and [Gradle](https://gradle.org/) to build and run.


### Building and Running the PaymentezStore

Before you can run the PaymentezStore application, you need to provide it with your Paymentez Credentials and a Sample Backend.

1. If you don't have any Credentials yet, please ask your contact on Paymentez Team for it.
2. Replace the `PAYMENTEZ_CLIENT_APP_CODE` and `PAYMENTEZ_CLIENT_APP_KEY` constants in Constants.java with your own Paymentez Client Credentials.
3. Head to https://github.com/paymentez/example-java-backend and click "Deploy to Heroku" (you may have to sign up for a Heroku account as part of this process). Provide your Paymentez Server Credentials PAYMENTEZ_SERVER_APP_CODE and  PAYMENTEZ_SERVER_APP_KEY fields under 'Env'. Click "Deploy for Free".
4. Replace the `BACKEND_URL` variable in the Constants.java file with the app URL Heroku provides you with (e.g. "https://my-example-app.herokuapp.com")

