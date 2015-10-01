package com.wuxibus.app.volley;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.wuxibus.app.InitApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashSet;

/**
 * @author lizhen  2014-4-11下午1:51:03 lizhen@touch-spring.com
 *         图片内存缓存
 */
public class BitmapCache implements ImageCache {
    private LruCache<String, Bitmap> mCache = null;

    /**
     * 存放图片的软引用
     */
    public static HashSet<SoftReference<Bitmap>> mReusableBitmaps = new HashSet<SoftReference<Bitmap>>();

    /**
     * 程序最大可用内存
     */
    public static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    /**
     * 最大可使用内存的1/8
     */
    public static int DEFAULT_MEM_CACHE_SIZE = maxMemory / 4; // 6MB
    private static BitmapCache bitmapCache;

    private BitmapCache() {
        InitApplication.appLog.i("程序最大可使用内存的1/4为：" + DEFAULT_MEM_CACHE_SIZE);
        mCache = new LruCache<String, Bitmap>(DEFAULT_MEM_CACHE_SIZE) {
            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }

            @Override
            protected int sizeOf(String key, Bitmap value) {
                // final int bitmapSize = getBitmapSize(value) / 1024;
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public static synchronized BitmapCache getInstern() {
        if (bitmapCache == null) {
            bitmapCache = new BitmapCache();
        }
        return bitmapCache;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }


    public boolean getSDCardBitmap(final String url, final ImageView imageView, final CallBackSDcardImg callBackSDcardImg) {
        // 不在内存缓存中
        if (getBitmap(url) == null) {
            //判断是否在本地
            //modify by kee
            //index_pic + /200x150

            final String imgPath;
            if(url.toLowerCase().endsWith("jpg")|| url.toLowerCase().endsWith("png")){
                imgPath = VolleyFileManager.getBitmapDirectory() + "/" + url.substring(url.lastIndexOf("/") + 1, url.length());

            }else{
                int lastIndex = url.lastIndexOf("/");
                String tempUrl = url.substring(0,lastIndex);
                imgPath = VolleyFileManager.getBitmapDirectory() + "/" + tempUrl.substring(tempUrl.lastIndexOf("/") + 1, tempUrl.length());
            }
            File file = new File(imgPath);

            InitApplication.appLog.i("本地文件路径imagePath = " + imgPath);
            if (file.exists()) {
                //开启线程下载
                final Handler handler = new Handler() {
                    //加载本地图片
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bitmap bitmap = (Bitmap) msg.obj;
                        InitApplication.appLog.i("从SDcard读取图片");
                        callBackSDcardImg.setImgToView(bitmap, imageView);
                        //放入内存缓存中 防止溢出
                        putBitmap(url, bitmap);
                    }
                };
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            //从本地SDCard读取图片
                            FileInputStream fis = new FileInputStream(imgPath);
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            fis.close();
                            Message msg = handler.obtainMessage();
                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                InitApplication.appLog.i("文件存在...");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        //判断Bitmap在硬盘中是否存
        saveImg(url, bitmap);
        if(url != null && bitmap != null )
        mCache.put(url, bitmap);
    }

    /**
     * 将图片存入硬盘缓存
     *
     * @param url
     */
    protected void saveImg(String url, final Bitmap bitmap) {
        final String imgPath = VolleyFileManager.getBitmapDirectory() + "/" + url.substring(url.lastIndexOf("/") + 1, url.length());
        final File imgFile = new File(imgPath);
        if (!imgFile.exists()) {
            //线程中存储图片
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        FileOutputStream out = new FileOutputStream(imgFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public interface CallBackSDcardImg {

        public void setImgToView(Bitmap bitmap, ImageView imgView);
    }
}
