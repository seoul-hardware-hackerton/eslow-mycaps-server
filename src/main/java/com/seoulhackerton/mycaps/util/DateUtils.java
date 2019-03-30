
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class DateUtils {
	private static TimeZone timeZone;

	private static SimpleDateFormat yyyyFmt = new SimpleDateFormat("yyyy", Locale.KOREAN);
	private static SimpleDateFormat yyyyMMFmt = new SimpleDateFormat("yyyy.MM", Locale.KOREAN);
	public static final String FMT_YYYYMMDDHH 	= "yyyyMMddHHmm";
	public static final String FMT_YYYYMMDDHHSS = "yyyyMMddHHmmss";
	public static final String FMT_YYYY_MM_DD_HH_SS = "yyyy-MM-dd HH:mm:ss";

	
	static {		
		timeZone = TimeZone.getTimeZone("GMT+09:00");		
	}

	public static Date getDate() {
		Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);
		return cal.getTime();
	}

	public static Date getDate(long offset) {
		Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);
		cal.setTime(new Date(cal.getTime().getTime() + (offset * 1000)));
		return cal.getTime();
	}

	public static Date getDate(Date date, long offset) {
		return new Date(date.getTime() + (offset * 1000));
	}

	public static String getDateString(String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		simpleFormat.setTimeZone(timeZone);
		return simpleFormat.format(getDate());
	}

	public static String getDateString() {
		return getDateString("yyyy-MM-dd HH:mm:ss");
	}

	public static long getDateLong(String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		simpleFormat.setTimeZone(timeZone);
		return Long.parseLong(simpleFormat.format(getDate()));
	}

	public static long getDateLong() {
		return getDateLong(FMT_YYYYMMDDHHSS);
	}

	public static long getDateLongS() {
		return getDateLong("yyyyMMddHHmmssSSS");
	}

	public static Date getDate(int year, int month, int day, int hour, int minute, int second) {

		GregorianCalendar cal = new GregorianCalendar(timeZone, Locale.KOREAN);
		cal.set(year, month - 1, day, hour, minute, second);
		return cal.getTime();
	}

	public static String dateToString(Date date, String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		simpleFormat.setTimeZone(timeZone);
		return simpleFormat.format(date);
	}

	public static String getDateToString(String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		Calendar now = Calendar.getInstance(timeZone, Locale.KOREAN);
		return simpleFormat.format(now.getTime());
	}

	public static Date stringToDate(String dateString, String format) throws ParseException {

		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		simpleFormat.setTimeZone(timeZone);
		return simpleFormat.parse(dateString);
	}

	public static int getWeekOfMonth(String dateString, String format) throws ParseException {
		Date dt = stringToDate(dateString, format);

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int n = cal.get(Calendar.WEEK_OF_MONTH);
		return n;
	}

	public static int getDayOfWeek(Date dt) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int n = cal.get(Calendar.DAY_OF_WEEK);
		return n;
	}

	public static long dateToLong(Date date, String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		simpleFormat.setTimeZone(timeZone);
		return Long.parseLong(simpleFormat.format(date));
	}

	public static Date longToDate(long dateLong, String format) throws ParseException {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREAN);
		simpleFormat.setTimeZone(timeZone);
		return simpleFormat.parse(Long.toString(dateLong));
	}

	public static String longToString(long dateLong, String format) throws ParseException {
		return dateToString(longToDate(dateLong, FMT_YYYYMMDDHHSS), format);
	}

	public static int getAfterDays(Date date1, Date date2) {
		return (int) ((date1.getTime() - date2.getTime()) / 86400000);
	}

	public static int getAfterSeconds(Date date1, Date date2) {
		return (int) ((date1.getTime() - date2.getTime()) / 1000);
	}

	public static int getAfterMilliSeconds(Date date1, Date date2) {
		return (int) (date1.getTime() - date2.getTime());
	}

	/**
	 * 해당 월의 첫 번째 날짜를 구한다.
	 * 
	 * @param year
	 * @param month
	 * @param format
	 * @return
	 */
	public static String getCurMonthFirstDate(String year, String month, String format) {

		Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);

		int curYear = Integer.parseInt(year);
		int curMonth = Integer.parseInt(month);

		cal.set(curYear, curMonth - 1, 1);
		int curMinDay = cal.getActualMinimum(Calendar.DATE);

		Date curDate = DateUtils.getDate(curYear, curMonth, curMinDay, 0, 0, 0);

		return DateUtils.dateToString(curDate, format);
	}

	/**
	 * 해당 월의 마지막 날짜를 구한다.
	 * 
	 * @param year
	 * @param month
	 * @param format
	 * @return
	 */
	public static int getCurMonthLastDate(int year, int month) {
		Calendar now = Calendar.getInstance();
		now.set(year, (month - 1), 1);
		return now.getActualMaximum(Calendar.DATE);
	}

	/**
	 * 현재 요일을 구한다.
	 * 
	 * @return
	 */
	public static String getDay() {
		Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);
		int curDay = cal.get(Calendar.DAY_OF_WEEK);
		String[] days = { "", "일", "월", "화", "수", "목", "금", "토" };
		return days[curDay];
	}

	/**
	 * 현재 요일을 숫자로 구한다.
	 * 
	 * @return
	 */
	public static int getIntDay() {
		Calendar cal = Calendar.getInstance(timeZone, Locale.KOREAN);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static boolean isPastDay(String date) {
		boolean result = false;		
		Date pdate = null;
		try {
			pdate = DateUtils.stringToDate(date, "yyyyMMddHHmmss");
		} catch (ParseException e) {
			e.getMessage().toString();
		}
		Date cdate = DateUtils.getDate();
		int days = DateUtils.getAfterDays(cdate, pdate);
		if (days > 3) {
			result = true;
		} else {
			result = false;
		}
		
		return result;
	}
	

	public static String getAfterYears(int year) {
		return getAfterYears(FMT_YYYYMMDDHH, year);
	}

	public static String getAfterYears(String format,int year) {
		return getAfterBeforeDate(format, Calendar.YEAR, year);
	}

	public static String getAfterMonths(int month) {
		return getAfterMonths(FMT_YYYYMMDDHH, month);
	}

	public static String getAfterMonths(String format, int month) {
		return getAfterBeforeDate(format, Calendar.MONTH, month);
	}
	
	public static String getAfterDays(int day) {
		return getAfterDays(FMT_YYYYMMDDHH, day);
	}

	public static String getAfterDays(String format, int day) {
		return getAfterBeforeDate(format, Calendar.DATE, day);
	}

	public static String getAfterHours(int hour) {
		return getAfterBeforeDate(FMT_YYYYMMDDHH, Calendar.HOUR_OF_DAY, hour);
	}
	
	public static String getAfterHours(String format, int hour) {
		return getAfterBeforeDate(format, Calendar.HOUR_OF_DAY, hour);
	}

	public static String getAfterMinute(int minute) {
		return getAfterBeforeDate(FMT_YYYYMMDDHH, Calendar.MINUTE, minute);
	}
	
	public static String getAfterMinute(String format, int minute) {
		return getAfterBeforeDate(format, Calendar.MINUTE, minute);
	}
	
	public static String getAfterSeconds(int seconds) {
		return getAfterBeforeDate(FMT_YYYYMMDDHHSS, Calendar.SECOND, seconds);
	}
	
	public static String getAfterSeconds(String format, int seconds) {
		return getAfterBeforeDate(format, Calendar.SECOND, seconds);
	}
	
	public static String getBeforeDays(int day) {
		return getBeforeDays(FMT_YYYYMMDDHH, day);
	}

	public static String getBeforeDays(String format, int day) {
		return getAfterBeforeDate(format, Calendar.DATE, -1 * day);
	}	
	
	public static String getBeforeMinute(int minute) {
		return getAfterBeforeDate(FMT_YYYYMMDDHH, Calendar.MINUTE, -minute);
	}

	public static String getBeforeMinute(String format, int minute) {
		return getAfterBeforeDate(format, Calendar.MINUTE, -minute);
	}
	
	public static String getBeforeSeconds(int minute) {
		return getAfterBeforeDate(FMT_YYYYMMDDHH, Calendar.SECOND, -minute);
	}
	
	public static String getBeforeSeconds(String format, int minute) {
		return getAfterBeforeDate(format, Calendar.SECOND, -minute);
	}
	
	public static String getAfterBeforeDate(String format, int calendar, int val) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREA);
		simpleFormat.setTimeZone(timeZone);
		Calendar now = Calendar.getInstance();
		now.add(calendar, val);
		return simpleFormat.format(now.getTime());
	}
	
	public static String getInputDateAfterDate(String format, Date date, int calendar, int val) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREA);
		simpleFormat.setTimeZone(timeZone);
		Calendar now = Calendar.getInstance();
		
		now.add(calendar, val);
		return simpleFormat.format(now.getTime());
	}

	public static String getyyyyMMddHHmmssSSS() {
		return getDateString("yyyyMMddHHmmssSSS");
	}

	public boolean isHoliday(Date date) {
		boolean isHoliday = false;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			isHoliday = true;
		}
		return isHoliday;
	}

	public static boolean isHoliday() {
		boolean isHoliday = false;

		Calendar cal = Calendar.getInstance();

		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			isHoliday = true;
		}
		return isHoliday;
	}

	/**
	 * description:시스템의 년을 취득합니다.
	 * 
	 * @return [yyyy]
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static Date getCurrentYear() throws ParseException {
		Calendar currCal = Calendar.getInstance();
		Date currYear = yyyyFmt.parse(yyyyFmt.format(currCal.getTime()));
		return currYear;
	}

	/**
	 * description:시스템의 년을 문자열로 취득합니다.
	 * 
	 * @return [yyyy]형식의 문자
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static String getCurrentYearAsString() throws ParseException {
		return yyyyFmt.format(getCurrentYear());
	}

	/**
	 * description:오늘 날짜를 문자열로 취득합니다.
	 * 
	 * @return Date
	 * @throws Exception
	 */
	public static String getCurrentDayOnlyAsString() {
		Calendar day = Calendar.getInstance();
		String currDay = String.valueOf(day.get(Calendar.DATE));
		return currDay;
	}

	/**
	 * description:오늘 날짜를 0을 포함한 문자열로 취득합니다.
	 * 
	 * @return Date
	 * @throws Exception
	 */
	public static String getCurrentDayOnlyAsZeroString() {
		Calendar day = Calendar.getInstance();
		String currDay = String.valueOf(day.get(Calendar.DATE));

		if (Integer.parseInt(currDay) < 10)
			currDay = "0".concat(currDay);

		return currDay;
	}

	/**
	 * description:시스템의 년월을 취득합니다.
	 * 
	 * @return Date
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static Date getCurrentMonth() throws ParseException {
		Calendar currCal = Calendar.getInstance();
		Date currMonth = yyyyMMFmt.parse(yyyyMMFmt.format(currCal.getTime()));
		return currMonth;
	}

	/**
	 * description:시스템의 년월을 문자열로 취득합니다.
	 * 
	 * @return [yyyy/MM]형식의 문자열
	 * @throws ParseException 
	 * @throws Exception
	 */
	public static String getCurrentMonthAsString() throws ParseException {
		return yyyyMMFmt.format(getCurrentMonth());
	}

	/**
	 * RSS 용 날짜 형식(Mon, 04 Feb 2013 14:03:45 GMT)
	 */
	public static String rss(Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
		sf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		return sf.format(date) + " GMT";
	}

	/**
	 * Calendar에서 YYYYMMDDH24MISSMMM 추출
	 * 
	 * @param c
	 *            Calendar
	 * @param isMillisecond
	 *            Millisecond 추가 여부
	 * @return YYYYMMDDH24MISSMMM
	 */
	public static String toDTime(Calendar c, boolean isMillisecond) {

		StringBuffer sb = new StringBuffer(17);

		/** 년 */
		if (c.get(Calendar.YEAR) < 10)
			sb.append('0');
		sb.append(c.get(Calendar.YEAR));

		/** 월 */
		if (c.get(Calendar.MONTH) + 1 < 10)
			sb.append('0');
		sb.append(c.get(Calendar.MONTH) + 1);

		/** 일 */
		if (c.get(Calendar.DAY_OF_MONTH) < 10)
			sb.append('0');
		sb.append(c.get(Calendar.DAY_OF_MONTH));

		/** 시 */
		if (c.get(Calendar.HOUR_OF_DAY) < 10)
			sb.append('0');
		sb.append(c.get(Calendar.HOUR_OF_DAY));

		/** 분 */
		if (c.get(Calendar.MINUTE) < 10)
			sb.append('0');
		sb.append(c.get(Calendar.MINUTE));

		/** 초 */
		if (c.get(Calendar.SECOND) < 10)
			sb.append('0');
		sb.append(c.get(Calendar.SECOND));

		/** MILLISECOND */
		if (isMillisecond) {
			int mil = c.get(Calendar.MILLISECOND);
			if (mil == 0) {
				sb.append("000");
			} else if (mil < 10) {
				sb.append("00");
			} else if (mil < 100) {
				sb.append("0");
			}

			sb.append(mil);
		}

		return sb.toString();
	}

	/**
	 * 특정 포맷의 날짜 문자열을 새로운 포맷의 문자열로 변환한다.
	 * 
	 * @param srcDate
	 *            날짜 문자열
	 * @param sourceFormat
	 *            원본 날짜 포맷
	 * @param targetFormat
	 *            변환할 날짜 포맷
	 * @return 특정 포맷의 날짜 문자열을 새로운 포맷의 문자열로 변환
	 */
	public static String replaceDateFormat(String srcDate, String sourceFormat, String targetFormat) {
		return replaceDateFormat(srcDate, sourceFormat, targetFormat, Locale.ENGLISH, Locale.ENGLISH);
	}

	/**
	 * 특정 포맷의 날짜 문자열을 새로운 포맷의 문자열로 변환한다. Ex.)
	 * System.out.println(replaceDateFormat("20150213", "yyyyMMdd", "yyyy.MM.dd
	 * (E)"), Locale.ENGLISH, Locale.ENGLISH); ==> 2015.02.13 (Fri)
	 * System.out.println(replaceDateFormat("20150213", "yyyyMMdd", "yyyy.MM.dd
	 * (E)"), Locale.KOREA, Locale.KOREA); ==> 2015.02.13 (금)
	 * 
	 * @param srcDate
	 *            날짜 문자열
	 * @param sourceFormat
	 *            원본 날짜 포맷
	 * @param targetFormat
	 *            타겟 날짜 포맷
	 * @param sourceLocale
	 *            원본 로케일
	 * @param targetLocale
	 *            타겟 로케일
	 * @return 새로운 날짜 문자열
	 * @since 2015. 2. 13.
	 */
	public static String replaceDateFormat(String srcDate, String sourceFormat, String targetFormat, Locale sourceLocale, Locale targetLocale) {
		String result = null;
		try {
			if (StringUtils.isNotEmpty(srcDate)) {
				SimpleDateFormat srcFormat = new SimpleDateFormat(sourceFormat, sourceLocale);
				SimpleDateFormat destFormat = new SimpleDateFormat(targetFormat, targetLocale);

				result = destFormat.format(srcFormat.parse(srcDate));
			} else {
				result = srcDate;
			}
		} catch (ParseException e) {
			e.getMessage().toString();
		}
		return result;
	}
	
	/**
	 * 문자열 데이트 체크
	 * 
	 * @param date
	 * @return
	 */
	public static boolean checkDate(String date) {
		boolean dateValidity = true;
	
		SimpleDateFormat df = new SimpleDateFormat(FMT_YYYYMMDDHHSS, Locale.KOREA); //20041102101244
		df.setLenient(false); 
		try {
			df.parse(date);
		} catch (ParseException pe) {
			dateValidity = false;
		} catch(IllegalArgumentException ae) {
			dateValidity = false;
		}
	
		return dateValidity;
	}
}
