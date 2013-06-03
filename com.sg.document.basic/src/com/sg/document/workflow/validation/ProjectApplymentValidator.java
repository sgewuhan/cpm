package com.sg.document.workflow.validation;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;


public class ProjectApplymentValidator implements IValidationHandler {
	

	private String message;
	public static final String SOURCE_EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A";// ��Ŀ���������


	public ProjectApplymentValidator() {

	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		message = "";
		//��ù���id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs==null){
			message = "����Ҫ�����Ŀ��������������ύ�������";
			return false;
		}
		//���û�мƻ���ʼ�ͼƻ����ʱ�䣬������ʾ
		if(docs.get(IDBConstants.FIELD_PROJECT_PLANSTART)==null||docs.get(IDBConstants.FIELD_PROJECT_PLANFINISH)==null){
			message = "��û��ȷ���ƻ�ʱ����\n��Ŀ�����������Ҫ��д�ƻ�ʱ�䣬����д��Щ��Ҫ��Ϣ�����ύ��";
			return false;
		}

		//���û�з�����ϯʦ��Ϣ����ʾ
		BasicDBList directorList = (BasicDBList) docs.get("director");
		if(directorList==null||directorList.size()==0){
			message = "��û��ȷ����ϯʦ��\n��ϯʦ����Ŀ�������̵Ĳ����ߣ���û��ȷ����ϯʦ�������е���ϯʦ��˻�޷���ɣ�\n����д��Щ��Ҫ��Ϣ�����ύ��";
			return false;
		}

		Object value = docs.get("research");
		if(value==null||value.toString().length()<1){
			message = "����Ҫ����Ŀ���������д�о����ݡ�";
			return false;
		}
		return true;
	}

	@Override
	public String getMessage() {

		return message;
	}

}
