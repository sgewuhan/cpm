package com.sg.widget.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.util.Util;
import com.sg.db.model.ISingleObject;

public class MultiLineTextFieldPart extends AssistantFieldPart {

	private Text control;

	public MultiLineTextFieldPart(Composite parent, FieldConfiguration cfield,
			IEditorInput input) {
		super(parent, cfield, input);
	}

	@Override
	protected void createControl(Composite parent) {
		// 检查数据类型不匹配的字段
		Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_STRING),
				TYPE_MISMATCH + field.getId());

		if(field.isLabelVisible()||hasAssist()){
			
		Label blank = new Label(parent, SWT.NONE);
		blank.setLayoutData(getControlLayoutData());
		addToolbar(parent);
		}

		control = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		int textLimit = field.getTextLimit();
		if (textLimit != 0) {
			control.setTextLimit(textLimit);
		}

		GridData td = getControlLayoutData();
		td.horizontalSpan = 3;
		td.heightHint = field.getHeightHint() == 0 ? 80 : field.getHeightHint();
		control.setLayoutData(td);
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
		
		//设置编辑提示
		String textMessage = field.getTextMessage();
		if(!Util.isNullOrEmptyString(textMessage)){
			control.setMessage(textMessage);
		}
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
	protected void presentValue(ISingleObject data, Object value,
			String presentValue) {
		if(control.isDisposed()){
			return;
		}
		control.setText(presentValue);
	}

	@Override
	protected void setEditable(boolean editable) {
		control.setEditable(editable);
//		Display display = control.getDisplay();
//		control.setForeground(editable ? display
//				.getSystemColor(SWT.COLOR_BLACK) : display
//				.getSystemColor(SWT.COLOR_GRAY));
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {
		String inputValue = control.getText();
		try {
			return Util.getValue(field.getType(), inputValue);
		} catch (Exception e) {
		}
		return null;
	}

}
