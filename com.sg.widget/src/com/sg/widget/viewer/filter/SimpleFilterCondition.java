package com.sg.widget.viewer.filter;


/**
 * 封装查询数据的过滤条件
 *
 */
public class SimpleFilterCondition
{
	//查询条件的逻辑适配符
	public static final String GREATER_THAN  = ">";
	public static final String GREATER_THAN_OR_EQUAL  = ">=";
	public static final String LESS_THAN = "<";
	public static final String LESS_THAN_OR_EQUAL = "<=";
	public static final String EQUALS = "=";
	public static final String NO_EQUALS = "≠";
	public static final String INCLUDED = "包含";
	public static final String NOT_INCLUDED = "不包含";
	
	public static final String TYPE_NUMBER = "Number";
	public static final String TYPE_STRING = "String";
	public static final String TYPE_DATE = "Date";
	
	
//	public DTOColumn dtoCol;
	public String type;
	public String name;
	public String operator;
	public Object condition;
	public String title;
	
	/**
	 * 构造方法
	 * @param dmflist 所有的字段列表
	 * @param dmf 查询条件用的字段
	 * @param operator 查询条件的适配符
	 * @param condition 查询条件的适配值
	 * @param condition2 
	 */
	public SimpleFilterCondition(String name,String title,String type,String operator,Object condition)
	{
//		this.dtoCol = dtoCol;
		this.type = type;
		this.name = name;
		this.operator = operator;
		this.condition = condition;
		this.title = title;
	}
	


	/**
	 * 获取查询条件适配符
	 * @return
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * 设置查询条件适配符
	 * @param operator
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * 获取查询条件适配值
	 * @return
	 */
	public Object getCondition() {
		return condition;
	}
	
}
