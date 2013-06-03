package com.sg.document.tmt.projectreport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.db.DBActivator;
import com.sg.widget.WidgetConstants;
import com.sg.widget.util.ExcelExportJob;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;

public class ReportExport {

	public void report(String year, String month, ObjectId deptId) {
		WorkService workService = BusinessService.getWorkService();
		DBCollection docCollection = DBActivator
				.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);

		// �г������µ�������Ŀ
		List<DBObject> projectList = workService.getProjectOfOrganization(
				deptId, true);

		DBCursor cur = docCollection.find(new BasicDBObject()
				.append("year", year)
				.append("month", month)
				.append(IDBConstants.FIELD_SYSTEM_EDITOR,
						"com.sg.cpm.editor.projectmonthreport")
				.append("reportStatus", "����׼"));

		List<DBObject> reportList = new ArrayList<DBObject>();
		while (cur.hasNext()) {
			DBObject doc = cur.next();
			if (inProject(doc, projectList)) {
				reportList.add(doc);
			}
		}
		
		String exportTemplatePath = System.getProperty("user.dir")+"/export/template/ProjectMonthlyReportTemplate.xls";
		final String exportOutputPath = WidgetConstants.PATH_TEMP+"/ProjectMonthlyReport_"+year+"_"+month+".xls" ;
		Map<String, Object> headData = new HashMap<String,Object>();
		headData.put("YEAR", year);
		headData.put("MONTH", month);
		
		List<Object[]> bodyData = new ArrayList<Object[]>();
		Object[] grid = new Object[2];
		grid[0] = "GRD:1";
		String[][] data = new String[reportList.size()][10];
		grid[1] = data;
		
		for(int i=0;i<reportList.size();i++){
			DBObject report = reportList.get(i);
			DBObject project = (DBObject) report.get("project");
			DBObject pm = (DBObject) project.get(IDBConstants.FIELD_PROJECT_PM);
			
			
			Date dPlanstart = (Date) project
					.get(IDBConstants.FIELD_PROJECT_PLANSTART);
			Date dPlanfinish = (Date) project
					.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
			Date dActualstart = (Date) project
					.get(IDBConstants.FIELD_PROJECT_ACTUALSTART);
			Date dActualfinish = (Date) project
					.get(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
			SimpleDateFormat sdf = Util
					.getDateFormat(Util.SDF_YY_MM_DD);
			String planstart = dPlanstart == null ? "" : sdf
					.format(dPlanstart);
			String planfinish = dPlanfinish == null ? "" : sdf
					.format(dPlanfinish);
			String actualstart = dActualstart == null ? "" : sdf
					.format(dActualstart);
			String actualfinish = dActualfinish == null ? "" : sdf
					.format(dActualfinish);

			Double budget = (Double) project.get("budget");
			Double actual = (Double) report.get("costFinishedMonth");
			String percent = "";
			if(budget!=null&&budget!=0){
				percent = Util.getDecimalFormat(Util.NUMBER_P2_PERC).format(actual/budget);
			}
			
			
			String _budget = (budget==null)?"":(Util.getDecimalFormat(Util.NUMBER_P2).format(budget));
			
			//���
			data[i][0] = ""+i;
			
			//�������
			data[i][1] = (String) project.get(IDBConstants.FIELD_ID);
			
			//��Ŀ����
			data[i][2] = (String) project.get(IDBConstants.FIELD_DESC);
			
			//������
			data[i][3] = (String) pm.get(IDBConstants.FIELD_NAME);
			
			//����
			data[i][4] = planstart+"~"+planfinish;
			
			data[i][5] = actualstart+"~"+actualfinish;

			//Ԥ��
			data[i][6] = _budget;
			
			//��Ŀ������ִ�������������ɹ��������
			data[i][7] = (String) report.get("workSummary");
			
			//��ĿԤ��ִ�������%��
			data[i][8] = percent;
			
			//��ϯʦ����
			data[i][9] = (String) report.get("comment");
			
			
		}
		
		final Display display = Display.getCurrent();
		bodyData.add(grid);
		ExcelExportJob job = new ExcelExportJob(exportTemplatePath, exportOutputPath, headData, bodyData);
		job.setUser(true);
		job.addJobChangeListener(new JobChangeAdapter(){
			@Override
			public void done(IJobChangeEvent event) {
				
				display.asyncExec(new Runnable() {

					public void run() {

						FileUtil.download(exportOutputPath);
					}
				});
			}
		});
		job.schedule();
	}

	private boolean inProject(DBObject doc, List<DBObject> projectList) {
		DBObject project = (DBObject) doc.get("project");
		for (DBObject dbObject : projectList) {
			if (dbObject.get(IDBConstants.FIELD_SYSID).equals(
					project.get(IDBConstants.FIELD_SYSID))) {
				return true;
			}
		}
		return false;
	}

}
