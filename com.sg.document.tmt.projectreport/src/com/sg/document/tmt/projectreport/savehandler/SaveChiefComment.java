package com.sg.document.tmt.projectreport.savehandler;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.db.DBActivator;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class SaveChiefComment extends SingleObjectEditorDialogCallback {


	private static final String editorId = "com.sg.cpm.editor.projectmonthreport";

	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		
		DBObject workData = input.getInputData().getData();
		String comment = (String) workData.get("comment");
		
		WorkService service = BusinessService.getWorkService();
		List<DBObject> documents = service.getDocumentOfWork((ObjectId) workData.get(WorkService.FIELD_SYSID), editorId);
		
		if(documents!=null&&documents.size()>0){
			DBObject docData = documents.get(0);
			docData.put("comment", comment);
			
			DBCollection docCol = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
			
			docCol.save(docData);
		}
		return false;
	}


}
