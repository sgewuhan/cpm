package com.sg.document.tmt.projectreport.editor.defaultvalue;

import java.util.Calendar;

import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;

public class DefaultMonth implements IDefaultValueProvider {

	public DefaultMonth() {
	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;
		return ""+month;
	}

}
