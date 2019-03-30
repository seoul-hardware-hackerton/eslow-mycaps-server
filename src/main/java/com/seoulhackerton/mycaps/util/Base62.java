
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

public class Base62 {
	private static final String BASEDIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String toBase62(int decimalNumber) {
		return fromDecimalToOtherBase(62, decimalNumber);
	}

	public static String toBase62(long decimalNumber) {
		return fromLongDecimalToOtherBase(62, decimalNumber);
	}

	public static int fromBase62(String base62Number) {
		return fromOtherBaseToDecimal(62, base62Number);
	}

	public static long LongfromBase62(String base62Number) {
		return fromOtherBaseToLongDecimal(62, base62Number);
	}

	private static String fromDecimalToOtherBase(int base, int decimalNo) {
		String result = decimalNo == 0 ? "0" : "";
		int decimalNum = decimalNo;
		while (decimalNum != 0) {
			int mod = decimalNum % base;
			result = BASEDIGITS.substring(mod, mod + 1).concat(result);
			decimalNum = decimalNum / base;
		}
		return result;
	}
	
	private static String fromLongDecimalToOtherBase(int base, long decimalNo) {
		String result = decimalNo == 0 ? "0" : "";
		long decimalNum = decimalNo;
		while (decimalNum != 0) {
			long mod = decimalNum % base;
			result = BASEDIGITS.substring((int) mod, (int) mod + 1).concat(result);
			decimalNum = decimalNum / base;
		}
		return result;
	}

	private static int fromOtherBaseToDecimal(int base, String number) {
		int result = 0;
		for (int pos = number.length(), multiplier = 1; pos > 0; pos--) {
			result += BASEDIGITS.indexOf(number.substring(pos - 1, pos)) * multiplier;
			multiplier *= base;
		}
		return result;
	}
	
	private static long fromOtherBaseToLongDecimal(int base, String number) {
		long result = 0L;
		long multiplier=1;
		for (int pos = number.length(); pos > 0; pos--) {
			result += BASEDIGITS.indexOf(number.substring(pos - 1, pos)) * multiplier;
			multiplier *= base;
		}
		return result;
	}
}