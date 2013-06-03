package com.sg.document.tmt.projectreport.taskparameter;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class GetActChiefEngineer implements IProcessParameterDelegator {

	public GetActChiefEngineer() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		DBObject chiefengineer = (DBObject) taskFormData.get("chiefengineer");
		return chiefengineer.get(IDBConstants.FIELD_UID);
	}

}
