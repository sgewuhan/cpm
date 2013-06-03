package com.sg.document.workflow.inputHandler;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.ITaskFormInputHandler;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;

public class ProjectCloseSubmitInputHandler implements ITaskFormInputHandler {

	private static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG002A";// 

	public ProjectCloseSubmitInputHandler() {
	}

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs!=null){
			DBObject project = (DBObject) docs.get("project");
			if(project!=null){
				taskFormData.put("project", project);

				ObjectId projectId = (ObjectId) project.get(IDBConstants.FIELD_SYSID);
				List<DBObject> documents = BusinessService.getWorkService().getProjectDocuments(projectId);
				for (DBObject doc : documents) {
					if("com.sg.cpm.editor.JZ-QR-XG003A--1".equals(doc.get(IDBConstants.FIELD_SYSTEM_EDITOR))){
						DBObject chiefengineer = (DBObject)doc.get("chiefengineer");
						taskFormData.put("chiefengineer", chiefengineer);
					}
				}
			}
		}
		return new SingleObject().setData(taskFormData);
	}

}
