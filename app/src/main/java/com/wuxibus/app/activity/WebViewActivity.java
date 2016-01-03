package com.wuxibus.app.activity;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.wuxibus.app.R;
import com.wuxibus.app.util.JSBridge;
import com.wuxibus.app.volley.VolleyManager;

import java.net.URLEncoder;


/**
 * Created by zhongkee on 15/8/5.
 *
 * view-source:http://www.wxbus.com.cn/view/info-347.html  新闻url
 var shareData = {
 text: '周末玩啥进入2015最后一个月啦，大家最期待的，最新、最全、最优惠的无锡各大银行信用卡活动大全来啦！',
 title: '无锡12月银行卡优惠活动大全！1毛看电影！5折享美食！ 分享自无锡智慧公交',
 image_url: 'http://img.wxbus.com.cn/attachment/2015/12/1449300767.jpg',
 link: link
 };
 if (window.bus_show_share_button) {
 bus_show_share_button(shareData);
 }
 */
public class WebViewActivity extends Activity implements View.OnClickListener{

    String url;
    String title;
    TextView titleTextView;
    WebView webView;
    ImageView backImageView;
    public TextView shareTextView;

    String shareText;
    String shareTitle;
    String shareImgUrl;
    String shareLink;
    String defaultTitle;

    Bitmap firstIcon;
    public String defaultShareImgUrl;//默认的分享图片url

    // 首先在您的Activity中添加如下成员变量
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    shareTextView.setVisibility(View.VISIBLE);
                    break;
                case 2:shareTextView.setVisibility(View.GONE);
                    break;
                case 3:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        webView = (WebView) findViewById(R.id.webview);
        shareTextView = (TextView) findViewById(R.id.share_textview);
        shareTextView.setOnClickListener(this);
//        shareTextView.setVisibility(View.GONE);

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

        webView.getSettings().setUserAgentString(ua + " wxbusapp/" + versionName + "_" + versionCode);


        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);//支持js
        JSBridge jsBridge = new JSBridge(this);
        webView.addJavascriptInterface(jsBridge, "android");
        backImageView = (ImageView) findViewById(R.id.back_imageview);


        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");
        titleTextView.setText(title);
//        String testUrl = "http://www.wxbus.com.cn/view/code/test.html";
//        webView.loadUrl(testUrl);
        webView.loadUrl(url);
        backImageView.setOnClickListener(this);

        WebChromeClient wcc = new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                defaultTitle = title;
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon)
            {
                if(firstIcon == null){
                    firstIcon = icon;
                }

            }

        };

        webView.setWebChromeClient(wcc);
        webView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                //document.getElementsByTagName('img')[0].src
//                view.loadUrl("javascript:window.android.showFirstImg(document.getElementsByTagName('img')[0].src);");
//                view.loadUrl("javascript:window.android.showFirstImg(function(){" +
//                        "var images = document.getElementsByTagName('img');" +
//                        "alert('test');"+
//                        "for(i = 0;i<images.length;i++){"+
//                        " if(images[i].src != '')"+
//                        " return images[i].src;"+
//                        "}"+
//                        ");");
//                String jsContent = "var images = document.getElementsByTagName('img'); " +
//                        "for(i = 0;i<images.length;i++){" +
//                        "if(images[i].src != ''){" +
//                        "window.android.showFirstImg(images[i].src);break;" +
//                        "}" +
//                        "}";
                String jsContent = "var defaultImg = 'http://www.wxbus.com.cn/images/shareicon.png';" +
                        "          var img_url = defaultImg;" +
                        "          var imgs = document.getElementsByTagName('img');" +
                        "          var getImg = function(i) {" +
                        "            if (i < imgs.length) {" +
                        " if(imgs[i].src != ''){"+
                        "              var img = new Image();" +
                        "              img.src = imgs[i].src;" +
                        "              img.onload = function() {" +
                        "                if (img.width >= 300 || img.height >= 300) {" +
                        "                  img_url = img.src;" +
                        "                  " +
                        "                  window.android.showFirstImg(img_url);" +
                        "                } else {" +
                        "                  getImg(++i);" +
                        "                }" +

                        "              };" +
                        "}else{" +
                        "getImg(++i);}" +
                        "            } else {" +
                        "            window.android.showFirstImg(img_url); " +
                        "              " +
                        "            }" +
                        "          };" +
                        "          if (imgs.length > 0) {" +
                        "            getImg(0);" +
                        "          } else {" +
                        "            window.android.showFirstImg(img_url);" +
                        "          }";

                try {
                    view.loadUrl("javascript:"+jsContent);

//                    view.loadUrl("javascript:window.android.showFirstImg(document.getElementsByTagName('img')[0].src);");


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        //config
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.SMS);
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.SMS);

        // 添加短信平台
        addSMS();


    }

    @Override
    public void onClick(View view) {
        if(backImageView == view){

            String originalUrl = webView.getOriginalUrl();

            if(webView.getOriginalUrl() == null || webView.getOriginalUrl().equals(url) || originalUrl.equals(url+"/")
                    || url.contains(webView.getOriginalUrl())){
                this.finish();
            }else{
                webView.goBack();
            }
        }else if(shareTextView == view){
            shareUmeng();
        }
    }

    public void shareUmeng(){


// 设置分享内容
        //mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
// 设置分享图片, 参数2为图片的url地址
       // mController.setShareMedia(new UMImage(this,
       //         "http://www.baidu.com/img/bdlogo.png"));


        defaultShare();
        mController.openShare(this, false);
    }

    /**
     * 设置默认分享参数
     */
    public void defaultShare(){
        if(this.shareText == null || this.shareText.trim().equals("")){
            mController.setShareContent(defaultTitle);
            if (defaultShareImgUrl != null){
                mController.setShareImage(new UMImage(this,defaultShareImgUrl));
            }else{
                mController.setShareImage(new UMImage(this,firstIcon));

            }

            configWeixin();

        }
    }

    public void loadShareImage(final String imgUrl){
//                     Message msg = new Message();
//                msg.what = 3;   //获取默认的图片      h
//                handler.sendMessage(msg);
                             this.defaultShareImgUrl = imgUrl;

               // mController.setShareMedia(new UMImage(this, imgUrl));



    }

    public void showShareButton(String text, String title, String imgUrl, String link){
        this.shareText = text;
        this.shareTitle = title;
        this.shareImgUrl = imgUrl;
        this.shareLink = link;

        //shareTextView.setVisibility(View.VISIBLE);//默认不显示，只有网页中回调了函数，才显示分享
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);


        mController.setShareContent(shareText);
        mController.setShareMedia(new UMImage(this, shareImgUrl));

        configWeixin();

    }

//    无锡智慧公交
//    AppID：wx89e0eb76583bdac4
//    AppSecret：46219fc131e097e56db85485141d8cf6重置
//            已通过


    public void configWeixin(){
        String appID = "wx89e0eb76583bdac4";//wx89e0eb76583bdac4
        String appSecret = "46219fc131e097e56db85485141d8cf6";
// 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this,appID,appSecret);
        wxHandler.addToSocialSDK();
        wxHandler.setTitle(shareTitle);
        //设置分享的url，如果没有source=share,并且要对？
        if(!url.contains("source=wxbusapp")){
            if (url.contains("?")){
                url += "&source=wxbusapp";
            }else{
                url+="?source=wxbusapp";
            }

        }
        if(this.shareText == null || this.shareText.equals("")){
            wxHandler.setTargetUrl(url);
        }else{
            wxHandler.setTargetUrl(this.shareLink);
        }
// 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this,appID,appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        wxCircleHandler.setTitle(shareText);
        if(this.shareText == null || this.shareText.equals("")){
            wxCircleHandler.setTargetUrl(url);wxCircleHandler.setTitle(defaultTitle);
        }else{
            wxCircleHandler.setTargetUrl(this.shareLink);

        }

    }

    /**
     * 添加短信平台</br>
     */
    private void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

//    public void shareQQ(){
//        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
//        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468",
//                "c7394704798a158208a74ab60104f0ba");
//        qqSsoHandler.addToSocialSDK();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 默认隐藏按钮
     */
    public void hideShareButton() {

        Message msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);

    }
}
