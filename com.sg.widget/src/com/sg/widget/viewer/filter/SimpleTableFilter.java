package com.sg.widget.viewer.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.mongodb.DBObject;
import com.sg.widget.util.Util;


/**
 * 封装表格数据过滤
 *
 */
public class SimpleTableFilter extends ViewerFilter implements NamedViewerFilter{

	protected SimpleFilterCondition fc;	//数据过滤条件
	
	/**
	 * 构造方法
	 * @param fc 数据过滤条件
	 */
	public SimpleTableFilter(SimpleFilterCondition fc) {
		this.fc = fc;
	}

	/**
	  * @Override
	 * 判断传入的对象是否符合条件
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
	 * 根据查询条件判断字符串类型的数据是否符合条件
	 * INCLUDED 使用了拼音匹配模式
	 * @param condition 查询条件
	 * @param value 被判断的值
	 * @return 符合条件返回true,否则返回false
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
	 * 根据查询条件判断数值类型的数据是否符合条件
	 * @param condition 查询条件
	 * @param value 被判断的值
	 * @return 符合条件返回true,否则返回false
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
