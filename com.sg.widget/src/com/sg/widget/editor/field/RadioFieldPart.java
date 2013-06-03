package com.sg.widget.editor.field;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.db.model.ISingleObject;
import com.sg.widget.resource.Enumerate;

public class RadioFieldPart extends AbstractFieldPart {

	private Button[] buttons;
	private Composite group;
	private List<Enumerate> enumerateList;

	public RadioFieldPart(Composite parent, FieldConfiguration field, IEditorInput input) {
		super(parent, field, input);
	}

	@Override
	protected void createControl(Composite parent) {
		enumerateList = getOption().getChildren();

		group = new Composite(parent, SWT.NONE);

		GridData td = new GridData(SWT.FILL, SWT.TOP, true, false,
				controlSpace, 1);
		td.widthHint = field.getWidthHint() == 0 ? 120 : field.getWidthHint();
		group.setLayoutData(td);

		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;

		rowLayout.pack = true;
		rowLayout.justify = false;
		rowLayout.marginBottom = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;

		group.setLayout(rowLayout);

		buttons = new Button[enumerateList.size()];
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button(group, SWT.RADIO);
			final Enumerate enumerate = enumerateList.get(i);
			buttons[i].setText(enumerate.getLabel());
			buttons[i].setData(enumerate.getValue());
			buttons[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setValue(enumerate.getValue());
					updateDataValue();
				}

			});
		}
	}

	@Override
	public Control getControl() {
		return group;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub
		super.valueChanged(key, oldValue, newValue);

	}

	@Override
	protected void presentValue(ISingleObject data, Object value,
			String presentValue) {
		for (int i = 0; i < enumerateList.size(); i++) {
			if(buttons[i].isDisposed()){
				return;
			}
			buttons[i].setSelection(buttons[i].getData().equals(value));
		}

	}

	@Override
	protected void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setText(enumerateList.get(i).getLabel());
			buttons[i].setData(enumerateList.get(i).getValue());
			buttons[i].setEnabled(editable);
		}
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		return getValue();
	}


}
