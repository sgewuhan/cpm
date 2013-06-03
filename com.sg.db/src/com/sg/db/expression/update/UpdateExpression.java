package com.sg.db.expression.update;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import com.sg.db.DBActivator;
import com.sg.db.IServiceError;
import com.sg.db.Util;
import com.sg.db.expression.DBExpression;
import com.sg.db.expression.IConfConstants;
import com.sg.db.expression.IFieldValueProvider;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.IJSONTranslate;

public class UpdateExpression extends DBExpression implements IJSONTranslate {

	public static final String COUNT = "COUNT";
	private Boolean upsert;
	private Boolean multi;
	private BasicDBObject updateObject;
	private BasicDBObject queryObject;

	public UpdateExpression(IConfigurationElement ice) {
		super(ice);
	}

	private String createKey(IConfigurationElement ce) {
		String operName = ce.getAttribute(IConfConstants.ATT_OPERATOR_KEY);
		if (Util.isNullorEmpty(operName)) {
			return ce.getAttribute(IConfConstants.ATT_FIELD_NAME);
		} else {
			return operName;
		}
	}

	private BasicDBObject setValue(BasicDBObject jo, IConfigurationElement ce) {
		String key = createKey(ce);
		IConfigurationElement[] ces = ce.getChildren(IConfConstants.ATT_SINGLE_VALUE);

		if (ces != null && ces.length > 0) {
			jo = setSingleValue(jo, key, ces[0]);
		}

		ces = ce.getChildren(IConfConstants.ATT_LIST_VALUE);
		if (ces != null && ces.length > 0) {
			jo = setListValue(jo, key, ces[0]);
		}

		ces = ce.getChildren(IConfConstants.ATT_UPDATE_OBJECT);
		if (ces != null && ces.length > 0) {
			jo = setObjectValue(jo, key, ces[0]);
		}

		return jo;
	}

	private BasicDBObject setSingleValue(BasicDBObject jo, String key, IConfigurationElement cem) {
		Object val;

		//考虑从IFieldValueProvider中获得数据的情况。
		IFieldValueProvider vp = getParameterValueProvider(cem);
		if(vp!=null){
			val = vp.getValue(collection,key);
			if (jo == null)
				jo = new BasicDBObject();
			jo.put(key, val);
			return jo;
		}
		
		// 取出参数名
		String parameterName = cem.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		
		// 优先从参数取值
		if (!Util.isNullorEmpty(parameterName)) {
			// 当传入的参数中，参数名包含了这个字段的参数名时，返回该参数的值
			if (parameters != null && parameters.keySet().contains(parameterName)) {
				String inputValue = parameters.get(parameterName);
				String type = cem.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				val = Util.getTypeValue(inputValue, type);
				if (jo == null)
					jo = new BasicDBObject();
				jo.put(key, val);
				return jo;
			}
		}

		// 如果没有定义参数，或者定义的参数没有传入值，无法从参数中获得值时，从conf定义中取值
		String inputValue = cem.getAttribute(IConfConstants.ATT_VALUE);
		if (!Util.isNullorEmpty(inputValue)) {
			String type = cem.getAttribute(IConfConstants.ATT_VALUE_TYPE);
			val = Util.getTypeValue(inputValue, type);
			if (jo == null)
				jo = new BasicDBObject();
			jo.put(key, val);
			return jo;
		}

		// 如果没有定义参数，或者定义的参数没有传入值，或者没有在conf中定义值，不向jo中放置任何东西
		return jo;
	}

	private BasicDBObject setObjectValue(BasicDBObject jo, String key, IConfigurationElement cem) {
		BasicDBObject val = createDBObject(cem);
		if (val != null) {
			if (jo == null) {
				jo = new BasicDBObject();
				jo.put(key, val);
			}
		}
		return jo;
	}

	private BasicDBObject setListValue(BasicDBObject jo, String key, IConfigurationElement cem) {
		BasicDBList listValue = new BasicDBList();
		IConfigurationElement[] ces = cem.getChildren(IConfConstants.ATT_SINGLE_VALUE);
		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++) {
				Object singleObject = createSingleValueObject(ces[i]);
				if (singleObject != null) {
					listValue.add(singleObject);
				}
			}
		}

		ces = cem.getChildren(IConfConstants.ATT_UPDATE_OBJECT);
		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++) {
				BasicDBObject dbObject = createDBObject(ces[i]);
				if (dbObject != null)
					listValue.add(dbObject);
			}
		}
		if (listValue.size() > 0) {
			if (jo == null)
				jo = new BasicDBObject();
			jo.put(key, listValue);
		}
		return jo;
	}

	private BasicDBObject createDBObject(IConfigurationElement cem) {
		IConfigurationElement[] ces = cem.getChildren(IConfConstants.ATT_KEY_VALUE);
		if (ces != null && ces.length > 0) {
			BasicDBObject jo = null;
			for (int i = 0; i < ces.length; i++) {
				jo = setValue(jo, ces[i]);
			}
			return jo;
		}
		return null;
	}

	/**
	 * 覆盖父类的方法，处理直接传递对象类型参数值的情况
	 */
	protected Object createSingleValueObject(IConfigurationElement cem) {
		// 取出参数名
		String parameterName = cem.getAttribute(IConfConstants.ATT_PARAMETERNAME);

		// 优先从参数取值
		if (!Util.isNullorEmpty(parameterName)) {
			// 当传入的参数中，参数名包含了这个字段的参数名时，返回该参数的值
			if (parameters != null && parameters.keySet().contains(parameterName)) {
				String inputValue = parameters.get(parameterName);
				String type = cem.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		}

		// 无法从参数中获得值时，从conf定义中取值
		String inputValue = cem.getAttribute(IConfConstants.ATT_VALUE);
		Assert.isTrue(!Util.isNullorEmpty(inputValue), IServiceError.ERR_CANNOT_GET_VALUE_FROM_PARAMETER_OR_INPUT);

		String type = cem.getAttribute(IConfConstants.ATT_VALUE_TYPE);
		return Util.getTypeValue(inputValue, type);
	}

	public void loadCondition() {
		if (queryObject == null) {
			// 获得查询表达式
			String queryExpId = ce.getAttribute(IConfConstants.ELEMENT_NAME_QUERY);
			Assert.isTrue(!Util.isNullorEmpty(queryExpId), IServiceError.ERR_EMPTY_QUERY_EXPRESSION);

			QueryExpression queryExpession = null;
			try {
				queryExpession = DBActivator.getQueryExpression(queryExpId);
			} catch (Exception e) {
			}
			Assert.isNotNull(queryExpession, IServiceError.ERR_EMPTY_QUERY_EXPRESSION);
			Assert.isNotNull(parameters, IServiceError.ERR_EMPTY_INPUT_PARAMETER);
			queryExpession.setParamValueMap(parameters);

			queryObject = queryExpession.loadCondition();
			Assert.isNotNull(queryObject, IServiceError.ERR_NULL_QUERYOBJECT);
		}

		// 设置upsert以及multi的值
		if (upsert == null)
			upsert = IConfConstants.VALUE_TRUE.equals(ce.getAttribute(IConfConstants.ATT_UPSERT));
		if (multi == null)
			multi = IConfConstants.VALUE_TRUE.equals(ce.getAttribute(IConfConstants.ATT_MULTI));

		// 获得update的表达式
		if (updateObject == null) {
			IConfigurationElement[] conditionsCEs = ce.getChildren(IConfConstants.ATT_UPDATE_OBJECT);
			Assert.isTrue(conditionsCEs != null && conditionsCEs.length > 0, IServiceError.ERR_EMPTY_UPDATE_EXPRESSION);
			updateObject = createDBObject(conditionsCEs[0]);
		}
	}

	@Override
	public DBObject run(Map<String, String> paramValueMap) {
		setParamValueMap(paramValueMap);
		return run();
	}

	@Override
	public DBObject run() {
		loadCondition();
		WriteResult result = collection.update(queryObject, updateObject, upsert, multi);

		BasicDBObject dbo = new BasicDBObject();
		dbo.put("QUERY", queryObject);
		dbo.put("UPDATE", updateObject);
		dbo.put("UPSERT", upsert);
		dbo.put("MULTI", multi);
		dbo.put(COUNT, result.getN());

		reset();
		return dbo;
	}

	// *************************************************************************
	// **********************************************用于手工设置和获取表达式的值

	private void reset() {
		updateObject = null;
		queryObject = null;
		upsert = null;
		multi = null;
	}

	public void setUpsert(boolean b) {
		upsert = b;
	}

	public void setMulti(boolean b) {
		multi = b;
	}

	public void setQueryObject(BasicDBObject dbo) {
		Assert.isNotNull(dbo, IServiceError.ERR_NULL_QUERYOBJECT);
		queryObject = dbo;
	}

	public void setUpdateObject(BasicDBObject dbo) {
		Assert.isNotNull(dbo, IServiceError.ERR_NULL_UPDATEOBJECT);
		updateObject = dbo;
	}

	public DBCollection getCollection() {
		return collection;
	}

	public Boolean getUpsert() {
		return upsert;
	}

	public Boolean getMulti() {
		return multi;
	}

	public BasicDBObject getUpdateObject() {
		return updateObject;
	}

	public BasicDBObject getQueryObject() {
		return queryObject;
	}

	// **********************************************用于手工设置和获取表达式的值
	// *************************************************************************

	@Override
	public String getJSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return JSON.serialize(getBSONResult(transferFields,removeFields));
	}

	@Override
	public DBObject getBSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return run();
	}

}
