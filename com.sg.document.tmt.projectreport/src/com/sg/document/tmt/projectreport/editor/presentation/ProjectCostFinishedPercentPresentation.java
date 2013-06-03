package com.sg.document.tmt.projectreport.editor.presentation;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.service.WorkService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.util.Util;

public class ProjectCostFinishedPercentPresentation extends AbstractProjectDSDPresentation{

	
	private Double costFinishedMonth = null;
	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		DBObject editorData = data.getData();
		
		ObjectId newProjectId = getProjectId(editorData);
		Double newCostFinishedMonth =	(Double) editorData.get("costFinishedMonth");
		
		if(org.eclipse.jface.util.Util.equals(projectId, newProjectId)&&org.eclipse.jface.util.Util.equals(costFinishedMonth, newCostFinishedMonth)){
			return pValue;
		}else{
			projectId = newProjectId;
			costFinishedMonth = newCostFinishedMonth;
		}
		
		pValue = getValue(editorData,projectId,format);
		return pValue;
	}
	
	
	@Override
	protected String getValue(DBObject editorData,ObjectId projectId, String format) {
		
		if(costFinishedMonth == null){
			return "";
		}
		
		Double budget = (Double) getDataFromDocument(projectId, "budget");
		if(budget == null){
			return "";
		}
		
		//ȡ����Ŀ�������±���¼
		WorkService workService = BusinessService.getWorkService();
		List<DBObject> reportList = workService.getProjectDocuments(projectId, "com.sg.cpm.editor.projectmonthreport");
		
		//ȡÿ�µ��������ý����ۼƣ�costFinishedMonth
		double sum = 0;
		for(int i = 0 ; i< reportList.size();i++){
			DBObject report = reportList.get(i);
			String reportStatus = (String) report.get("reportStatus");
			if("����׼".equals(reportStatus)){
				Double cost = (Double) report.get("costFinishedMonth");
				if(cost!=null){
					sum = sum + cost.doubleValue();
				}
			}
		}
		
		double total = sum+costFinishedMonth.doubleValue();
		
		return Util.getDecimalFormat(format).format(total/budget.doubleValue());
	}


}
