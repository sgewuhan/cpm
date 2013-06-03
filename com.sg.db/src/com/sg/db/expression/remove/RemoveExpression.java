package com.sg.db.expression.remove;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import com.sg.db.Util;
import com.sg.db.expression.DBExpression;
import com.sg.db.expression.IConditionProvider;
import com.sg.db.expression.IConfConstants;
import com.sg.db.model.IJSONTranslate;

public class RemoveExpression extends DBExpression implements IJSONTranslate{

	public RemoveExpression(IConfigurationElement ice) {
		super(ice);
	}

	private BasicDBObject expression;
	

	private BasicDBObject createConditionObject(IConfigurationElement ce) {
		IConditionProvider conditionProvider = null;
		try {
			conditionProvider = (IConditionProvider) ce
					.createExecutableExtension(IConfConstants.ATT_CONDITION_PROVIDER);
		} catch (CoreException e) {
		}

		if (conditionProvider != null) {
			return conditionProvider.getCondition();
		}

		IConfigurationElement[] ces = ce
				.getChildren(IConfConstants.ATT_KEY_VALUE);
		if (ces != null && ces.length > 0) {
			BasicDBObject jo = new BasicDBObject();
			String key;
			Object value;
			for (int i = 0; i < ces.length; i++) {
				key = createKey(ces[i]);
				value = createValueObject(ces[i]);
				jo.put(key, value);
			}
			return jo;
		}
		return null;
	}

	private String createKey(IConfigurationElement ce) {
		String operName = ce.getAttribute(IConfConstants.ATT_OPERATOR_KEY);
		if (operName != null && !operName.equals("")) {
			return operName;
		} else {
			return ce.getAttribute(IConfConstants.ATT_FIELD_NAME);
		}
	}

	private BasicDBList createListValueObject(IConfigurationElement ce) {
		BasicDBList conditions = new BasicDBList();
		IConfigurationElement[] ces = ce
				.getChildren(IConfConstants.ATT_SINGLE_VALUE);
		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++)
				conditions.add(createSingleValueObject(ces[i]));
			return conditions;
		}

		ces = ce.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++)
				conditions.add(createConditionObject(ces[i]));
			return conditions;
		}

		return conditions;
	}
	

	private Object createValueObject(IConfigurationElement ce) {
		IConfigurationElement[] ces = ce
				.getChildren(IConfConstants.ATT_SINGLE_VALUE);
		if (ces != null && ces.length > 0) {
			return createSingleValueObject(ces[0]);
		}

		ces = ce.getChildren(IConfConstants.ATT_LIST_VALUE);
		if (ces != null && ces.length > 0) {
			return createListValueObject(ces[0]);
		}

		ces = ce.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
		if (ces != null && ces.length > 0) {
			return createConditionObject(ces[0]);
		}

		return null;
	}

	private void load() {

		
		IConfigurationElement[] conditionsCEs = ce
				.getChildren(IConfConstants.ATT_CONDITION_OBJECT);
		if (conditionsCEs != null && conditionsCEs.length > 0) {
			expression = createConditionObject(conditionsCEs[0]);
		}
	}

	public BasicDBObject getExpression() {
		return expression;
	}

	public DBCollection getCollection() {
		return collection;
	}


	@Override
	public DBObject run(Map<String, String> paramValueMap) {
		setParamValueMap(paramValueMap);
		return run();
	}

	
	public BasicDBObject loadCondition(){
		load();
		return expression;
	}
	
	// *******update by Liutao for pageSplit start 2012/3/16******//
	@Override
	public DBObject run() {
		loadCondition();
		WriteResult result = collection.remove(expression);
		
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("REMOVE", expression);
		dbo.put("COUNT", result.getN());
		reset();
		return dbo;
	}

	// *******update by Liutao for pageSplit end 2012/3/16******//

	private void reset() {
		expression = null;
	}


	/**
	 * 覆盖父类的方法，处理直接传递对象类型参数值的情况
	 */
	protected Object createSingleValueObject(IConfigurationElement ce) {
		String parameterName = ce
				.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		if ("".equals(parameterName) || (parameterName == null)) {
			String inputValue = ce.getAttribute(IConfConstants.ATT_VALUE);
			if (inputValue == null || inputValue.equals("")) {
				return null;
			} else {
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		} else {
			if (parameters != null
					&& parameters.keySet().contains(parameterName)) {// 然后从外部参数表中查找
				String inputValue = parameters.get(parameterName);
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		}
		return null;
	}
	// *******for cascade query by Zhonghua end 2012/3/15******//

	@Override
	public String getJSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return JSON.serialize(getBSONResult(transferFields,removeFields));
	}

	@Override
	public Object getBSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		return run();
	}
	
}
