package com.sg.document.workflow.parameters;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.OrganizationService;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class PassDeptManager implements IProcessParameterDelegator {

	public PassDeptManager() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		Object workId = taskFormData.get(IDBConstants.FIELD_SYSID);
		DBObject doc = BusinessService.getDocumentService().getWorkDocument((ObjectId) workId, "com.sg.cpm.editor.JZ-QR-XG003A--1");
		ObjectId deptId = (ObjectId) doc.get("dept");
		//找出部门经理
		OrganizationService os = BusinessService.getOrganizationService();
		DBObject role = os.getRoleInTeamByName(deptId,"部门经理");
		if(role!=null){
			BasicDBList userList = os.getUsersInRole((ObjectId) role.get(OrganizationService.FIELD_SYSID));
			if(userList!=null&&userList.size()>0){
				DBObject user = (DBObject) userList.get(0);
				return user.get(OrganizationService.FIELD_UID);
			}
		}
		return "000000";
	}

}
