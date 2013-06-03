package com.sg.document.tmt.projectreport.editor.defaultvalue;

import java.util.Calendar;

import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;

public class DefaultYear implements IDefaultValueProvider {

	public DefaultYear() {
	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		return ""+year;
	}
	

}
