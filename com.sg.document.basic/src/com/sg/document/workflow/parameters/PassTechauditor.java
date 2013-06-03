package com.sg.document.workflow.parameters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class PassTechauditor implements IProcessParameterDelegator {

	public PassTechauditor() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		
		DBObject user = (DBObject) taskFormData.get(taskDatakey);
		return user.get(IDBConstants.FIELD_UID);
	}

}
