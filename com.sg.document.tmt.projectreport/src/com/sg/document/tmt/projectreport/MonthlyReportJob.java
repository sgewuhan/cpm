package com.sg.document.tmt.projectreport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.db.DBActivator;

public class MonthlyReportJob extends ScheduleJob {
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		DBCollection worktCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK_TEMPLATE);
		DBCollection workCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);
		DBCollection docCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
		WorkService workService = BusinessService.getWorkService();

		
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		if(day != BusinessService.getDefault().getMonthlyReportDate()){
			return Status.OK_STATUS;
		}

		//ȡģ��


		DBObject templateData = worktCollection.findOne(new BasicDBObject().append("desc", "��Ŀ�±�").append(IDBConstants.FIELD_TEMPLATE_TYPE, IDBConstants.VALUE_WBS_TASK_TYPE));
		ObjectId templateId = (ObjectId) templateData.get(IDBConstants.FIELD_SYSID);
		
		//�����������ڽ��е���Ŀ,���߱��¹رյ���Ŀ
		List<DBObject> reportProjectList = new ArrayList<DBObject>();

		DBCollection projectCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_PROJECT);
		BasicDBList condition = new BasicDBList();
		condition.add(new BasicDBObject().append(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_PROCESS));
		condition.add(new BasicDBObject().append(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_CLOSE));
		DBCursor cur = projectCollection.find(new BasicDBObject()
			.append("$or", condition
					));
		while(cur.hasNext()){
			DBObject projectData = cur.next();
			if(IDBConstants.VALUE_PROCESS_PROCESS.equals(projectData.get(IDBConstants.FIELD_PROCESS_STATUS))){
				reportProjectList.add(projectData);
				continue;
			}
			
			Date actualFinish = (Date) projectData.get(IDBConstants.FIELD_PROJECT_ACTUALFINISH);
			if(actualFinish!=null){
				Calendar a = Calendar.getInstance();
				a.setTime(actualFinish);
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				if((a.get(Calendar.YEAR)==c.get(Calendar.YEAR))&&(a.get(Calendar.MONTH)==c.get(Calendar.MONTH))){
					reportProjectList.add(projectData);
					continue;
				}
			}
		}
		
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		Date planStart = now;
		c.setTime(now);
		c.add(Calendar.DATE, 2);
		Date planFinish = c.getTime();
				
		Calendar a = Calendar.getInstance();
		a.setTime(new Date());
		
		String year = ""+a.get(Calendar.YEAR);
		String month = "" +(a.get(Calendar.MONTH)+1);
		
		for(int i=0;i<reportProjectList.size();i++){
			DBObject projectData = reportProjectList.get(i);
			//�ҳ����������
			//��������
			DBObject workData = new BasicDBObject();
			//��������
			String title = "��Ŀ�±�"+year+"-"+month;
			workData.put(IDBConstants.FIELD_DESC, title);
			
			//����ģ��
			workData.put(IDBConstants.FIELD_TEMPLATE, templateId);
			
			//�ҳ����̷�����
			workData.put(IDBConstants.FIELD_WORK_PM, projectData.get(IDBConstants.FIELD_PROJECT_PM));
			
			//����ʼʱ��
			workData.put(IDBConstants.FIELD_PROJECT_PLANSTART,planStart);
			
			//�������ʱ��
			workData.put(IDBConstants.FIELD_PROJECT_PLANFINISH,planFinish);

			ObjectId workid = new ObjectId();
			workData.put(IDBConstants.FIELD_SYSID, workid);
			workCollection.insert(workData);

			// �Ƿ�ֱ����������
			workService.createWork(workData,true,"ʱ���²���վ��");
			
			
			//����ĵ�
			List<DBObject> docList = workService.getDocumentOfWork(workid, "com.sg.cpm.editor.projectmonthreport");
			if(docList!=null&&!docList.isEmpty()){
				DBObject doc = docList.get(0);
				doc.put("desc", title);
				doc.put("year", year);
				doc.put("month", month);
				doc.put("project", projectData);
				docCollection.save(doc);
			}
		}
		return Status.OK_STATUS;
	}


}
