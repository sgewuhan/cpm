package com.sg.common.ui;

import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;


public class CurrentUserDefaultValue2 implements IDefaultValueProvider {

	public CurrentUserDefaultValue2() {

	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		UserSessionContext session = UserSessionContext.getSession();
		return session.getUserFullName()+"/"+session.getUserName();
	}

}
