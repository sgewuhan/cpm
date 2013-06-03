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
		//����ĵ�
		message = "";
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG002A-01");
		if(doc==null){
			message = "����Ҫ�ύ�������Ϻ�����ύ�������";
			return false;
		}

		Object project = doc.get("project");
		if(project == null){
			message += "����Ҫ����Ŀ����������ѡ���ύ�������Ŀ\n";
		}
		Object stage = doc.get("stage");
		if(Util.isNullOrEmptyString(stage)){
			message += "����Ҫ����Ŀ����������ѡ������Ľ׶�";
		}
		BasicDBList attachment = (BasicDBList) doc.get("attachment");
		if(attachment==null||attachment.size()<1||attachment.get(0)==null){
			message += "����Ҫ����Ŀ�����������ϴ�����ļ�������";
		}
		
		return message.length()==0;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
