package com.sg.document.tmt.projectreport.input;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.db.model.ISingleObject;

public class Submit2 extends Submit{

	@Override
	public ISingleObject getTaskFormInputData(DBObject taskFormData,
			TaskFormConfig taskFormConfig) {
		ISingleObject data = super.getTaskFormInputData(taskFormData, taskFormConfig);
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.projectmonthreport");
		Object costFinishedMonth = docs.get("costFinishedMonth");
		data.setValue("costFinishedMonth", costFinishedMonth, null, false);
		
		return data;
	}
	
	
}
