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
				//这个方法是用于产生列表的方法 比如构造一个类型[12,23,45],这个列表中的是一个单值,并不是对象数组
				//无论是何种情况,这个方法返回的空都应当被查询忽略
				
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
		// 考虑从IfieldValueProvider获得数据的方式
		IFieldValueProvider vp = getParameterValueProvider(ice);
		if (vp != null) {
			val = vp.getValue(collection, key);
			return getObjectPutValue(jo,key,val);
		}

		String parameterName = ice.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		
		// 优先从参数取值,判断参数名称是否为空,首先处理参数名不为空的情况
		if (!Util.isNullorEmpty(parameterName)) {
			// 当传入的参数中参数名包含了这个字段的参数名时返回该参数的值
			// 先从内部参数映射表中查找
		
			if (internalParameters.keySet().contains(parameterName)) {
				val = internalParameters.get(parameterName);
				return getObjectPutValue(jo,key,val);
			} else if (parameters != null && parameters.keySet().contains(parameterName)) {
			
				// 然后从外部参数表中查找
				String inputValue = parameters.get(parameterName);
				String type = ice.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				val = Util.getTypeValue(inputValue, type);
				return getObjectPutValue(jo,key,val);			}
			
		}
		//这时的情况包括 参数名称为空 或者 参数名不为空但 该参数没有得到外部的传入 或者 级联查询的内部传入
		//如果在定义中定义了一个字段的参数 但是参数没有得到输入 这种情况应当予以忽略
		//下面开始处理 参数名称为空的情况
		String inputValue = ice.getAttribute(IConfConstants.ATT_VALUE);
		
		//如果在配置中有输入值
		if (!Util.isNullorEmpty(inputValue)) {
			String type = ice.getAttribute(IConfConstants.ATT_VALUE_TYPE);
			val = Util.getTypeValue(inputValue, type);
			return getObjectPutValue(jo,key,val);
		} else {
			//如果在配置中也没有输入值,这表明,该配置文件只写了一个字段名,既没有给出参数,也没有输出值
			//这个情况是一个错误,程序在这里忽略这种情况
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
		
		// 考虑从IfieldValueProvider获得数据的方式
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
			// 先从内部参数映射表中查找
			if (internalParameters.keySet().contains(parameterName)) {
				return internalParameters.get(parameterName);
			} else if (parameters != null && parameters.keySet().contains(parameterName)) {// 然后从外部参数表中查找
				String inputValue = parameters.get(parameterName);
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		}
		return null;
	}

	/**
	 * 读取根据返回字段配置文件 返回对象
	 * 
	 * @param ce
	 * @return
	 */
	private BasicDBObject createReturnFieldsObject(IConfigurationElement ce) {
		IConfigurationElement[] returnFieldNameCEs = ce.getChildren(IConfConstants.ATT_RETURN_FIELD_NAME);
		return getSettingObjectFromConfiguration(returnFieldNameCEs);
	}

	/**
	 * 读取根据排序字段配置文件 返回对象
	 * 
	 * @param ce
	 * @return
	 */
	private BasicDBObject createSortFieldsObject(IConfigurationElement ce) {
		IConfigurationElement[] returnFieldNameCEs = ce.getChildren(IConfConstants.ATT_SORT_NAME);
		return getSettingObjectFromConfiguration(returnFieldNameCEs);
	}

	/**
	 * 根据传入的IConfigurationElement[]配置文件 产生对象
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
	 * ;分割条目,分割参数值
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
			} catch (Exception e) {// 传入的参数不能转换为整形的时候发生错误
				skip = -1;
				limit = -1;
			}
		}
		skiplimitSetted = true;
	}

	/**
	 * 
	 * @param paramValueMap
	 *            参数名称，参数值
	 * @param returnFieldList
	 *            返回字段名，逗号分隔
	 * @param skip
	 *            忽略记录数
	 * @param limit
	 *            限制
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
	 *            参数名称，参数值
	 * @param returnFieldList
	 *            ，返回的字段逗号分隔
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

		// 根据不同的需求进行不同的查询
		if (returnFields != null && expression != null) {
			ret = collection.find(expression, returnFields);
		} else if (returnFields == null && expression != null) {
			ret = collection.find(expression);
		} else {
			ret = collection.find();
		}

		// 处理分页
		if (skip != -1 && limit != -1) {
			ret = ret.skip(skip).limit(limit);
		}

		// 处理排序
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
	// 在级联查询的时候，上级DBObject的某个字段值可以传递到下一级的查询表达式中，这些值直接就是MongoDB的类型，无需转换
	/**
	 * 直接传递对象类型的参数,无需按类型转换
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
