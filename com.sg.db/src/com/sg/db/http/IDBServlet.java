package com.sg.db.http;

public interface IDBServlet {
	
	public static final int EXP_PARAM = 0;// 表达式的id

	public static final int SKIP = 1;// skip属性设置

	public static final int LIMIT = 2;// 取记录的条数的设置

	public static final int RETURN = 3;// 返回字段的参数，用逗号分隔

	public static final int SORT = 4;// 排序

	public static final int REMOVE = 5;

	public static final int RETURN_NEW = 6;

	public static final int UPSERT = 7;

	public static final int QUERY_EXP = 8;

	public static final int UPDATE_EXP = 9;
	
	public static final int MULTI = 10;
	

	public static final String[] KEEPS = new String[] { "exp", "skip", "limit", "return", "sort", "remove", 
		"returnNew", "upsert", "queryexp", "updateexp", "multi" };
}
