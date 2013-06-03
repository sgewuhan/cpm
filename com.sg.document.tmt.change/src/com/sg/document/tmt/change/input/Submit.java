package com.sg.document.tmt.change.input;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.ITaskFormInputHandler;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;

public class Submit implements ITaskFormInputHandler {

	private static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-PCN";// 

	public Submit() {
	}

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs!=null){
			taskFormData.put("project", docs.get("project"));
		}
		return new SingleObject().setData(taskFormData);
	}

}
