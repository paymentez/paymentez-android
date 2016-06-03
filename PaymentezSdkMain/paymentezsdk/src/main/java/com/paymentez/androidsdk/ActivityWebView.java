package com.paymentez.androidsdk;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ActivityWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_web_view);


        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("Agregar Tarjeta");
        //ab.setSubtitle("This is Subtitle");


        final WebView webView1 = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView1.setWebViewClient(new PaymentWebViewClient());

        String payment_url = "";

        Bundle b = getIntent().getExtras();

        if(b != null)
            payment_url = b.getString("url");




        System.out.println(payment_url);
        webView1.loadUrl(payment_url);
    }



    public class PaymentWebViewClient extends WebViewClient {
        private int       webViewPreviousState;
        private final int PAGE_STARTED    = 0x1;
        private final int PAGE_REDIRECTED = 0x2;
        private boolean is_error;

        private ProgressDialog dialog;

        public PaymentWebViewClient()
        {
            super();
            is_error = false;
        }


        @Override
        public void onLoadResource (WebView view, String url)
        {
            super.onLoadResource(view, url);


        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webViewPreviousState = PAGE_REDIRECTED;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            webViewPreviousState = PAGE_STARTED;
            if (dialog == null || !dialog.isShowing()){
                dialog = new ProgressDialog(ActivityWebView.this);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("");
                dialog.show();
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(url.contains("save") && !is_error)
            {

                String cookie=getCookie(url, "pmntz_add_success");

                if(cookie!=null)
                {
                    if(cookie.equalsIgnoreCase("true"))
                    {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityWebView.this);
                        builder1.setMessage( "Card registered successfully");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        finish();
                    }
                    else
                    {
                        String cookie2=getCookie(url, "pmntz_error_message");

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityWebView.this);
                        builder1.setMessage( "Card not registered successfully. Error: "+cookie2);
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

            try
            {
                if (webViewPreviousState == PAGE_STARTED) {
                    dialog.dismiss();
                    dialog = null;
                }

            }
            catch(Exception e){}
        }

        @Override
        public void onReceivedError(WebView view, int errorCod,String description, String failingUrl) {
            Toast.makeText(ActivityWebView.this, "Card not registered successfully", Toast.LENGTH_LONG).show();

            is_error = true;


        }

        public String getCookie(String siteName,String CookieName){
            String CookieValue = null;

            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(siteName);
            if(cookies!=null)
            {
                String[] temp=cookies.split("[;]");
                for (String ar1 : temp ){
                    if(ar1.contains(CookieName)){
                        String[] temp1=ar1.split("[=]");
                        CookieValue = temp1[1];
                    }
                }
            }
            return CookieValue;
        }
    }



}
