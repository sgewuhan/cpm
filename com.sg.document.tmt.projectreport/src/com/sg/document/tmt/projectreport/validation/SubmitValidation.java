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
			message = "您需要完成月报的填写";
			return false;
		}

		if (doc.get("project") == null) {
			message = "您需要选择月报的项目";
			return false;
		}

		String year = (String) doc.get("year");
		try {
			Integer y = Integer.parseInt(year);
			if (y < 2010) {
				message = "月报中的年份填写不正确";
				return false;
			}
		} catch (Exception e) {
			message = "月报中的年份填写不正确";
			return false;
		}
		String month = (String) doc.get("month");
		if (month == null) {
			message = "月报中的月份填写不正确";
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
					message = year + "年"+month+"，您已经提交该项目的月报,您无需重复提交";
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
