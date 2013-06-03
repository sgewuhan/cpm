package com.sg.document.tmt.taskform;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;
import com.sg.widget.util.Util;

public class ProjectReviewApplymentValidator implements IValidationHandler {

	private String message;

	public ProjectReviewApplymentValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		//获得文档
		message = "";
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG002A-01");
		if(doc==null){
			message = "您需要提交评审资料后才能提交这个任务。";
			return false;
		}

		Object project = doc.get("project");
		if(project == null){
			message += "您需要在项目评审资料中选择提交评审的项目\n";
		}
		Object stage = doc.get("stage");
		if(Util.isNullOrEmptyString(stage)){
			message += "您需要在项目评审资料中选择评审的阶段";
		}
		BasicDBList attachment = (BasicDBList) doc.get("attachment");
		if(attachment==null||attachment.size()<1||attachment.get(0)==null){
			message += "您需要在项目评审资料中上传评审的技术资料";
		}
		
		return message.length()==0;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
