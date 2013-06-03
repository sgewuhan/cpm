package com.sg.widget.viewer.filter;


/**
 * ��װ��ѯ���ݵĹ�������
 *
 */
public class SimpleFilterCondition
{
	//��ѯ�������߼������
	public static final String GREATER_THAN  = ">";
	public static final String GREATER_THAN_OR_EQUAL  = ">=";
	public static final String LESS_THAN = "<";
	public static final String LESS_THAN_OR_EQUAL = "<=";
	public static final String EQUALS = "=";
	public static final String NO_EQUALS = "��";
	public static final String INCLUDED = "����";
	public static final String NOT_INCLUDED = "������";
	
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
	 * ���췽��
	 * @param dmflist ���е��ֶ��б�
	 * @param dmf ��ѯ�����õ��ֶ�
	 * @param operator ��ѯ�����������
	 * @param condition ��ѯ����������ֵ
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
	 * ��ȡ��ѯ���������
	 * @return
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * ���ò�ѯ���������
	 * @param operator
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * ��ȡ��ѯ��������ֵ
	 * @return
	 */
	public Object getCondition() {
		return condition;
	}
	
}
