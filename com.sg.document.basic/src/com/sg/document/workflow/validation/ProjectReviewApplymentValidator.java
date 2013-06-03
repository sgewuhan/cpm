package com.sg.document.workflow.validation;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.common.workflow.IValidationHandler;

public class ProjectReviewApplymentValidator implements IValidationHandler {

	private String message;
	private static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG002A-01";//

	public ProjectReviewApplymentValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		// �ж��Ƿ�ѡ������Ŀ
		message = "";
		// ��ù���id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DocumentService documentService = BusinessService.getDocumentService();
		DBObject docs = documentService.getWorkDocument((ObjectId) workId,
				SOURCE_EDITOR_ID);
		if (docs == null) {
			message = "����Ҫ�����Ŀ������������д������ύ�������";
			return false;
		}

		DBObject project = (DBObject) docs.get("project");
		if (project == null) {
			message = "����Ҫ����Ŀ�����������ѡ���ύ�������Ŀ������д��Щ��Ҫ��Ϣ�����ύ��";
			return false;
		}

		BasicDBList attachment = (BasicDBList) docs.get("attachment");
		if (!(attachment instanceof BasicDBList)
				|| ((BasicDBList) attachment).size() < 1) {
			message = "����Ҫ����Ŀ�������������������ϵĵ����ļ������������Щ��Ҫ��Ϣ�����ύ��";
			return false;
		}

		if ("��������".equals(docs.get("stage"))) {
			// �ж��Ƿ��ǽ�����������ǽ��������жϽ������������������
			ObjectId projectId = (ObjectId) project
					.get(IDBConstants.FIELD_SYSID);
			List<Object[]> unCompleteness = BusinessService.getWorkService()
					.completenessCheck(projectId);
			if (unCompleteness.size() > 0) {
				message = "���ύ�������Ŀ�ĵ�������������Ҫ���������µ��ļ��������ύ������Ҳ������ʱ����Ŀ�����к˶��Ƿ�����������Ҫ��";
				for (int i = 0; i < unCompleteness.size(); i++) {
					DBObject doc = (DBObject) unCompleteness.get(i)[0];
					String name = (String) doc.get(IDBConstants.FIELD_DESC);
					String reason = (String) unCompleteness.get(i)[1];
					message = message + "\n" + "�ĵ���" + name + " ���⣺" + reason;
				}
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
