package com.sg.document.tmt.projectreport.editor.presentation;

import java.util.Date;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class ProjectTimeFinishedPresentation implements IValuePresentation {

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		
		DBObject editorData = data.getData();
		DBObject projectData = (DBObject) editorData.get("project");
		if(projectData==null){
			return "";
		}
		
		Date actualStartDate = (Date) projectData.get(IDBConstants.FIELD_PROJECT_ACTUALSTART);
		if(actualStartDate==null){
			return "";
		}
		
		Date now = new Date();
		
		long between = now.getTime()-actualStartDate.getTime();
		long days = between/(1000*60*60*24);
		
		return Long.toString(days);
	}



}
