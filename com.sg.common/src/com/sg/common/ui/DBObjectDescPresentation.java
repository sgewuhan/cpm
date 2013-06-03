package com.sg.common.ui;

import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class DBObjectDescPresentation implements IValuePresentation {

	public DBObjectDescPresentation() {
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value, String format) {
		if(value instanceof DBObject){
			return (String) ((DBObject)value).get("desc");
		}
		return "";
	}

}
