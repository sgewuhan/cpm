package com.sg.document.tmt.projectreport.editor.presentation;

import java.util.Date;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;
import com.sg.widget.util.Util;

public class ProjectTimeFinishedPercentPresentation implements
		IValuePresentation {

	public ProjectTimeFinishedPercentPresentation() {
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		
		DBObject editorData = data.getData();
		DBObject projectData = (DBObject) editorData.get("project");
		if(projectData==null){
			return "";
		}
		
		Date actualStartDate = (Date) projectData.get(IDBConstants.FIELD_PROJECT_ACTUALSTART);
		Date planStartDate = (Date) projectData.get(IDBConstants.FIELD_PROJECT_PLANSTART);
		Date planFinishDate = (Date) projectData.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		if(actualStartDate==null||planStartDate==null||planFinishDate==null){
			return "";
		}
		
		Date now = new Date();
		long actual = now.getTime()-actualStartDate.getTime();
		
		long plan = planFinishDate.getTime() - planStartDate.getTime();
		
		double result = (actual/(double)plan);
		
		return Util.getDecimalFormat(format).format(result);
	}

}
