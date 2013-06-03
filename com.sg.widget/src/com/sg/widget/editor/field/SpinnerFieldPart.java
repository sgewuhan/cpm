package com.sg.widget.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.db.model.ISingleObject;

public class SpinnerFieldPart extends AssistantFieldPart {

	private Spinner control;

	public SpinnerFieldPart(Composite parent, FieldConfiguration cfield, IEditorInput input) {
		super(parent, cfield, input);
	}

	@Override
	protected void createControl(Composite parent) {
		String fieldType = field.getType();

		Assert.isLegal(IFieldTypeConstants.FIELD_INTEGER.equals(fieldType), TYPE_MISMATCH + field.getId());

		control = new Spinner(parent, SWT.BORDER);
		int[] spinnerSetting = field.getSpinnerSetting();
		if (spinnerSetting != null) {
			control.setMinimum(spinnerSetting[0]);
			control.setMaximum(spinnerSetting[1]);
			control.setIncrement(spinnerSetting[2]);
			control.setPageIncrement(spinnerSetting[3]);
		}
		GridData td = getControlLayoutData();
		control.setLayoutData(td);
		control.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDataValue();
			}

		});
		super.createControl(parent);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub
		super.valueChanged(key, oldValue, newValue);

	}

	@Override
	protected void presentValue(ISingleObject data, Object value, String presentValue) {
		if (control.isDisposed())
			return;

		if (value != null)
			control.setSelection((Integer) value);
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		return control.getSelection();
	}

	@Override
	protected void setEditable(boolean editable) {
		if (control.isDisposed())
			return;
		control.setEnabled(editable);
	}

}
