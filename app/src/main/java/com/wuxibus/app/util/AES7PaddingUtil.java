package com.wuxibus.app.util;


import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.wuxibus.app.InitApplication;
import com.wuxibus.app.constants.AllConstants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhongkee on 15/8/29.
 *  AES aes = new AES();
 //        [nonce] => C9A5BC60-68B7-4211-B115-BDAFD0D4FB32
 //                [m] => get_start_pic
 String aesParams = "{\"nonce\":\"C9A5BC60-68B7-4211-B115-BDAFD0D4FB32\",\"m\":\"get_start_pic\"}";
 byte  [] aesEncode = aes.encrypt(aesParams.getBytes(), AllConstants.AESKey.getBytes());

 String base64Encode = Base64.encodeToString(aesEncode, Base64.DEFAULT);

 InitApplication.appLog.i("base64:"+base64Encode);
 //InitApplication.appLog.i("base64:"+encodeStr);
 base64Encode = base64Encode.replace("\n","");


 try {
 base64Encode  = URLEncoder.encode(base64Encode, "UTF-8");
 } catch (UnsupportedEncodingException e) {
 e.printStackTrace();
 }

 &doctorwho=johnsmith&debug=1
以下测试连接
 http://api.wxbus.com.cn/api/?a=Android&
 b=fyA79Ie3p7ZkA4C2EepKHhrvPDXPwQ6DKrCnB%2Fuo9gNTBEVwhuu9qBZ47%2B2vDSLrqtS%2BEyEqgmE5%0AUFTQhWy%2BdpB4bqWfIFPTKGQYzX1o9Yk%3D%0A
 &doctorwho=johnsmith&debug=1

 */
public class AES7PaddingUtil {

    /**
     * 加密通道
     * @param map
     * @return
     */
    public static Map<String,String>  toAES7Padding(Map<String,String> map){

        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();
        //System.out.println("uuidStr = "+uuidStr);

        //map.put("nonce","C9A5BC60-68B7-4211-B115-BDAFD0D4FB32");//测试nonce
        map.put("nonce",uuidStr);
        String aesParams = JSON.toJSONString(map);
        AES aes = new AES();
        byte  [] aesEncode = aes.encrypt(aesParams.getBytes(), AllConstants.AESKey.getBytes());
        String base64Encode = Base64.encodeToString(aesEncode, Base64.DEFAULT);
        String encodeStr = "";
        try {
            encodeStr = URLEncoder.encode(base64Encode,AllConstants.charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String,String> aesMap = new HashMap<String,String>();
        aesMap.put("a","Android");
        aesMap.put("b",encodeStr);

        return  aesMap;

    }

    public static void main(String []argv){
        Map<String,String> map = new HashMap<String,String>();
        map.put("nonce","C9A5BC60-68B7-4211-B115-BDAFD0D4FB32");
        map.put("m","all_search");
        map.put("k","33");
        map = toAES7Padding(map);
        String jsonString = JSON.toJSONString(map);
        System.out.println(jsonString);
    }
}
