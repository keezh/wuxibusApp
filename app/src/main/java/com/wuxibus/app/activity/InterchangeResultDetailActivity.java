package com.wuxibus.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wuxibus.app.InterchangeModel;
import com.wuxibus.app.R;
import com.wuxibus.app.adapter.InterchangeViewPagerAdapter;
import com.wuxibus.app.entity.InterchangeScheme;
import com.wuxibus.app.entity.InterchangeSearch;
import com.wuxibus.app.entity.InterchangeStep;
import com.wuxibus.app.entity.InterchangeVehicle;
import com.wuxibus.app.util.DensityUtil;
import com.wuxibus.app.util.Tools;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/11/1.
 */
public class InterchangeResultDetailActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    ViewPager detailViewPager;
    List<View> pageViews = new ArrayList<View>();
    ImageView backImageView;
    LinearLayout pageControls;
    int currentIndex;//当前选中状态
    List<ImageView> pageControlViews = new ArrayList<ImageView>();
    TextView titleTextView;
    TextView mapTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interchange_result_detail);
        currentIndex = this.getIntent().getIntExtra("currentIndex",0);//获取那个page
        detailViewPager = (ViewPager) findViewById(R.id.detail_viewPager);
        backImageView = (ImageView) findViewById(R.id.back_imageview);
        pageControls = (LinearLayout) findViewById(R.id.pageControls);
        titleTextView = (TextView) findViewById(R.id.title_tv);
        mapTextView = (TextView) findViewById(R.id.map_interchange);
        backImageView.setOnClickListener(this);
        mapTextView.setOnClickListener(this);

        initPageView();

        InterchangeViewPagerAdapter adapter = new InterchangeViewPagerAdapter(pageViews);
        detailViewPager.setAdapter(adapter);
        detailViewPager.setCurrentItem(currentIndex);
        detailViewPager.setOnPageChangeListener(this);
        titleTextView.setText("方案"+(currentIndex+1));
    }

    public void initPageView(){
        List<InterchangeScheme> schemeList = InterchangeModel.getInstance().schemeList;
        for (int i = 0; i < schemeList.size(); i++) {
            View view = View.inflate(this,R.layout.interchange_detail_page,null);
            LinearLayout container = (LinearLayout) view.findViewById(R.id.title_container);
            TextView tipText = (TextView) view.findViewById(R.id.stop_info_tv);
            String tipHtml = schemeList.get(i).getTip_text();
            if(tipHtml != null && !tipHtml.equals("")){
                tipText.setText(Html.fromHtml(tipHtml));
            }else{
                tipText.setVisibility(View.GONE);
            }
            TextView detailTextView = (TextView) view.findViewById(R.id.detail_tv);
            List<List<InterchangeStep>> steps = schemeList.get(i).getSteps();
            layoutTitleContainer(steps,container);

            int time = schemeList.get(i).totalTime;
            int nums = schemeList.get(i).totalStops;
            int meters = schemeList.get(i).totalMeters;

            detailTextView.setText(Tools.getTimes(time)+"  |  "+nums+"站"+"  |  步行"+meters+"米");

            LinearLayout stepLayout = (LinearLayout) view.findViewById(R.id.step_layout);
            layoutStepDetail(steps,stepLayout);

            pageViews.add(view);
        }

        initPageControls();

    }

    public void initPageControls(){
        List<InterchangeScheme> schemeList = InterchangeModel.getInstance().schemeList;
        for (int i = 0; i < schemeList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            if(i == currentIndex){
                imageView.setImageResource(R.drawable.carousel_indicator_current_light);
            }else{
                imageView.setImageResource(R.drawable.carousel_indicator);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            pageControls.addView(imageView,layoutParams);
            pageControlViews.add(imageView);//保存对象应用，可以改变其是否是当前状态的图片
        }

    }

    /**
     * 使用线性布局
     * @param stepList
     * @param container
     */
    public void layoutStepDetail(List<List<InterchangeStep>> stepList,LinearLayout container){

        boolean isBeginStop = true;//

        int lineWidth = DensityUtil.dip2px(this,2);


        for (int i = 0; i <stepList.size(); i++) {

            final InterchangeStep step = stepList.get(i).get(0);//

            int fontSize = 16;

           // if(i == 0){
                LinearLayout lineContainer = new LinearLayout(this);
                lineContainer.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout leftContainer = new LinearLayout(this);
                int width = DensityUtil.dip2px(this,65);
                int height = DensityUtil.dip2px(this,62);

                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height));
                lineContainer.setLayoutParams(lineParams);
                LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                leftContainer.setLayoutParams(leftParams);
                LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                rightParams.gravity = Gravity.CENTER_VERTICAL;
                LinearLayout rightContainer = new LinearLayout(this);
                rightContainer.setLayoutParams(rightParams);

                leftContainer.setOrientation(LinearLayout.VERTICAL);
                leftContainer.setGravity(Gravity.CENTER_HORIZONTAL);

                lineContainer.addView(leftContainer);
                lineContainer.addView(rightContainer);
                container.addView(lineContainer);

            if(i == 0) {


                View leftView = View.inflate(this, R.layout.interchange_detail_left_start, null);
                leftContainer.addView(leftView);

                TextView textView = new TextView(this);
                textView.setText(step.getStepInstruction());
                textView.setTextSize(16);
                rightContainer.addView(textView);

            }else if(step.getType() == 3) {//起点，或终点
                if (step.getVehicle() != null) {
                    TextView startName = new TextView(this);
                    TextView endName = new TextView(this);
                    TextView lineName = new TextView(this);
                    TextView upTip = new TextView(this);
                    // TextView stopNum = new TextView(this);
                    TextView time = new TextView(this);
                    TextView toDirection = new TextView(this);

                    upTip.setText("  上车");
                    upTip.setTextSize(fontSize);
                    lineName.setText(step.getVehicle().getName());
                    lineName.setTextColor(Color.argb(255, 220, 69, 69));
                    //点击跳转到线路单程
                    lineName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //SearchLineResultActivity
                            Intent intent = new Intent(InterchangeResultDetailActivity.this,SearchLineResultActivity.class);
                            intent.putExtra("lineName",step.getVehicle().getName());
                            startActivity(intent);

                        }
                    });
                    startName.setText(step.getVehicle().getStart_name());
                    startName.setTextSize(fontSize);

                    startName.setTextColor(Color.BLACK);
                    toDirection.setText("  往 " + step.getVehicle().getEnd_name() + "  途径" +
                            step.getVehicle().getStop_num() + "站");
                    endName.setText(step.getVehicle().getEnd_name());
                    // stopNum.setText(step.getVehicle().getStop_num());
                    time.setText("首 " + step.getVehicle().getStart_time() + "   末 " + step.getVehicle().getEnd_time());

                    LinearLayout upContainer = new LinearLayout(this);
                    upContainer.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout firstLine = new LinearLayout(this);
                    firstLine.setOrientation(LinearLayout.HORIZONTAL);
                    firstLine.addView(startName);
                    firstLine.addView(upTip);
                    LinearLayout secondLine = new LinearLayout(this);
                    secondLine.setOrientation(LinearLayout.HORIZONTAL);

                    secondLine.addView(lineName);
                    secondLine.addView(toDirection);

                    upContainer.addView(firstLine);
                    upContainer.addView(secondLine);
                    upContainer.addView(time);

                    View leftView = View.inflate(this, R.layout.interchange_detail_left, null);
                    ImageView icon = (ImageView) leftView.findViewById(R.id.icon_iv);
                    icon.setImageResource(R.drawable.interchange_detail_icon_on);
                    leftContainer.addView(leftView);
                    rightContainer.addView(upContainer);

                    //下车view
                    LinearLayout downContainer = new LinearLayout(this);
                    downContainer.setOrientation(LinearLayout.HORIZONTAL);
                    downContainer.setLayoutParams(rightParams);
                    downContainer.addView(endName);
                    endName.setTextColor(Color.BLACK);
                    endName.setTextSize(fontSize);

                    TextView downTip = new TextView(this);
                    downTip.setText("  下车");
                    downContainer.addView(downTip);

                    View leftDownView = View.inflate(this, R.layout.interchange_detail_left, null);
                    ImageView iconDown = (ImageView) leftDownView.findViewById(R.id.icon_iv);
                    iconDown.setImageResource(R.drawable.interchange_detail_icon_off);
                    LinearLayout downLineContainer = new LinearLayout(this);
                    downLineContainer.setLayoutParams(lineParams);
                    LinearLayout leftDownContainer = new LinearLayout(this);
                    // LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(width,height);
                    leftDownContainer.setLayoutParams(leftParams);
                    //leftParams.gravity = Gravity.CENTER_HORIZONTAL;
                    leftDownContainer.addView(leftDownView);
                    leftDownContainer.setGravity(Gravity.CENTER_HORIZONTAL);
                    downLineContainer.addView(leftDownContainer);
                    downLineContainer.addView(downContainer);

                    container.addView(downLineContainer);
                }

            }else if(step.getType() == 5){//步行,一般最后一项

                        TextView downStop = new TextView(this);
                        downStop.setText(step.getStepInstruction());
                        downStop.setTextSize(fontSize);

                        View leftView = View.inflate(this,R.layout.interchange_detail_left,null);
                        ImageView icon = (ImageView) leftView.findViewById(R.id.icon_iv);
                        icon.setImageResource(R.drawable.interchange_detail_icon_walk);
                        leftContainer.addView(leftView);
                        rightContainer.addView(downStop);

            }

            if(i == stepList.size() - 1){//最后项，添加重点名称,需要重新构建视图
                View leftView = View.inflate(this,R.layout.interchange_detail_end,null);
                //leftContainer.addView(leftView);

                TextView endPostion = new TextView(this);
                endPostion.setText(InterchangeSearch.destinationInfo.name);
                endPostion.setTextSize(fontSize);
                //rightContainer.addView(endPostion);

                LinearLayout lastContainer = new LinearLayout(this);
                lastContainer.setLayoutParams(lineParams);
                LinearLayout lastLeftContainer = new LinearLayout(this);
                lastLeftContainer.setLayoutParams(leftParams);
                lastLeftContainer.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout lastRightContainer = new LinearLayout(this);
                lastRightContainer.setLayoutParams(rightParams);

                lastContainer.addView(lastLeftContainer);
                lastContainer.addView(lastRightContainer);

                container.addView(lastContainer);

                lastLeftContainer.addView(leftView);
                lastRightContainer.addView(endPostion);



            }

        }


    }

    /**
     * 动态添加头
     * @param stepList
     * @param container
     */
    public void layoutTitleContainer(List<List<InterchangeStep>> stepList,LinearLayout container){

        TextView nextImageView = null;
        int margin = DensityUtil.dip2px(this, 5);//5dp

        for (int i = 0; i < stepList.size(); i++) {
            List<InterchangeStep> steps = stepList.get(i);

            InterchangeVehicle vehicle = steps.get(0).getVehicle();
            if(vehicle != null){
                TextView titleView = new TextView(this);
                titleView.setTextColor(Color.BLACK);
                titleView.setTextSize(18);
                titleView.setText(vehicle.getName());
                nextImageView = new TextView(this);
                nextImageView.setTextSize(18);
                nextImageView.setText(" → ");
               // nextImageView.setImageResource(R.drawable.interchange_plan_icon_next);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(margin, margin, margin, margin);
                nextImageView.setLayoutParams(lp);
                container.addView(titleView);
                container.addView(nextImageView);
            }
        }
        //移除最后一个view
        container.removeView(nextImageView);
        container.requestLayout();
    }

    @Override
    public void onClick(View v) {
        if(v == backImageView){
            finish();
        }else if(v == mapTextView){

            Intent intent = new Intent(this,InterchangeMapActivity.class);
            intent.putExtra("currentIndex",currentIndex);
            startActivity(intent);

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //currentIndex = position+1;
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < pageControlViews.size(); i++) {
            if(i == position){
                pageControlViews.get(i).setImageResource(R.drawable.carousel_indicator_current_light);
            }else{
                pageControlViews.get(i).setImageResource(R.drawable.carousel_indicator);
            }
        }
        currentIndex = position;

        titleTextView.setText("方案"+(position + 1));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
