package com.sg.document.tmt.projectreport.editor.presentation;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class ProjectContentPresentation extends AbstractProjectDSDPresentation{

	public ProjectContentPresentation() {
	}
	
	@Override
	protected String getValue(DBObject editorData,ObjectId projectId, String format) {
		Object result = getDataFromDocument(projectId, "research");
		if (result == null)
			return "";
		return result.toString();
	}

}
