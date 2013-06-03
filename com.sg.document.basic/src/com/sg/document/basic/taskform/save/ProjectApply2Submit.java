package com.sg.document.basic.taskform.save;

import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class ProjectApply2Submit extends SingleObjectEditorDialogCallback {

//	private static final String editorId = "com.sg.cpm.editor.JZ-QR-XG003A--1";

	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		// get dept
//		DBObject workData = input.getInputData().getData();
//		ObjectId deptId = (ObjectId) workData.get("dept");
//		DBObject chiefengineer = (DBObject) workData.get("chiefengineer");
//		String deptPath = BusinessService.getOrganizationService().getOBSPath(
//				deptId);
//		WorkService service = BusinessService.getWorkService();
//		List<DBObject> documents = service.getDocumentOfWork(
//				(ObjectId) workData.get(WorkService.FIELD_SYSID), editorId);
//		if (documents != null && documents.size() > 0) {
//			DBObject docData = documents.get(0);
//			DBCollection docCol = DBActivator
//					.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
//			docCol.update(new BasicDBObject().append(IDBConstants.FIELD_SYSID,
//					docData.get(IDBConstants.FIELD_SYSID)), new BasicDBObject()
//					.append("$set",new BasicDBObject()
//								.append("partyb", deptPath)
//								.append("dept", deptId)
//								.append("chiefengineer", chiefengineer)));
//		}
		return false;
	}

}
