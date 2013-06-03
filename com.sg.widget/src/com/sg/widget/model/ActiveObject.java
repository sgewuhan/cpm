package com.sg.widget.model;

import com.sg.user.ISessionAuthorityControl;

@Deprecated
public interface ActiveObject extends ISessionAuthorityControl{

	public void remove();

	public String getCollection();
	
}
