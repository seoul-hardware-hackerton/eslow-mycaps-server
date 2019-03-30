
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

public class SecurityException extends RuntimeException {

	private static final long serialVersionUID = 2811322985571355482L;
	
	public SecurityException() {}
	
	public SecurityException(String message) {
		super(message);
	}
	
	public SecurityException(Throwable throwable) {
		super(throwable);
	}
	
	public SecurityException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
