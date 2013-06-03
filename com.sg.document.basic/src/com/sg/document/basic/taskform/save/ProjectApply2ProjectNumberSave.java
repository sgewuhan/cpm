package com.sg.document.basic.taskform.save;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.db.DBActivator;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class ProjectApply2ProjectNumberSave extends SingleObjectEditorDialogCallback {


	private static final String editorId = "com.sg.cpm.editor.JZ-QR-XG003A--1";
	
	
	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		//get dept
		DBObject workData = input.getInputData().getData();
		String projectNumber =  (String) workData.get("id");
		WorkService service = BusinessService.getWorkService();
		List<DBObject> documents = service.getDocumentOfWork((ObjectId) workData.get(WorkService.FIELD_SYSID), editorId);
		if(documents!=null&&documents.size()>0){
			DBObject docData = documents.get(0);
			DBCollection docCol = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
			docCol.update(new BasicDBObject().append(IDBConstants.FIELD_SYSID,docData.get(IDBConstants.FIELD_SYSID) ),new BasicDBObject().append("$set", new BasicDBObject().append("projectnumber", projectNumber)) );
		}
		return false;
	}


}
