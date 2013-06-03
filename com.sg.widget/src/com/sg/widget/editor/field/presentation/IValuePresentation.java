package com.sg.widget.editor.field.presentation;

import com.sg.db.model.ISingleObject;

public interface IValuePresentation {

	public String getPresentValue(String key, ISingleObject data, Object value,String format);

}
