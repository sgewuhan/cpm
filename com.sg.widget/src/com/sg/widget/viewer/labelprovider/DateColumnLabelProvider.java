package com.sg.widget.viewer.labelprovider;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Assert;

import com.sg.widget.configuration.ColumnConfiguration;
import com.sg.widget.editor.field.IFieldTypeConstants;
import com.sg.widget.util.Util;




public class DateColumnLabelProvider extends ViewerColumnLabelProvider{

	@Override
	public String getText(Object element) {
		Object data = getValue(element);
		if(data==null){
			return null;
		}
		
		ColumnConfiguration conf = getColumnConfiguration();
		String type = conf.getType();
		
		String format = getFormat();
		SimpleDateFormat  dateFormat ;
		if(format == null){
			dateFormat = Util.getDateFormat(Util.SDF_YY_MM_DD);
		}else{
			dateFormat = new SimpleDateFormat(format);
		}
		if(IFieldTypeConstants.FIELD_DATE.equals(type)||IFieldTypeConstants.FIELD_TIME.equals(type)){
			return dateFormat.format((Date) data);
		}else{
			Assert.isLegal(false, "DateColumnLabelProvider只能适用于Date或者Time类型的字段");
		}
		return "";
	}

	protected String getFormat() {
		return null;
	}

	
}
