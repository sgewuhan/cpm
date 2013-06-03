package com.sg.document.tmt.export;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.widget.editor.export.IExportParameterProvider;
import com.sg.widget.util.Util;

public class ProjectReviewApplymentExport implements IExportParameterProvider {

	private SimpleDateFormat sdf;

	public ProjectReviewApplymentExport() {
		sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);
	}

	@Override
	public Map<String, Object> getParameters(Map<String, Object> map) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		DBObject projectData = (DBObject) map.get("project");
		// PROJNAME
		Object value = projectData.get(IDBConstants.FIELD_DESC);
		result.put("PROJNAME", value);

		// PJNUMBER
		value = projectData.get(IDBConstants.FIELD_ID);
		result.put("PJNUMBER", value);

		// PMNAME
		DBObject pmData = (DBObject) projectData
				.get(IDBConstants.FIELD_PROJECT_PM);
		result.put("PMNAME", pmData.get(IDBConstants.FIELD_NAME));

		// STAGE
		result.put("STAGE", map.get("stage"));

		// ACTC
		DBObject act_review_convener = (DBObject) map
				.get("act_review_convener");
		result.put("ACTC", act_review_convener.get(IDBConstants.FIELD_NAME));

		// ACTLIST
		BasicDBList act_reviewer_list = (BasicDBList) map
				.get("act_reviewer_list");
		value = "";
		if (act_reviewer_list != null) {
			for (int i = 0; i < act_reviewer_list.size(); i++) {
				DBObject acti = (DBObject) act_reviewer_list.get(i);
				String name = (String) acti.get(IDBConstants.FIELD_NAME);
				if (i == 0) {
					value = name;
				} else {
					value = value + "," + name;
				}
			}
		}
		result.put("ACTLIST", value);

		// FILES
		// 扫描项目下的所有文件
		WorkService workService = BusinessService.getWorkService();
		ObjectId projectId = (ObjectId) projectData
				.get(IDBConstants.FIELD_SYSID);
		List<DBObject> documents = workService.getProjectDocuments(projectId);
		String files = "";
		for (int i = 0; i < documents.size(); i++) {
			DBObject doc = documents.get(i);
			 BasicDBList attachment = (BasicDBList)doc.get("attachment");
			 if(attachment==null||attachment.size()<1){
				 continue;
			 }
			if (i == 0) {
				files = (String) doc.get(IDBConstants.FIELD_DESC);
			} else {
				files = files + "\n" + doc.get(IDBConstants.FIELD_DESC);
			}
		}
		result.put("FILES", files);

		// 获得当前的父级工作
		ObjectId workId = (ObjectId) map.get(IDBConstants.FIELD_WBSPARENT);
		DBObject workObject = workService.getWorkObject(workId);
		// 取出流程历史
		BasicDBList processHis = (BasicDBList) workObject
				.get(IDBConstants.FIELD_PROCESSHISTORY);
		setLatestActivityInfo("研究室审核", processHis, result,"1");
		setLatestActivityInfo("批准评审", processHis, result,"2");

		return result;
	}

	private void setLatestActivityInfo(String activitiName, BasicDBList processHis,
			HashMap<String, Object> result, String typeName) {
		if (processHis == null || processHis.size() < 1) {
			return;
		}

		DBObject proc = null;
		for (int i = 0; i < processHis.size(); i++) {
			DBObject newProcess = (DBObject) processHis.get(i);
			if (activitiName.equals(newProcess.get("taskName"))) {
				if(proc==null){
					proc = newProcess;
				}else{
					Date date = (Date) proc.get("closedate");
					Date newDate = (Date)newProcess.get("closedate");
					if(newDate!=null&&newDate.after(date)){
						proc = newProcess;
					}
				}
			}
		}
		
		if(proc!=null){
			String comment = proc.get("comment") == null ? ""
					: (String) proc.get("comment");
			result.put("COMMENT"+typeName, comment);
			String choice = proc.get("choice") == null ? "" : (String) proc
					.get("choice");
			result.put("RESULT"+typeName, choice);
			String actorName = proc.get("actorName") == null ? ""
					: (String) proc.get("actorName");
			result.put("ACTOR"+typeName, actorName);
			Date date = (Date) proc.get("closedate");
			if (date != null) {
				result.put("DATE"+typeName, sdf.format(date));
			} else {
				result.put("DATE"+typeName, "");
			}
		}
	}

}
