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
				if (vp != null) {// 1. �ж��Ƿ���valueProvider�Ķ���
					dbObject.put(key, vp.getValue(collection,key));
				} else {// 2. û��vp����ʱ������������ļ��еĶ����Լ�����Ĳ���ֵ�����ֵΪ��ʱ���û�û��������ֶε�ֵ
					String parameterName = valueCes[0].getAttribute(IConfConstants.ATT_PARAMETERNAME);
					
					Object value = null;
					// �ȴ��ڲ�����ӳ����в���
					if (internalParameters.keySet().contains(parameterName)) {
						value = internalParameters.get(parameterName);
					} else if (parameters != null && parameters.keySet().contains(parameterName)) {// Ȼ����ⲿ�������в���
						String inputValue = parameters.get(parameterName);
						String type = valueCes[0].getAttribute(IConfConstants.ATT_VALUE_TYPE);
						value =  Util.getTypeValue(inputValue, type);
					}
					
					
					String inputValue = valueCes[0].getAttribute(IConfConstants.ATT_VALUE);
					//�����������������ֵ
					if (!Util.isNullorEmpty(inputValue)) {
						String type = valueCes[0].getAttribute(IConfConstants.ATT_VALUE_TYPE);
						value = Util.getTypeValue(inputValue, type);
					} else {
						//�����������Ҳû������ֵ,�����,�������ļ�ֻд��һ���ֶ���,��û�и�������,Ҳû�����ֵ
						//��������һ������,��������������������
					}
					if(value!=null){

						dbObject.put(key, value);
						
					}
					
				}
				// ****************zhonghua 2012/3/24
			}

			// update ���⣬������ĵ���ĳЩ�ֶ���ֵ�����Ǳ���update�Ĳ�����û����Щ�ֶεĸ�ֵʱ��ֱ�ӵ�update��������ЩֵΪ��
			ObjectId oid = dbObject.getObjectId("_id");
			if (oid != null) {// �������
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
		if (oid == null) {// �������
			oid = new ObjectId();
			dbObject.put("_id", oid);
			collection.insert(dbObject);
		} else {// ���²���
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
