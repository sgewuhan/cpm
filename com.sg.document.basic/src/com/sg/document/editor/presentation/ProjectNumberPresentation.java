package com.sg.document.editor.presentation;

import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class ProjectNumberPresentation implements IValuePresentation {

	public ProjectNumberPresentation() {
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		DBObject projectData = (DBObject) data.getValue("project");
		if(projectData==null) return "";
		return (String) projectData.get(IDBConstants.FIELD_ID);
	}

}
