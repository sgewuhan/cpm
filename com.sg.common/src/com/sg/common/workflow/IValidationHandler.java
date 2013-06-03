package com.sg.common.workflow;

import com.mongodb.DBObject;


public interface IValidationHandler {

	boolean validateBeforeOpen(DBObject workData);

	String getMessage();

}
