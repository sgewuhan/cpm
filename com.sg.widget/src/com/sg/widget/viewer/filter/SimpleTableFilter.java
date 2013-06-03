package com.sg.widget.viewer.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.mongodb.DBObject;
import com.sg.widget.util.Util;


/**
 * ��װ������ݹ���
 *
 */
public class SimpleTableFilter extends ViewerFilter implements NamedViewerFilter{

	protected SimpleFilterCondition fc;	//���ݹ�������
	
	/**
	 * ���췽��
	 * @param fc ���ݹ�������
	 */
	public SimpleTableFilter(SimpleFilterCondition fc) {
		this.fc = fc;
	}

	/**
	  * @Override
	 * �жϴ���Ķ����Ƿ��������
	 */
	public boolean select(Viewer viewer, Object parentElement,
			Object element) {
		if(!(element instanceof DBObject)){
			return false;
		}
		DBObject row = (DBObject)element;
		Object value = row.get(fc.name);
		
		if(fc.condition == value)
		{
			return true;
		}
		if(value == null)
		{
			return false;
		}
		if(SimpleFilterCondition.TYPE_NUMBER.equals(fc.type)){
			try{
				return typeNumber((Number)fc.condition,Double.parseDouble((String)value));
			}catch(Exception e){
			}
		}else{
			return typeString((String)fc.condition,(String)value);
		}
		return true;
	}
	
	/**
	 * ���ݲ�ѯ�����ж��ַ������͵������Ƿ��������
	 * INCLUDED ʹ����ƴ��ƥ��ģʽ
	 * @param condition ��ѯ����
	 * @param value ���жϵ�ֵ
	 * @return ������������true,���򷵻�false
	 */
	protected boolean typeString(String condition,String value)
	{
		if(fc.operator.equals(SimpleFilterCondition.EQUALS))
		{
			return condition.equals(value);
		}
		if(fc.operator.equals(SimpleFilterCondition.INCLUDED))
		{
			if(condition.length() == condition.getBytes().length
					&& value.length() != value.getBytes().length){
				String alphaValue = Util.String2Alpha(value);
				String alphaCond = Util.String2Alpha(condition);
				return alphaValue.contains(alphaCond);
			}else{
				return value.toUpperCase().contains(condition.toUpperCase());
			}
		}
		if(fc.operator.equals(SimpleFilterCondition.NOT_INCLUDED))
		{
			if(condition.length() == condition.getBytes().length
					&& value.length() != value.getBytes().length){
				String alpValue = Util.String2Alpha(value);
				String alpCond = Util.String2Alpha(condition);
				return !alpValue.contains(alpCond);
			}	
			else{
				return !value.toUpperCase().contains(condition.toUpperCase());
			}
		}
		return false;
	}
	
	/**
	 * ���ݲ�ѯ�����ж���ֵ���͵������Ƿ��������
	 * @param condition ��ѯ����
	 * @param value ���жϵ�ֵ
	 * @return ������������true,���򷵻�false
	 */
	private boolean typeNumber(Number condition,Number value)
	{
		
		if(fc.operator.equals(SimpleFilterCondition.EQUALS))
		{
			return condition.doubleValue() == value.doubleValue() ;
		}
		if(fc.operator.equals(SimpleFilterCondition.NO_EQUALS))
		{
			return !(condition.doubleValue() == value.doubleValue());
		}
		if(fc.operator.equals(SimpleFilterCondition.GREATER_THAN))
		{
			return condition.doubleValue() < value.doubleValue() ;
		}
		if(fc.operator.equals(SimpleFilterCondition.GREATER_THAN_OR_EQUAL))
		{
			return condition.doubleValue() <= value.doubleValue() ;
		}
		if(fc.operator.equals(SimpleFilterCondition.LESS_THAN))
		{
			return condition.doubleValue() > value.doubleValue() ;
		}
		if(fc.operator.equals(SimpleFilterCondition.LESS_THAN_OR_EQUAL))
		{
			return condition.doubleValue() >= value.doubleValue() ;
		}
		return false;
	}

	@Override
	public String getName() {
		return fc.title + fc.operator +fc.condition;
	}
	
}
