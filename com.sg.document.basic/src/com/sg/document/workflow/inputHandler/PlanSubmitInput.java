package com.sg.document.workflow.inputHandler;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.ITaskFormInputHandler;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;

public class PlanSubmitInput implements ITaskFormInputHandler {

	public PlanSubmitInput() {
	}

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG004A");
		if(docs!=null){
			taskFormData.put("techauditor", docs.get("techauditor"));
		}
		return new SingleObject().setData(taskFormData);
	}

}
