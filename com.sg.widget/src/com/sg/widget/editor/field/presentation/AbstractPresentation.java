package com.sg.widget.editor.field.presentation;

import com.sg.db.model.ISingleObject;

public abstract class AbstractPresentation implements IValuePresentation {

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value, String format) {
		String pv = (String)data.getValue(getValueFieldName(key));
		if(pv==null){
			return "";
		}
		return pv;
	}

	protected abstract String getValueFieldName(String key) ;

}
