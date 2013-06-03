package com.sg.widget.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.db.model.ISingleObject;
import com.sg.db.model.IValueChangeListener;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.editor.IDirtyControl;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;
import com.sg.widget.editor.field.editable.IEditableHandler;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.editor.field.presentation.IValuePresentation;
import com.sg.widget.editor.field.validator.IInputValidator;
import com.sg.widget.resource.Enumerate;

public abstract class AbstractFieldPart implements IValueChangeListener, IDirtyControl, IFormPart {

	protected int controlSpace = 3;

	protected FieldConfiguration field;

	private ISingleObjectEditorInput input;

	private ISingleObject data;

	protected String key;

	protected static final String TYPE_MISMATCH = "Field type is not matched field editor, Please check this field type and editorType setting  and input data. Field Id:";

	private Object originalValue;// ԭʼ�������͵��ֶ�ֵ

	private Object value; // ԭʼ�������͵��ֶ�ֵ

	protected String presentValue;// ��ʾֵ

	private IValuePresentation pres;

	private IMessageManager messageManager;

	private IEditableHandler editableHandler;

	private Enumerate option;

	private boolean dynamicOption;

	private Enumerate selectedOption;

	private IManagedForm form;

	private Composite parent;

	public AbstractFieldPart(Composite parent, FieldConfiguration cfield, IEditorInput input) {

		field = cfield;
		this.parent = parent;
		this.input = (ISingleObjectEditorInput) input;
	}

	@Override
	public void initialize(IManagedForm form) {

		this.form = form;
		key = field.getName();
		editableHandler = field.getEditableHandler();
		data = this.input.getInputData();
		IOptionProvider op = field.getOptionProvider();
		dynamicOption = (op != null);
		caculteOption();
		createContent(parent);
	}

	public abstract Control getControl();

	protected void createContent(Composite parent) {

		// �����Ĵ����ؼ�����
		if (field.isLabelVisible() && createLabel()) {
			Label label = new Label(parent, SWT.NONE);

			String labelTitle = field.getLabel();
//			Assert.isNotNull(labelTitle,"��ǩ����Ϊ��");
			if (isEditable()&&field.isRequired()) {
				// label.setForeground(parent.getDisplay().getSystemColor(
				// SWT.COLOR_BLUE));
				labelTitle = "*" + labelTitle;
			}
			label.setText(labelTitle);
			String tips = field.getTooltips();
			if (tips != null) {
				label.setToolTipText(tips);
			}
			label.setLayoutData(getLabelLayoutData());
		}
		// �����ؼ�����
		createControl(parent);

		// ���ó�ʼֵ
		Object inputValue;
		if (field.isOpenClean()) {
			inputValue = getDefaultValue();
		}else{
			if (input.isNewObject()) {
				// ����Ĭ��ֵ
				inputValue = getDefaultValue();
			} else {
				if(data.hasKey(key)){
					inputValue = getInputValue();
				}else{
					inputValue = getDefaultValue();
				}
			}
		}


		if (inputValue != null) {// Ϊnull��ʱ�������û������ֶλ������ݿ���ȡ�����ǿ�
			setOriginalValue(inputValue);
			setValue(inputValue);

			// update value changed
			// updateDataValue();
			data.setValue(key, value, this, true);
			form.dirtyStateChanged();

		}

		// ���������ʾ
		presentValue();

		// ����ֻ��
		setFieldEditable();

		// ����������������data�ĸı�
		data.addValueListener(this);

	}

	protected boolean createLabel() {

		return true;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {

		if (this.key.equals(key)) {
			this.value = newValue;
		}
		editableStateChange();
		if (dynamicOption) {
			reCaculteOption();
		}
	}

	private void editableStateChange() {

		setFieldEditable();
	}

	private void setFieldEditable() {

		if (!input.isEditable()) {
			setEditable(false);
		} else {
			if (editableHandler != null) {
				boolean editable = editableHandler.isEditable(data, key, value);
				setEditable(editable);
			} else {
				setEditable(field.isEditable());
			}
		}
	}

	protected abstract void setEditable(boolean editable);

	/**
	 * ������Ը����������ı���
	 */
	protected void presentValue() {

		pres = field.getValuePresentation();
		if (pres != null) {
			presentValue = pres.getPresentValue(key, data, value, field.getFormat());
		} else if (selectedOption != null) {
			presentValue = selectedOption.getLabel();
		} else {
			presentValue = value == null ? "" : value.toString();
		}
		presentValue(data, value, presentValue);
	}

	public boolean isValuePresented() {

		return pres != null;
	}

	protected abstract void presentValue(ISingleObject data, Object value, String presValue);

	protected ISingleObjectEditorInput getInput() {

		return input;
	}

	protected abstract void createControl(Composite parent);

	/**
	 * ȡ����ǰ��ֵ
	 * 
	 * @return
	 */
	protected Object getInputValue() {

		Object dataValue = data.getValue(key);
		return dataValue;
	}

	/**
	 * ȡ��ȱʡֵ
	 * 
	 * @return
	 */
	private Object getDefaultValue() {

		IDefaultValueProvider dp = field.getDefaultValueProvider();
		Object defualtValue = null;
		if (dp != null) {
			defualtValue = dp.getDefaultValue(data, key);
		} else {
			String defaultValueString = field.getDefaultValue();
			try {
				defualtValue = com.sg.widget.util.Util.getValue(field.getType(), defaultValueString);
			} catch (Exception e) {
			}
		}
		return defualtValue;
	}

	GridData getControlLayoutData() {

		GridData td = new GridData(SWT.FILL, SWT.CENTER, true, false, controlSpace, 1);
		td.widthHint = field.getWidthHint() == 0 ? 80 : field.getWidthHint();
		return td;
	}

	public FieldConfiguration getField() {

		return field;
	}

	GridData getLabelLayoutData() {

		GridData td = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		controlSpace--;
		return td;
	}

	public Shell getShell() {

		return getControl().getShell();
	}

	public IManagedForm getForm() {

		return form;
	}

	public Object getValue() {

		return value;
	}

	/**
	 * �ı䵱ǰ�ؼ���ֵ��������д���󶨵�data
	 * 
	 * @param value
	 */
	public void setValue(Object value) {

		this.value = value;
	}

	/**
	 * �ؼ���ֵ����������ģ���д���󶨵�data
	 * 
	 * @param text
	 */
	public void updateDataValueAndPresent() {

		updateDataValue();
		presentValue();
	}

	/**
	 * �ؼ���ֵ����������ģ���д���󶨵�data
	 * 
	 * @param text
	 */
	public void updateDataValue() {

		Object valueForUpdate = getValueForUpdate(messageManager);
		validate(valueForUpdate);
		value = valueForUpdate;
		data.setValue(key, valueForUpdate, this, true);
		form.dirtyStateChanged();
	}

	private boolean validate(Object valueForUpdate) {

		// ����������
		if (field.isRequired()) {
			if (com.sg.widget.util.Util.isNullOrEmptyString(valueForUpdate)) {
				messageManager.addMessage(field.getId(), WidgetConstants.MESSAGE_NOT_NULL, null, IMessageProvider.ERROR, getControl());
				return false;
			}
		}

		// �����������

		IInputValidator v = field.getInputValidator();
		if (v != null) {
			return v.validate(data, field, valueForUpdate, messageManager, getControl());
		}
		return true;
	}

	public boolean checkValidOnSave() {

		return validate(value);
	}

	/**
	 * �ڸ���value֮ǰ��updateValue(Object input)���ã�����
	 * 
	 * @param messageManager
	 * 
	 * @param inputValue
	 * @return ��������ִ�и��µ�ֵ
	 */
	protected abstract Object getValueForUpdate(IMessageManager messageManager);

	private void setOriginalValue(Object value) {

		Assert.isNotNull(value);
		this.originalValue = value;
	}

	public boolean isDirty() {

		return !Util.equals(originalValue, value);
	}

	public void setMessageManager(IMessageManager messageManager) {

		this.messageManager = messageManager;
	}

	private void caculteOption() {

		IOptionProvider op = field.getOptionProvider();
		if (op == null) {
			option = field.getOption();
		} else {
			option = op.getOption(input, data, key, value);
		}
	}

	private void reCaculteOption() {

		if (dynamicOption) {
			IOptionProvider op = field.getOptionProvider();
			Enumerate newOption = op.getOption(input, data, key, value);
			if (!Util.equals(newOption, option)) {
				option = newOption;
				controlOptionChanged();
			}
		}
	}

	protected void controlOptionChanged() {

	}

	public Enumerate getOption() {

		return option;
	}

	public void setValueFromOption(Enumerate option) {

		if (option != null) {
			setValue(option.getValue());
			selectedOption = option;
		}
	}

	@Override
	public void dispose() {

		// ����������
		data.removeValueListener(this);
	}

	@Override
	public void commit(boolean onSave) {

		if (onSave)
			this.originalValue = value;
	}

	@Override
	public boolean setFormInput(Object input) {

		return false;
	}

	@Override
	public void setFocus() {

	}

	@Override
	public boolean isStale() {

		return false;
	}

	@Override
	public void refresh() {

	}
	
	protected boolean isEditable(){
		return input.isEditable();
	}

}
