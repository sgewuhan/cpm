package com.sg.widget.viewer.labelprovider;


import java.text.DecimalFormat;

import org.eclipse.core.runtime.Assert;

import com.sg.widget.configuration.ColumnConfiguration;
import com.sg.widget.editor.field.IFieldTypeConstants;
import com.sg.widget.util.Util;



public class NumberColumnLabelProvider extends ViewerColumnLabelProvider{
	@Override
	public String getText(Object element) {
		Object data = getValue(element);
		if(data==null){
			return null;
		}
		
		ColumnConfiguration conf = getColumnConfiguration();
		String type = conf.getType();
		
		String format = getFormat();
		DecimalFormat moneyFormat ;
		if(format == null){
			moneyFormat = Util.getDecimalFormat(Util.NUMBER_P2);
		}else{
			moneyFormat = Util.getDecimalFormat(format);
		}
		if(IFieldTypeConstants.FIELD_DOUBLE.equals(type)){
			return moneyFormat.format((Double) data);
		}else if(IFieldTypeConstants.FIELD_INTEGER.equals(conf.getType())){
			return moneyFormat.format((Integer) data);
		}else{
			Assert.isLegal(false, "NumberColumnLabelProvider只能适用于Double和Integer类型的字段");
		}
		return "";
	}

	protected String getFormat() {
		return null;
	}

}
