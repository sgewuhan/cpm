package com.sg.common.workflow.parameter;

import com.mongodb.BasicDBObject;


public interface IProcessParameterDelegator {

	Object getValue(String processParameter, String taskDatakey, BasicDBObject taskFormData);

}
