package com.sg.widget.viewer.labelprovider;



public class TimeColumnLabelProvider extends DateColumnLabelProvider{

	@Override
	protected String getFormat() {
		return "HH:mm:ss";
	}
}
