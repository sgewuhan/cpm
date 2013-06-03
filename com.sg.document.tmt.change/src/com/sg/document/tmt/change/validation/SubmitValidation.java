package com.sg.document.tmt.change.validation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class SubmitValidation implements IValidationHandler {

	private String message;

	public SubmitValidation() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument(
				(ObjectId) workId, "com.sg.cpm.editor.JZ-QR-PCN");
		if (docs == null) {
			message = "您需要完成变更申请的填写";
			return false;
		} else {

			if (docs.get("project") == null) {
				message = "您需要完成变更申请的填写";
				return false;
			} else {
				message = null;
				return true;
			}
		}
	}

	@Override
	public String getMessage() {
		return message;
	}

}
