package com.sg.common.workflow;

import com.mongodb.DBObject;

public interface IWorkflowInfoProvider {

	Object getWorkflowInformation(DBObject workData);

}
