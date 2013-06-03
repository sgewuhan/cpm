package com.sg.widget.editor.field;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.db.model.ISingleObject;
import com.sg.widget.resource.Enumerate;
import com.sg.widget.util.Util;

public class ComboFieldPart extends AssistantFieldPart {

	private Combo control;
	private List<Enumerate> enumerateList;

	public ComboFieldPart(Composite parent, FieldConfiguration fcc, IEditorInput input) {
		super(parent, fcc, input);
	}


	@Override
	protected void createControl(Composite parent) {
		String fieldType = field.getType();
		enumerateList = getOption().getChildren();
		
		//类型检查 并且根据数据类型设置文本的对齐方式
		int style = SWT.BORDER|SWT.DROP_DOWN;
		if((IFieldTypeConstants.FIELD_INTEGER.equals(fieldType)) 
				|| (IFieldTypeConstants.FIELD_DOUBLE.equals(fieldType))
				){
			style = style|SWT.RIGHT;
		}else if(IFieldTypeConstants.FIELD_STRING.equals(fieldType)){
		
		}else{
			Assert.isLegal(false,TYPE_MISMATCH+field.getId());
		}
		
		
		control = new Combo(parent, style);
		int textLimit = field.getTextLimit();
		if(textLimit!=0){
			control.setTextLimit(textLimit);
		}
		
		createControlOption();


		control.setLayoutData(getControlLayoutData());

		control.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDataValueAndPresent();
			}

		});
		control.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent event) {
				updateDataValueAndPresent();
			}

			@Override
			public void focusGained(FocusEvent event) {
				if (isValuePresented()) {
					String editableValue = getValue() == null ? "" : getValue()
							.toString();
					control.setText(editableValue);
				}
			}
		});
		
		//设置字数控制提示
		if ((textLimit != 0) && (field.isEditable()) && (!isValuePresented())
				&& (field.getType().equals(IFieldTypeConstants.FIELD_STRING))) {
			new TextLimitToolTipsControl(this);
		}
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
	protected void controlOptionChanged() {
		createControlOption();
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
	protected void presentValue(ISingleObject data, Object value,
			String presentValue) {
		if(control.isDisposed()){
			return;
		}
		control.setText(presentValue);
	}

	@Override
	protected void setEditable(boolean editable) {
		control.setEnabled(editable);
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		String inputValue = control.getText();
		try {
			messageManager.removeMessage(field.getId(), control);
			return Util.getValue(field.getType(),inputValue);
		} catch (Exception e) {
			messageManager.addMessage(field.getId(), field.getLabel()+WidgetConstants.INVALID_INPUTVALUE, null, IMessageProvider.ERROR, control);
		}
		return null;
	}



}
