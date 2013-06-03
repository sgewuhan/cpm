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
		//��ù���id
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject docs = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, SOURCE_EDITOR_ID);
		if(docs==null){
			message = "����Ҫ��ɼ���֧��ί�е�����д������ύ�������";
			return false;
		}

		//check project name.  projectdesc
		if(docs.get("projectdesc")==null){
			message += "����֧��ί�е�ȱ����д����Ŀ���ơ�\n";
		}
		
		//check devision. direction
		if(docs.get("direction")==null){
			message += "����֧��ί�е�ȱ��ѡ�񡰼�������\n";
		}
		
		//check party a   partya
		if(docs.get("partya")==null){
			message += "����֧��ί�е�ȱ�١�ί�е�λ��";
		}
		
		//check time  planfinish
		if(docs.get("planfinish")==null){
			message += "����֧��ί�е�ȱ����д��Ҫ����ɵ�ʱ�䡱\n";
		}	

		//�����Ŀ�ſ�
		if(docs.get("summary")==null){
			message += "����֧��ί�е�ȱ����д����Ŀ�ſ���\n";
		}	

		if(docs.get("research")==null){
			message += "����֧��ί�е�ȱ����д��Լ��Ŀ�ꡢ���ݼ�������ʽ��\n";
		}
		
		if(docs.get("reason")==null){
			message += "����֧��ί�е�ȱ����д��ί��ԭ��\n";
		}	

		if(docs.get("partyacomment")==null){
			message += "����֧��ί�е�ȱ����д��ί�е�λ�����������\n";
		}	
		
		if(docs.get("partya_manager")==null){
			message += "����֧��ί�е�ȱ��ѡ��ί�е�λ�����ˡ�\n";
		}

		BasicDBList contractList = (BasicDBList) docs.get("contract");
		if(contractList==null||contractList.size()<0){
			message += "����֧��ί�е�ȱ���ϴ���ί����ĵ����ļ���\n";
		}

		return message.length()<1;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
