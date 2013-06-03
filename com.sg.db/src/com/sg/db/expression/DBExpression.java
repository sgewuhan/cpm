package com.sg.db.expression;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.DBCollection;
import com.sg.db.DBActivator;
import com.sg.db.Util;

public abstract class DBExpression {

	protected String id;
	protected IConfigurationElement ce;
	protected String name;
	protected DBCollection collection;
	protected Map<String, String> parameters;
	protected Set<String> parameterNameSet;

	public DBExpression(IConfigurationElement ice) {
		ce = ice;
		id = ce.getAttribute(IConfConstants.ATT_ID);
		name = ce.getAttribute(IConfConstants.ATT_NAME);
		String collectionName = ce.getAttribute(IConfConstants.ATT_COLLECTION);
		Assert.isTrue(!Util.isNullorEmpty(collectionName), id+": Expression need define a collection name.");
		collection = DBActivator.getDefaultDBCollection(collectionName);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setParamValueMap(Map<String, String> paramValueMap) {
		//读取参数定义
		if(parameterNameSet==null){
			loadParameterDefinition();
		}
		//跳过未定义的参数
		Iterator<String> iter = parameterNameSet.iterator();
		parameters = new HashMap<String,String>();
		while(iter.hasNext()){
			String key = iter.next();
			if(paramValueMap.containsKey(key)){
				parameters.put(key, paramValueMap.get(key));
			}
		}
		
	}

	public abstract Object run();

	public abstract Object run(Map<String, String> paramValueMap);

	//******zhonghua 12/3/6*************************考虑到只有查询会使用以下的方法，取消这两个方法
//	public abstract Object run(Map<String, String> paramValueMap,
//			String returnFieldList);
//
//	public abstract Object run(Map<String, String> paramValueMap,
//			String returnFieldList, int skip, int limit) ;
	//******zhonghua 12/3/6*************************

	
//	/**
//	 * 这个方法无法区分 显式地设置为 null 或没有设置的情况，必须取代为分别调用以下的方法
//	 * getParameterValueProvider()
//	 * getStringInputValueFromParameter(),当这个方法返回为null时表示，用户没有传递这个参数的值
//	 * getTypedValue(String inputValue),这个方法是根据类型来转换字符串值到对象值，[null]表示设置该字段为空
//	 * @param ce
//	 * @return
//	 */
//	@Deprecated
//	protected Object createSingleValueObject(IConfigurationElement ce) {
//		try {
//			IFieldValueProvider vp = (IFieldValueProvider) ce
//					.createExecutableExtension(IConfConstants.ATT_VALUE_PROVIDER);// 注册了查询参数
//			if (vp != null) {
//				return vp.getParameterValue();
//			}
//		} catch (CoreException e1) {
//		}
//
//		String parameterName = ce
//				.getAttribute(IConfConstants.ATT_PARAMETERNAME);
//		String inputValue;
//		if (Util.isNullorEmpty(parameterName)) {
//			inputValue = ce.getAttribute(IConfConstants.ATT_VALUE);
//		} else if(parameters.containsKey(parameterName)){
//			inputValue = parameters.get(parameterName);
//		} else{
//			return null;
//		}
//		
//		String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
//		return Util.getTypeValue(inputValue, type);
//
//	}
	
	protected String getStringInputValueFromParameter(IConfigurationElement ce){
		String parameterName = ce
				.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		String inputValue;
		if (Util.isNullorEmpty(parameterName)) {
			inputValue = ce.getAttribute(IConfConstants.ATT_VALUE);
		} else if(parameters.containsKey(parameterName)){
			inputValue = parameters.get(parameterName);
		} else{
			return null;
		}
		return inputValue;
	}
	
	protected Object getTypedValue(IConfigurationElement ce,String inputValue){
		String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
		return Util.getTypeValue(inputValue, type);
	}
	
	
	protected IFieldValueProvider getParameterValueProvider(IConfigurationElement ce) {
			IFieldValueProvider vp;
			try {
				vp = (IFieldValueProvider) ce
						.createExecutableExtension(IConfConstants.ATT_VALUE_PROVIDER);
				return vp;
			} catch (CoreException e) {}
			return null;
	}
	
	
	protected void loadParameterDefinition(){
		IConfigurationElement[] children = ce.getChildren(IConfConstants.ATT_PARAMETERNAME);
		parameterNameSet = new HashSet<String>();
		for(IConfigurationElement child:children){
			String paramName = child.getAttribute(IConfConstants.ATT_NAME);
			if(!Util.isNullorEmpty(paramName)){
				parameterNameSet.add(paramName);
			}
		}
	}

	public DBCollection getCollection(){
		return collection;
	}

}
