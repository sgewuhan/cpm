package com.sg.document.workflow.parameters;

import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.service.OrganizationService;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class PassChiefEngineer2 implements IProcessParameterDelegator {

	public PassChiefEngineer2() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		DBObject user = (DBObject) taskFormData.get("chiefengineer");
		Assert.isNotNull(user, "��ϯʦΪ�գ��޷���ȡ��������Ĳ���");
		return user.get(OrganizationService.FIELD_UID);
	}

}
