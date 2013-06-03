package com.sg.document.tmt.projectreport.editor.presentation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.widget.util.Util;

public class ProjectBudgetPresentation extends AbstractProjectDSDPresentation {

	
	public ProjectBudgetPresentation() {
	}


	@Override
	protected String getValue(DBObject editorData,ObjectId projectId, String format) {
		Object result = getDataFromDocument(projectId, "budget");
		if (result == null)
			return "";
		return Util.getDecimalFormat(format).format(result);
	}

}
