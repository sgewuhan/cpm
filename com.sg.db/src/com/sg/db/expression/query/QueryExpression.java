package com.sg.db.expression.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.db.IServiceError;
import com.sg.db.Util;
import com.sg.db.expression.DBExpression;
import com.sg.db.expression.IConditionProvider;
import com.sg.db.expression.IConfConstants;
import com.sg.db.expression.IFieldValueProvider;
import com.sg.db.model.IJSONTranslate;

public class QueryExpression extends DBExpression implements IJSONTranslate {

	public QueryExpression(IConfigurationElement ice) {
		super(ice);
	}

	private BasicDBObject expression;
	private BasicDBObject returnFields;
	private BasicDBObject sortFields;
	private Map<String, Object> internalParameters = new HashMap<String, Object>();
	private int skip = -1;
	private int limit = -1;

	private boolean skiplimitSetted = false;

	private String createKey(IConfigurationElement ce) {
		String operName = ce.getAttribute(IConfConstants.ATT_OPERATOR_KEY);
		if (operName != null && !operName.equals("")) {
			return operName;
		} else {
			return ce.getAttribute(IConfConstants.ATT_FIELD_NAME);
		}
	}

	private BasicDBObject createConditionObject(IConfigurationElement ce) {
		IConditionProvider conditionProvider = null;
		try {
			conditionProvider = (IConditionProvider) ce.createExecutableExtension(IConfConstants.ATT_CONDITION_PROVIDER);
		} catch (CoreException e) {
		}

		if (conditionProvider != null) {
			return conditionProvider.getCondition();
		}

		IConfigurationElement[] ces = ce.getChildren(IConfConstants.ATT_KEY_VALUE);
		if (ces != null && ces.length > 0) {
			BasicDBObject jo = null;
			for (int i = 0; i < ces.length; i++) {
				jo = setValue(jo, ces[i]);
			}
			return jo;
		}
		return null;
	}

	private BasicDBObject setValue(BasicDBObject jo, IConfigurationElement ice) {
		String key = createKey(ice);
		IConfigurationElement[] ces = ice.getChildren(IConfConstants.ATT_SINGLE_VALUE);
		if (ces != null && ces.length > 0) {
			jo = setSingleValue(jo, key, ces[0]);
		}

		ces = ice.getChildren(IConfConstants.ATT_LIST_VALUE);
		if (ces != null && ces.length > 0) {
			jo = setListValue(jo, key, ces[0]);
		}

		ces = ice.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
		if (ces != null && ces.length > 0) {
			jo = setDBObject(jo, key, ces[0]);
		}
		
		return jo;
	}

	private BasicDBObject setDBObject(BasicDBObject jo, String key, IConfigurationElement ice) {
		BasicDBObject val = createConditionObject(ice);
		return getObjectPutValue(jo,key,val);
	}

	private BasicDBObject setListValue(BasicDBObject jo, String key, IConfigurationElement ice) {
		BasicDBList val = new BasicDBList();
		IConfigurationElement[] ces = ice.getChildren(IConfConstants.ATT_SINGLE_VALUE);
		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++) {
				
				
				Object singleObject = createSingleValueObjectFromConfiguration(ces[i]);
				//������������ڲ����б�ķ��� ���繹��һ������[12,23,45],����б��е���һ����ֵ,�����Ƕ�������
				//�����Ǻ������,����������صĿն�Ӧ������ѯ����
				
				if (singleObject != null) {
					val.add(singleObject);
				}
			}
		}

		ces = ice.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++) {
				BasicDBObject dbObject = createConditionObject(ces[i]);
				if (dbObject != null)
					val.add(dbObject);
			}
		}
		if (val.size() > 0) {
			return getObjectPutValue(jo,key,val);
		}
		return jo;
	}

	private BasicDBObject setSingleValue(BasicDBObject jo, String key, IConfigurationElement ice) {
		Object val = null;
		// ���Ǵ�IfieldValueProvider������ݵķ�ʽ
		IFieldValueProvider vp = getParameterValueProvider(ice);
		if (vp != null) {
			val = vp.getValue(collection, key);
			return getObjectPutValue(jo,key,val);
		}

		String parameterName = ice.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		
		// ���ȴӲ���ȡֵ,�жϲ��������Ƿ�Ϊ��,���ȴ����������Ϊ�յ����
		if (!Util.isNullorEmpty(parameterName)) {
			// ������Ĳ����в���������������ֶεĲ�����ʱ���ظò�����ֵ
			// �ȴ��ڲ�����ӳ����в���
		
			if (internalParameters.keySet().contains(parameterName)) {
				val = internalParameters.get(parameterName);
				return getObjectPutValue(jo,key,val);
			} else if (parameters != null && parameters.keySet().contains(parameterName)) {
			
				// Ȼ����ⲿ�������в���
				String inputValue = parameters.get(parameterName);
				String type = ice.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				val = Util.getTypeValue(inputValue, type);
				return getObjectPutValue(jo,key,val);			}
			
		}
		//��ʱ��������� ��������Ϊ�� ���� ��������Ϊ�յ� �ò���û�еõ��ⲿ�Ĵ��� ���� ������ѯ���ڲ�����
		//����ڶ����ж�����һ���ֶεĲ��� ���ǲ���û�еõ����� �������Ӧ�����Ժ���
		//���濪ʼ���� ��������Ϊ�յ����
		String inputValue = ice.getAttribute(IConfConstants.ATT_VALUE);
		
		//�����������������ֵ
		if (!Util.isNullorEmpty(inputValue)) {
			String type = ice.getAttribute(IConfConstants.ATT_VALUE_TYPE);
			val = Util.getTypeValue(inputValue, type);
			return getObjectPutValue(jo,key,val);
		} else {
			//�����������Ҳû������ֵ,�����,�������ļ�ֻд��һ���ֶ���,��û�и�������,Ҳû�����ֵ
			//��������һ������,��������������������
		}
		
		return jo;
	}

	private BasicDBObject getObjectPutValue(BasicDBObject jo, String key, Object val) {
//		if(val!=null){
			
			if (jo == null)
				jo = new BasicDBObject();
			jo.put(key, val);
//		}
		return jo;
	}

//	private BasicDBList createListValueObject(IConfigurationElement ce) {
//		BasicDBList conditions = new BasicDBList();
//		IConfigurationElement[] ces = ce.getChildren(IConfConstants.ATT_SINGLE_VALUE);
//		if (ces != null && ces.length > 0) {
//			for (int i = 0; i < ces.length; i++)
//				conditions.add(createSingleValueObject(ces[i]));
//			return conditions;
//		}
//
//		ces = ce.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
//		if (ces != null && ces.length > 0) {
//			for (int i = 0; i < ces.length; i++)
//				conditions.add(createConditionObject(ces[i]));
//			return conditions;
//		}
//
//		return conditions;
//	}

	/**
	 *  
	 */
	protected Object createSingleValueObjectFromConfiguration(IConfigurationElement ce) {
		
		// ���Ǵ�IfieldValueProvider������ݵķ�ʽ
		IFieldValueProvider vp = getParameterValueProvider(ce);
		if (vp != null) {
			return vp.getValue(collection, ce.getAttribute(IConfConstants.ATT_FIELD_NAME));
		}

		// ****************zhonghua 2012/3/24
		String parameterName = ce.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		if ("".equals(parameterName) || (parameterName == null)) {
			String inputValue = ce.getAttribute(IConfConstants.ATT_VALUE);
			if (inputValue == null || inputValue.equals("")) {
				return null;
			} else {
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		} else {
			// �ȴ��ڲ�����ӳ����в���
			if (internalParameters.keySet().contains(parameterName)) {
				return internalParameters.get(parameterName);
			} else if (parameters != null && parameters.keySet().contains(parameterName)) {// Ȼ����ⲿ�������в���
				String inputValue = parameters.get(parameterName);
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		}
		return null;
	}

	/**
	 * ��ȡ���ݷ����ֶ������ļ� ���ض���
	 * 
	 * @param ce
	 * @return
	 */
	private BasicDBObject createReturnFieldsObject(IConfigurationElement ce) {
		IConfigurationElement[] returnFieldNameCEs = ce.getChildren(IConfConstants.ATT_RETURN_FIELD_NAME);
		return getSettingObjectFromConfiguration(returnFieldNameCEs);
	}

	/**
	 * ��ȡ���������ֶ������ļ� ���ض���
	 * 
	 * @param ce
	 * @return
	 */
	private BasicDBObject createSortFieldsObject(IConfigurationElement ce) {
		IConfigurationElement[] returnFieldNameCEs = ce.getChildren(IConfConstants.ATT_SORT_NAME);
		return getSettingObjectFromConfiguration(returnFieldNameCEs);
	}

	/**
	 * ���ݴ����IConfigurationElement[]�����ļ� ��������
	 * 
	 * @param returnFieldNameCEs
	 * @return
	 */
	private BasicDBObject getSettingObjectFromConfiguration(IConfigurationElement[] returnFieldNameCEs) {
		if (returnFieldNameCEs != null && returnFieldNameCEs.length > 0) {
			BasicDBObject jo = new BasicDBObject();
			String key;
			int value;

			for (int i = 0; i < returnFieldNameCEs.length; i++) {
				key = returnFieldNameCEs[i].getAttribute(IConfConstants.ATT_NAME);
				value = (IConfConstants.VALUE_FALSE.equals(returnFieldNameCEs[i].getAttribute(IConfConstants.VALUE_INCLUDE))) ? 0 : 1;
				jo.put(key, value);
			}

			return jo;
		}
		return null;
	}

	private void load() {

		if (!skiplimitSetted) {
			String sSkip = ce.getAttribute(IConfConstants.ATT_SKIP);
			String sLimit = ce.getAttribute(IConfConstants.ATT_LIMIT);
			setSkipAndLimit(sSkip, sLimit);
		}

		if (returnFields == null) {
			IConfigurationElement[] returnFieldCEs = ce.getChildren(IConfConstants.ATT_RETURN_FIELDS);
			if (returnFieldCEs != null && returnFieldCEs.length > 0) {
				returnFields = createReturnFieldsObject(returnFieldCEs[0]);
			}
		}

		if (sortFields == null) {
			IConfigurationElement[] sortFieldCEs = ce.getChildren(IConfConstants.ATT_SORT_FIELDS);
			if (sortFieldCEs != null && sortFieldCEs.length > 0) {
				sortFields = createSortFieldsObject(sortFieldCEs[0]);
			}
		}

		IConfigurationElement[] conditionsCEs = ce.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
		if (conditionsCEs != null && conditionsCEs.length > 0) {
			expression = createConditionObject(conditionsCEs[0]);
		}
	}

	/**
	 * ;�ָ���Ŀ,�ָ����ֵ
	 * @param returnFieldList
	 */
	public void setReturnFieldsFromString(String returnFieldList) {
		returnFields = getSettingObjectFromString(returnFieldList);
	}

	public BasicDBObject getSettingObjectFromString(String keyValue) {
		try {
			if (!Util.isNullorEmpty(keyValue)) {
				BasicDBObject ret = new BasicDBObject();
				String[] keyValueList = keyValue.split(";");
				for (String keyValuePair : keyValueList) {
					String[] element = keyValuePair.split(",");
					String key = element[0];
					if (!Util.isNullorEmpty(key)) {
						int value = Integer.parseInt(element[1]);
						ret.put(key, value);
					}
				}
				return ret;
			}
		} catch (Exception e) {
		}
		return null;
	}

	public void setSortFieldsFromString(String sortFieldList) {
		sortFields = getSettingObjectFromString(sortFieldList);
	}

	public BasicDBObject getExpression() {
		return expression;
	}

	public BasicDBObject getReturnFields() {
		return returnFields;
	}

	public BasicDBObject getSortFields() {
		return sortFields;
	}

	public DBCollection getCollection() {
		return collection;
	}

	public void setSkipAndLimit(String sSkip, String sLimit) {
		if ((!Util.isNullorEmpty(sSkip)) && (!Util.isNullorEmpty(sLimit))) {
			try {
				skip = Integer.valueOf(skip).intValue();
				limit = Integer.valueOf(limit).intValue();
			} catch (Exception e) {// ����Ĳ�������ת��Ϊ���ε�ʱ��������
				skip = -1;
				limit = -1;
			}
		}
		skiplimitSetted = true;
	}

	/**
	 * 
	 * @param paramValueMap
	 *            �������ƣ�����ֵ
	 * @param returnFieldList
	 *            �����ֶ��������ŷָ�
	 * @param skip
	 *            ���Լ�¼��
	 * @param limit
	 *            ����
	 * @return
	 */
	public DBCursor run(Map<String, String> paramValueMap, String returnFieldList, String skip, String limit) {
		setParamValueMap(paramValueMap);
		setReturnFieldsFromString(returnFieldList);
		setSkipAndLimit(skip, limit);
		return run();
	}

	/**
	 * @param paramValueMap
	 *            �������ƣ�����ֵ
	 * @param returnFieldList
	 *            �����ص��ֶζ��ŷָ�
	 * @return
	 */
	public DBCursor run(Map<String, String> paramValueMap, String returnFieldList) {
		setParamValueMap(paramValueMap);
		setReturnFieldsFromString(returnFieldList);
		return run();
	}

	@Override
	public DBCursor run(Map<String, String> paramValueMap) {
		setParamValueMap(paramValueMap);
		return run();
	}

	public BasicDBObject loadCondition() {
		load();
		return expression;
	}

	// *******update by Liutao for pageSplit start 2012/3/16******//
	@Override
	public DBCursor run() {
		loadCondition();
		DBCursor ret;

		// ���ݲ�ͬ��������в�ͬ�Ĳ�ѯ
		if (returnFields != null && expression != null) {
			ret = collection.find(expression, returnFields);
		} else if (returnFields == null && expression != null) {
			ret = collection.find(expression);
		} else {
			ret = collection.find();
		}

		// �����ҳ
		if (skip != -1 && limit != -1) {
			ret = ret.skip(skip).limit(limit);
		}

		// ��������
		if (sortFields != null) {
			ret = ret.sort(sortFields);
		}

		reset();
		return ret;
	}

	// *******update by Liutao for pageSplit end 2012/3/16******//

	private void reset() {
		expression = null;
		returnFields = null;
		sortFields = null;
		internalParameters = new HashMap<String, Object>();
		skip = -1;
		limit = -1;
		skiplimitSetted = false;
	}

	// *******for cascade query by Zhonghua end 2012/3/15******//
	// �ڼ�����ѯ��ʱ���ϼ�DBObject��ĳ���ֶ�ֵ���Դ��ݵ���һ���Ĳ�ѯ���ʽ�У���Щֱֵ�Ӿ���MongoDB�����ͣ�����ת��
	/**
	 * ֱ�Ӵ��ݶ������͵Ĳ���,���谴����ת��
	 * 
	 * @param parameters
	 */
	public void passParamValueMap(Map<String, Object> parameters) {
		internalParameters= parameters;
	}
	
	public QueryExpression setParamValue(String key, Object value) {
		if (internalParameters == null) {
			internalParameters = new HashMap<String, Object>();
		}
		internalParameters.put(key, value);
		return this;
	}


	public String getJSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		BasicDBList ret = getBSONResult(transferFields,removeFields);
		if(ret.size()==0){
			return IServiceError.WAR_FIND_NO_RESULT;
		}else{
			return JSON.serialize(ret);
		}
	}
	
	public BasicDBList getBSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		BasicDBList dbl = new BasicDBList();
		DBCursor cur = run();
		if (cur != null) {
			while (cur.hasNext()) {
				DBObject dbo = cur.next();
				dbl.add(Util.translateBSON(dbo,null,transferFields,removeFields));
			}
		}
		return dbl;
	}

	public void clean() {
		internalParameters.clear();
	}

}
