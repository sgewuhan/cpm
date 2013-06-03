package com.sg.document.tmt.taskform;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.db.DBActivator;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class SaveReviewComment extends SingleObjectEditorDialogCallback {
	
	private DocumentService docService;

	public SaveReviewComment() {
		docService = BusinessService.getDocumentService();
	}
	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		DBObject inputData = input.getInputData().getData();
		Object choice = inputData.get("choice");
		/*
		 *无论是否通过，都可能有专家意见，需要把专家意见记录下来 
		 */
//		if("通过".equals(choice)){
//			return false;
//		}

		ObjectId workOid = (ObjectId) inputData.get(
				IDBConstants.FIELD_SYSID);

		Object comment = inputData.get("comment");
		Object comment_attachment = inputData.get("comment_attachment");
		Object date = inputData.get("date");
		Object actor = inputData.get("actor");
		
		BasicDBObject commentData = new BasicDBObject();
		commentData.put("comment", comment);
		commentData.put("comment_attachment", comment_attachment);
		commentData.put("choice", choice);
		commentData.put("date", date);
		commentData.put("actor", actor);
		commentData.put("_id", new ObjectId());
		
		// 项目评审申请表
		DBObject doc = docService.getWorkDocument(workOid,
				"com.sg.cpm.editor.JZ-QR-XG002A-01");
		
		if(doc==null){
			return false;
		}
		
		ObjectId _id = (ObjectId) doc.get(IDBConstants.FIELD_SYSID);

		DBCollection docCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
		docCollection.update(new BasicDBObject().append(IDBConstants.FIELD_SYSID, _id), 
				new BasicDBObject().append("$push", new BasicDBObject().append("reviewcomment", commentData)));
		
		return false;
	}


}
