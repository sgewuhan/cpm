package com.sg.widget.model;

import com.sg.user.ISessionAuthorityControl;

public abstract class ActiveCollection implements ISessionAuthorityControl{
	

	public abstract String getCollectionName();
}
