package com.sg.document.basic.export;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class ProjectApplymentExport implements IExportParameterProvider {

	private static final String STEP3 = "����������׼";
	private static final String STEP2 = "����������ʽ���";
	private static final String STEP1 = "���������о������";
	private SimpleDateFormat sdf;

	public ProjectApplymentExport() {

		sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

	}

	@Override
	public Map<String, Object> getParameters(Map<String, Object> map) {

		HashMap<String, Object> result = new HashMap<String, Object>();

		Object value;
		// PROJNAME:��Ŀ����
		value = map.get("projectdesc");
		result.put("PROJNAME", value);

		// DEPT:�е�����
		Object obsparent = map.get("dept");
		DBObject dept = BusinessService.getOrganizationService().getOBSItemData((ObjectId) obsparent);
		value = dept.get(IDBConstants.FIELD_DESC);
		result.put("DEPT", value);

		// PMNAME:��Ŀ��������
		DBObject pmdata = (DBObject) map.get(IDBConstants.FIELD_PROJECT_PM);
		value = pmdata.get(IDBConstants.FIELD_NAME);
		result.put("PMNAME", value);

		// DEGREE:ѧ��
		value = map.get("pmdegree");
		result.put("DEGREE", value);

		// TITLE:�ڲ�ְ��
		value = map.get("pmtitle");
		result.put("TITLE", value);

		// DIRECTION:��������
		value = map.get("direction");
		result.put("DIRECTION", value);

		// DIRECTOR:ָ����
		BasicDBList dirlist = (BasicDBList) map.get("director");
		value = "";
		if (dirlist != null) {
			for (int i = 0; i < dirlist.size(); i++) {
				DBObject item = (DBObject) dirlist.get(i);
				Object name = item.get(IDBConstants.FIELD_NAME);
				if (i == 0) {
					value = name;
				} else {
					value = value + "," + name;
				}
			}
		}
		result.put("DIRECTOR", value);

		// N2:��ϯ������
		Integer n2 = (Integer) map.get("mastercnt");
		n2 = n2 == null ? 0 : n2;
		result.put("N2", n2);

		// N3:����ʦ����
		Integer n3 = (Integer) map.get("directorcnt");
		n3 = n3 == null ? 0 : n3;
		result.put("N3", n3);

		// N4:�߼�������
		Integer n4 = (Integer) map.get("seniorcnt");
		n4 = n4 == null ? 0 : n4;
		result.put("N4", n4);

		// N5:��������
		Integer n5 = (Integer) map.get("joniorcnt");
		n5 = n5 == null ? 0 : n5;
		result.put("N5", n5);

		// N1����Ŀ������
		result.put("N1", (n2 + n3 + n4 + n5));

		// STARTDATE:��ʼʱ��
		value = (Date) map.get(IDBConstants.FIELD_PROJECT_PLANSTART);
		result.put("STARTDATE", sdf.format(value));

		// FINISHDATE�����ʱ��
		value = (Date) map.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		result.put("FINISHDATE", sdf.format(value));

		// PROJTYPE����Ŀ����
		value = map.get("type");
		result.put("PROJTYPE", value);

		// CONTENT����Ҫ�о�����
		value = map.get("research");
		result.put("CONTENT", value);

		// RESULT��Ԥ�ڳɹ�
		value = "";
		Object res = map.get("result_1");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", ר��"):(value+"ר��");
		}
		res = map.get("result_2");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", ������׼"):(value+"������׼");
		}
		res = map.get("result_3");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", �²�Ʒ"):(value+"�²�Ʒ");
		}
		res = map.get("result_4");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", �¹���"):(value+"�¹���");
		}
		res = map.get("result_5");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", ��װ��"):(value+"��װ��");
		}
		res = map.get("result_6");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", �²���"):(value+"�²���");
		}
		res = map.get("result_7");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", ��������"):(value+"��������");
		}
		res = map.get("result_8");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", �о�����"):(value+"�о�����");
		}
		res = map.get("result_9");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", ����"):(value+"����");
		}
		result.put("RESULT", value);

		// BUDGET��Ԥ��
		Double bgt = (Double) map.get("budget");
		value = bgt == null ? "" : (Util.getDecimalFormat("###,###,###").format(bgt)+"��Ԫ");
		result.put("BUDGET", value);

		// ���������Ϣ
		Object workId = map.get(IDBConstants.FIELD_WBSPARENT);
		DBObject work = BusinessService.getWorkService().getWorkObject((ObjectId) workId);
		BasicDBList processHistory = (BasicDBList) work.get(IDBConstants.FIELD_PROCESSHISTORY);
		if (processHistory == null || processHistory.size() == 0) {
			return result;
		}

		int index1 = getIndexOfProcesss(processHistory, STEP1);
		int index2 = getIndexOfProcesss(processHistory, STEP2);
		int index3 = getIndexOfProcesss(processHistory, STEP3);

		if (index1 != -1) {
			DBObject procLast = (DBObject) processHistory.get(index1);
			setProcess(procLast, result, 1);
		}

		if (index2 != -1) {
			DBObject procLast = (DBObject) processHistory.get(index2);
			setProcess(procLast, result, 2);
		}

		if (index3 != -1) {

			DBObject procLast = (DBObject) processHistory.get(index3);
			setProcess(procLast, result, 3);
		}

//		if (index1 < index2) {
//
//			cleanProcess(result, 2);
//			cleanProcess(result, 3);
//
//		}
//
//		if (index2 < index3) {
//			cleanProcess(result, 3);
//		}

		return result;
	}

	private int getIndexOfProcesss(ArrayList<Object> processHistory, Object taskName) {

		for (int i = 0; i < processHistory.size(); i++) {
			DBObject pi = (DBObject) processHistory.get(i);
			if (taskName.equals(pi.get(IDBConstants.FIELD_WFINFO_TASKNAME))) {
				return i;
			}
		}
		return -1;

	}

	private void cleanProcess(HashMap<String, Object> result, int index) {

		// ֻ�ܷ�����һ��
		// COMMENT1�������������
		result.put("COMMENT" + index, "");

		// RESULT1:��������Ƿ�ͨ��
		result.put("RESULT" + index, "");

		// DATE1:�������ʱ��
		result.put("DATE" + index, "");

		// ACTOR1:��������
		result.put("ACTOR" + index, "");

	}

	private void setProcess(DBObject procLast, HashMap<String, Object> result, int index) {

		// taskId 2
		// taskName;���������о������,����������ʽ��飬����������׼
		// taskOperation: ���
		// choice����ͬ��"
		// comment"Ԥ�㲻��ȷ
		// actorName������
		// closedate��date
		if ("���".equals(procLast.get(IDBConstants.FIELD_WF_HISTORY_TASK_OPERATION))) {

			// ֻ�ܷ�����һ��
			// COMMENT1�������������
			result.put("COMMENT" + index, procLast.get(IDBConstants.FIELD_WF_HISTORY_COMMENT));

			// RESULT1:��������Ƿ�ͨ��
			result.put("RESULT" + index, procLast.get("choice"));

			// DATE1:�������ʱ��
			Date closeData = (Date) procLast.get(IDBConstants.FIELD_WF_HISTORY_CLOSE_DATE);
			result.put("DATE" + index, sdf.format(closeData));

			// ACTOR1:��������
			result.put("ACTOR" + index, procLast.get(IDBConstants.FIELD_WFINFO_ACTORNAME));

		} else {
			// ��������������飬����¼
		}
	}
}
