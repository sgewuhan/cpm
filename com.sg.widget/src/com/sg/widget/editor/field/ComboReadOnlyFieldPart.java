package com.sg.widget.editor.field;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.db.model.ISingleObject;
import com.sg.widget.resource.Enumerate;

public class ComboReadOnlyFieldPart extends AssistantFieldPart {

	private Combo control;
	private List<Enumerate> enumerateList;

	public ComboReadOnlyFieldPart(Composite parent, FieldConfiguration fcc,
			IEditorInput input) {
		super(parent, fcc, input);
	}

	@Override
	protected void createControl(Composite parent) {

		control = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

		createControlOption();


		control.setToolTipText(field.getTooltips());
		control.setLayoutData(getControlLayoutData());

		control.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue(enumerateList.get(control.getSelectionIndex()).getValue());
				updateDataValue();
			}

		});
		super.createControl(parent);
	}

	private void createControlOption() {
		control.removeAll();
		Enumerate enumerateRoot = getOption();
		enumerateList = enumerateRoot.getChildren();
		for (int i = 0; i < enumerateList.size(); i++) {
			control.add(enumerateList.get(i).getLabel());
		}
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		super.valueChanged(key, oldValue, newValue);
	}

	@Override
	protected void controlOptionChanged() {
		createControlOption();
	}
	
	@Override
	protected void presentValue(ISingleObject data, Object value,
			String presentValue) {
		if(control.isDisposed()){
			return;
		}
		for (int i = 0; i < enumerateList.size(); i++) {
			if (enumerateList.get(i).getValue().equals(value)) {
				control.select(i);
			}
		}
	}

	@Override
	protected void setEditable(boolean editable) {
		if(control.isDisposed()){
			return;
		}
		control.setEnabled(editable);
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		return getValue();
	}

}
