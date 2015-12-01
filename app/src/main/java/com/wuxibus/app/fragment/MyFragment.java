package com.wuxibus.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.wuxibus.app.R;
import com.wuxibus.app.activity.MyFavActivity;
import com.wuxibus.app.activity.WebViewActivity;
import com.wuxibus.app.adapter.MyItemAdapter;
import com.wuxibus.app.customerView.MyListView;
import com.wuxibus.app.entity.MyItem;
import com.wuxibus.app.util.Tools;
import com.wuxibus.app.volley.VolleyFileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/6/16.
 */
public class MyFragment extends Fragment implements AdapterView.OnItemClickListener,View.OnClickListener{
    FeedbackAgent fb;

    TextView favTextView;
    MyListView infoListView;
    MyListView aboutListView;
    TextView versionTip;

    List<MyItem> infoList = new ArrayList<MyItem>();
    List<MyItem> aboutList = new ArrayList<MyItem>();


//        使用帮助：
    String helpUrl = "http://api.wxbus.com.cn/view/?m=list&id=158";
//    信息纠错：
    String infoUrl = "http://api.wxbus.com.cn/view/?m=report";

//    关于我们：
    String abountUrl = "http://api.wxbus.com.cn/view/?m=about";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,null);
        favTextView = (TextView) view.findViewById(R.id.fav_textview);
        infoListView = (MyListView) view.findViewById(R.id.info_listview);
        aboutListView = (MyListView) view.findViewById(R.id.about_listview);
        versionTip = (TextView) view.findViewById(R.id.version_tip);
        infoListView.setOnItemClickListener(this);
        aboutListView.setOnItemClickListener(this);

        initData();
        MyItemAdapter infoAdapter = new MyItemAdapter(this.getActivity(),infoList);
        infoListView.setAdapter(infoAdapter);

        MyItemAdapter aboutAdapter = new MyItemAdapter(this.getActivity(),aboutList);
        aboutListView.setAdapter(aboutAdapter);

        initListeners();
       // setUpUmengFeedback();

        String version = getAppInfo();
        versionTip.setText(version);


        return view;
    }

    private String getAppInfo() {
        try {
            String pkName = this.getActivity().getPackageName();
            String versionName = this.getActivity().getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = this.getActivity().getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return   "版本：" + versionName + " build " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }



    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("my-fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("my-fragment");
    }

    public void initListeners(){
        favTextView.setOnClickListener(this);
        infoListView.setOnItemClickListener(this);
        aboutListView.setOnItemClickListener(this);

    }

    public void initData(){
        MyItem info1 = new MyItem(R.drawable.my_icon_correct,"信息纠错");
        MyItem info2 = new MyItem(R.drawable.my_icon_clean,"清除缓存");
        infoList.add(info1);
        infoList.add(info2);


        MyItem about1 = new MyItem(R.drawable.my_icon_about,"关于我们");
        MyItem about2 = new MyItem(R.drawable.my_icon_help,"使用帮助");
        MyItem about3 = new MyItem(R.drawable.my_icon_feedback,"意见反馈");
        MyItem about4 = new MyItem(R.drawable.my_icon_share,"检测更新");
//        MyItem about5 = new MyItem(R.drawable.my_icon_rate,"给我们打分");

        aboutList.add(about1);
        aboutList.add(about2);
        aboutList.add(about3);
        aboutList.add(about4);
//        aboutList.add(about5);


    }

//    使用帮助：http://api.wxbus.com.cn/view/?m=list&id=158
//    信息纠错：http://api.wxbus.com.cn/view/?m=report
//    关于我们：http://api.wxbus.com.cn/view/?m=about

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == infoListView){
            MyItem item = (MyItem)view.getTag();
            if(item.getImgResId() == R.drawable.my_icon_correct){
                Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
                intent.putExtra("title","信息纠错");
                intent.putExtra("url",infoUrl);
                startActivity(intent);
            }else if(item.getImgResId() == R.drawable.my_icon_clean){
                VolleyFileManager.deleteFileCache();//清除缓存
                Tools.showToast(this.getActivity(),"清除缓存成功!", Toast.LENGTH_SHORT);

            }

        }else if(adapterView == aboutListView){
            MyItem item = (MyItem)view.getTag();
            if(item.getImgResId() == R.drawable.my_icon_about){
                Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
                intent.putExtra("title","关于我们");
                intent.putExtra("url",abountUrl);
                startActivity(intent);
            }else if(item.getImgResId() == R.drawable.my_icon_help){
                Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
                intent.putExtra("title","使用帮助");
                intent.putExtra("url",helpUrl);
                startActivity(intent);
            }else if(item.getImgResId() == R.drawable.my_icon_feedback){
                FeedbackAgent agent = new FeedbackAgent(this.getActivity());
                agent.startFeedbackActivity();
            }else if(item.getImgResId() == R.drawable.my_icon_share){
                UmengUpdateAgent.forceUpdate(this.getActivity());
            }

        }
    }

    private void setUpUmengFeedback() {
        fb = new FeedbackAgent(this.getActivity());
        // check if the app developer has replied to the feedback or not.
        fb.sync();
        fb.openAudioFeedback();
        fb.openFeedbackPush();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = fb.updateUserInfo();
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if(favTextView == view){
            Intent intent = new Intent(this.getActivity(), MyFavActivity.class);
            startActivity(intent);

        }
    }
}
