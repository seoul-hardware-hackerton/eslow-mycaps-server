
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

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils{

	public static boolean isNumber(String s) {
		return s.matches("[0-9]+");	
	}  

    public static String format(double i, int j) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(j);
        return nf.format(i);
    }
	
	public static String toMoneyFormat(String s) {	
		if (s == null || "".equals(s))
			return "";
		
		DecimalFormat df = new DecimalFormat("###,###,###");
		return df.format(Double.parseDouble(s));
	}	
	
	public static String toMoneyFormat(int i) {
		return toMoneyFormat(Integer.toString(i));
	}
	
}
