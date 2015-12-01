package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuxibus.app.R;

/**
 * Created by zhongkee on 15/8/5.
 */
public class WebViewActivity extends Activity implements View.OnClickListener{

    String url;
    String title;
    TextView titleTextView;
    WebView webView;
    ImageView backImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        webView = (WebView) findViewById(R.id.webview);

        String ua = webView.getSettings().getUserAgentString();
        //获取versionname,versioncode
        String pkName = this.getPackageName();
        String versionName = "";
        int versionCode = 0;
        try {
            versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            versionCode = this.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        webView.getSettings().setUserAgentString(ua+" wxbusapp/"+versionName+"_"+versionCode);


        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);//支持js
        backImageView = (ImageView) findViewById(R.id.back_imageview);


        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");
        titleTextView.setText(title);
        webView.loadUrl(url);
        backImageView.setOnClickListener(this);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(backImageView == view){

            if(webView.getOriginalUrl() == null || webView.getOriginalUrl().equals(url)){
                this.finish();
            }else{
                webView.goBack();
            }
        }
    }
}
