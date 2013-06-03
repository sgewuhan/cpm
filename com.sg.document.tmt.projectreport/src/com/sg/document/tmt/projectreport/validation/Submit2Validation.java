package com.sg.document.tmt.projectreport.validation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class Submit2Validation implements IValidationHandler {
	private String message;

	public Submit2Validation() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument(
				(ObjectId) workId, "com.sg.cpm.editor.projectmonthreport");
		if (doc == null) {
			message = "����Ҫ����±�����д";
			return false;
		}

		if (doc.get("costFinishedMonth") == null) {
			message = "����Ҫ��д����Ŀ���������ķ���";
			return false;
		}


		message = null;
		return true;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
