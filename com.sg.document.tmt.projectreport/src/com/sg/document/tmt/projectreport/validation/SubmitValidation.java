package com.sg.document.tmt.projectreport.validation;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.workflow.IValidationHandler;

public class SubmitValidation implements IValidationHandler {
	private String message;

	public SubmitValidation() {
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		Object workId = workData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument(
				(ObjectId) workId, "com.sg.cpm.editor.projectmonthreport");
		if (doc == null) {
			message = "����Ҫ����±�����д";
			return false;
		}

		if (doc.get("project") == null) {
			message = "����Ҫѡ���±�����Ŀ";
			return false;
		}

		String year = (String) doc.get("year");
		try {
			Integer y = Integer.parseInt(year);
			if (y < 2010) {
				message = "�±��е������д����ȷ";
				return false;
			}
		} catch (Exception e) {
			message = "�±��е������д����ȷ";
			return false;
		}
		String month = (String) doc.get("month");
		if (month == null) {
			message = "�±��е��·���д����ȷ";
			return false;
		}

		DBObject projectData = (DBObject) doc.get("project");
		ObjectId projectId = (ObjectId) projectData
				.get(IDBConstants.FIELD_SYSID);
		
		List<DBObject> reportList = BusinessService.getWorkService().getProjectDocuments(projectId, "com.sg.cpm.editor.projectmonthreport");
		for(int i=0;i<reportList.size();i++){
			DBObject report = reportList.get(i);
			if(report.get("year").equals(year)&&report.get("month").equals(month)){
				DBObject _project = (DBObject) report.get("project");
				if(_project.get(IDBConstants.FIELD_SYSID).equals(projectId)){
					message = year + "��"+month+"�����Ѿ��ύ����Ŀ���±�,�������ظ��ύ";
					return false;
				}
			}
		}

		message = null;
		return true;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
