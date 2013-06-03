package com.sg.document.tmt.taskform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class ReplyValidator implements IValidationHandler {

	private String message;

	public ReplyValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		//检查所有专家的意见是否都回复了
		
		//获得文档
		
		message = "";
		
		
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG002A-01");
		if(doc==null){
			message = "您需要提交评审资料后才能提交这个任务。";
			return false;
		}

		BasicDBList reviewcomment = (BasicDBList) doc.get("reviewcomment");
		BasicDBList replycomment = (BasicDBList) doc.get("replycomment");

		// 构造一个树结构
		HashMap<ObjectId, DBObject> questionMap = new HashMap<ObjectId, DBObject>();
		HashMap<ObjectId, List<DBObject>> replyMap = new HashMap<ObjectId, List<DBObject>>();

		if (reviewcomment != null) {
			for (int i = 0; i < reviewcomment.size(); i++) {
				DBObject item = (DBObject) reviewcomment.get(i);
				questionMap.put((ObjectId) item.get(IDBConstants.FIELD_SYSID),
						item);
			}
		}

		if (replycomment != null) {
			for (int i = 0; i < replycomment.size(); i++) {
				DBObject item = (DBObject) replycomment.get(i);
				ObjectId reviewId = (ObjectId) item.get("reviewId");
				List<DBObject> replyList = replyMap.get(reviewId);
				if (replyList == null) {
					replyList = new ArrayList<DBObject>();
				}
				replyList.add(item);
				replyMap.put(reviewId, replyList);
			}
		}

		Iterator<ObjectId> iter = questionMap.keySet().iterator();
		while(iter.hasNext()){
			ObjectId key = iter.next();
			List<DBObject> reply = replyMap.get(key);
			if(reply==null||reply.isEmpty()){
				DBObject quest = questionMap.get(key);
				message = "您还没有回复 "+quest.get("actor")+" 提出的问题。请打开评审资料文档，回复后重新提交";
				return false;
			}
		}
		
		
		return true;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
