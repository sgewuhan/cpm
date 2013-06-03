package com.sg.widget.editor.field;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.mongodb.DBObject;
import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.configuration.ColumnConfiguration;
import com.sg.widget.editor.field.IFieldTypeConstants;
import com.sg.widget.util.Util;

public class TableFieldColumnLabelProvider extends ColumnLabelProvider {

	private ColumnConfiguration cc;

	public TableFieldColumnLabelProvider(ColumnConfiguration cc) {
		this.cc = cc;
	}

	public TableFieldColumnLabelProvider() {
	}

	public void setColumnConfigruation(ColumnConfiguration cc) {
		this.cc = cc;
	}

	protected ColumnConfiguration getColumnConfiguration() {
		return cc;
	}

	protected Object getValue(Object element) {
		DBObject row = (DBObject) element;
		return row.get(cc.getColumn());
	}

	private boolean hasKey(Object element) {
		DBObject row = (DBObject) element;
		return row.containsField(cc.getColumn());
	}

	@Override
	public String getText(Object element) {
		Object data = getValue(element);
		if (data == null) {
			return "";
		}

		if (IFieldTypeConstants.FIELD_BOOLEAN.equals(cc.getType())) {
			return "";
		}

		try {
			return Util.getText(cc.getType(), data);
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public Image getImage(Object element) {
		if (hasKey(element)) {
			if (IFieldTypeConstants.FIELD_BOOLEAN.equals(cc.getType())) {
				Object data = getValue(element);
				if (Boolean.TRUE.equals(data)) {
					return Widget.getImage(IWidgetImage.IMG_CHECKED);
				} else {
					return Widget.getImage(IWidgetImage.IMG_UNCHECKED);

				}
			}
		}
		return super.getImage(element);
	}

}
