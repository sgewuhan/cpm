package com.sg.document.basic.export;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.widget.editor.export.IExportParameterProvider;
import com.sg.widget.util.Util;

public class ProjectPlanExport implements IExportParameterProvider {

	private static String[] CN = { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" };

	public ProjectPlanExport() {

	}

	@Override
	public Map<String, Object> getParameters(Map<String, Object> map) {

		HashMap<String, Object> result = new HashMap<String, Object>();

		Object value;

		// PROJTYPE项目类型
		value = map.get("type");
		result.put("PROJTYPE", value);

		// PROJNAME项目名称
		value = map.get("projectdesc");
		result.put("PROJNAME", value);

		// DEPT所属部门
		Object obsparent = map.get("dept");
		DBObject dept = BusinessService.getOrganizationService().getOBSItemData((ObjectId) obsparent);
		value = dept.get(IDBConstants.FIELD_DESC);
		result.put("DEPT", value);

		// PMNAME项目负责人姓名
		DBObject pmdata = (DBObject) map.get(IDBConstants.FIELD_PROJECT_PM);
		value = pmdata.get(IDBConstants.FIELD_NAME);
		result.put("PMNAME", value);

		// DIRECTOR方向首席师
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

		// STARTDATE计划开始
		result.put("STARTDATE", getDate(map, IDBConstants.FIELD_PROJECT_PLANSTART));

		// FINISHDATE计划完成
		result.put("FINISHDATE", getDate(map, IDBConstants.FIELD_PROJECT_PLANFINISH));

		// APPDATE填报日期
		result.put("APPDATE", getDate(map, IDBConstants.FIELD_CREATE_DATE));

		// APPDATE2中文的填报日期

		result.put("APPDATE2", dateToCN((Date) map.get(IDBConstants.FIELD_CREATE_DATE)));

		// GEND性别
		value = map.get("pmgender");
		result.put("GEND", value);

		// BIR出生年月
		result.put("BIR", getDate(map, "pmbirthday"));

		// DEGREE学历
		value = map.get("pmdegree");
		result.put("DEGREE", value);

		// TITLE内部职称
		value = map.get("pmtitle");
		result.put("TITLE", value);

		// DIRECTION项目方向
		value = map.get("direction");
		result.put("DIRECTION", value);

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

		// CONTENT研究内容
		value = map.get("research");
		result.put("CONTENT", value);

		// 预期成果
		value = "";
		Object res = map.get("result_1");
		if (Boolean.TRUE.equals(res)) {
			value = value + "专利";
		}
		res = map.get("result_2");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 技术标准";
		}
		res = map.get("result_3");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 新产品";
		}
		res = map.get("result_4");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 新工艺";
		}
		res = map.get("result_5");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 新装置";
		}
		res = map.get("result_6");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 新材料";
		}
		res = map.get("result_7");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 论文论著";
		}
		res = map.get("result_8");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 研究报告";
		}
		res = map.get("result_9");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", 其他";
		}
		result.put("RESULT", value);

		// R1国外专利数量
		Integer n = (Integer) map.get("pi1");
		n = n == null ? 0 : n;
		result.put("R1", n);

		// R2国内专利数量
		n = (Integer) map.get("pi2");
		n = n == null ? 0 : n;
		result.put("R2", n);

		// R3其他数量
		n = (Integer) map.get("pi3");
		n = n == null ? 0 : n;
		result.put("R3", n);

		// STD标准，“□国际标准 □国外标准 □国家标准 □行业标准 □企业标准”
		String std = "";
		value = map.get("std_1");
		if (Boolean.TRUE.equals(value)) {
			std = std + "[√]国际标准";
		} else {
			std = std + "[x]国际标准";
		}

		value = map.get("std_2");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [√]国外标准";
		} else {
			std = std + " [x]国外标准";
		}

		value = map.get("std_3");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [√]国家标准";
		} else {
			std = std + " [x]国家标准";
		}

		value = map.get("std_4");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [√]行业标准";
		} else {
			std = std + " [x]行业标准";
		}

		value = map.get("std_5");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [√]企业标准";
		} else {
			std = std + " [x]企业标准";
		}
		result.put("STD", std);

		// PAPER论文 "EI   篇 SCI   篇 ISTP    篇 中文核心期刊    篇 其他  篇"
		StringBuffer sb = new StringBuffer();

		value = map.get("pp1");
		sb.append("EI ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 篇");

		value = map.get("pp2");
		sb.append(" SCI ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 篇");

		value = map.get("pp3");
		sb.append(" ISTP ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 篇");

		value = map.get("pp4");
		sb.append(" 中文核心期刊 ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 篇");

		value = map.get("pp5");
		sb.append(" 其他 ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 篇");

		result.put("PAPER", sb.toString());

		// TRAINNING人才培养 首席师级 tmastercnt人 主任师级tdirectorcnt 人 高级师级 tseniorcnt人
		// 师级tjoniorcnt 人
		sb = new StringBuffer();

		value = map.get("tmastercnt");
		sb.append("首席师级 ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 人");

		value = map.get("tdirectorcnt");
		sb.append(" 主任师级 ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 人");

		value = map.get("tseniorcnt");
		sb.append(" 高级师级 ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 人");

		value = map.get("tjoniorcnt");
		sb.append(" 师级 ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" 人");
		result.put("TRAINNING", sb.toString());

		// BUDGET经费预算
		Double bgt = (Double) map.get("budget");
		value = bgt == null ? "" : Util.getDecimalFormat("###,###,###.00").format(bgt) + "万元";
		result.put("BUDGET", value);

		// P1目的意义
		value = map.get("f1");
		result.put("P1", value);

		// P2二、国内外研究现状与发展趋势
		value = map.get("f2");
		result.put("P2", value);

		// P3三、项目实施主要内容与创新点、预期目标
		value = map.get("f3");
		result.put("P3", value);

		// P4四、项目专利、情报检索情况
		value = map.get("f4");
		result.put("P4", value);

		// P5五、项目预期成果
		value = map.get("f5");
		result.put("P5", value);

		// P6六、项目经济、社会效益分析，项目风险分析
		value = map.get("f6");
		result.put("P6", value);

		// P7七、技术关键、技术难点和主要试验内容
		value = map.get("f7");
		result.put("P7", value);

		// P8八、现有工作基础、条件和优势
		value = map.get("f8");
		result.put("P8", value);

		// P9九、项目实施方案
		value = map.get("f9");
		result.put("P9", value);

		// P10十、试验安排
		value = map.get("f10");
		result.put("P10", value);

		// 十一、项目进度安排
		setPlan(map, result);

		// 十二、经费概算及使用计划
		setBudget(map, result);

		// 十三、主要设备和仪器
		setEquripment(map, result);

		// 十四、承担部门和主要协作部门，项目组成员及分工

		setTeam(map, result);

		setComment(map, result);
		
		//设置项目的工作令号
		setProjectNumber(map, result);

		return result;
	}

	private void setProjectNumber(Map<String, Object> map,
			HashMap<String, Object> result) {
		Object pn = map.get("projectNumber");
		result.put("PROJID", pn==null?"":pn);
	}

	private void setComment(Map<String, Object> map, HashMap<String, Object> result) {

		// 方向首席师COMMENT1
		// 研究室负责人COMMENT2，RESULT2，ACTOR2，DATE2
		// 形式审查意见COMMENT3，RESULT3,ACTOR3,DATE3
		// 常务副主任COMMENT4,RESULT4,ACTOR4,DATE4

		// 获得流程信息
		
		DBObject work = null;
		Object workId = map.get(IDBConstants.FIELD_WBSPARENT);
		work = BusinessService.getWorkService().getWorkObject((ObjectId) workId);
		BasicDBList processHistory = (BasicDBList) work.get(IDBConstants.FIELD_PROCESSHISTORY);

		if (processHistory == null || processHistory.size() == 0) {
			BasicDBList hws = (BasicDBList) map.get(IDBConstants.FIELD_DOCUMENT_WORKS);
			if(hws!=null&&hws.size()>0){
				work = (DBObject) hws.get(0);
				processHistory = (BasicDBList) work.get(IDBConstants.FIELD_PROCESSHISTORY);
			}
		}		
		
		if (processHistory == null || processHistory.size() == 0) {
			
			return;
		}
		// 判断最后一个任务的名称
		// 如果是批准，可以返回前三个的
		// 如果是审查，返回前两个的
		// 如果是部门审查，只返回部门的

		// 找到最后一个 开发任务书批准
		int index5 = getIndexOfProcesss(processHistory, "技术副主任审核");
		int index4 = getIndexOfProcesss(processHistory, "开发任务书批准");
		int index3 = getIndexOfProcesss(processHistory, "开发任务书形式审查");
		int index2 = getIndexOfProcesss(processHistory, "开发任务书研究室审核");
		int index1 = getIndexOfProcesss(processHistory, "首席师审核");

		// 读取其他的方向首席师审核的记录,写入

		if (index1 != -1) {
			DBObject pi = (DBObject) processHistory.get(index1);
			setSingleComment(result, pi, 1);
//			String comment1 = "";
//			DBObject pi = (DBObject) processHistory.get(index1);
//			Date date = (Date) pi.get("closedate");
//			comment1 = comment1 + "审核意见:"+(pi.get("comment")==null?"无":pi.get("comment")) + "   ，审核结论:" + pi.get("choice") + "  ，审核人:" + pi.get("actorName") + " ，"
//					+ Util.getDateFormat(Util.SDF_YY_MM_DD).format(date) + "\n";
//			result.put("COMMENT1", comment1);
		}

		if (index2 != -1) {
			DBObject pi = (DBObject) processHistory.get(index2);
			setSingleComment(result, pi, 2);
		}

		if (index3 != -1) {
			DBObject pi = (DBObject) processHistory.get(index3);
			setSingleComment(result, pi, 3);
		}

		if (index4 != -1) {
			DBObject pi = (DBObject) processHistory.get(index4);
			setSingleComment(result, pi, 4);
		}
		
		if (index5 != -1) {
			DBObject pi = (DBObject) processHistory.get(index5);
			setSingleComment(result, pi, 5);
		}
		
//		if (lastindex1 < index2) {// 最后一次是方向首席师审核
//			clean(result, 4);
//			clean(result, 3);
//			clean(result, 2);
//		}
//		
//		if(index2<index3){
//			clean(result, 4);
//			clean(result, 3);
//		}
//		
//		if(index3<index4){
//			clean(result, 4);
//		}
		

	}

	private void setSingleComment(HashMap<String, Object> result, DBObject pi, int i) {

		result.put("COMMENT" + i, pi.get("comment"));
		result.put("RESULT" + i, pi.get("choice"));
		result.put("ACTOR" + i, pi.get("actorName"));
		Date date = (Date) pi.get("closedate");
		if(date==null){
			date = (Date) pi.get("opendate");
		}
		if(date!=null){
			result.put("DATE" + i, Util.getDateFormat(Util.SDF_YY_MM_DD).format(date));
		}
	}

//	private void clean(HashMap<String, Object> result, int i) {
//
//		result.put("COMMEN" + i, "");
//		result.put("RESULT" + i, "");
//		result.put("ACTOR" + i, "");
//		result.put("DATE" + i, "");
//
//	}

	private int getIndexOfProcesss(ArrayList<Object> processHistory, Object taskName) {

		for (int i = 0; i < processHistory.size(); i++) {
			DBObject pi = (DBObject) processHistory.get(i);
			if (taskName.equals(pi.get(IDBConstants.FIELD_WFINFO_TASKNAME))) {
				return i;
			}
		}
		return -1;

	}

	private String getDate(Map<String, Object> map, String fieldName) {

		SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);
		Object value = map.get(fieldName);
		if (value instanceof Date) {
			return sdf.format(value);

		}
		return "";
	}

	private void setTeam(Map<String, Object> map, HashMap<String, Object> result) {

		// 姓 名 所在部门 专业方向 内部职称 主要职责
		// [@U000] [@U001] [@U002] [@U003] [@U004]
		// 一共19行
		DecimalFormat nf1 = Util.getDecimalFormat("U00");

		BasicDBList data = (BasicDBList) map.get("teamdetail");
		if (data == null || data.isEmpty()) {
			return;
		}

		for (int i = 0; i < data.size(); i++) {
			String rowsn = nf1.format(i);

			DBObject row = (DBObject) data.get(i);

			// 姓 名
			DBObject pm = (DBObject) row.get(IDBConstants.FIELD_WORK_PM);
			if(pm!=null){
				result.put(rowsn + "0", pm.get(IDBConstants.FIELD_NAME));
			}

			// 所在部门
			result.put(rowsn + "1", row.get("dept"));

			// 专业方向
			result.put(rowsn + "2", row.get("direction"));

			// 内部职称
			result.put(rowsn + "3", row.get("title"));

			// 主要职责
			result.put(rowsn + "4", row.get("duty"));

		}
	}

	private void setEquripment(Map<String, Object> map, HashMap<String, Object> result) {

		// 序号 名称规格 型号 数量 解决途径 用途 需要时间
		// [@M000] [@M001] [@M002] [@M003] [@M004] [@M005] [@M006]
		// 一共19行

		DecimalFormat nf1 = Util.getDecimalFormat("M00");

		BasicDBList data = (BasicDBList) map.get("equipreq");
		// [@000] [@001] [@002] [@003] [@004]
		if (data == null || data.isEmpty()) {
			return;
		}

		for (int i = 0; i < data.size(); i++) {
			String rowsn = nf1.format(i);

			DBObject row = (DBObject) data.get(i);
			// 序号
			result.put(rowsn + "0", i + 1);

			// 名称规格
			result.put(rowsn + "1", row.get(IDBConstants.FIELD_DESC));

			// 型号
			result.put(rowsn + "2", row.get(IDBConstants.FIELD_SPEC));

			// 数量
			result.put(rowsn + "3", row.get(IDBConstants.FIELD_QTY));

			// 解决途径
			result.put(rowsn + "4", row.get("solution"));

			// 用途
			result.put(rowsn + "5", row.get("usage"));

			// 需要时间
			Date start = (Date) row.get("udate");
			if (start == null) {
				result.put(rowsn + "6", "");
			} else {
				SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);
				result.put(rowsn + "6", sdf.format(start));
			}

		}

	}

	private void setBudget(Map<String, Object> map, HashMap<String, Object> result) {

		// 项目内部直接支出 [@A0]
		// 材料燃料费 [@A1] cost_1_1
		// 仪器设备购置费 [@A2] cost_1_2
		// 仪器设备制作费 [@A3] cost_1_3
		// 仪器设备租赁及维护费 [@A4] cost_1_4
		// 外聘人员劳务费 [@A5] cost_1_5
		// 无形资产费 [@A6] cost_1_6
		// 中间试验和产品试制工装费 [@A7] cost_1_7
		// 会议及知识产权事务费 [@A8] cost_1_8
		// 与研发活动直接相关的其他费用 [@A9] cost_1_9
		// 项目外部支出 [@A10]
		// 项目支出合计 [@A11]
		DBObject budgetList = (BasicDBObject) map.get("budgetlist");
		if(budgetList==null) return;
		Double cost_1_1 = (Double) budgetList.get("cost_1_1");
		Double cost_1_2 = (Double) budgetList.get("cost_1_2");
		Double cost_1_3 = (Double) budgetList.get("cost_1_3");
		Double cost_1_4 = (Double) budgetList.get("cost_1_4");
		Double cost_1_5 = (Double) budgetList.get("cost_1_5");
		Double cost_1_6 = (Double) budgetList.get("cost_1_6");
		Double cost_1_7 = (Double) budgetList.get("cost_1_7");
		Double cost_1_8 = (Double) budgetList.get("cost_1_8");
		Double cost_1_9 = (Double) budgetList.get("cost_1_9");
		Double cost_2 = (Double) budgetList.get("cost_2");

		double cost_1 = sum(new Double[] { cost_1_1, cost_1_2, cost_1_3, cost_1_4, cost_1_5, cost_1_6, cost_1_7, cost_1_8, cost_1_9 });
		double cost_root = sum(new Double[] { cost_1, cost_2 });

		result.put("A0", getMoney(cost_1));
		result.put("A1", getMoney(cost_1_1));
		result.put("A2", getMoney(cost_1_2));
		result.put("A3", getMoney(cost_1_3));
		result.put("A4", getMoney(cost_1_4));
		result.put("A5", getMoney(cost_1_5));
		result.put("A6", getMoney(cost_1_6));
		result.put("A7", getMoney(cost_1_7));
		result.put("A8", getMoney(cost_1_8));
		result.put("A9", getMoney(cost_1_9));
		result.put("A10", getMoney(cost_2));
		result.put("A11", getMoney(cost_root));

	}

	private String getMoney(Double money) {

		return (money == null) ? "" : Util.getDecimalFormat("###,###,###.00").format(money);
	}

	private Double sum(Double[] doubles) {

		double d = 0;
		for (int i = 0; i < doubles.length; i++) {
			d = doubles[i] == null ? d : (d + doubles[i]);
		}
		return d;
	}

	private void setPlan(Map<String, Object> map, HashMap<String, Object> result) {

		DecimalFormat nf1 = Util.getDecimalFormat("00");

		// 序号??0 工作内容??1 计划开始??2 计划完成??3 负责人??4//一共19行
		BasicDBList projectPlan = (BasicDBList) map.get("projectplan");
		// [@000] [@001] [@002] [@003] [@004]
		if (projectPlan == null || projectPlan.isEmpty()) {
			return;
		}

		for (int i = 0; i < projectPlan.size(); i++) {
			String rowsn = nf1.format(i);

			DBObject row = (DBObject) projectPlan.get(i);
			// 序号
			result.put(rowsn + "0", i + 1);

			// 工作内容
			result.put(rowsn + "1", row.get(IDBConstants.FIELD_DESC));

			// 计划开始
			Date start = (Date) row.get(IDBConstants.FIELD_PROJECT_PLANSTART);
			if (start == null) {
				result.put(rowsn + "2", "");
			} else {
				SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

				result.put(rowsn + "2", sdf.format(start));
			}

			// 计划完成
			Date finish = (Date) row.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
			if (finish == null) {
				result.put(rowsn + "3", "");
			} else {
				SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

				result.put(rowsn + "3", sdf.format(finish));
			}

			// 负责人
			result.put(rowsn + "4", row.get(IDBConstants.FIELD_WORK_PM));

		}
	}

	private String dateToCN(Date date) {

		if (null == date || "".equals(date)) {
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		StringBuffer cn = new StringBuffer();
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		for (int i = 0; i < year.length(); i++) {
			cn.append(CN[year.charAt(i) - 48]);
		}
		cn.append("年");
		int mon = calendar.get(Calendar.MONTH) + 1;
		if (mon < 10) {
			cn.append(CN[mon]);
		} else if (mon < 20) {
			if (mon == 10) {
				cn.append("十");
			} else {
				cn.append("十").append(CN[mon % 10]);
			}
		}
		cn.append("月");
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			cn.append(CN[day]);
		} else if (day < 20) {
			if (day == 10) {
				cn.append("十");
			} else {
				cn.append("十").append(CN[day % 10]);
			}
		} else if (day < 30) {
			if (day == 20) {
				cn.append("二十");
			} else {
				cn.append("二十").append(CN[day % 10]);
			}
		} else {
			cn.append("三十").append(CN[day % 10]);
		}
		cn.append("日");
		return cn.toString();
	}

}
