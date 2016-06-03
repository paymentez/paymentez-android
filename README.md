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
// @param mContext Context of the Main Activity
// @param dev_environment false to use production environment
// @param app_code Application identifier (provided by Paymentez).
// @param app_secret_key Application Secret key (provided by Paymentez).
PaymentezSDKClient paymentezsdk = new PaymentezSDKClient(this, true, "your_app_code", "your_app_secret_key");
```
###List Cards
```java

```

