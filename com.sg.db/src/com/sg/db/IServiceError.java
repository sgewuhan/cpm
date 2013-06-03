package com.sg.db;

public interface IServiceError {

	
	public static final String SUCCESS = "#200,SUCCESS";

	
	public static final  String WAR_UPDATE_NO_RESULT = "#200,WAR_UPDATE_NO_RESULT";
	public static final  String WAR_FIND_NO_RESULT = "#200,WAR_FIND_NO_RESULT";
	public static final String WAR_UNSUPPORT = "#200,WAR_UN_SUPPORT_OPERATION";

	
	public static final  String ERR_NULL_QUERYOBJECT = "#500,ERR_NULL_QUERYOBJECT";
	public static final  String ERR_NULL_UPDATEOBJECT = "#500,ERR_NULL_UPDATEOBJECT";

	public static final  String ERR_NOT_ENOUGH_PARAMETER = "#400,ERR_NOT_ENOUGH_PARAMETER";
	public static final  String ERR_CANNOT_GET_VALUE_FROM_PARAMETER_OR_INPUT = "#400,ERR_CANNOT_GET_VALUE_FROM_PARAMETER_OR_INPUT";

	public static final  String ERR_INVALID_EXPRESSION = "#400,ERR_INVALID_EXPRESSION";

	public static final  String ERR_EMPTY_QUERY_EXPRESSION = "#400,ERR_EMPTY_QUERY_EXPRESSION";
	public static final  String ERR_EMPTY_INPUT_PARAMETER = "#400,ERR_EMPTY_INPUT_PARAMETER";
	public static final  String ERR_EMPTY_UPDATE_EXPRESSION = "#400,ERR_EMPTY_UPDATE_EXPRESSION";

	public static final  String ERR_UNKNOWN = "#500,ERR_UNKNOWN";
	
	
	public static final String DATABASE_RESETFAILED = "#500,DATABASE_RESETFAILED";
	

	
}
