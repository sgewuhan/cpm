package com.sg.document.tmt.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.common.service.WorkService;
import com.sg.widget.util.Util;

public class ArchiveService extends ServiceProvider {

	public ArchiveService() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {

		String workId = (String) getInputValue("workId");
		WorkService workService = BusinessService.getWorkService();
		List<DBObject> docList = workService.getDocumentOfWork(new ObjectId(
				workId), "com.sg.cpm.editor.JZ-QR-XG002A-01");
		if (docList == null || docList.size() < 1) {
			return null;
		}

		DBObject doc = docList.get(0);

		String fieldName = getOperation();
		BasicDBList fileList = new BasicDBList();

		// -----------------------------------------------------------------
		// �����������
		appendList(doc, "applyment", "��Ŀ���������", fileList,"1");

		// -----------------------------------------------------------------
		// ����ύ���������
		appendList(doc, "attachment", "��Ŀ��������", fileList,"2");

		// -----------------------------------------------------------------
		// �������ϸ����ļ�
		DBObject projectData = (DBObject) doc.get("project");

		ObjectId projectId = (ObjectId) projectData
				.get(IDBConstants.FIELD_SYSID);
		List<DBObject> documents = workService.getProjectDocuments(projectId);
		for (int i = 0; i < documents.size(); i++) {
			DBObject pjdoc = documents.get(i);
			String fileName = "��Ŀ�ĵ�_"+(String) pjdoc.get(IDBConstants.FIELD_DESC);

			appendList(pjdoc, "attachment", fileName, fileList,"3."+i);
		}

		// -----------------------------------------------------------------
		// ���ר������Լ��ظ�
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
		int index = 0;
		while (iter.hasNext()) {
			ObjectId _id = iter.next();
			DBObject quest = questionMap.get(_id);
			String actor = (String) quest.get("actor");
			Date date = (Date) quest.get("date");
			SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYYMMDDHHMMSS);
			String questFileName = "ר�����_"+actor.substring(0, actor.indexOf("/")) + "_"+ sdf.format(date);
			String replyFileName = "��_"+questFileName;
			
			appendList(quest, "comment_attachment", questFileName, fileList,"4."+index+"."+0);
			
			List<DBObject> replyList = replyMap.get(_id);
			if(replyList!=null){
				for(int i=0;i<replyList.size();i++){
					DBObject replyData = replyList.get(i);
					appendList(replyData, "comment_attachment", replyFileName, fileList,"4."+index+"."+(i+1));
				}
			}
			
			index ++;
		}

		// -----------------------------------------------------------------
		// ��û��������¼
		appendList(doc, "process_attachment", "��Ŀ��������¼", fileList,"5");

		// -----------------------------------------------------------------
		// ������ĵ��ĵ�
		List<DBObject> docList2 = workService.getDocumentOfWork(new ObjectId(
				workId), "com.sg.cpm.editor.changeplan");
		if (docList2 != null && docList2.size() > 0) {
			DBObject doc2 = docList2.get(0);
			appendList(doc2, "attachment", "��Ŀ�����ļ�", fileList,"6");
		}

		doc.put(fieldName, fileList);
		DocumentService docService = BusinessService.getDocumentService();
		docService.saveDocument(doc);
		return null;
	}

	private void appendList(DBObject doc, String fieldName, String fileName,
			BasicDBList fileList, String index) {
		BasicDBList files = (BasicDBList) doc.get(fieldName);
		if (files != null) {
			for (int i = 0; i < files.size(); i++) {
				DBObject o = (DBObject) files.get(0);
				changeName(o, index+"."+i+"."+fileName);
				fileList.add(o);
			}
		}
	}

	private void changeName(DBObject o, String newFileName) {
		String originalFileName = (String) o.get("fileName");
		o.put("fileName",
				newFileName
						+ originalFileName.substring(originalFileName
								.lastIndexOf(".")));
	}
}
