package com.sg.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.db.expression.IConfConstants;

public class Util {

	public static final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final String prefixKey = ".";

	public static final boolean equals(final Object left, final Object right) {
		return left == null ? right == null : ((right != null) && left.equals(right));
	}

	public static boolean inArray(Object element, Object[] arr) {
		if (arr == null) {
			return false;
		}
		for (Object o : arr) {
			if (o.equals(element)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNullorEmpty(String text) {
		return text == null || text.length() == 0;
	}

	public static Object getTypeValue(String inputValue, String type) {
		if (inputValue == null || inputValue.equalsIgnoreCase("null")) {
			return null;
		} else {
			try {
				if (type.equals(IConfConstants.FIELD_OBJECTID)) {
					if (inputValue.length() == 0)
						return null;
					return new ObjectId(inputValue.toString());
				} else if (type.equals(IConfConstants.FIELD_STRING)) {
					return inputValue;
				} else if (type.equals(IConfConstants.FIELD_BOOLEAN)) {
					if (inputValue.length() == 0)
						return null;
					return IConfConstants.VALUE_TRUE.equalsIgnoreCase(inputValue);
				} else if (type.equals(IConfConstants.FIELD_DATE)) {
					if (inputValue.length() == 0)
						return null;
					return new SimpleDateFormat(IConfConstants.SDF_YYYY__MM__DD__HH__MM__SS).parseObject(inputValue);
				} else if (type.equals(IConfConstants.FIELD_DOUBLE)) {
					if (inputValue.length() == 0)
						return null;
					return Double.parseDouble(inputValue);
				} else if (type.equals(IConfConstants.FIELD_INTEGER)) {
					if (inputValue.length() == 0)
						return null;
					return Integer.parseInt(inputValue);
				} else if (type.equals(IConfConstants.FIELD_LONG)) {
					if (inputValue.length() == 0)
						return null;
					return Long.parseLong(inputValue);
				} else if (type.equals(IConfConstants.FIELD_ARRAY)) {
					if (inputValue.length() == 0)
						return null;
					return JSON.parse(inputValue);
				} else if(type.equals(IConfConstants.FIELD_REGEX)){
					Pattern pattern = Pattern.compile(inputValue,Pattern.CASE_INSENSITIVE );
					return pattern;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static DBObject translateBSON(DBObject dbObject) {
		return translateBSON(dbObject, null, null, null);
	}

	public static DBObject translateBSON(DBObject dbObject, String key) {
		return translateBSON(dbObject, key, null, null);
	}

	public static DBObject translateBSON(DBObject dbObject, String prefix, Map<String, String> transferKeyMap, Set<String> removeKeys) {
		return translateBSON(dbObject, prefix, transferKeyMap, removeKeys, true);
	}

	public static DBObject translateBSON(DBObject dbObject, String prefix, Map<String, String> transferKeyMap, Set<String> removeKeys,
			boolean defaultValueTransfer) {
		BasicDBObject dbo = new BasicDBObject();
		if (dbObject != null) {
			// 更改id的类型和Date的类型
			Iterator<String> iter = dbObject.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();

				// 去除不需要的字段
				if (removeKeys != null && removeKeys.contains(key)) {
					continue;
				}

				Object value = dbObject.get(key);
				if (value != null) {
					Object inputValue = value;

					if (defaultValueTransfer) {//缺省的值转换
						if (value instanceof Date) {
							inputValue = ((Date) value).getTime();
							// inputValue = Util.SDF_FULL.format((Date) value);
						} else if (value instanceof ObjectId) {
							inputValue = ((ObjectId) value).toString();
						}
					}

					// 设置转换字段名称 如果设置了转换字段名称映射表
					// 如果包含的 主键按照转换字段列表中的进行替换
					if (transferKeyMap != null) {
						String newKey = transferKeyMap.get(key);
						if (newKey != null) {
							key = newKey;
						}
					}

					if (prefix != null) {
						dbo.put(prefix + prefixKey + key, inputValue);
					} else {
						dbo.put(key, inputValue);
					}
				}
			}
		}
		return dbo;

	}

	public static String[] split(String key, String str) {
		String[] ret = new String[2];
		int idx = key.indexOf(str);
		if (idx == -1)
			return null;
		ret[0] = key.substring(0, idx);
		if (ret[0].length() == 0)
			ret[0] = null;
		ret[1] = key.substring(idx + str.length(), key.length());
		if (ret[1].length() == 0)
			ret[1] = null;
		return ret;
	}

	/**
	 * 系统使用了一个ids进行自动增加的数字，从1000位开始
	 * 
	 * @param collectionName
	 * @param ids
	 * @return
	 */
	public static int getIncreasedID(DBCollection ids, String collectionName) {
		DBObject query = new BasicDBObject();
		query.put("name", collectionName);

		DBObject fields = new BasicDBObject();
		fields.put("id", 1);

		DBObject update = new BasicDBObject().append("$inc", new BasicDBObject().append("id", 1));

		DBObject result = ids.findAndModify(query, fields, null, false, update, true, true);

		return (Integer) result.get("id");
	}

}
