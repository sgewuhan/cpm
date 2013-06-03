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

	private static String[] CN = { "��", "һ", "��", "��", "��", "��", "��", "��", "��", "��" };

	public ProjectPlanExport() {

	}

	@Override
	public Map<String, Object> getParameters(Map<String, Object> map) {

		HashMap<String, Object> result = new HashMap<String, Object>();

		Object value;

		// PROJTYPE��Ŀ����
		value = map.get("type");
		result.put("PROJTYPE", value);

		// PROJNAME��Ŀ����
		value = map.get("projectdesc");
		result.put("PROJNAME", value);

		// DEPT��������
		Object obsparent = map.get("dept");
		DBObject dept = BusinessService.getOrganizationService().getOBSItemData((ObjectId) obsparent);
		value = dept.get(IDBConstants.FIELD_DESC);
		result.put("DEPT", value);

		// PMNAME��Ŀ����������
		DBObject pmdata = (DBObject) map.get(IDBConstants.FIELD_PROJECT_PM);
		value = pmdata.get(IDBConstants.FIELD_NAME);
		result.put("PMNAME", value);

		// DIRECTOR������ϯʦ
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

		// STARTDATE�ƻ���ʼ
		result.put("STARTDATE", getDate(map, IDBConstants.FIELD_PROJECT_PLANSTART));

		// FINISHDATE�ƻ����
		result.put("FINISHDATE", getDate(map, IDBConstants.FIELD_PROJECT_PLANFINISH));

		// APPDATE�����
		result.put("APPDATE", getDate(map, IDBConstants.FIELD_CREATE_DATE));

		// APPDATE2���ĵ������

		result.put("APPDATE2", dateToCN((Date) map.get(IDBConstants.FIELD_CREATE_DATE)));

		// GEND�Ա�
		value = map.get("pmgender");
		result.put("GEND", value);

		// BIR��������
		result.put("BIR", getDate(map, "pmbirthday"));

		// DEGREEѧ��
		value = map.get("pmdegree");
		result.put("DEGREE", value);

		// TITLE�ڲ�ְ��
		value = map.get("pmtitle");
		result.put("TITLE", value);

		// DIRECTION��Ŀ����
		value = map.get("direction");
		result.put("DIRECTION", value);

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

		// CONTENT�о�����
		value = map.get("research");
		result.put("CONTENT", value);

		// Ԥ�ڳɹ�
		value = "";
		Object res = map.get("result_1");
		if (Boolean.TRUE.equals(res)) {
			value = value + "ר��";
		}
		res = map.get("result_2");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", ������׼";
		}
		res = map.get("result_3");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", �²�Ʒ";
		}
		res = map.get("result_4");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", �¹���";
		}
		res = map.get("result_5");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", ��װ��";
		}
		res = map.get("result_6");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", �²���";
		}
		res = map.get("result_7");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", ��������";
		}
		res = map.get("result_8");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", �о�����";
		}
		res = map.get("result_9");
		if (Boolean.TRUE.equals(res)) {
			value = value + ", ����";
		}
		result.put("RESULT", value);

		// R1����ר������
		Integer n = (Integer) map.get("pi1");
		n = n == null ? 0 : n;
		result.put("R1", n);

		// R2����ר������
		n = (Integer) map.get("pi2");
		n = n == null ? 0 : n;
		result.put("R2", n);

		// R3��������
		n = (Integer) map.get("pi3");
		n = n == null ? 0 : n;
		result.put("R3", n);

		// STD��׼���������ʱ�׼ �������׼ �����ұ�׼ ����ҵ��׼ ����ҵ��׼��
		String std = "";
		value = map.get("std_1");
		if (Boolean.TRUE.equals(value)) {
			std = std + "[��]���ʱ�׼";
		} else {
			std = std + "[x]���ʱ�׼";
		}

		value = map.get("std_2");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [��]�����׼";
		} else {
			std = std + " [x]�����׼";
		}

		value = map.get("std_3");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [��]���ұ�׼";
		} else {
			std = std + " [x]���ұ�׼";
		}

		value = map.get("std_4");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [��]��ҵ��׼";
		} else {
			std = std + " [x]��ҵ��׼";
		}

		value = map.get("std_5");
		if (Boolean.TRUE.equals(value)) {
			std = std + " [��]��ҵ��׼";
		} else {
			std = std + " [x]��ҵ��׼";
		}
		result.put("STD", std);

		// PAPER���� "EI   ƪ SCI   ƪ ISTP    ƪ ���ĺ����ڿ�    ƪ ����  ƪ"
		StringBuffer sb = new StringBuffer();

		value = map.get("pp1");
		sb.append("EI ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ƪ");

		value = map.get("pp2");
		sb.append(" SCI ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ƪ");

		value = map.get("pp3");
		sb.append(" ISTP ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ƪ");

		value = map.get("pp4");
		sb.append(" ���ĺ����ڿ� ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ƪ");

		value = map.get("pp5");
		sb.append(" ���� ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ƪ");

		result.put("PAPER", sb.toString());

		// TRAINNING�˲����� ��ϯʦ�� tmastercnt�� ����ʦ��tdirectorcnt �� �߼�ʦ�� tseniorcnt��
		// ʦ��tjoniorcnt ��
		sb = new StringBuffer();

		value = map.get("tmastercnt");
		sb.append("��ϯʦ�� ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ��");

		value = map.get("tdirectorcnt");
		sb.append(" ����ʦ�� ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ��");

		value = map.get("tseniorcnt");
		sb.append(" �߼�ʦ�� ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ��");

		value = map.get("tjoniorcnt");
		sb.append(" ʦ�� ");
		sb.append(Util.isNullOrEmptyString(value) ? " " : value);
		sb.append(" ��");
		result.put("TRAINNING", sb.toString());

		// BUDGET����Ԥ��
		Double bgt = (Double) map.get("budget");
		value = bgt == null ? "" : Util.getDecimalFormat("###,###,###.00").format(bgt) + "��Ԫ";
		result.put("BUDGET", value);

		// P1Ŀ������
		value = map.get("f1");
		result.put("P1", value);

		// P2�����������о���״�뷢չ����
		value = map.get("f2");
		result.put("P2", value);

		// P3������Ŀʵʩ��Ҫ�����봴�µ㡢Ԥ��Ŀ��
		value = map.get("f3");
		result.put("P3", value);

		// P4�ġ���Ŀר�����鱨�������
		value = map.get("f4");
		result.put("P4", value);

		// P5�塢��ĿԤ�ڳɹ�
		value = map.get("f5");
		result.put("P5", value);

		// P6������Ŀ���á����Ч���������Ŀ���շ���
		value = map.get("f6");
		result.put("P6", value);

		// P7�ߡ������ؼ��������ѵ����Ҫ��������
		value = map.get("f7");
		result.put("P7", value);

		// P8�ˡ����й�������������������
		value = map.get("f8");
		result.put("P8", value);

		// P9�š���Ŀʵʩ����
		value = map.get("f9");
		result.put("P9", value);

		// P10ʮ�����鰲��
		value = map.get("f10");
		result.put("P10", value);

		// ʮһ����Ŀ���Ȱ���
		setPlan(map, result);

		// ʮ�������Ѹ��㼰ʹ�üƻ�
		setBudget(map, result);

		// ʮ������Ҫ�豸������
		setEquripment(map, result);

		// ʮ�ġ��е����ź���ҪЭ�����ţ���Ŀ���Ա���ֹ�

		setTeam(map, result);

		setComment(map, result);
		
		//������Ŀ�Ĺ������
		setProjectNumber(map, result);

		return result;
	}

	private void setProjectNumber(Map<String, Object> map,
			HashMap<String, Object> result) {
		Object pn = map.get("projectNumber");
		result.put("PROJID", pn==null?"":pn);
	}

	private void setComment(Map<String, Object> map, HashMap<String, Object> result) {

		// ������ϯʦCOMMENT1
		// �о��Ҹ�����COMMENT2��RESULT2��ACTOR2��DATE2
		// ��ʽ������COMMENT3��RESULT3,ACTOR3,DATE3
		// ��������COMMENT4,RESULT4,ACTOR4,DATE4

		// ���������Ϣ
		
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
		// �ж����һ�����������
		// �������׼�����Է���ǰ������
		// �������飬����ǰ������
		// ����ǲ�����飬ֻ���ز��ŵ�

		// �ҵ����һ�� ������������׼
		int index5 = getIndexOfProcesss(processHistory, "�������������");
		int index4 = getIndexOfProcesss(processHistory, "������������׼");
		int index3 = getIndexOfProcesss(processHistory, "������������ʽ���");
		int index2 = getIndexOfProcesss(processHistory, "�����������о������");
		int index1 = getIndexOfProcesss(processHistory, "��ϯʦ���");

		// ��ȡ�����ķ�����ϯʦ��˵ļ�¼,д��

		if (index1 != -1) {
			DBObject pi = (DBObject) processHistory.get(index1);
			setSingleComment(result, pi, 1);
//			String comment1 = "";
//			DBObject pi = (DBObject) processHistory.get(index1);
//			Date date = (Date) pi.get("closedate");
//			comment1 = comment1 + "������:"+(pi.get("comment")==null?"��":pi.get("comment")) + "   ����˽���:" + pi.get("choice") + "  �������:" + pi.get("actorName") + " ��"
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
		
//		if (lastindex1 < index2) {// ���һ���Ƿ�����ϯʦ���
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

		// �� �� ���ڲ��� רҵ���� �ڲ�ְ�� ��Ҫְ��
		// [@U000] [@U001] [@U002] [@U003] [@U004]
		// һ��19��
		DecimalFormat nf1 = Util.getDecimalFormat("U00");

		BasicDBList data = (BasicDBList) map.get("teamdetail");
		if (data == null || data.isEmpty()) {
			return;
		}

		for (int i = 0; i < data.size(); i++) {
			String rowsn = nf1.format(i);

			DBObject row = (DBObject) data.get(i);

			// �� ��
			DBObject pm = (DBObject) row.get(IDBConstants.FIELD_WORK_PM);
			if(pm!=null){
				result.put(rowsn + "0", pm.get(IDBConstants.FIELD_NAME));
			}

			// ���ڲ���
			result.put(rowsn + "1", row.get("dept"));

			// רҵ����
			result.put(rowsn + "2", row.get("direction"));

			// �ڲ�ְ��
			result.put(rowsn + "3", row.get("title"));

			// ��Ҫְ��
			result.put(rowsn + "4", row.get("duty"));

		}
	}

	private void setEquripment(Map<String, Object> map, HashMap<String, Object> result) {

		// ��� ���ƹ�� �ͺ� ���� ���;�� ��; ��Ҫʱ��
		// [@M000] [@M001] [@M002] [@M003] [@M004] [@M005] [@M006]
		// һ��19��

		DecimalFormat nf1 = Util.getDecimalFormat("M00");

		BasicDBList data = (BasicDBList) map.get("equipreq");
		// [@000] [@001] [@002] [@003] [@004]
		if (data == null || data.isEmpty()) {
			return;
		}

		for (int i = 0; i < data.size(); i++) {
			String rowsn = nf1.format(i);

			DBObject row = (DBObject) data.get(i);
			// ���
			result.put(rowsn + "0", i + 1);

			// ���ƹ��
			result.put(rowsn + "1", row.get(IDBConstants.FIELD_DESC));

			// �ͺ�
			result.put(rowsn + "2", row.get(IDBConstants.FIELD_SPEC));

			// ����
			result.put(rowsn + "3", row.get(IDBConstants.FIELD_QTY));

			// ���;��
			result.put(rowsn + "4", row.get("solution"));

			// ��;
			result.put(rowsn + "5", row.get("usage"));

			// ��Ҫʱ��
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

		// ��Ŀ�ڲ�ֱ��֧�� [@A0]
		// ����ȼ�Ϸ� [@A1] cost_1_1
		// �����豸���÷� [@A2] cost_1_2
		// �����豸������ [@A3] cost_1_3
		// �����豸���޼�ά���� [@A4] cost_1_4
		// ��Ƹ��Ա����� [@A5] cost_1_5
		// �����ʲ��� [@A6] cost_1_6
		// �м�����Ͳ�Ʒ���ƹ�װ�� [@A7] cost_1_7
		// ���鼰֪ʶ��Ȩ����� [@A8] cost_1_8
		// ���з��ֱ����ص��������� [@A9] cost_1_9
		// ��Ŀ�ⲿ֧�� [@A10]
		// ��Ŀ֧���ϼ� [@A11]
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

		// ���??0 ��������??1 �ƻ���ʼ??2 �ƻ����??3 ������??4//һ��19��
		BasicDBList projectPlan = (BasicDBList) map.get("projectplan");
		// [@000] [@001] [@002] [@003] [@004]
		if (projectPlan == null || projectPlan.isEmpty()) {
			return;
		}

		for (int i = 0; i < projectPlan.size(); i++) {
			String rowsn = nf1.format(i);

			DBObject row = (DBObject) projectPlan.get(i);
			// ���
			result.put(rowsn + "0", i + 1);

			// ��������
			result.put(rowsn + "1", row.get(IDBConstants.FIELD_DESC));

			// �ƻ���ʼ
			Date start = (Date) row.get(IDBConstants.FIELD_PROJECT_PLANSTART);
			if (start == null) {
				result.put(rowsn + "2", "");
			} else {
				SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

				result.put(rowsn + "2", sdf.format(start));
			}

			// �ƻ����
			Date finish = (Date) row.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
			if (finish == null) {
				result.put(rowsn + "3", "");
			} else {
				SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YYYY__MM__DD);

				result.put(rowsn + "3", sdf.format(finish));
			}

			// ������
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
		cn.append("��");
		int mon = calendar.get(Calendar.MONTH) + 1;
		if (mon < 10) {
			cn.append(CN[mon]);
		} else if (mon < 20) {
			if (mon == 10) {
				cn.append("ʮ");
			} else {
				cn.append("ʮ").append(CN[mon % 10]);
			}
		}
		cn.append("��");
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if (day < 10) {
			cn.append(CN[day]);
		} else if (day < 20) {
			if (day == 10) {
				cn.append("ʮ");
			} else {
				cn.append("ʮ").append(CN[day % 10]);
			}
		} else if (day < 30) {
			if (day == 20) {
				cn.append("��ʮ");
			} else {
				cn.append("��ʮ").append(CN[day % 10]);
			}
		} else {
			cn.append("��ʮ").append(CN[day % 10]);
		}
		cn.append("��");
		return cn.toString();
	}

}
