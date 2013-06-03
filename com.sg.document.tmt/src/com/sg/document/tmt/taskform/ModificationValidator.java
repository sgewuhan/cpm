package com.sg.document.tmt.taskform;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class ModificationValidator implements IValidationHandler {

	private String message;

	public ModificationValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		
		message = "";
		
		
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.changeplan");
		if(doc==null){
			message = "����Ҫ�ύ���ļƻ�������ύ�������";
			return false;
		}

		BasicDBList attachment = (BasicDBList) doc.get("attachment");
		if(attachment ==null||attachment .isEmpty()){
			message = "����Ҫ�����ļƻ����ύ�����ļ�������ύ�������";
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
