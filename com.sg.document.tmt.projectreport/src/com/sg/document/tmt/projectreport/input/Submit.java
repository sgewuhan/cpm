package com.sg.document.tmt.projectreport.input;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.ITaskFormInputHandler;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;

public class Submit implements ITaskFormInputHandler {

	public Submit() {
	}

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.projectmonthreport");
		if(docs!=null){
			DBObject project = (DBObject) docs.get("project");
			if(project!=null){
				taskFormData.put("project", project);
				ObjectId projectId = (ObjectId) project.get(IDBConstants.FIELD_SYSID);
				List<DBObject> doc = BusinessService.getWorkService().getProjectDocuments(projectId, "com.sg.cpm.editor.JZ-QR-XG004A");//科研开发任务书
				DBObject chiefEngineer = null;
				
				if(!doc.isEmpty()){
					BasicDBList users = (BasicDBList)doc.get(0).get("director");
					chiefEngineer = (DBObject) users.get(0);
				}else{
					doc = BusinessService.getWorkService().getProjectDocuments(projectId, "com.sg.cpm.editor.JZ-QR-XG003A--1");//委托书
					if(!doc.isEmpty()){
						chiefEngineer = (DBObject) doc.get(0).get("chiefengineer");
					}
				}
				if(chiefEngineer!=null){
					taskFormData.put("chiefengineer", chiefEngineer);
				}
			}
			
			Object year = docs.get("year");
			taskFormData.put("year", year);

			Object month = docs.get("month");
			taskFormData.put("month", month);

		}
		
		
		return new SingleObject().setData(taskFormData);
	}

}
