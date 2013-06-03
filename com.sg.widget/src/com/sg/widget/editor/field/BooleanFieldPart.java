package com.sg.widget.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.util.Util;
import com.sg.db.model.ISingleObject;

public class BooleanFieldPart extends AbstractFieldPart {

	private Button button;

	public BooleanFieldPart(Composite parent, FieldConfiguration fcc,IEditorInput input) {
		super(parent, fcc,input);
	}

	@Override
	protected boolean createLabel() {
		return false;
	}

	@Override
	protected void createControl(Composite parent) {
		//检查数据类型不匹配的字段
		Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_BOOLEAN),TYPE_MISMATCH+field.getId());
		
		button = new Button(parent, SWT.CHECK);
		button.setText(field.getLabel());
		GridData controlLayoutData = getControlLayoutData();
		button.setLayoutData(controlLayoutData);
		button.setSelection(Boolean.TRUE.equals(getValue()));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDataValue();
			}
			
		});
	}

	@Override
	public Control getControl() {
		return button;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		super.valueChanged(key, oldValue, newValue);
	}

	@Override
	protected void presentValue(ISingleObject data, Object value,String presentValue) {
		if(button.isDisposed()){
			return;
		}
		button.setSelection(Boolean.TRUE.equals(value));
	}

	@Override
	protected void setEditable(boolean editable) {
		button.setEnabled(editable);
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		boolean inputValue = button.getSelection();
		try {
			return Util.getValue(field.getType(),inputValue);
		} catch (Exception e) {
		}
		return null;
	}


}
