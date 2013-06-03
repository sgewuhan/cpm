package com.sg.widget.editor.field;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.db.model.ISingleObject;

public class DateTimeFieldPart extends AssistantFieldPart {

	private DateTime dateTime;

	public DateTimeFieldPart(Composite parent, FieldConfiguration cfield, IEditorInput input) {
		super(parent, cfield, input);
	}

	@Override
	protected void createControl(Composite parent) {

		
		int style = SWT.BORDER | SWT.MEDIUM;
		if(field.getEditPart().equals(IFieldTypeConstants.TYPE_DATE_B)){
			// 类型检查 并且根据数据类型设置文本的对齐方式
			Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_DATE),
					TYPE_MISMATCH + field.getId());

		}else if(field.getEditPart().equals(IFieldTypeConstants.TYPE_DATE_D)){
			// 类型检查 并且根据数据类型设置文本的对齐方式
			Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_DATE),
					TYPE_MISMATCH + field.getId());
			style = style|SWT.DROP_DOWN;
		}else if(field.getEditPart().equals(IFieldTypeConstants.TYPE_TIME_B)){
			// 类型检查 并且根据数据类型设置文本的对齐方式
			Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_TIME),
					TYPE_MISMATCH + field.getId());

			style = style|SWT.TIME;
		}
		
		dateTime = new DateTime(parent, style);
		
		GridData td = getControlLayoutData();
		dateTime.setLayoutData(td);
		dateTime.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDataValue();
			}

		});
		super.createControl(parent);
	}

	@Override
	public Control getControl() {
		return dateTime;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub
		super.valueChanged(key, oldValue, newValue);

	}

	@Override
	protected void presentValue(ISingleObject data, Object value,
			String presentValue) {
		if(dateTime.isDisposed()){
			return;
		}
		if(value!=null){
			Date date = (Date)value;
			Calendar c =Calendar.getInstance();
			c.setTime(date);
			dateTime.setYear(c.get(Calendar.YEAR));
			dateTime.setMonth(c.get(Calendar.MONTH));
			dateTime.setDay(c.get(Calendar.DATE));
			dateTime.setHours(c.get(Calendar.HOUR_OF_DAY));
			dateTime.setMinutes(c.get(Calendar.MINUTE));
			dateTime.setSeconds(c.get(Calendar.SECOND));
		}else{
		}
	}

	@Override
	protected Date getValueForUpdate(IMessageManager messageManager) {
		Calendar c = Calendar.getInstance();
		c.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(),dateTime.getHours(),dateTime.getMinutes(),dateTime.getSeconds());
		return c.getTime();
	}

	@Override
	protected void setEditable(boolean editable) {
		dateTime.setEnabled(editable);
	}
	

}
