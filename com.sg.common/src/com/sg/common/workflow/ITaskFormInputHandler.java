package com.sg.common.workflow;

import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;


public interface ITaskFormInputHandler {

	ISingleObject getTaskFormInputData(DBObject taskFormData, TaskFormConfig taskFormConfig);

}
