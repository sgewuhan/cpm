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
		// 判断是否选定了项目
		message = "";
		// 获得工作id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DocumentService documentService = BusinessService.getDocumentService();
		DBObject docs = documentService.getWorkDocument((ObjectId) workId,
				SOURCE_EDITOR_ID);
		if (docs == null) {
			message = "您需要完成项目评审申请表的填写后才能提交这个任务。";
			return false;
		}

		DBObject project = (DBObject) docs.get("project");
		if (project == null) {
			message = "您需要在项目评审申请表中选择提交评审的项目，请填写这些必要信息后再提交。";
			return false;
		}

		BasicDBList attachment = (BasicDBList) docs.get("attachment");
		if (!(attachment instanceof BasicDBList)
				|| ((BasicDBList) attachment).size() < 1) {
			message = "您需要在项目评审申请表添加评审资料的电子文件，请添加了这些必要信息后再提交。";
			return false;
		}

		if ("结题评审".equals(docs.get("stage"))) {
			// 判断是否是结题评审，如果是结题评审，判断结题评审的资料完整性
			ObjectId projectId = (ObjectId) project
					.get(IDBConstants.FIELD_SYSID);
			List<Object[]> unCompleteness = BusinessService.getWorkService()
					.completenessCheck(projectId);
			if (unCompleteness.size() > 0) {
				message = "您提交评审的项目文档不符合完整性要求，请检查以下的文件后重新提交。（您也可以随时在项目导航中核对是否满足完整性要求）";
				for (int i = 0; i < unCompleteness.size(); i++) {
					DBObject doc = (DBObject) unCompleteness.get(i)[0];
					String name = (String) doc.get(IDBConstants.FIELD_DESC);
					String reason = (String) unCompleteness.get(i)[1];
					message = message + "\n" + "文档：" + name + " 问题：" + reason;
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
