package com.sg.document.editor.presentation;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class PMNamePresentation implements IValuePresentation {

	public PMNamePresentation() {
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		DBObject projectData = (DBObject) data.getValue("project");
		if(projectData==null) return "";

		DBObject pmData =  (DBObject) projectData.get(IDBConstants.FIELD_PROJECT_PM);
		if(pmData==null) return "";
		return DataUtil.getUserLable2(pmData);
	}

}
