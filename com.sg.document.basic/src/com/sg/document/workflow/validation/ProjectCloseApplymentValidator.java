package com.sg.document.workflow.validation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class ProjectCloseApplymentValidator implements IValidationHandler {

	private String message;
	private static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG002A";// 
	
	public ProjectCloseApplymentValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		//判断是否选定了项目
		message = "";
		//获得工作id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs==null){
			message = "您需要完成项目结题申请表的填写后才能提交这个任务。";
			return false;
		}

		if(docs.get("project")==null){
			message = "项目结题申请表的填写还没有完成吗？\n项目结题申请表需要填写项目，请填写这些必要信息后再提交。";
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
