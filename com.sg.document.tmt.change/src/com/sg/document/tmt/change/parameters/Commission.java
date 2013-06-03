package com.sg.document.tmt.change.parameters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class Commission implements IProcessParameterDelegator {

	public Commission() {
	}

	@Override
	public Object getValue(String processParameter, String taskDatakey,
			BasicDBObject taskFormData) {
		
		DBObject project = (DBObject) taskFormData.get("project");
		DBObject template = (DBObject) project.get("template");
		if(template!=null){
			Object desc = template.get("desc");
			return "技术中心-技术支持类项目".equals(desc);
		}
		return false;
	}

}
