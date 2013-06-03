package com.sg.user;

import com.mongodb.BasicDBList;

public interface IAuthorityResponse {

	public static String MESSAGE_USER_NOT_BELONG_ANY_ORG = "�ܱ�Ǹ�������˻���û�м��뵽�κ�һ����֯����������ǰ�Ĳ�����Ҫ�õ��й���֯����Ȩ��\n�������Ҫ��������������������վ�����֯����Ա��ϵ���������˺ż��뵽��֯��";

	public static String MESSAGE_USER_AND_HIS_ROLE_HAVENOT_PERMISSION = "�ܱ�Ǹ������ǰ�Ĳ�����Ҫ�õ��й���֯����Ȩ��\n�������Ҫ��������������������վ�����֯����Ա��ϵ��";

	public void setPermission(boolean b);

	public void setPermissionContextList(BasicDBList result);

	public void setPermission(boolean b, String message);
	
	public boolean isPermission() ;

	public BasicDBList getContextList() ;

	public String getMessage() ;

}
