package com.sg.widget.viewer.labelprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.mongodb.DBObject;
import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.util.Util;

public class DBObjectColumnLabelProvider extends ColumnLabelProvider {

	private String key;

	public DBObjectColumnLabelProvider(String key) {

		this.key = key;
	}

	protected Object getValue(Object element) {

		DBObject row = (DBObject) element;
		return row.get(key);
	}

	private boolean hasKey(Object element) {

		DBObject row = (DBObject) element;
		return row.containsField(key);
	}

	@Override
	public String getText(Object element) {

		Object data = getValue(element);
		if (data == null) {
			return "";
		}
		if (element instanceof Boolean) {
			return "";
		}
		return Util.getText(data);
	}

	@Override
	public Image getImage(Object element) {

		if (hasKey(element)) {
			Object data = getValue(element);
			if (Boolean.TRUE.equals(data)) {
				return Widget.getImage(IWidgetImage.IMG_CHECKED);
			} else if (Boolean.FALSE.equals(data)) {
				return Widget.getImage(IWidgetImage.IMG_UNCHECKED);

			}
		}
		return super.getImage(element);
	}

}
