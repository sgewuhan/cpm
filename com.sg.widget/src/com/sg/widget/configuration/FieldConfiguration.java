package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.sg.widget.Widget;
import com.sg.widget.editor.field.IAddTableItemHandler;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;
import com.sg.widget.editor.field.editable.IEditableHandler;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.editor.field.presentation.IValuePresentation;
import com.sg.widget.editor.field.validator.IInputValidator;
import com.sg.widget.resource.Enumerate;
import com.sg.widget.util.Util;

public class FieldConfiguration extends Configuration {

	private List<FieldActionConfiguration> fieldActions = new ArrayList<FieldActionConfiguration>();
	
	private String id;

	private String name;

	private String type;

	private String editPart;

	private String label;

	private boolean labelVisible;

	private String tooltips;

	private boolean visible;

	private boolean editable;

	private boolean persistable;

	private boolean required;

	private String defaultValue;

	private Enumerate option;

	private int heightHint;

	private int widthHint;

	private IValuePresentation valuePresentation;

	private String format;

	private String spinnerSetting;

	private IInputValidator inputValidator;

	private IEditableHandler editableHandler;

	private IOptionProvider optionProvider;

	private IDefaultValueProvider defaultValueProvider;

	private int textLimit;

	private boolean computeField;

	private String namespace;

	private String textMessage;

	private boolean isPassword;

	private boolean isOpenClean;
	
	private boolean saveHistory;
	
	private String dataSelectorDefinition;

	//tableconfig
	
	private List<ColumnConfiguration> tableColumns = new ArrayList<ColumnConfiguration>();


	private boolean headerVisiable;

	private boolean lineVisiable;

	private boolean markupEnabled;

	private int itemHeight;

	private IAddTableItemHandler addItemHandler;
	
	

	public FieldConfiguration(IConfigurationElement ce) {

		super(ce);
		this.id = ce.getAttribute("id");
		this.name = ce.getAttribute("name");
		this.type = ce.getAttribute("type");
		this.editPart = ce.getAttribute("editPart");
		this.label = ce.getAttribute("label");
		if(label ==null){
			label = "";
		}
		this.labelVisible = !("false".equals(ce.getAttribute("labelVisible")));
		this.tooltips = ce.getAttribute("tooltips");
		if (Util.isNullOrEmptyString(tooltips)) {
			tooltips = label;
		}
		this.visible = !("false".equals(ce.getAttribute("visible")));
		this.editable = !("false".equals(ce.getAttribute("editable")));
		this.persistable = !("false".equals(ce.getAttribute("persistable")));
		this.required = "true".equals(ce.getAttribute("required"));
		this.defaultValue = ce.getAttribute("defaultValue");
		String heightHintString = ce.getAttribute("heightHint");
		try {
			heightHint = Integer.parseInt(heightHintString);
		} catch (Exception e) {
		}
		String widthHintString = ce.getAttribute("widthHint");
		try {
			widthHint = Integer.parseInt(widthHintString);
		} catch (Exception e) {
		}

		String textLimitString = ce.getAttribute("textLimit");
		try {
			textLimit = Integer.parseInt(textLimitString);
		} catch (Exception e) {
		}
		String optionId = ce.getAttribute("option");
		if (optionId != null) {
			this.option = Widget.getDefault().getEnumerate(optionId);
		}
		IConfigurationElement[] actions = ce.getChildren("fieldaction");
		for (int i = 0; i < actions.length; i++) {
			fieldActions.add(new FieldActionConfiguration(actions[i]));
		}


		try {
			valuePresentation = (IValuePresentation) ce.createExecutableExtension("presentation");
		} catch (CoreException e) {
		}

		try {
			inputValidator = (IInputValidator) ce.createExecutableExtension("inputValidator");
		} catch (CoreException e) {
		}
		try {
			optionProvider = (IOptionProvider) ce.createExecutableExtension("optionProvider");
		} catch (CoreException e) {
		}
		try {
			editableHandler = (IEditableHandler) ce.createExecutableExtension("editableHandler");
		} catch (CoreException e) {
		}
		try {
			defaultValueProvider = (IDefaultValueProvider) ce.createExecutableExtension("defaultValueProvider");
		} catch (CoreException e) {
		}
		this.format = ce.getAttribute("presentationFormat");
		this.spinnerSetting = ce.getAttribute("spinnerSetting");
		this.computeField = "true".equals(ce.getAttribute("computeField"));
		this.namespace = ce.getAttribute("nameSpace");
		this.textMessage = ce.getAttribute("textMessage");
		isPassword = "true".equals(ce.getAttribute("isPassword"));
		isOpenClean = "true".equals(ce.getAttribute("openClean"));
		saveHistory = "true".equals(ce.getAttribute("saveHistory"));
		
		//table
		headerVisiable = !"false".equals(ce
				.getAttribute("headerVisiable"));
		lineVisiable = !"false".equals(ce.getAttribute("lineVisiable"));
		
		markupEnabled = "true".equals(ce.getAttribute("markupEnabled"));

		String strItemHeight = ce.getAttribute("customItemHeight");
		
		if(!Util.isNullOrEmptyString(strItemHeight)){
			try{
				itemHeight = Integer.parseInt(strItemHeight);
			}catch(Exception e){
			}
		}
		
		IConfigurationElement[] columns = ce.getChildren("column");
		for (int i = 0; i < columns.length; i++) {
			tableColumns.add(new ColumnConfiguration(columns[i]));
		}
		try {
			addItemHandler = (IAddTableItemHandler) ce.createExecutableExtension("addTableItemHandler");
		} catch (CoreException e) {
		}
		
		dataSelectorDefinition = ce.getAttribute("dataSelectorCollection");
	}

	public List<FieldActionConfiguration> getFieldActions() {

		return fieldActions;
	}

	public String getTextMessage() {

		return textMessage;
	}

	public String getId() {

		return id;
	}

	public String getName() {

		return name;
	}

	public String getType() {

		return type;
	}

	public String getEditPart() {

		return editPart;
	}

	public String getLabel() {

		return label;
	}

	public boolean isLabelVisible() {

		return labelVisible;
	}

	public String getTooltips() {

		return tooltips;
	}

	public boolean isVisible() {

		return visible;
	}

	public boolean isEditable() {

		return editable;
	}

	public boolean isPersistable() {

		return persistable;
	}

	public boolean isRequired() {

		return required;
	}

	public String getDefaultValue() {

		return defaultValue;
	}

	public Enumerate getOption() {

		return option;
	}

	public int getHeightHint() {

		return heightHint;
	}

	public int getWidthHint() {

		return widthHint;
	}

	public IValuePresentation getValuePresentation() {

		return valuePresentation;
	}

	public String getFormat() {

		return format;
	}

	public int[] getSpinnerSetting() {

		if (Util.isNullOrEmptyString(spinnerSetting)) {
			try {
				String[] settings = spinnerSetting.split(",");
				int[] result = new int[4];
				result[0] = Integer.parseInt(settings[0]);
				result[1] = Integer.parseInt(settings[1]);
				result[2] = Integer.parseInt(settings[2]);
				result[3] = Integer.parseInt(settings[3]);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public IInputValidator getInputValidator() {

		return inputValidator;
	}

	public IEditableHandler getEditableHandler() {

		return editableHandler;
	}

	public int getTextLimit() {

		return textLimit;
	}

	public boolean isComputeField() {

		return computeField;
	}

	public IOptionProvider getOptionProvider() {

		return optionProvider;
	}

	public IDefaultValueProvider getDefaultValueProvider() {

		return defaultValueProvider;
	}

	public String getNamespace() {

		return namespace;
	}

	public boolean isPassword() {

		return isPassword;
	}

	public boolean isOpenClean() {

		return isOpenClean;
	}

	
	public boolean isSaveHistory() {
	
		return saveHistory;
	}
	
	//嵌入表格控件使用的方法

	public List<ColumnConfiguration> getColumnsConfigurations() {

		return tableColumns;
	}
	
	public boolean isHeaderVisiable() {
		return headerVisiable;
	}

	public boolean isLineVisiable() {
		return lineVisiable;
	}

	public boolean isMarkupEnabled() {
		return markupEnabled;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public IAddTableItemHandler getAddEventHandler() {
		return addItemHandler;
	}
	
	public String getDataSelectorDefinition(){
		return dataSelectorDefinition;
	}

}
