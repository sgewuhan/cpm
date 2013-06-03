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
		//�������ר�ҵ�����Ƿ񶼻ظ���
		
		//����ĵ�
		
		message = "";
		
		
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG002A-01");
		if(doc==null){
			message = "����Ҫ�ύ�������Ϻ�����ύ�������";
			return false;
		}

		BasicDBList reviewcomment = (BasicDBList) doc.get("reviewcomment");
		BasicDBList replycomment = (BasicDBList) doc.get("replycomment");

		// ����һ�����ṹ
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
				message = "����û�лظ� "+quest.get("actor")+" ��������⡣������������ĵ����ظ��������ύ";
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
