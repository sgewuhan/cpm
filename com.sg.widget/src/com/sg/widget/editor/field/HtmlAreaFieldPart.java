package com.sg.widget.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.db.model.ISingleObject;
import com.sg.widget.component.ckeditor.CKEditor;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.util.Util;

public class HtmlAreaFieldPart extends AssistantFieldPart {

	private CKEditor control;

	public HtmlAreaFieldPart(Composite parent, FieldConfiguration cfield,
			IEditorInput input) {
		super(parent, cfield, input);
	}

	@Override
	protected void createControl(Composite parent) {
		// 检查数据类型不匹配的字段
		Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_STRING),
				TYPE_MISMATCH + field.getId());

		Label blank = new Label(parent, SWT.NONE);
		blank.setLayoutData(getControlLayoutData());
		addToolbar(parent);

		control = new CKEditor(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);

		GridData td = getControlLayoutData();
		td.horizontalSpan = 3;
		td.heightHint = field.getHeightHint() == 0 ? 80 : field.getHeightHint();
		control.setLayoutData(td);
	}

	@Override
	public Control getControl() {
		return control;
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
