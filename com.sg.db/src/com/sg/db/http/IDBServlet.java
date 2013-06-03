package com.sg.db.http;

public interface IDBServlet {
	
	public static final int EXP_PARAM = 0;// ���ʽ��id

	public static final int SKIP = 1;// skip��������

	public static final int LIMIT = 2;// ȡ��¼������������

	public static final int RETURN = 3;// �����ֶεĲ������ö��ŷָ�

	public static final int SORT = 4;// ����

	public static final int REMOVE = 5;

	public static final int RETURN_NEW = 6;

	public static final int UPSERT = 7;

	public static final int QUERY_EXP = 8;

	public static final int UPDATE_EXP = 9;
	
	public static final int MULTI = 10;
	

	public static final String[] KEEPS = new String[] { "exp", "skip", "limit", "return", "sort", "remove", 
		"returnNew", "upsert", "queryexp", "updateexp", "multi" };
}
