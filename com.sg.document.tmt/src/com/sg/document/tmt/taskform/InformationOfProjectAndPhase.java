package com.sg.document.tmt.taskform;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IWorkflowInfoProvider;

public class InformationOfProjectAndPhase implements IWorkflowInfoProvider {

	public InformationOfProjectAndPhase() {
	}

	@Override
	public Object getWorkflowInformation(DBObject workData) {
		DBObject projectData = (DBObject) workData.get("project");
		if(projectData!=null){
			return "ÏîÄ¿:"+projectData.get(IDBConstants.FIELD_DESC);
		}
		return null;
	}

}
