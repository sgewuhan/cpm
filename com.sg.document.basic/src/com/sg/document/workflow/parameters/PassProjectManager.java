package com.sg.document.workflow.parameters;

import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.service.OrganizationService;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class PassProjectManager implements IProcessParameterDelegator {

	public PassProjectManager() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		DBObject pm = (DBObject) taskFormData.get(taskDatakey);
		Assert.isNotNull(pm, "项目负责人空，无法提取流程参数");
		if(processParameter.equals("projectManagerId")){
			return pm.get(OrganizationService.FIELD_UID);
		}else if(processParameter.equals("projectManagerOid")){
			return pm.get(OrganizationService.FIELD_SYSID).toString();
		}
		return null;
	}

}
