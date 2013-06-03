package com.sg.document.workflow.validation;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class Project2ApplymentValidator implements IValidationHandler {
	
	private String message;
	public static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A--1";// 


	public Project2ApplymentValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		message = "";
		//获得工作id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs==null){
			message = "您需要完成技术支持委托单的填写后才能提交这个任务。";
			return false;
		}

		//check project name.  projectdesc
		if(docs.get("projectdesc")==null){
			message += "技术支持委托单缺少填写“项目名称”\n";
		}
		
		//check devision. direction
		if(docs.get("direction")==null){
			message += "技术支持委托单缺少选择“技术方向”\n";
		}
		
		//check party a   partya
		if(docs.get("partya")==null){
			message += "技术支持委托单缺少“委托单位”";
		}
		
		//check time  planfinish
		if(docs.get("planfinish")==null){
			message += "技术支持委托单缺少填写“要求完成的时间”\n";
		}	

		//检查项目概况
		if(docs.get("summary")==null){
			message += "技术支持委托单缺少填写“项目概况”\n";
		}	

		if(docs.get("research")==null){
			message += "技术支持委托单缺少填写“约定目标、内容及交付方式”\n";
		}
		
		if(docs.get("reason")==null){
			message += "技术支持委托单缺少填写“委托原因”\n";
		}	

		if(docs.get("partyacomment")==null){
			message += "技术支持委托单缺少填写“委托单位负责人意见”\n";
		}	
		
		if(docs.get("partya_manager")==null){
			message += "技术支持委托单缺少选择“委托单位负责人”\n";
		}

		BasicDBList contractList = (BasicDBList) docs.get("contract");
		if(contractList==null||contractList.size()<0){
			message += "技术支持委托单缺少上传“委托书的电子文件”\n";
		}

		return message.length()<1;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
