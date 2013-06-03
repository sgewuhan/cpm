package com.sg.widget.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.util.Util;
import com.sg.db.model.ISingleObject;

public class TextFieldPart extends AssistantFieldPart {

	private Text control;
	private boolean isComputeField;
	private boolean isEditable = true;

	public TextFieldPart(Composite parent, FieldConfiguration cfield, IEditorInput input) {
		super(parent, cfield, input);
	}

	@Override
	protected void createControl(Composite parent) {
		isComputeField = field.isComputeField();
		String fieldType = field.getType();

		// ���ͼ�� ���Ҹ����������������ı��Ķ��뷽ʽ
		int style ;
		if(isComputeField){
			style = SWT.NONE;
		}else{
			style = SWT.BORDER;
		}
		
		if(field.isPassword()){
			style = style|SWT.PASSWORD;
		}
		
		if ((IFieldTypeConstants.FIELD_INTEGER.equals(fieldType))
				|| (IFieldTypeConstants.FIELD_DOUBLE.equals(fieldType))
				) {
			style = style | SWT.RIGHT;
		} else if (IFieldTypeConstants.FIELD_STRING.equals(fieldType)) {

		} else {
			Assert.isLegal(false, TYPE_MISMATCH + field.getId());
		}

		control = new Text(parent, style);
		GridData td = getControlLayoutData();
		control.setLayoutData(td);
		
		if (!isComputeField) {
			//������������
			int textLimit = field.getTextLimit();
			if (textLimit != 0) {
				control.setTextLimit(textLimit);
			}
			// �����������ֵ
			control.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent event) {
					//���ֻ���������κα仯
					if(isEditable){
						updateDataValueAndPresent();
					}
					
				}

				@Override
				public void focusGained(FocusEvent event) {
					if (isEditable&&isValuePresented()) {
						String editableValue = getValue() == null ? ""
								: getValue().toString();
						control.setText(editableValue);
					}
				}
			});

			// ��������������ʾ
			if ((textLimit != 0) && (field.isEditable())
					&& (!isValuePresented())
					&& (field.getType().equals(IFieldTypeConstants.FIELD_STRING))) {
				new TextLimitToolTipsControl(this);
			}
			
			//���ñ༭��ʾ
			String textMessage = field.getTextMessage();
			if(!Util.isNullOrEmptyString(textMessage)){
				control.setMessage(textMessage);
			}
			
		}
		super.createControl(parent);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {
		super.valueChanged(key, oldValue, newValue);
//		if(isComputeField){//����Ǳ��ʽ�ֶΣ���Ҫ���¼���
			presentValue();
//		}
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
	protected Object getValueForUpdate(IMessageManager messageManager) {
		String inputValue = control.getText();
		try {
			messageManager.removeMessage(field.getId(), control);
			return Util.getValue(field.getType(), inputValue);
		} catch (Exception e) {
			messageManager.addMessage(field.getId(), field.getLabel()
					+ WidgetConstants.INVALID_INPUTVALUE, null,
					IMessageProvider.ERROR, control);
		}
		return null;
	}

	@Override
	protected void setEditable(boolean editable) {
		if(control.isDisposed()||control==null){
			return;
		}
		if (isComputeField) {
			control.setEditable(false);
		} else {
			control.setEditable(editable);
//			Display display = control.getDisplay();
//			control.setForeground(editable ? display
//					.getSystemColor(SWT.COLOR_BLACK) : display
//					.getSystemColor(SWT.COLOR_GRAY));
		}
		this.isEditable = editable;
	}

}
