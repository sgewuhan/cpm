package com.sg.common.workflow.parameter;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class Object_Id2String implements IProcessParameterDelegator {

	@Override
	public Object getValue(String processParameter, String taskDatakey, BasicDBObject taskFormData) {

		ObjectId id = (ObjectId) taskFormData.get(taskDatakey);
		return id.toString();
	}

}
