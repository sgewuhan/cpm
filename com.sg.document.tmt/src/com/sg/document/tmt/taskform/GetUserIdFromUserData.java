package com.sg.document.tmt.taskform;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class GetUserIdFromUserData implements IProcessParameterDelegator {

	public GetUserIdFromUserData() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		DBObject userData = (DBObject) taskFormData.get(taskDatakey);
		return userData.get(IDBConstants.FIELD_UID);
	}

}
