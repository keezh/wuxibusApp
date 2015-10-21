package com.wuxibus.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wuxibus.app.R;
import com.wuxibus.app.activity.InterchangeLocationActivity;
import com.wuxibus.app.activity.InterchangeResultActivity;
import com.wuxibus.app.activity.MainActivity;
import com.wuxibus.app.constants.AllConstants;
import com.wuxibus.app.entity.GPS;
import com.wuxibus.app.entity.HomeCompanyLocation;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.util.Tools;

/**
 * Created by zhongkee on 15/6/16.
 */
public class InterchangeFragment extends Fragment implements View.OnClickListener{

    /**
     * 起始位置
     */
    private String orign;

    private String destination;

    private TextView orignTextView;
    private TextView destinationTextView;
    private ImageView reverseImageView;
    private ImageView submitImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interchange,null);
        orignTextView = (TextView) view.findViewById(R.id.orign_tv);
        destinationTextView = (TextView) view.findViewById(R.id.destination_tv);
        reverseImageView = (ImageView) view.findViewById(R.id.reverse_iv);
        submitImageView = (ImageView) view.findViewById(R.id.submit_iv);


        orignTextView.setOnClickListener(this);
        destinationTextView.setOnClickListener(this);
        reverseImageView.setOnClickListener(this);
        submitImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //重新获取GPS坐标，当前位置坐标为GPS class中
        ((MainActivity)InterchangeFragment.this.getActivity()).updateBaiduGps();

        if(InterchangeSearch.sourceInfo != null){
            orignTextView.setText(InterchangeSearch.sourceInfo.name);

        }

        if(InterchangeSearch.destinationInfo != null){
            destinationTextView.setText(InterchangeSearch.destinationInfo.name);
        }


    }

    public void saveBindDivice(){
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences= this.getActivity().getSharedPreferences(AllConstants.HomeCompanyXml,
                Activity.MODE_PRIVATE);
//实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
//用putString的方法保存数据
        editor.putString("homeAddress", HomeCompanyLocation.homeAddress);
        editor.putString("homeLatitude", HomeCompanyLocation.homeLatitude);
        editor.putString("homeLongitude", HomeCompanyLocation.homeLongitude);
        editor.putString("companyAddress", HomeCompanyLocation.companyAddress);
        editor.putString("companyLatitude", HomeCompanyLocation.companyLatitude);
        editor.putString("companyLongitude", HomeCompanyLocation.companyLongitude);


//提交当前数据
        editor.commit();
    }

    public void readBindDevice(){

        SharedPreferences sharedPreferences= this.getActivity().getSharedPreferences(AllConstants.HomeCompanyXml,
                Activity.MODE_PRIVATE);
// 使用getString方法获得value，注意第2个参数是value的默认值
        HomeCompanyLocation.homeAddress =sharedPreferences.getString("homeAddress","");
        HomeCompanyLocation.homeLatitude =sharedPreferences.getString("homeLatitude","");
        HomeCompanyLocation.homeLongitude =sharedPreferences.getString("homeLongitude","");
        HomeCompanyLocation.companyAddress =sharedPreferences.getString("companyAddress","");
        HomeCompanyLocation.companyLatitude =sharedPreferences.getString("companyLatitude","");
        HomeCompanyLocation.companyLongitude =sharedPreferences.getString("companyLongitude","");

    }

    @Override
    public void onClick(View v) {
        if(v == orignTextView){
            Intent intent = new Intent(this.getActivity(), InterchangeLocationActivity.class);
            intent.putExtra("location","origin");//是否是出发地
            startActivity(intent);

        }else if(v == destinationTextView){

        } else if(v == reverseImageView){

        }else if(v == submitImageView){
            String orignTemp = orignTextView.getText().toString().trim();
            String destinationTemp = destinationTextView.getText().toString().trim();
            if(orignTemp == null || orignTemp.equals("")){//没有输入则获取当前位置
                orign = GPS.latitude+","+GPS.longitude;
                orignTemp = orign;

            }
            if(destinationTemp == null || destinationTemp.trim().equals("")){
                Tools.showToast(this.getActivity(),"目的地不能为空", Toast.LENGTH_SHORT);
            }

            //Bundle bundle = new Bundle();
            Intent intent = new Intent(this.getActivity(), InterchangeResultActivity.class);
            intent.putExtra("orign", orignTemp);
            intent.putExtra("destination",destinationTemp);
            startActivity(intent);


        }
    }
}
