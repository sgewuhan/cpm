package com.sg.widget.viewer.labelprovider;



public class DateTimeColumnLabelProvider extends DateColumnLabelProvider{

	@Override
	protected String getFormat() {
		return "yy/MM/dd E HH:mm:ss";
	}
	
	
}
