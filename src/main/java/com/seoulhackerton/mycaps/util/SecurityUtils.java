/*
 * G 서비스 관리서버 version 1.0
 *
 *  Copyright ⓒ 2016 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 */
package com.seoulhackerton.mycaps.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;

public class SecurityUtils {
	
	private static final String[] ENCRYPT_ALGORITHM = {"AES"};
	private static final String[] ENCRYPT_MODE = {"ECB"};
	private static final String[] ENCRYPT_PADDING = {"PKCS5Padding","NoPadding"};
	//private static final String ENCRYPT_KEY = "com.kt.gbox!@#$%";	/* 16 bytes = 128 bits */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static String convertEncording(String text, String sourceEncodingType, String targetEncodingType) {		
		String result = null;
		
		if (text == null || "".equals(text)
				|| sourceEncodingType == null || "".equals(sourceEncodingType)
				|| targetEncodingType == null || "".equals(targetEncodingType)) {
			throw new SecurityException(
					"Some Parameters are null. text = [" + text + "], "
							+ "sourceEncodingType = [" + sourceEncodingType + "], " 
							+ "targetEncodingType = [" + targetEncodingType + "]");
		}
		
		if (sourceEncodingType.equals(targetEncodingType)) {
			throw new SecurityException(
					"sourceEncodingType and targetEncodingType is same. "
							+ "sourceEncodingType = [" + sourceEncodingType	+ "], " 
							+ "targetEncodingType = [" + targetEncodingType + "]");
			
		} else if ("BASE64".equals(sourceEncodingType) && "HEX".equals(targetEncodingType)) {
			result = Hex.encodeHexString(Base64.decodeBase64(text.getBytes()));
		} else if ("HEX".equals(sourceEncodingType) && "BASE64".equals(targetEncodingType)) {
			try {
				result = Base64.encodeBase64String(Hex.decodeHex(text.toCharArray()));
			} catch (DecoderException e) {
				throw new SecurityException(e);
			}			
		} else {
			throw new SecurityException(
					"Unknown encoding type : "
							+ "sourceEncordingType = [" + sourceEncodingType + "] "
							+ "targetEncodingType = ["	+ targetEncodingType + "]. Reserved way is [HEX] to [BASE64] or [BASE64] to [HEX].");
		}		
		return result;
	}
	
	public static String getSHA2(String text) {
		return getSHA2(text, "BASE64");
	}
	
	public static String getSHA2(String text, String encodingType) {
		MessageDigest digest = null;
		String result = null; 
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.getMessage().toString();
			throw new SecurityException(e);
		}
		
		if (digest == null)
			return text;
		
		try {
			digest.update(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.getMessage().toString();
			throw new SecurityException(e);
		}
		
		byte[] rawData = digest.digest();
		
		if("BASE64".equals(encodingType)) {
			result = Base64.encodeBase64String(rawData);
		} else if("HEX".equals(encodingType)) {
			result = Hex.encodeHexString(rawData);
		} else {
			throw new SecurityException("Unknown encoding type : " + encodingType + ". Reserved type is [HEX] or [BASE64].");
		}		
		return result;
	}
	
	public static String getMD5(String text) {
		return getMD5(text, "BASE64");
	}
	
	public static String getMD5_HEX(String text) {
		return getMD5(text, "HEX");
	}
	
	public static String getMD5(String text, String encodingType) {
		String result = null;
		byte[] rawData = null;
		MessageDigest messageDigest = null;
		
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			rawData = messageDigest.digest(text.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.getMessage().toString();
			throw new SecurityException(e);
		} catch (UnsupportedEncodingException e) {
			e.getMessage().toString();
			throw new SecurityException(e);
		}
		
		if("BASE64".equals(encodingType)) {
			result = Base64.encodeBase64String(rawData);
		} else if("HEX".equals(encodingType)) {
			result = Hex.encodeHexString(rawData);
		}

		return result;
	}
	
	public static String makeCouponSerialNumber(int campaignID, int length) {
		StringBuffer couponID = new StringBuffer();
		Random r = new Random();
		couponID.append(campaignID);
		String szCampaignID = Integer.toString(campaignID);
		
		for (int i = 0; i < length - szCampaignID.length(); i++) {
			if(0 == r.nextInt(2)) {
				int number = r.nextInt(10);
				couponID.append(number);
			} else {
				int ranInt26 = r.nextInt(26);
				
				// I, O 문제 스킵
				if(8 == ranInt26 || 14 == ranInt26) {
					ranInt26++;
				}
				
				char alphabet = (char)(ranInt26 + 65);
				couponID.append(alphabet);
			}
		}
		
		// 랜덤 값이 영문자 혹은 숫자로만 나올 경우 재실행!
		Pattern pNum = Pattern.compile("[\\d]+");
		Pattern pLEng = Pattern.compile("[A-Z]+");
		if (pNum.matcher(couponID.toString()).matches() || pLEng.matcher(couponID.toString()).matches()) {
			return makeCouponSerialNumber(campaignID, length);
		}
		
		return couponID.toString();
	}
	
	/*
	 * 인터레스트 미 암호화 모듈
	 */
	public static String getTransform(int algorithmMode, int encryptMode, int paddingMode) {
		return ENCRYPT_ALGORITHM[algorithmMode]+"/"+ENCRYPT_MODE[encryptMode] + "/"+ENCRYPT_PADDING[paddingMode];
	}
	
	public static String getEncrypt(String decryptValue, String encryptKey) {
		return getEncrypt(decryptValue, 0, 0, 0, 0, encryptKey);
	}
	
	public static String getEncrypt(String decryptValue, int encodingMode, String encryptKey) {
		return getEncrypt(decryptValue, 0, 0, 0, encodingMode, encryptKey);
	}
	
	public static String getEncrypt(String decryptValue, int algorithmMode, int encryptMode, int paddingMode, int encodingMode, String encryptKey) {
		String sEncryptValue = null;
		try {
			SecretKeySpec spec = new SecretKeySpec(encryptKey.getBytes(), ENCRYPT_ALGORITHM[algorithmMode]);

			Cipher cipher = Cipher.getInstance(getTransform(algorithmMode, encryptMode, paddingMode));
			cipher.init(Cipher.ENCRYPT_MODE, spec);
			
			if (encodingMode == 1) {
				sEncryptValue = byteArrayToString(cipher.doFinal(decryptValue.getBytes("UTF-8")));
			} else {
				
				sEncryptValue = Base64.encodeBase64String((cipher.doFinal(decryptValue.getBytes("UTF-8"))));
			}
		} catch (Exception e) {
			e.getMessage().toString();
		}

		return sEncryptValue;
	}
	
	/**
	 * 
	 * 2017-09-15 수정 
	 * 
	 * @param decryptValue
	 * @param algorithmMode
	 * @param encryptMode
	 * @param paddingMode
	 * @param encodingMode
	 * @param encryptKey
	 * @param iv
	 * @return
	 */
	public static String getEncrypty(String decryptValue, int algorithmMode, int encryptMode, int paddingMode, int encodingMode, String encryptKey, String iv) {
		if(StringUtils.isEmpty(decryptValue)){
			return decryptValue;
		}
		
		String sEncryptValue = null;
		try {
			SecretKeySpec spec = new SecretKeySpec(encryptKey.getBytes(), ENCRYPT_ALGORITHM[algorithmMode]);

			Cipher cipher = Cipher.getInstance(getTransform(algorithmMode, encryptMode, paddingMode));
			
			switch (encryptMode) {
			case 1:
				cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(iv.getBytes("UTF-8")));
				break;
			default:
				cipher.init(Cipher.ENCRYPT_MODE, spec);
				break;
			}
			
			if (encodingMode == 1) {
				sEncryptValue = byteArrayToString(cipher.doFinal(decryptValue.getBytes("UTF-8")));
			} else {				
				sEncryptValue = Base64.encodeBase64String((cipher.doFinal(decryptValue.getBytes("UTF-8"))));
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		}

		return sEncryptValue;
	}
	
	
	public static String getDecrypt(String encryptValue, String encryptKey) {
		return getDecrypt(encryptValue, 0, 0, 0, 0, encryptKey);
	}
	
	public static String getDecrypt(String encryptValue, int encodingMode, String encryptKey) {
		return getDecrypt(encryptValue, 0, 0, 0, encodingMode, encryptKey);
	}

	public static String getDecrypt(String encryptValue, int algorithmMode, int encryptMode, int paddingMode, int encodingMode, String encryptKey) {
		String sDecryptValue = null;

		try {
			SecretKeySpec skeySpec = new SecretKeySpec(encryptKey.getBytes(), ENCRYPT_ALGORITHM[algorithmMode]);
			Cipher cipher = Cipher.getInstance(getTransform(algorithmMode, encryptMode, paddingMode));
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			
			if(encodingMode == 1) {
				sDecryptValue = new String(cipher.doFinal(stringToByteArray(encryptValue)), "UTF-8");	
			} else {
				sDecryptValue = new String(cipher.doFinal(Base64.decodeBase64(encryptValue)), "UTF-8");
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		}

		return sDecryptValue;
	}
	
	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}
	
	public static String byteArrayToString(byte hexByte[])  {
		StringBuffer strbuf = new StringBuffer(hexByte.length * 2);

		for(int i=0; i<hexByte.length; i++) {
			if(((int) hexByte[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			
			strbuf.append(Long.toString((int) hexByte[i] & 0xff, 16));
		}

		return strbuf.toString();
		
		// BigInteger를 사용해 hex를 byte[] 로 바꿀 경우 음수 영역의 값을 제대로 변환하지 못하는 문제가 있다.
		// return new java.math.BigInteger(hexByte).toString(16);
	}
	
	public static byte[] stringToByteArray(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0) {
			return new byte[]{};
		}

		byte[] bytes = new byte[hexString.length() / 2];
		
		for (int i=0; i<hexString.length(); i += 2) {
			byte value = (byte)Integer.parseInt(hexString.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}
		
		return bytes;
		
		// BigInteger를 사용해 hex를 byte[] 로 바꿀 경우 음수 영역의 값을 제대로 변환하지 못하는 문제가 있다.
		// return new java.math.BigInteger(hexString, 16).toByteArray();
	}
	
//	public static void main(String[] args) throws ParseException{
//	}
	
}