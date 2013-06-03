package com.sg.common.ui;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;


public class CurrentUserDefaultValue implements IDefaultValueProvider {

	public CurrentUserDefaultValue() {

	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		ObjectId oid = UserSessionContext.getSession().getUserOId();
		DBObject user = BusinessService.getOrganizationService().getUserObject(oid);
		return DataUtil
				.getRefData(user, IDBConstants.DATA_USER_BASIC);
	}

}
