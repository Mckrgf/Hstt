package com.example.yb.hstt.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	
//	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	protected static MessageDigest md = null;
	
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

	// 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
	
	public static String encrypt(String plaintext) {
        return byteToString(md.digest(plaintext.getBytes()));

	}
	
	/**
	 * 计算文件MD5值
	 * @param filePath
	 * @return
	 */
	public static String calculationFileMD5(String filePath) {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		FileChannel ch = null;
		try {
			ch = new FileInputStream(file).getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			md.update(byteBuffer); 
			return byteToString(md.digest()); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ch != null)
					ch.close();
			} catch (IOException e1){}
		}
		return null;
	}
}