package com.wuxibus.app.volley;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lizhen 2013-12-24上午9:50:22 用来管理文件缓存的类
 * 缓存路径为：sd卡根目录+"Android/data/"+包名+"cache"
 * 没有sd就在应用程序缓存路径下
 */
public class VolleyFileManager {
	/** 项目包名 */
	private static String packageName;
	/** 缓存根目录 */
	private static String cache_root;
	/** 文字缓存路径 */
	private static String cache_txt ;
	/** 图片缓存路径 */
	private static String cache_bitmap ;
    /** Volley缓存路径 */
    private static String cache_volley;
	private static String TEXT_SUFFIX = ".cach";

	private static final int MB = 1024 * 1024;
	private static final int MAX_CACHE_SIZE = 5;
	
	/**应用路径  */
	private static String appDir;
	
	/**
	 * @author lizhen  2014-4-11上午11:25:25 lizhen@touch-spring.com
	 * 初始化数据
	 * @param application
	 */
	public static void init(Context application){
		appDir=application.getCacheDir().getPath();
		
		packageName=application.getPackageName();
		cache_root="Android/data/"+packageName;
		cache_txt = cache_root + "/cache/object";
		cache_volley = cache_root + "/cache/volley";
        cache_bitmap = cache_root + "/cache/images";
		
	}
    public static String getVolleyDirectory(){
        String dir = getSDpath() + "/" + cache_volley;
        return dir;
    }
	public static String getTextDirectory() {
		String dir = getSDpath() + "/" + cache_txt;
		return dir;
	}

	public static String getBitmapDirectory() {
		String dir = getSDpath() + "/" + cache_bitmap;
        //创建图片缓存区域
        File dirFile=new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
		return dir;
	}

	/**
	 * 删除缓存文件
	 * 
	 * @author lizhen 2013-12-24上午10:13:20
	 */
	public static void deleteFileCache() {
		File file = new File(cache_root);
		if (file.exists()) {
			file.delete();
			Log.i("Tag", "删除缓存=========>");
		}
	}

	/**
	 * 获取sd卡的path
	 * 
	 * @author lizhen 2013-12-24上午9:53:00
	 * @return
	 */
	public static String getSDpath() {
		String sdDir = null;
		if (hasSdCard()) {
			sdDir = Environment.getExternalStorageDirectory().getPath();
		}
		if (sdDir != null) {
			return sdDir;
		} else {
			//没有SD卡就返回程序缓存路径
			return appDir;
		}
	}

	/**
	 * 判断是否存在sd卡
	 * 
	 * @author lizhen 2013-12-24上午9:53:49
	 * @return
	 */
	public static boolean hasSdCard() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取sd卡可用大小/MB
	 * 
	 * @author lizhen 2013-12-24上午9:52:46
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static int getFreeSpaceOnSd() {
		StatFs stat = new StatFs(getSDpath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getFreeBlocks()) / MB;
		return (int) sdFreeMB;
	}
	

	public static String getTextName(String url,String categroy) {
		String[] strs = url.split("/");
		String strs1 = strs[strs.length - 1]+categroy;
		return strs1 + TEXT_SUFFIX;
	}
	
	/**返回新闻内容
	 * @author lizhen  2014-2-18上午11:13:26
	 * @param url
	 * @return
	 */
	public static String getNewsFileName(String url){
		String str=(url.substring(url.lastIndexOf("/")+1)).replaceAll("\\u003F", "#");
		return "news#"+str+TEXT_SUFFIX;
	}
	
	public static String getDesignerFileName(String url){
		String str=(url.substring(url.lastIndexOf("/")+1)).replaceAll("\\u003F", "#");
		return "designer#"+str+TEXT_SUFFIX;
	}
	public static String getCacheFileName(String url,String type){
		String str=(url.substring(url.lastIndexOf("/")+1)).replaceAll("\\u003F", "#");
		return type+"#"+str+TEXT_SUFFIX;
	}
	
	
	/**读取文本
	 * @author lizhen  2013-12-24下午2:21:23
	 * @param file
	 * @return
	 */
	public static String readTextFile(String filename){
		File file=new File(getTextDirectory(),filename);
		if(!file.exists()){
			return null;
		}
		String fileText=null;
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(file);
			fileText=new String(getArrayByte(fis));
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(fileText!=null && !fileText.equals("null")){
			return fileText;
		}else{
			file.delete();
		}
		return null;
	}
	
	public static byte[] getArrayByte(InputStream is){
		if(is!=null){
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			try {
				byte[] buf=new byte[1024];
				int len=0;
				while((len=is.read(buf))!=-1){
					baos.write(buf, 0, len);
				}
				return baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(baos!=null){
					try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	/**存入文件
	 * @author lizhen  2014-2-18上午10:47:28
	 * @param text
	 * @param filename
	 */
	public static void saveTextToFile(String text,String filename){
		if(text==null){
			return;
		}
		if(getFreeSpaceOnSd()<MAX_CACHE_SIZE){
			return;
		}
		Log.i("Tag", "text名===>"+filename);
		File dirFile=new File(getTextDirectory());
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File file=new File(dirFile, filename);
		try {
			file.createNewFile();
			FileOutputStream os=new FileOutputStream(file);
			byte[] buf=text.getBytes("UTF-8");
			os.write(buf,0,buf.length);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
