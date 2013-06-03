package com.sg.document.workflow.parameters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class PassProjectId implements IProcessParameterDelegator {

	public PassProjectId() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		DBObject projectData =  (DBObject) taskFormData.get("project");
		return projectData.get("_id").toString();
	}

}
