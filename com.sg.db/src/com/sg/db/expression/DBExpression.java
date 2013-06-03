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
		//��ȡ��������
		if(parameterNameSet==null){
			loadParameterDefinition();
		}
		//����δ����Ĳ���
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

	//******zhonghua 12/3/6*************************���ǵ�ֻ�в�ѯ��ʹ�����µķ�����ȡ������������
//	public abstract Object run(Map<String, String> paramValueMap,
//			String returnFieldList);
//
//	public abstract Object run(Map<String, String> paramValueMap,
//			String returnFieldList, int skip, int limit) ;
	//******zhonghua 12/3/6*************************

	
//	/**
//	 * ��������޷����� ��ʽ������Ϊ null ��û�����õ����������ȡ��Ϊ�ֱ�������µķ���
//	 * getParameterValueProvider()
//	 * getStringInputValueFromParameter(),�������������Ϊnullʱ��ʾ���û�û�д������������ֵ
//	 * getTypedValue(String inputValue),��������Ǹ���������ת���ַ���ֵ������ֵ��[null]��ʾ���ø��ֶ�Ϊ��
//	 * @param ce
//	 * @return
//	 */
//	@Deprecated
//	protected Object createSingleValueObject(IConfigurationElement ce) {
//		try {
//			IFieldValueProvider vp = (IFieldValueProvider) ce
//					.createExecutableExtension(IConfConstants.ATT_VALUE_PROVIDER);// ע���˲�ѯ����
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
