package com.sg.common.model;

import java.util.ArrayList;
import java.util.List;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class UserOption implements IOptionProvider {

	public UserOption() {
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		List<ISingleObject> userList = DataUtil.getSiteUsers(UserSessionContext
				.getSession().getSiteContextId(), true);
		List<Enumerate> list = new ArrayList<Enumerate>();
		for (ISingleObject user : userList) {
			list.add(new Enumerate(user
					.getText(IDBConstants.FIELD_NAME),
					user.getText(IDBConstants.FIELD_DESC)
					+ " "
					+ user.getText(IDBConstants.FIELD_EMAIL), DataUtil
					.getRefData(user.getData(), IDBConstants.DATA_USER_BASIC),
					null));
		}
		Enumerate e = new Enumerate(key, data.toString(), data, list);
		return e;
	}

}
