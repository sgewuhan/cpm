package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;

public class LockProject extends ServiceProvider {

	public LockProject() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String _oid = (String) getInputValue("p_ProjectId");
		BusinessService.getWorkService().lockProject(new ObjectId(_oid));
		return null;
	}

}
