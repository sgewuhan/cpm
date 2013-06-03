package com.sg.user;

import com.mongodb.BasicDBList;

public class AuthorityResponse implements IAuthorityResponse {

	private boolean permission;
	private BasicDBList contextList;
	private String message;

	public boolean isPermission() {
		return permission;
	}

	public BasicDBList getContextList() {
		return contextList;
	}

	public String getMessage() {
		return message;
		
	}

	@Override
	public void setPermission(boolean b) {
		permission = b;
	}

	@Override
	public void setPermissionContextList(BasicDBList list) {
		contextList = list;
	}

	@Override
	public void setPermission(boolean b, String message) {
		permission = b;
		this.message = message;
	}

}
