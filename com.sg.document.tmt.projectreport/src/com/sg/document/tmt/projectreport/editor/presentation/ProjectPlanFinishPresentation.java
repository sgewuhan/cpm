package com.sg.document.tmt.projectreport.editor.presentation;

import java.util.Date;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;
import com.sg.widget.util.Util;

public class ProjectPlanFinishPresentation implements IValuePresentation {

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		DBObject editorData = data.getData();
		DBObject projectData = (DBObject) editorData.get("project");
		if(projectData==null){
			return "";
		}
		
		Date date = (Date) projectData.get(IDBConstants.FIELD_PROJECT_PLANFINISH);
		if(date==null){
			return "";
		}
		
		return Util.getDateFormat(Util.SDF_YYYY__MM__DD).format(date);
	}


}
