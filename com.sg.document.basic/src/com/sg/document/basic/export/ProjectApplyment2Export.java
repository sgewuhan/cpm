package com.sg.document.basic.export;

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

/**
 * ����֧��ί�е��ĵ���
 * 
 * @author hua
 * 
 */
public class ProjectApplyment2Export implements IExportParameterProvider {
	private SimpleDateFormat sdf;

	public ProjectApplyment2Export() {
		sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

	}

	@Override
	public Map<String, Object> getParameters(Map<String, Object> map) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		// PROJNAME ��Ŀ����
		result.put("PROJNAME", map.get("projectdesc"));
		// PJNUMBER �������
		result.put("PJNUMBER", map.get("projectnumber"));

		// PARTYA ί�в���
		result.put("PARTYA", map.get("partya"));

		// PARTYB �е�����
		ObjectId deptId = (ObjectId) map.get("dept");
		String partyb = BusinessService.getOrganizationService().getOBSItemLabel(deptId);
		result.put("PARTYB", partyb);

		// INTRO ��Ŀ�ſ�
		result.put("INTRO", map.get("summary"));

		// REQUIREMENT ί��ԭ��
		result.put("REQUIREMENT", map.get("reason"));

		// CONTENT Լ��Ŀ�ꡢ���ݼ�������ʽ
		result.put("CONTENT", map.get("research"));

		// PM ��Ŀ������
		DBObject pm = (DBObject) map.get(IDBConstants.FIELD_PROJECT_PM);
		result.put("PM", pm.get("name"));

		// BUDGET ����Ԥ��
		Double bgt = (Double) map.get("budget");
		String value = bgt == null ? "" : (Util.getDecimalFormat("###,###,###")
				.format(bgt) + "��Ԫ");
		result.put("BUDGET", value);

		// PLANFINISH ����ʱ��
		Date d = (Date) map.get("planfinish");
		if (d != null) {
			result.put("PLANFINISH", sdf.format(d));
		} else {
			result.put("PLANFINISH", "");
		}

		// COMMENT1 ί�е�λ���������������
		result.put("COMMENT1", map.get("partyacomment"));

		// ACTOR1 ί�е�λ����������
		result.put("ACTOR1", map.get("partya_manager"));

		// DATE1
		d = (Date) map.get("partya_date");
		if (d != null) {
			result.put("DATE1", sdf.format(d));
		} else {
			result.put("DATE1", "");
		}

		// ���������Ϣ
		Object workId = map.get(IDBConstants.FIELD_WBSPARENT);
		DBObject work = BusinessService.getWorkService().getWorkObject((ObjectId) workId);
		BasicDBList processHistory = (BasicDBList) work.get(IDBConstants.FIELD_PROCESSHISTORY);
		if (processHistory == null || processHistory.size() == 0) {
			return result;
		}
		
		DBObject activiti = getProcessData(processHistory, "�����о����������");
		if (activiti != null) {
			// COMMENT2
			String val = (String) activiti.get("comment");
			result.put("COMMENT2", val == null ? "" : val);
			// RESULT2
			val = (String) activiti.get("choice");
			result.put("RESULT2", val == null ? "" : val);
			// ACTOR2
			val = (String) activiti.get("actorName");
			result.put("ACTOR2", val == null ? "" : val);
			// DATE2
			Date date = (Date) activiti.get("closedate");
			if (date != null) {
				result.put("DATE2", sdf.format(date));
			}
		}

		activiti = getProcessData(processHistory, "��ϯʦ���");
		if (activiti != null) {
			String val = (String) activiti.get("comment");
			result.put("COMMENT3", val == null ? "" : val);
			val = (String) activiti.get("choice");
			result.put("RESULT3", val == null ? "" : val);
			val = (String) activiti.get("actorName");
			result.put("ACTOR3", val == null ? "" : val);
			Date date = (Date) activiti.get("closedate");
			if (date != null) {
				result.put("DATE3", sdf.format(date));
			}
		}

		activiti = getProcessData(processHistory, "��Ŀ��׼");
		if (activiti != null) {
			String val = (String) activiti.get("comment");
			result.put("COMMENT4", val == null ? "" : val);
			val = (String) activiti.get("choice");
			result.put("RESULT4", val == null ? "" : val);
			val = (String) activiti.get("actorName");
			result.put("ACTOR4", val == null ? "" : val);
			Date date = (Date) activiti.get("closedate");
			if (date != null) {
				result.put("DATE4", sdf.format(date));
			}
		}

		activiti = getProcessData(processHistory, "ȷ���������");
		if (activiti != null) {
			String val = (String) activiti.get("actorName");
			result.put("ACTOR5", val == null ? "" : val);
			Date date = (Date) activiti.get("closedate");
			if (date != null) {
				result.put("DATE5", sdf.format(date));
			}
		}
		return result;
	}

	private DBObject getProcessData(BasicDBList processList, String taskName) {
		for (int i = 0; i < processList.size(); i++) {
			DBObject dbObject = (DBObject) processList.get(i);
			if (taskName.equals(dbObject.get("taskName"))) {
				return dbObject;
			}
		}
		return null;
	}

}
