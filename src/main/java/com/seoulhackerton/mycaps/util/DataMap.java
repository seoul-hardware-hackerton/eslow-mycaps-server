
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DataMap extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 229580443150395728L;
    private String sortKey = null;
    private int sortOrder = 1; //
    private boolean sortType = true; //

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DataMap() {
        super();
    }

    public DataMap(HashMap<String, Object> map) {
        super(map);
    }

    public DataMap(String value) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        String strValue = value.substring(1, value.length() - 1);
        for (String c : strValue.split(",")) {
            String[] m = c.split("=");
            map.put(m[0].trim(), m.length == 1 ? "" : m[1].trim());
        }
        this.putAll(map);
    }

    /**
     * <p>
     * 생성자 map인자를 복사하여 객체를 생성한다.
     * </p>
     *
     * @throws Exception
     */
    public DataMap(HttpServletRequest request) {
        try {
            this.putAll(CollectionUtils.requestDataMap(request));
        } catch (Exception e) {
            logger.info("Exception");
        }
    }

    public DataMap get(String[] keys) {
        DataMap data = new DataMap();

        for (String key : keys) {
            data.put(key, this.get(key));
        }

        return data;
    }

    /**
     * <p>
     * 해당 Key의 String Value를 반환한다.
     * </p>
     *
     * @param key
     * @return String Value
     * @throws Exception
     */
    public String getString(String key) {
        Object object = super.get(key);
        if (object != null) {
            if (object.getClass().isArray()) {
                return String.valueOf(((Object[]) object)[0]).trim();
            } else {
                return String.valueOf(object).trim();
            }
        } else {
            return "";
        }
    }

    /**
     * <p>
     * 해당 Key의 String Value를 반환한다.
     * </p>
     *
     * @param key
     * @return String Value
     * @throws Exception
     */
    public String[] getStringArr(String key) {
        Object object = super.get(key);
        if (object != null) {
            if (object.getClass().isArray()) {
                return (String[]) object;
            } else {
                return new String[]{String.valueOf(object).trim()};
            }
        } else {
            return null;
        }
    }

    /**
     * <p>
     * 해당 Key의 Integer Value를 반환한다.
     * </p>
     *
     * @param key
     * @return Integer Value
     * @throws Exception
     */
    public Integer getInteger(String key) {
        return getInt(key);
    }

    /**
     * <p>
     * 해당 Key의 Int Value를 반환한다.
     * </p>
     *
     * @param key
     * @return Int Value
     * @throws Exception
     */
    public int getInt(String key) {
        Object object = super.get(key);
        if (object == null) return 0;

        int result = -1;
        if (object instanceof String) {
            String str = ((String) object).trim();
            result = str.isEmpty() ? 0 : Integer.parseInt(str);
        } else if (object instanceof Number) {
            result = ((Number) object).intValue();
        } else if (object instanceof java.sql.Timestamp) {
            result = (int) ((java.sql.Timestamp) object).getTime();
        }
        return result;
    }

    /**
     * <p>
     * 해당 Key의 Long Value를 반환한다.
     * </p>
     *
     * @param key
     * @return long Value
     * @throws Exception
     */
    public long getLong(String key) {
        Object obj = super.get(key);
        if (obj == null) return 0;

        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).getTime();
        } else if (obj instanceof String) {
            return Long.parseLong((String) obj);
        }
        return 0;
    }

    /**
     * <p>
     * 해당 Key의 Date Value를 반환한다.
     * </p>
     *
     * @param key
     * @return Date Value
     * @throws Exception
     */
    public Date getDate(String key) {
        return (Date) (super.get(key));
    }

    /**
     * <p>
     * 해당 Key의 Date Value를 반환한다.
     * </p>
     *
     * @param key
     * @param format
     * @return Date Value
     * @throws Exception
     */
    public String getDateString(String key, String format) {
        return DateUtils.dateToString((getDate(key)), format);
    }


    public boolean getBoolean(String key) {
        String val = getString(key);
        if (val.isEmpty()) {
            return false;
        }
        return (val.equalsIgnoreCase("true"));
    }

    /**
     * <p>
     * 해당 Key의 Object Value를 반환한다.
     * </p>
     *
     * @param key
     * @return Object Value
     * @throws Exception
     */
    public Object getObject(String key) {
        return super.get(key);
    }

    /**
     * <p>
     * 해당 Key의 byte Value를 반환한다.
     * </p>
     *
     * @param key
     * @return byte Value
     * @throws Exception
     * @throws IOException
     */
    public byte[] getBinary(String key) {
        return (byte[]) (super.get(key));
    }

    /**
     * <p>
     * 해당 Key의 StreamToString Value를 반환한다.
     * </p>
     *
     * @param key
     * @return StreamToString Value
     * @throws Exception
     * @throws IOException
     */
    public String getStreamToString(String key) throws IOException {
        String result = null;
        InputStream in = null;

        StringBuffer sb = new StringBuffer();
        in = (InputStream) (super.get(key));
        byte buf[] = new byte[5];
        int readCount = 0;
        while ((readCount = in.read(buf)) != -1) {
            sb.append(new String(buf, 0, readCount));
        }
        result = sb.toString();

        return result;
    }

    /**
     * <p>
     * 해당 Key의 Object Value를 넣는다.
     * </p>
     *
     * @param key
     * @param value
     * @throws Exception
     */
    public void set(String key, Object value) {
        if (key == null || value == null) {
            return;
        }

        this.put(key, value);
    }

    /**
     * <p>
     * 해당 Key의 String Value를 넣는다.
     * </p>
     *
     * @param key
     * @param s
     */
    public void setString(String key, String s) {
        this.put(key, s);
    }

    /**
     * <p>
     * 해당 Key의 String Value를 넣는다.
     * </p>
     *
     * @param key
     * @param s
     */
    public void setStringArr(String key, String[] s) {
        this.put(key, s);
    }

    /**
     * <p>
     * 해당 Key의 int Value를 넣는다.
     * </p>
     *
     * @param key
     * @param i
     */
    public void setInt(String key, int i) {
        this.put(key, i);
    }

    /**
     * <p>
     * 해당 Key의 Double Value를 넣는다.
     * </p>
     *
     * @param key
     * @param d
     */
    public void setDouble(String key, double d) {
        Double db = new Double(d);
        this.put(key, db);
    }

    /**
     * <p>
     * 해당 Key의 Float Value를 넣는다.
     * </p>
     *
     * @param key
     * @param f
     */
    public void setFloat(String key, float f) {
        Float fl = new Float(f);
        this.put(key, fl);
    }

    /**
     * <p>
     * 해당 Key의 Long Value를 넣는다.
     * </p>
     *
     * @param key
     * @param l
     */
    public void setLong(String key, long l) {
        this.put(key, Long.valueOf(l));
    }

    /**
     * <p>
     * 해당 Key의 Short Value를 넣는다.
     * </p>
     *
     * @param key
     * @param st
     */
    public void setShort(String key, short st) {
        this.put(key, Short.valueOf(st));
    }

    /**
     * <p>
     * 해당 Key의 Boolean Value를 넣는다.
     * </p>
     *
     * @param key
     * @param bl
     */
    public void setBoolean(String key, boolean bl) {
        this.put(key, bl);
    }

    /**
     * <p>
     * 해당 Key의 BigDecimal Value를 넣는다.
     * </p>
     *
     * @param key
     * @param bigdecimal
     */
    public void setBigDecimal(String key, BigDecimal bigdecimal) {
        this.put(key, bigdecimal);
    }

    /**
     * @return
     */
    public String getSortKey() {
        String key = this.sortKey;
        if (key == null) {
            Iterator<?> iter = this.entrySet().iterator();
            if (iter.hasNext()) {
                key = (String) iter.next();
            }
        }
        return key;
    }

    public void setSortKey(String key, boolean typeString) {
        this.sortKey = key;
        this.sortType = typeString;
    }

    public void setSortAsc() {
        this.sortOrder = 1;
    }

    public void setSortDesc() {
        this.sortOrder = -1;
    }

    public int compareTo(Object o) {
        if (this.sortType) {
            String aKey = getSortKey();
            String bKey = ((DataMap) o).getSortKey();
            String aValue = null;
            String bValue = null;
            int order = sortOrder;

            try {
                aValue = this.getString(aKey);
                bValue = ((DataMap) o).getString(bKey);
            } catch (Exception e) {
                logger.info("Exception");
            }

            int result = order * aValue.compareTo(bValue);

            return result;
        } else {
            String aKey = getSortKey();
            String bKey = ((DataMap) o).getSortKey();
            Integer aValue = null;
            Integer bValue = null;
            int order = sortOrder;

            try {
                aValue = this.getInteger(aKey);
                bValue = ((DataMap) o).getInteger(bKey);
            } catch (Exception e) {
                logger.info("Exception");
            }

            int result = order * aValue.compareTo(bValue);

            return result;
        }
    }

    /**
     * @return
     */
    public Object[] toObject() {
        return this.values().toArray();
    }

    public boolean isEmpty(Object key) {
        return !this.isNotEmpty(key);
    }

    public boolean isNotEmpty(Object key) {
        boolean result = this.containsKey(key);

        if (!result) {
            return false;
        }

        Object object = this.get(key);

        if (object == null) {
            return false;
        }

        if (object instanceof String) {
            result = StringUtils.isNotEmpty(object);
        }

        return result;
    }

    public void defaultIfEmpty(String key, Object defaultObj) {
        if (isEmpty(key)) {
            this.set(key, defaultObj);
        }
    }
}
