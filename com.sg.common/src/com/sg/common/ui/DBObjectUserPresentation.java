package com.sg.common.ui;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class DBObjectUserPresentation extends ViewerColumnLabelProvider implements IValuePresentation {

	public DBObjectUserPresentation() {
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value, String format) {
		return getLable(value);
	}

	@Override
	public String getText(Object element) {
		Object value = getValue(element);
		return getLable(value);

	}
	
	public String getLable(Object value){
		if(value instanceof DBObject){
			return DataUtil.getUserLable(((DBObject)value));
		}
		return "";
	}
	
}
