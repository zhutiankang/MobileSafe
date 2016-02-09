package com.nl.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * md5加密方法
	 * 
	 * @param password
	 * @return
	 */
	public static String md5Password(String password) {
		try {
			// 得到一个信息摘要器
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// 把每一个byte与0xff做一个与运算
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// b&0xfff,不是标准的加密，称为加盐，只要保证可以解出就可
				// 转换为十六进制
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}
			//得到的是标准的md5加密后的结果
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}
}
