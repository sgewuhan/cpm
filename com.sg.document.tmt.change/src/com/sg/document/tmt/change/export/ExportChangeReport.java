package com.sg.document.tmt.change.export;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.widget.editor.export.IExportParameterProvider;
import com.sg.widget.util.Util;

public class ExportChangeReport implements IExportParameterProvider {

	private SimpleDateFormat sdf;

	public ExportChangeReport() {
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
		
		//DEPT
		ObjectId _id = (ObjectId) projectData.get(IDBConstants.FIELD_OBSPARENT);
		DBObject org = BusinessService.getOrganizationService().getOBSItemData(_id);
		if(org!=null){
			result.put("DEPT", org.get(IDBConstants.FIELD_DESC));
		}
		
		//REQ,changereq
		value = map.get("changereq");
		result.put("REQ", value==null?"":value);

		//N1,changeeffect4 范围
		value = map.get("changeeffect4");
		result.put("N1", value==null?"":value);
		
		//N2 changeeffect1 进度
		value = map.get("changeeffect1");
		result.put("N2", value==null?"":value);
		
		//N3 changeeffect2 预算
		value = map.get("changeeffect2");
		result.put("N3", value==null?"":value);
		
		//N4 changeeffect3 组织团队
		value = map.get("changeeffect3");
		result.put("N4", value==null?"":value);
		
		//COMMENT1
		//ACTOR1
		//RESULT1
		//DATE1
		// 获得流程信息
		Object workId = map.get(IDBConstants.FIELD_WBSPARENT);
		DBObject work = BusinessService.getWorkService().getWorkObject((ObjectId) workId);
		BasicDBList processHistory = (BasicDBList) work.get(IDBConstants.FIELD_PROCESSHISTORY);
		if (processHistory == null || processHistory.size() == 0) {
			return result;
		}
		
		setLatestActivityInfo("委托单位意见", processHistory, result,"1");
		setLatestActivityInfo("研究室批准", processHistory, result,"2");
		setLatestActivityInfo("首席师批准", processHistory, result,"3");
		setLatestActivityInfo("技术中心批准", processHistory, result,"4");

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
