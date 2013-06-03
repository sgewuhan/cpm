package com.sg.document.workflow.information;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IWorkflowInfoProvider;

public class Project2DeptReviewInfo implements IWorkflowInfoProvider {

	public Project2DeptReviewInfo() {
	}

	@Override
	public Object getWorkflowInformation(DBObject workData) {
		StringBuffer information = new StringBuffer();
		DBObject projectManager = (DBObject) workData.get("projectManager");
		if(projectManager!=null){
			information.append("ȷ����Ŀ�����ˣ�");
			information.append(projectManager.get(IDBConstants.FIELD_NAME));
		}
		
		DBObject chiefengineer = (DBObject) workData.get("chiefengineer");
		if(chiefengineer!=null){
			information.append(", ��ϯʦ��");
			information.append(chiefengineer.get(IDBConstants.FIELD_NAME));
		}
		
		return information.toString();
	}

}
