package com.sg.document.tmt.change.parameters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.service.OrganizationService;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class ChiefEngineer implements IProcessParameterDelegator {


	public ChiefEngineer() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		DBObject chiefengineer = (DBObject) taskFormData.get("chiefengineer");
		return chiefengineer.get(OrganizationService.FIELD_UID);
	}

}
