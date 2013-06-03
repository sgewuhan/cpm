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
		//�ж��Ƿ�ѡ������Ŀ
		message = "";
		//��ù���id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs==null){
			message = "����Ҫ�����Ŀ������������д������ύ�������";
			return false;
		}

		if(docs.get("project")==null){
			message = "��Ŀ������������д��û�������\n��Ŀ�����������Ҫ��д��Ŀ������д��Щ��Ҫ��Ϣ�����ύ��";
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
