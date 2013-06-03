package com.sg.db.expression.insert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.db.Util;
import com.sg.db.expression.DBExpression;
import com.sg.db.expression.IConfConstants;
import com.sg.db.expression.IFieldValueProvider;
import com.sg.db.model.IJSONTranslate;

public class InsertExpression extends DBExpression implements IJSONTranslate {
	
	private Map<String, Object> internalParameters = new HashMap<String, Object>();


	public InsertExpression(IConfigurationElement ice) {
		super(ice);
	}

	private BasicDBObject dbObject;

	private void load() {
		IConfigurationElement[] ces = ce.getChildren(IConfConstants.ATT_KEY_VALUE);
		if (ces != null && ces.length > 0) {
			dbObject = new BasicDBObject();
			String key;
			for (int i = 0; i < ces.length; i++) {
				key = ces[i].getAttribute(IConfConstants.ATT_FIELD_NAME);

				IConfigurationElement[] valueCes = ces[i].getChildren(IConfConstants.ATT_SINGLE_VALUE);
				if (valueCes == null || valueCes.length < 1)
					continue;
				
				// ****************zhonghua 2012/3/24
				IFieldValueProvider vp = getParameterValueProvider(valueCes[0]);
				if (vp != null) {// 1. 判断是否有valueProvider的定义
					dbObject.put(key, vp.getValue(collection,key));
				} else {// 2. 没有vp定义时，获得在配置文件中的定义以及传入的参数值，这个值为空时是用户没有输入该字段的值
					String parameterName = valueCes[0].getAttribute(IConfConstants.ATT_PARAMETERNAME);
					
					Object value = null;
					// 先从内部参数映射表中查找
					if (internalParameters.keySet().contains(parameterName)) {
						value = internalParameters.get(parameterName);
					} else if (parameters != null && parameters.keySet().contains(parameterName)) {// 然后从外部参数表中查找
						String inputValue = parameters.get(parameterName);
						String type = valueCes[0].getAttribute(IConfConstants.ATT_VALUE_TYPE);
						value =  Util.getTypeValue(inputValue, type);
					}
					
					
					String inputValue = valueCes[0].getAttribute(IConfConstants.ATT_VALUE);
					//如果在配置中有输入值
					if (!Util.isNullorEmpty(inputValue)) {
						String type = valueCes[0].getAttribute(IConfConstants.ATT_VALUE_TYPE);
						value = Util.getTypeValue(inputValue, type);
					} else {
						//如果在配置中也没有输入值,这表明,该配置文件只写了一个字段名,既没有给出参数,也没有输出值
						//这个情况是一个错误,程序在这里忽略这种情况
					}
					if(value!=null){

						dbObject.put(key, value);
						
					}
					
				}
				// ****************zhonghua 2012/3/24
			}

			// update 问题，如果该文档的某些字段有值，但是本次update的参数中没有这些字段的赋值时，直接的update将覆盖这些值为空
			ObjectId oid = dbObject.getObjectId("_id");
			if (oid != null) {// 插入操作
				DBObject original = collection.findOne(new BasicDBObject().append("_id", oid));
				original.keySet().removeAll(dbObject.keySet());

				Iterator<String> iter = original.keySet().iterator();
				while (iter.hasNext()) {
					key = iter.next();
					dbObject.put(key, original.get(key));
				}
			}
		}
	}

	public BasicDBObject loadCondition() {
		load();
		return dbObject;
	}

	@Override
	public DBObject run() {
		return run(true);
	}
	

	public DBObject run(boolean reload) {
		if(reload){
			loadCondition();
		}
		ObjectId oid = dbObject.getObjectId("_id");
		if (oid == null) {// 插入操作
			oid = new ObjectId();
			dbObject.put("_id", oid);
			collection.insert(dbObject);
		} else {// 更新操作
			collection.save(dbObject);
		}
		return dbObject;
	}

	public DBObject runInsert(ObjectId objectId) {
		loadCondition();
		dbObject.put("_id", objectId);
		collection.insert(dbObject);
		return dbObject;
	}
	
	public BasicDBObject getDataObject(){
		return dbObject;
	}

	@Override
	public DBObject run(Map<String, String> paramValueMap) {
		setParamValueMap(paramValueMap);
		return run();
	}

	public String getJSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return JSON.serialize( getBSONResult(transferFields,removeFields));
	}

	@Override
	public DBObject getBSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		DBObject dbo = run();
		return Util.translateBSON(dbo,null,transferFields,removeFields);
	}
	
	public InsertExpression appendValue(String key, Object value) {
		if (internalParameters == null) {
			internalParameters = new HashMap<String, Object>();
		}
		internalParameters.put(key, value);
		return this;
	}
}
