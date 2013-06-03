package com.sg.user;

public interface ISessionAuthorityControl {

	public final static int TYPE_COLLECTION = 1;

	public final static int TYPE_OBJECT = 0;

	public final static int TYPE_ADMIN = 2;

	public static final int TYPE_UNKNOW = 9;

	public int getObjectType();
	
	public String getDisplayText();

}
