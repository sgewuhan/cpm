package com.sg.document.tmt.taskform;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class SaveReviewer extends SingleObjectEditorDialogCallback {

	private DocumentService docService;

	public SaveReviewer() {
		docService = BusinessService.getDocumentService();
	}

	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		ObjectId workOid = (ObjectId) input.getInputData().getValue(
				IDBConstants.FIELD_SYSID);
		Object act_review_convener = input.getInputData().getValue(
				"act_review_convener");
		Object act_reviewer_list = input.getInputData().getValue(
				"act_reviewer_list");
		// œÓƒø∆¿…Û…Í«Î±Ì
		DBObject doc = docService.getWorkDocument(workOid,
				"com.sg.cpm.editor.JZ-QR-XG002A-01");
		doc.put("act_review_convener", act_review_convener);
		doc.put("act_reviewer_list", act_reviewer_list);

		docService.saveDocument(doc);
		return false;
	}

}
