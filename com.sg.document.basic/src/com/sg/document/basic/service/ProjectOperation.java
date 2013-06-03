package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.service.WorkService;

public class ProjectOperation extends ServiceProvider {

	private WorkService workService;

	public ProjectOperation() {
		workService = BusinessService.getWorkService();
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		if ("start".equalsIgnoreCase(getOperation())) {
			startProject();
		}
		return null;
	}

	private void startProject() {
		String _projectOid = (String) getInputValue("p_ProjectOid");
		ObjectId projectId = new ObjectId(_projectOid);

		Object startProject = getInputValue("p_StartProject");
		Object autoAssign = getInputValue("p_AutoAssign");
		if(Boolean.TRUE.equals(autoAssign)){
			workService.projectAutoAssign(projectId);
		}
		
		if (Boolean.TRUE.equals(startProject)) {
			workService.startProject(projectId);
		}

	}

}
