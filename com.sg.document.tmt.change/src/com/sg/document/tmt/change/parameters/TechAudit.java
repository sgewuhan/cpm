package com.sg.document.tmt.change.parameters;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class TechAudit implements IProcessParameterDelegator {

	public TechAudit() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		//如果是非技术支持类的
		DBObject projectData =  (DBObject) taskFormData.get("project");
		ObjectId projectId = (ObjectId) projectData.get("_id");
		WorkService workService = BusinessService.getWorkService();
		List<DBObject> documents = workService.getProjectDocuments(projectId);
		for (DBObject doc : documents) {
			if("com.sg.cpm.editor.JZ-QR-XG004A".equals(doc.get(IDBConstants.FIELD_SYSTEM_EDITOR))){//科研开发任务书
				DBObject techauditor = (DBObject) doc.get("techauditor");
				if(techauditor!=null){
					return techauditor.get(IDBConstants.FIELD_UID);
				}
			}
		}
		return "";
	}

}
