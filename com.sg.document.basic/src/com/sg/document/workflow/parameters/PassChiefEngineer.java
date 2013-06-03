package com.sg.document.workflow.parameters;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class PassChiefEngineer implements IProcessParameterDelegator {

	public PassChiefEngineer() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG003A--1");
		DBObject user = (DBObject) doc.get("chiefengineer");
		Assert.isNotNull(user, "首席师为空，无法提取流程所需的参数");
		return user.get(OrganizationService.FIELD_UID);
	}

}
