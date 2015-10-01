package com.wuxibus.app.customerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author : 苦咖啡
 * 
 * @version : 1.0
 * 
 * @date ：2015年4月14日
 * 
 * @blog : http://blog.csdn.net/cyp331203
 * 
 * @desc : SmartImageView，能根据给定的图片比例，自动调整宽高，解决拉伸变形的屏幕适配问题
 */
public class SmartImageView extends ImageView {

	/** 图片宽和高的比例 */
	private float ratio = 2.43f;

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SmartImageView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SmartImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmartImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// 父容器传过来的宽度方向上的模式
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// 父容器传过来的高度方向上的模式
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		// 父容器传过来的宽度的值
		int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
				- getPaddingRight();
		// 父容器传过来的高度的值
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
				- getPaddingRight();

		if (widthMode == MeasureSpec.EXACTLY
				&& heightMode != MeasureSpec.EXACTLY && ratio != 0.0f) {
			// 判断条件为，宽度模式为Exactly，也就是填充父窗体或者是指定宽度；
			// 且高度模式不是Exaclty，代表设置的既不是fill_parent也不是具体的值，于是需要具体测量
			// 且图片的宽高比已经赋值完毕，不再是0.0f
			// 表示宽度确定，要测量高度
			height = (int) (width / ratio + 0.5f);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					MeasureSpec.EXACTLY);
		} else if (widthMode != MeasureSpec.EXACTLY
				&& heightMode == MeasureSpec.EXACTLY && ratio != 0.0f) {
			// 判断条件跟上面的相反，宽度方向和高度方向的条件互换
			// 表示高度确定，要测量宽度
			width = (int) (height * ratio + 0.5f);

			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
					MeasureSpec.EXACTLY);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
