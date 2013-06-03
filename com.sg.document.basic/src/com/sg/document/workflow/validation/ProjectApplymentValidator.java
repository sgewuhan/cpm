package com.sg.document.workflow.validation;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;


public class ProjectApplymentValidator implements IValidationHandler {
	

	private String message;
	public static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A";// 项目立项申请表


	public ProjectApplymentValidator() {

	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		message = "";
		//获得工作id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs==null){
			message = "您需要完成项目立项申请表后才能提交这个任务。";
			return false;
		}
		//如果没有计划开始和计划完成时间，返回提示
		if(docs.get(IDBConstants.FIELD_PROJECT_PLANSTART)==null||docs.get(IDBConstants.FIELD_PROJECT_PLANFINISH)==null){
			message = "还没有确定计划时间吗？\n项目立项申请表需要填写计划时间，请填写这些必要信息后再提交。";
			return false;
		}

		//如果没有方向首席师信息，提示
		BasicDBList directorList = (BasicDBList) docs.get("director");
		if(directorList==null||directorList.size()==0){
			message = "还没有确定首席师吗？\n首席师是项目申请流程的参与者，您没有确认首席师，流程中的首席师审核活动无法完成？\n请填写这些必要信息后再提交。";
			return false;
		}

		Object value = docs.get("research");
		if(value==null||value.toString().length()<1){
			message = "您需要在项目申请表中填写研究内容。";
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {

		return message;
	}

}
