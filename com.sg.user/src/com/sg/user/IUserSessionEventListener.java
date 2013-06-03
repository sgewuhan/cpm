package com.sg.user;

public interface IUserSessionEventListener {

	public void fireUserSessionEvent(String method, String arg, ISessionAuthorityControl sc);

}
