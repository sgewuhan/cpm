package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.IServiceProvider;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;

public class ChangeProjectStatusFinish extends ServiceProvider implements
		IServiceProvider {

	public ChangeProjectStatusFinish() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String _oid = (String) getInputValue("p_ProjectId");
		ObjectId projectOid = new ObjectId(_oid);
		
		WorkService workService = BusinessService.getWorkService();

		workService.changeProjectStatus(projectOid,WorkService.VALUE_PROCESS_CLOSE);
		
		String _workOid =  (String) getInputValue("p_WorkId");
		ObjectId workId = new ObjectId(_workOid);

		workService.updateWorkStatus(workId, IDBConstants.VALUE_PROCESS_CLOSE);

		workService.attachWorkDocument(workId);

//		List<DBObject> doclist = workService.getDocumentOfWork(workId);
//		workService.attachDocumentToProject(projectOid,doclist,"项目结束");
		workService.attachWorkToProject(projectOid,workId,"项目结束");
		return null;
	}

}
