package com.sg.widget.viewer.labelprovider;

public class DateWeekColumnLabelProvider extends DateColumnLabelProvider{

	@Override
	protected String getFormat() {
		return "yy/MM/dd E";
	}
	
}
