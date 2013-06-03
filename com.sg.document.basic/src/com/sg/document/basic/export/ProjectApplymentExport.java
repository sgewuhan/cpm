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

	private static final String STEP3 = "立项申请批准";
	private static final String STEP2 = "立项申请形式审查";
	private static final String STEP1 = "立项申请研究室审核";
	private SimpleDateFormat sdf;

	public ProjectApplymentExport() {

		sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

	}

	@Override
	public Map<String, Object> getParameters(Map<String, Object> map) {

		HashMap<String, Object> result = new HashMap<String, Object>();

		Object value;
		// PROJNAME:项目名称
		value = map.get("projectdesc");
		result.put("PROJNAME", value);

		// DEPT:承担部门
		Object obsparent = map.get("dept");
		DBObject dept = BusinessService.getOrganizationService().getOBSItemData((ObjectId) obsparent);
		value = dept.get(IDBConstants.FIELD_DESC);
		result.put("DEPT", value);

		// PMNAME:项目经理姓名
		DBObject pmdata = (DBObject) map.get(IDBConstants.FIELD_PROJECT_PM);
		value = pmdata.get(IDBConstants.FIELD_NAME);
		result.put("PMNAME", value);

		// DEGREE:学历
		value = map.get("pmdegree");
		result.put("DEGREE", value);

		// TITLE:内部职称
		value = map.get("pmtitle");
		result.put("TITLE", value);

		// DIRECTION:技术方向
		value = map.get("direction");
		result.put("DIRECTION", value);

		// DIRECTOR:指导人
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

		// N2:首席是人数
		Integer n2 = (Integer) map.get("mastercnt");
		n2 = n2 == null ? 0 : n2;
		result.put("N2", n2);

		// N3:主任师人数
		Integer n3 = (Integer) map.get("directorcnt");
		n3 = n3 == null ? 0 : n3;
		result.put("N3", n3);

		// N4:高级是人数
		Integer n4 = (Integer) map.get("seniorcnt");
		n4 = n4 == null ? 0 : n4;
		result.put("N4", n4);

		// N5:其他人数
		Integer n5 = (Integer) map.get("joniorcnt");
		n5 = n5 == null ? 0 : n5;
		result.put("N5", n5);

		// N1：项目组人数
		result.put("N1", (n2 + n3 + n4 + n5));

		// STARTDATE:开始时间
		value = (Date) map.get(IDBConstants.FIELD_PROJECT_PLANSTART);
		result.put("STARTDATE", sdf.format(value));

		// FINISHDATE：完成时间
		value = (Date) map.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		result.put("FINISHDATE", sdf.format(value));

		// PROJTYPE：项目类型
		value = map.get("type");
		result.put("PROJTYPE", value);

		// CONTENT：主要研究内容
		value = map.get("research");
		result.put("CONTENT", value);

		// RESULT：预期成果
		value = "";
		Object res = map.get("result_1");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 专利"):(value+"专利");
		}
		res = map.get("result_2");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 技术标准"):(value+"技术标准");
		}
		res = map.get("result_3");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 新产品"):(value+"新产品");
		}
		res = map.get("result_4");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 新工艺"):(value+"新工艺");
		}
		res = map.get("result_5");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 新装置"):(value+"新装置");
		}
		res = map.get("result_6");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 新材料"):(value+"新材料");
		}
		res = map.get("result_7");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 论文论著"):(value+"论文论著");
		}
		res = map.get("result_8");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 研究报告"):(value+"研究报告");
		}
		res = map.get("result_9");
		if (Boolean.TRUE.equals(res)) {
			value = value.toString().length()>0?(value + ", 其他"):(value+"其他");
		}
		result.put("RESULT", value);

		// BUDGET：预算
		Double bgt = (Double) map.get("budget");
		value = bgt == null ? "" : (Util.getDecimalFormat("###,###,###").format(bgt)+"万元");
		result.put("BUDGET", value);

		// 获得流程信息
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

		// 只能返回这一个
		// COMMENT1：部门审批意见
		result.put("COMMENT" + index, "");

		// RESULT1:部门审核是否通过
		result.put("RESULT" + index, "");

		// DATE1:部门审核时间
		result.put("DATE" + index, "");

		// ACTOR1:部门主任
		result.put("ACTOR" + index, "");

	}

	private void setProcess(DBObject procLast, HashMap<String, Object> result, int index) {

		// taskId 2
		// taskName;立项申请研究室审核,立项申请形式审查，立项申请批准
		// taskOperation: 完成
		// choice：不同意"
		// comment"预算不明确
		// actorName：王进
		// closedate：date
		if ("完成".equals(procLast.get(IDBConstants.FIELD_WF_HISTORY_TASK_OPERATION))) {

			// 只能返回这一个
			// COMMENT1：部门审批意见
			result.put("COMMENT" + index, procLast.get(IDBConstants.FIELD_WF_HISTORY_COMMENT));

			// RESULT1:部门审核是否通过
			result.put("RESULT" + index, procLast.get("choice"));

			// DATE1:部门审核时间
			Date closeData = (Date) procLast.get(IDBConstants.FIELD_WF_HISTORY_CLOSE_DATE);
			result.put("DATE" + index, sdf.format(closeData));

			// ACTOR1:部门主任
			result.put("ACTOR" + index, procLast.get(IDBConstants.FIELD_WFINFO_ACTORNAME));

		} else {
			// 这个情况是正在审查，不记录
		}
	}
}
