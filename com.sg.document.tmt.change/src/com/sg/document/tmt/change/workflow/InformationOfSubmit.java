package com.sg.document.tmt.change.workflow;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IWorkflowInfoProvider;

public class InformationOfSubmit implements IWorkflowInfoProvider {

	public InformationOfSubmit() {
	}

	@Override
	public Object getWorkflowInformation(DBObject workData) {
		DBObject project = (DBObject) workData.get("project");
		if(project!=null){
			return "ÏîÄ¿:"+project.get(IDBConstants.FIELD_DESC);
		}else{
			return null;
		}
	}

}
