package com.sg.widget.viewer.labelprovider;

public class GenericColumnLabelProvider extends ViewerColumnLabelProvider {

	@Override
	public String getText(Object element) {
		Object value = getValue(element);
		return value==null?"":value.toString();
	}

}
