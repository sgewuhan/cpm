package com.sg.document.tmt.taskform;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class SaveProcessAttarchement extends SingleObjectEditorDialogCallback {
	
	private DocumentService docService;

	public SaveProcessAttarchement() {
		docService = BusinessService.getDocumentService();
	}
	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		ObjectId workOid = (ObjectId) input.getInputData().getValue(
				IDBConstants.FIELD_SYSID);
		BasicDBList process_attachment = (BasicDBList) input.getInputData().getValue(
				"process_attachment");
		if(process_attachment==null||process_attachment.size()==0){
			return false;
		}

		// œÓƒø∆¿…Û…Í«Î±Ì
		DBObject doc = docService.getWorkDocument(workOid,
				"com.sg.cpm.editor.JZ-QR-XG002A-01");
		BasicDBList docAtta = (BasicDBList) doc.get("process_attachment");
		if(docAtta==null){
			docAtta = new BasicDBList();
		}
		
		docAtta.addAll(0, process_attachment);
		doc.put("process_attachment", docAtta);

		docService.saveDocument(doc);
		return false;
	}


}
