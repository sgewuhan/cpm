package com.sg.widget.editor.field.defaultvalue;

import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;

public class SessionUserIdValueProvider implements IDefaultValueProvider {

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		return UserSessionContext.getSession().getUserId();
	}

}
