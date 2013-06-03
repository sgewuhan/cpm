package com.sg.widget.editor.field.presentation;

import com.sg.widget.util.Util;
import com.sg.db.model.ISingleObject;

public class NumberPresentation implements IValuePresentation {

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		if (value == null) {
			return "";
		}
		try {
			if (value instanceof String) {
				double num = Double.parseDouble((String) value);
				return Util.getDecimalFormat(format).format(num);
			} else if (value instanceof Short) {
				return Util.getDecimalFormat(format).format(value);
			} else if (value instanceof Long) {
				return Util.getDecimalFormat(format).format(value);
			} else if (value instanceof Double) {
				return Util.getDecimalFormat(format).format(value);
			} else if (value instanceof Float) {
				return Util.getDecimalFormat(format).format(value);
			}
		} catch (Exception e) {
		}
		return value.toString();

	}
}
