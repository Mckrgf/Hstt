package com.example.yb.hstt.Utils;

import com.google.gson.Gson;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;

public class SignUtils {
	private static final String ALGORITHM = "RSA";

	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
	public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
	
	private static final String DEFAULT_CHARSET = "UTF-8";

	public static String RSA_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALMT1kyPplJ4/dXvYRA+2xZIykzP++y2/zVsLi2XyDbBmkHTLTICrNMqRDt8AG91KYvjLKuJChtR9In1KDpnNF/SNuyz7KRTAO+9X1/sp7zFV+iNWQa/nvuHKzI27oyYaTx9+FOyl+eUoy+Im1OuhL0eiVVlM/MljRqmRPEV0HxxAgMBAAECgYEAnA51elLVBpoZ2w248IdWNZ1JbpbxW+A9lzinJWRYPh+JZkO55xMduQfkXlI5rufTROy8SOArxpMtH+xdGkhGAqRMLyRyT/KMvnnzmTEXrkFqyYD36syD2I6m6wu3OMfb9gqm6bw52nEaCsd9CgfSsfBxmDdyiK4qm7GW8Zl9xoECQQDq9hGA5wMbQsAV9cGZztOfG9fu8LyWpUWc2UsGhm/ZF70JfnHEqwbtcbHmtVLN1gbvoOrHVL29nL/hP8JziEXdAkEAwxzELL12BXJyUoYvLKc2a95sS1nJHkJyTyioPn0/yTrqdy3gex+WsqXxz1VEqnpQL2ThoRZAjh1HK+y/ZW55pQJATQyCMuYOY7sg6FKduQVU6iEkT0uMXE44JLYw3yPou6UILXvUbOy0qdqVvxUI4UzlG2GGe/Uy/2HOnlvdNSuj1QJASdR1yzQ6F5+R8PZDnZL8fd7hfbXQ67lbBectms/MNjosBMMAYba3UIyIrtrmK9gyxWxYEJuZ++zKRK3GUvx13QJAA7PaQQONacK2duK53CXnDD2EY1E90bEDuzkKCumwBJCgi+C8mzNCwK+ycBSd7RPqLxpvrDy+ltshGdvH5D7Ugg==";

	public static String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzE9ZMj6ZSeP3V72EQPtsWSMpMz/vstv81bC4tl8g2wZpB0y0yAqzTKkQ7fABvdSmL4yyriQobUfSJ9Sg6ZzRf0jbss+ykUwDvvV9f7Ke8xVfojVkGv577hysyNu6MmGk8ffhTspfnlKMviJtTroS9HolVZTPzJY0apkTxFdB8cQIDAQAB";
	
	
	
	/**
	 * 签名
	 * @param content	要签名内容
	 * @return
	 */
	public static String rsaSign(String content){
		return rsaSign(content,RSA_PRIVATE_KEY);
	}
	
	/**
	 * 签名
	 * @param content		要签名的字符串
	 * @param privateKey	私钥
	 * @return
	 */
	public static String rsaSign(String content, String privateKey) {
		try {
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(new PKCS8EncodedKeySpec(MyBase64.decode(privateKey)));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);
			
			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();
			return MyBase64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 验签
	 * @param content		要验证的参数
	 * @param sign				签名
	 * @return
	 */
	public static boolean rsaCheck(String content, String sign){
		if(isNullOrEmpty(sign) || isNullOrEmpty(content)){
			return false;
		}
		return rsaCheck(content,sign,RSA_PUBLIC_KEY);
	}
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0; // string.isEmpty() in Java 6
	}
	
	/**
	 * 验签
	 * @param content		要验证的参数
	 * @param sign				签名
	 * @param publicKey			公钥
	 * @return
	 */
	public static boolean rsaCheck(String content, String sign, String publicKey)
			throws RuntimeException {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(MyBase64.decode(publicKey)));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			return signature.verify(MyBase64.decode(sign));
		} catch (Exception e) {
			throw new RuntimeException("RSAcontent = " + content + ",sign="
					+ sign + ",charset = " + DEFAULT_CHARSET, e);
		}
	}
	
	/**
	 * 获取要签名的数据项字符串
	 * 
	 * @param sortedParams
	 * @return
	 */
	public static String getSignContent(Map<String, Object> sortedParams) {
		return new Gson().toJson(sortedParams);
	}
	
	/**
     * 公钥解密
     * 
     * @param content    	待解密内容
     * @return 				明文内容
     * @throws RuntimeException
     */
    public static String rsaDecrypt(String content) throws RuntimeException {
        try {
        	X509EncodedKeySpec keySpec = new X509EncodedKeySpec(MyBase64.decode(RSA_PUBLIC_KEY));
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            PublicKey keyPublic = kf.generatePublic(keySpec);
            // 数据解密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keyPublic);
            
            return new String(cipher.doFinal(MyBase64.decode(content)),DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("EncodeContent = " + content + ",charset = " + DEFAULT_CHARSET, e);
        }
    }
	
    
    /**
     * 私钥加密
     * @param content	待加密明文
     * @return			加密后密文
     */
    public static String rsaEncrypt(String content) {
        try {
        	// 得到私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(MyBase64.decode(RSA_PRIVATE_KEY));
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            PrivateKey keyPrivate = kf.generatePrivate(keySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
            return MyBase64.encode((cipher.doFinal(content.getBytes(DEFAULT_CHARSET))));
        } catch (Exception e) {
            throw new RuntimeException("EncodeContent = " + content + ",charset = " + DEFAULT_CHARSET, e);
        }
    }
	
	/**
	 * 检查指定的字符串列表是否不为空。
	 */
	protected static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = false;
		} else {
			for (String value : values) {
				result &= !isEmpty(value);
			}
		}
		return result;
	}
	
	
	
	
	protected static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
	
	public static void main(String[] args) {
//		String sign = SignUtils.rsaSign("hehe", RSA_PRIVATE_KEY);
//		System.out.println("sign : " + sign);
//		
		String c = "{\"id\":\"cb112ff25859494784a8d46bd2d62bc5\",\"username\":\"app\",\"name\":\"掌机\",\"sessionid\":\"b3d250e7b9fd4ac9a1989060595d2b66\",\"mobileLogin\":true}";
//		String s = "jb9Qin6hlAYOzGvfP6U8SFL5OKo8YNscwDuCWj5C/12miKQr17/ebaA6tGHEgJTewzE+m2AxhhlZR60Bvq4LN6sahZqw0bHHJUaEu8Wseok/rGJSFMFTOzX58mPLhNiBNxuFOYd6ZUVBVP2Jrg18c0/tl/jwATxs32wHJXlWKrQ=";

//		String sign = SignUtils.rsaSign(c);
//
//		boolean checked = SignUtils.rsaCheck(c, sign);
//		System.out.println("check : " + checked);
		
//		String entry = rsaEncrypt("hehe");
//		System.out.println(entry);
//		
//		String decry = rsaDecrypt(entry);
//		System.out.println(decry);
		
//		String s  ="{\"gps_lng\":\"116.39112\",\"asset_no\":\"201612271234567\",\"gps_lat\":\"39.116965\",\"term_addr\":\"9105\",\"area_code\":\"0608\"}";
		//签名
		String sign = SignUtils.rsaSign(c);
        //加密
		String rsaE = rsaEncrypt(c);
		//解密
		String rsaD = rsaDecrypt(rsaE);
        //验证签名
		System.out.println(rsaCheck(rsaD,sign));
	}

}
