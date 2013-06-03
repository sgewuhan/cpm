package com.sg.document.basic.export;

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

public class ProjectCloseApplyment2 implements IExportParameterProvider {

	private SimpleDateFormat sdf;

	public ProjectCloseApplyment2() {
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
			result.put("PARTYB", org.get(IDBConstants.FIELD_DESC));
		}

		// PARTYA ί�в���
		result.put("PARTYA", map.get("partya"));
		
		ObjectId projectId = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);
		WorkService workService = BusinessService.getWorkService();
		List<DBObject> documents = workService.getProjectDocuments(projectId);
		for (DBObject doc : documents) {
			if("com.sg.cpm.editor.JZ-QR-XG003A--1".equals(doc.get(IDBConstants.FIELD_SYSTEM_EDITOR))){
				result.put("PARTYA", doc.get("partya"));
				break;
			}
		}
		
		//CONTENT
		result.put("CONTENT", map.get("research"));
		
		//FINISHED
		result.put("FINISHED", map.get("finished"));

		
		Date finishdate = (Date) map.get("finishdate");
		try{
			result.put("PJFINISHED", sdf.format(finishdate));
		}catch(Exception e){

		}
		
		//DOCUMENT
		// ɨ����Ŀ�µ������ļ�
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
		result.put("DOCUMENT", files);
		
		
		// ��õ�ǰ�ĸ�������
		ObjectId workId = (ObjectId) map.get(IDBConstants.FIELD_WBSPARENT);
		DBObject workObject = workService.getWorkObject(workId);
		// ȡ��������ʷ
		BasicDBList processHis = (BasicDBList) workObject
				.get(IDBConstants.FIELD_PROCESSHISTORY);
		setLatestActivityInfo("ί�е�λ���", processHis, result,"1");
		setLatestActivityInfo("�о����������", processHis, result,"2");
		setLatestActivityInfo("��ϯʦ���", processHis, result,"3");
		setLatestActivityInfo("��Ŀ������׼", processHis, result,"4");
		
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
