package com.sg.document.workflow.validation;

import com.mongodb.DBObject;
import com.sg.common.workflow.IValidationHandler;

public class Project2DeptReviewValidator implements IValidationHandler {

	public Project2DeptReviewValidator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validateBeforeOpen(DBObject workData) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
