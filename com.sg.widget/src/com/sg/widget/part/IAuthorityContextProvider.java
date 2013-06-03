package com.sg.widget.part;

import com.sg.user.ISessionAuthorityControl;

public interface IAuthorityContextProvider extends ISessionAuthorityControl{

//	public String getEditorConfigruation();
	
	public String getAuthorityContextCollectionName();

	public boolean activeCollectionAdaptable();

}
