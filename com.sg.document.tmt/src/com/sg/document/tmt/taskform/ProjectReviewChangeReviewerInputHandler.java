package com.sg.document.tmt.taskform;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.ITaskFormInputHandler;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;

public class ProjectReviewChangeReviewerInputHandler implements
		ITaskFormInputHandler {
	private static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG002A-01";// 

	public ProjectReviewChangeReviewerInputHandler() {
	}

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs!=null){
			taskFormData.put("act_review_convener", docs.get("act_review_convener"));
			taskFormData.put("act_reviewer_list", docs.get("act_reviewer_list"));
		}
		return new SingleObject().setData(taskFormData);
	}

}
