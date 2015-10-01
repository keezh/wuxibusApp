package com.wuxibus.app.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by KIM on 2015/3/30 0030.
 */
public class Tools {
    /**
     * 提示窗Toast
     */
    private  static Toast mToast;

    /**
     * 显示提示窗
     * @param context
     * @param msg        显示内容
     * @param duration   显示时间
     */
    public static void showToast(Context context,String msg,int duration){
        if(mToast == null){
            mToast = Toast.makeText(context,msg,duration);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * 关闭提示窗
     */
    public static void closeToast(){
        if(mToast != null){
            mToast.cancel();
        }
    }
}
