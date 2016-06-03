Paymentez SDK Android
===================


 Paymentez SDK Android is a library that allows developers to easily connect to the Paymentez CREDITCARDS API

----------

***REMOVED******REMOVED***Installation

***REMOVED******REMOVED******REMOVED***Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    compile('com.paymentez.paymentezsdk:paymentezsdk:1.0.0') { transitive = true }


***REMOVED******REMOVED******REMOVED*** ProGuard

If you're planning on optimizing your app with ProGuard, make sure that you exclude the Paymentez bindings. You can do this by adding the following to your app's `proguard.cfg` file:

    -keep class com.paymentez.** { *; }

***REMOVED******REMOVED*** Example
Try out the sample application

https://www.dropbox.com/s/m52m0u807ftw1jc/paymentez-example.apk?dl=0

***REMOVED******REMOVED***Documentation
***REMOVED******REMOVED******REMOVED***init library
```java
// @param mContext Context of the Main Activity
// @param dev_environment false to use production environment
// @param app_code Application identifier (provided by Paymentez).
// @param app_secret_key Application Secret key (provided by Paymentez).
PaymentezSDKClient paymentezsdk = new PaymentezSDKClient(this, true, "your_app_code", "your_app_secret_key");
```
***REMOVED******REMOVED******REMOVED***List Cards
```java

```

