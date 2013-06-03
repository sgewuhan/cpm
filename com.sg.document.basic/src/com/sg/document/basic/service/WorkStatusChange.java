package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;

public class WorkStatusChange extends ServiceProvider {

	public WorkStatusChange() {

	}

	@Override
	public Map<String, Object> run(Object parameter) {
		if(parameter == null){
			return null;
		}

		ObjectId workId = new ObjectId((String) parameter);
		
		String status = getStatus();
		if (status == null)
			return null;

		WorkService workService = BusinessService.getWorkService();
		
		workService.updateWorkStatus(workId, status);
		
		workService.attachWorkDocument(workId);//��������ɵķ�����ִ��

		// ���ĵ��ҵ���Ŀ��������
		// Ѱ����Ŀ�������Ƶ����񣬰��ĵ��ҵ������С�
		String _projectId = (String)getInputValue("p_ProjectId");
		if(_projectId == null){
			return null;
		}
		ObjectId projectId = new ObjectId(_projectId);

		if(projectId!=null){
//			List<DBObject> doclist = workService.getDocumentOfWork(workId);
//			workService.attachDocumentToProject(projectId,doclist,"��Ŀ����");
			workService.attachWorkToProject(projectId,workId,"��Ŀ����");
			
		}
		return null;
	}

	private String getStatus() {

		String workStatus = getOperation();
		if ("cancel".equalsIgnoreCase(workStatus)) {
			return IDBConstants.VALUE_PROCESS_CANCEL;
		}

		if ("close".equalsIgnoreCase(workStatus)) {
			return IDBConstants.VALUE_PROCESS_CLOSE;
		}

		if ("pause".equalsIgnoreCase(workStatus)) {
			return IDBConstants.VALUE_PROCESS_PAUSE;
		}

		if ("process".equalsIgnoreCase(workStatus)) {
			return IDBConstants.VALUE_PROCESS_PROCESS;
		}

		if ("ready".equalsIgnoreCase(workStatus)) {
			return IDBConstants.VALUE_PROCESS_READY;
		}

		return null;
	}

}
