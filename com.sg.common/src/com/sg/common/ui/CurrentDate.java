package com.sg.common.ui;

import java.util.Date;

import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;


public class CurrentDate implements IDefaultValueProvider {

	public CurrentDate() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {

		return new Date();
	}

}
