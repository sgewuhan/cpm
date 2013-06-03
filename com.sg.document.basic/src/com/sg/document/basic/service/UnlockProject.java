package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;

public class UnlockProject extends ServiceProvider {

	public UnlockProject() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String _oid = (String) getInputValue("p_ProjectId");
		BusinessService.getWorkService().unlockProject(new ObjectId(_oid));
		return null;
	}

}
