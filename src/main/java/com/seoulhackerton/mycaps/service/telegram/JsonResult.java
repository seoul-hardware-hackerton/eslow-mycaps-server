
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

package com.seoulhackerton.mycaps.service.telegram;

import java.io.Serializable;

public class JsonResult implements Serializable {

	private static final long serialVersionUID = 1919432184578449738L;

	private int resultCode;
	
	private String redirectUrl;
	
	private Object data;
	
	public JsonResult() {}
	
	public JsonResult(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public JsonResult(int resultCode, String redirectUrl) {
		this.resultCode = resultCode;
		this.redirectUrl = redirectUrl;
	}
	
	public JsonResult(int resultCode, String redirectUrl, Object data) {
		this.resultCode = resultCode;
		this.redirectUrl = redirectUrl;
		this.data = data;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
