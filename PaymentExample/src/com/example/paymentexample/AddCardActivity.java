package com.example.paymentexample;

import com.paymentez.api.PaymentezCCSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class AddCardActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcard);
		
		final EditText editText1 = (EditText) findViewById(R.id.editText1);
		
		final EditText editText2 = (EditText) findViewById(R.id.editText2);
		
		final PaymentezCCSDK paymentezsdk = new PaymentezCCSDK(this, true, "ST-MX", "vgVfq0kLZveGIdD9ljGjPtt6ieYtIQ","PREPAID", "Ere68ttPklFTn89xZIhFYcqC5X8HX3Ob5qgbEkfjNfCLkud3wY"); 
		
		
		final WebView webView1 = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = webView1.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {            	
            	String payment_url = paymentezsdk.cardAdd(editText1.getText().toString(), editText2.getText().toString());
            	System.out.println(payment_url);
            	webView1.loadUrl(payment_url);
            }
        });
		
	}

}
