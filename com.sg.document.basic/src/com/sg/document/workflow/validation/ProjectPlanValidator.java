package com.sg.document.workflow.validation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class ProjectPlanValidator implements IValidationHandler {

	private String message = null;
	public ProjectPlanValidator() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		message = "";
		
		
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG004A");
		if(doc==null){
			message = "����Ҫ��ɿ�����Ŀ���������������ύ�������";
			return false;
		}

		// P1Ŀ������
		Object value = doc.get("techauditor");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ��ָ����˵ļ��������Ρ�\n";
		}
		
		// P1Ŀ������
		value = doc.get("f1");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ��Ŀ�����塱������\n";
		}
		
		// P2�����������о���״�뷢չ����
		value = doc.get("f2");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ���������о���״�뷢չ���ơ�������\n";
		}

		// P3������Ŀʵʩ��Ҫ�����봴�µ㡢Ԥ��Ŀ��
		value = doc.get("f3");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ���������о���״�뷢չ���ơ�������\n";
		}
		// P4�ġ���Ŀר�����鱨�������
		value = doc.get("f4");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ����Ŀר�����鱨���������������\n";
		}
		// P5�塢��ĿԤ�ڳɹ�
		value = doc.get("f5");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ����ĿԤ�ڳɹ���������\n";
		}
		// P6������Ŀ���á����Ч���������Ŀ���շ���
		value = doc.get("f6");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ����Ŀ���á����Ч���������Ŀ���շ�����������\n";
		}
		// P7�ߡ������ؼ��������ѵ����Ҫ��������
		value = doc.get("f7");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� �������ؼ��������ѵ����Ҫ�������ݡ�������\n";
		}
		// P8�ˡ����й�������������������
		value = doc.get("f8");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� �����й������������������ơ�������\n";
		}
		// P9�š���Ŀʵʩ����
		value = doc.get("f9");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ����Ŀʵʩ������������\n";
		}
		// P10ʮ�����鰲��
		value = doc.get("f10");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� �����鰲�š�������\n";
		}
		//���ȼƻ�
		value =  doc.get("projectplan");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� �����ȼƻ���������\n";
		}
		//����Ԥ��
		value =  doc.get("budgetlist");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ������Ԥ�㡱������\n";
		}
		//�豸����
		value =  doc.get("equipreq");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ���豸���󡱵�����\n";
		}
		
		value =  doc.get("teamdetail");
		if(value == null||value.toString().length()<1){
			message += "������Ŀ������������ȱ�� ����Ŀ�Ŷӡ�������";
		}

		return message.length()<1;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
