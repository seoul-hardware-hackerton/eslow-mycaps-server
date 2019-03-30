
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class CollectionUtils {

    /**
     * 파라미터를 DataMap key , value로 담는다.
     * @param req
     * @return
     * @throws Exception
     */
    public static DataMap requestDataMap( HttpServletRequest req ) {
        return requestDataMap(req , false);
    }
    
    /**
     * 파라미터를 HashMap key , value로 담는다.
     * @param req
     * @return
     * @throws Exception
     */
    public static HashMap<String, Object> requestDataHashMap( HttpServletRequest req ) {
        return requestDataHashMap(req , false);
    }
    
    /**
     * 파라미터를 DataMap key , value로 담는다.
     * @param req
     * @return
     * @throws Exception
     */
    public static DataMap requestDataMap(HttpServletRequest req , boolean isEscape ) {
        
        Enumeration<?> enume = req.getParameterNames();
        DataMap map = new DataMap();
        while( enume.hasMoreElements() ){
            String key = enume.nextElement().toString();
            String[] paramValues = req.getParameterValues(key);
            
            if (paramValues.length == 1){
                String value =  isEscape ? StringUtils.escapeString( paramValues[0] ) : paramValues[0];
                
                // integer -2,147,483,648 ~ 2,147,483,647 11개 문자열 이하인 경우 확인하여 숫자인경우 setInt.
//                if( value != null && value.length() < 11 && MStringUtil.isNumber(value) ){ 
//                	map.setInt( key , Integer.parseInt(value) );
//                } else {
//                	map.setString( key , value );
//                }
                
                map.setString( key , value );
                
            } else {
                map.setStringArr( key, paramValues);
            }
            
        }
        return map;
    }
    
    /**
     * 파라미터를 DataMap key , value로 담는다.
     * @param req
     * @return
     * @throws Exception
     */
    public static HashMap<String, Object> requestDataHashMap(HttpServletRequest req , boolean isEscape ) {
        Enumeration<?> enume = req.getParameterNames();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while( enume.hasMoreElements() ){
            String key = enume.nextElement().toString();
            String[] paramValues = req.getParameterValues(key);
            
            if (paramValues.length == 1){
                String value =  isEscape ? StringUtils.escapeString( paramValues[0] ) : paramValues[0] ; 
                map.put( key , value );
            } else {
                map.put( key, paramValues);
            }
            
        }
        return map;
    }
    
	/**
	 * 기본 1개의 key,value를 셋팅하여
	 * DataMap 를 생성해준다.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static DataMap makeStringDataMap(String key, String value) {
		DataMap result = new DataMap();
		result.put(key, value);
		return result;
	}

	/**
	 * Map을 List로 변경 해준다.
	 * 
	 * @param map
	 * @return
	 */
	public static List<Object> mapToList(Map<String, Object> map) {
		List<Object> result = new ArrayList<Object>();
		Object[] keyArr = map.keySet().toArray();
		for (int i = 0; i < map.size(); i++) {
			result.add(map.get(keyArr[i]));
		}
		return result;
	}
	
	
	/**
	 * key를 대소문자로 변형하여 reSet
	 * 
	 * @param map
	 * @return
	 */
	public static DataMap setkeyLowerNUpper(DataMap map) {
		DataMap temp = (DataMap) map.clone();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			temp.set(key.trim().toLowerCase(), map.get(key));
			temp.set(key.trim().toUpperCase(), map.get(key));
		}
		return temp;
	}
	
	/**
     * pes key를 소문자로 변형하여 reSet
     * 
     * @param map
     * @return
     */
    public static List<DataMap> setkeyLower(List<DataMap> map) {
        List<DataMap> resultLower =  new ArrayList<DataMap>();
        for(DataMap metaItem: map) {
            DataMap reset=  setkeyLower(metaItem);
            resultLower.add(reset);
        }
        
        return resultLower;
    }
    
	/**
     * pes key를 소문자로 변형하여 reSet
     * 
     * @param map
     * @return
     */
    public static DataMap setkeyLower(DataMap map) {
        DataMap temp = new DataMap();
        Iterator<String> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            temp.set(key.trim().toLowerCase(), map.get(key));
        }
        return temp;
    }
    
    
}