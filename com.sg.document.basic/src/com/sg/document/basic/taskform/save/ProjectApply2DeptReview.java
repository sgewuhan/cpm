package com.sg.document.basic.taskform.save;

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

public class ProjectApply2DeptReview extends SingleObjectEditorDialogCallback {


	private static final String editorId = "com.sg.cpm.editor.JZ-QR-XG003A--1";

	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		
		DBObject workData = input.getInputData().getData();
		//获得项目负责人数据
		DBObject pm = (DBObject) workData.get("projectManager");
		//获得预算数据
//		Double budget = (Double) workData.get("budget");
		
		WorkService service = BusinessService.getWorkService();
		List<DBObject> documents = service.getDocumentOfWork((ObjectId) workData.get(WorkService.FIELD_SYSID), editorId);
		
		if(documents!=null&&documents.size()>0){
			DBObject docData = documents.get(0);
			docData.put("pm", pm);
//			docData.put("budget", budget);
			
			DBCollection docCol = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
			
			docCol.save(docData);
		}
		return false;
	}


}
