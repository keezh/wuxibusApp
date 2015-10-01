package com.wuxibus.app.util;

import com.wuxibus.app.constants.AllConstants;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author ngh AES128 算法
 *
 *         CBC 模式
 *
 *         PKCS7Padding 填充模式
 *
 *         CBC模式需要添加一个参数iv
 *
 *         介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding
 *         没有什么区别 要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
 *         
 *         
		 * pkcs7padding 的算法
			先拿字符串长度除以16,如果有余数,把这个余数值作为 ascii 码转换成字符，添加到原来字符串的后面，有几个余数就这样添几个字符
			比如你 s 的余数 3, 那就是 s = s + chr(3) + chr(3) + chr(3)
		 
 *         
 *         key = "12345678\0\0\0\0\0\0\0\0"
填充 iv = "0000000000000000"
content="test"

ios 例子：
key = "12345678\0\0\0\0\0\0\0\0"
iv = "0000000000000000"
raw = "test"
加密后输出16进制应该是
e7 21 3a 50 2d f3 f2 a0 39 73 7d 19 98 e9 d6 ff
 */
public class AES {
	// 算法名称
	final String KEY_ALGORITHM = "AES";
	// 加解密算法/模式/填充方式
	//final String algorithmStr = "AES/CBC/PKCS7Padding";
	//bug，因为是自己实现了7padding算法，所以参数是NoPadding，被这个问题困扰了很久
	final String algorithmStr = "AES/CBC/NoPadding";
	
	//
	private Key key;
	private Cipher cipher;
	boolean isInited = false;

//	byte[] iv = { 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30,
//			0x30, 0x30, 0x30, 0x30, 0x30, 0x30 };
	

	public void init(byte[] keyBytes) {

		// 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
		
		//int base = 16;
//		if (keyBytes.length % base != 0) {
//			int groups = keyBytes.length / base
//					+ (keyBytes.length % base != 0 ? 1 : 0);
//			byte[] temp = new byte[groups * base];
//			Arrays.fill(temp, (byte) 0);
//			System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
//			keyBytes = temp;
//		}

		// 初始化
		Security.addProvider(new BouncyCastleProvider());
		// 转化成JAVA的密钥格式
		key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
		try {
			// 初始化cipher
			cipher = Cipher.getInstance(algorithmStr, "BC");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 加密方法
	 *
	 * @param content
	 *            要加密的字符串
	 * @param keyBytes
	 *            加密密钥
	 * @return
	 */
	public byte[] encrypt(byte[] content, byte[] keyBytes) {
		//add by kee
		content = paddingContent(content);

		byte[] encryptedText = null;
		init(keyBytes);
		//iv = "0000000000000000".getBytes();
		byte []iv = AllConstants.IV.getBytes();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

			encryptedText = cipher.doFinal(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedText;
	}


	/**
	 * pkcs7padding 的算法
	 先拿字符串长度除以16,如果有余数,如该加密的字符为test,则byte b= 16 - 余数 。则 加密后byte[] = {'t','e','s','t',ox0c,oxoc,
	 ox0c,,,,,,},12个ox0c
	 * @param content
	 * @return
	 */
	public static byte[] paddingContent(byte[] content){
		int base = 16;

		byte []result  = null;
		if(content.length % base != 0){
			int mode = content.length % base;
			int size = base - mode;
			result = new byte[content.length + size];

			System.arraycopy(content, 0, result, 0, content.length);


			byte [] temp = new byte[base - mode];
			for(int i = 0;i< base - mode;i++){
				temp[i] = (byte)(base - mode);
			}
			//把数组最后的temp.length个元素补齐，ox(base - mode)
			System.arraycopy(temp, 0, result, content.length, temp.length);

		}else{
			result = content;
		}

		return result;
	}

	/**
	 * 解密方法
	 *
	 * @param encryptedData
	 *            要解密的字符串
	 * @param keyBytes
	 *            解密密钥
	 * @return
	 */
	public byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
		byte[] encryptedText = null;
		init(keyBytes);
		System.out.println("IV：" + AllConstants.IV);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(AllConstants.IV.getBytes()));
			encryptedText = cipher.doFinal(encryptedData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedText;
	}
}
