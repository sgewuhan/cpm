package com.sg.common.workflow;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;

public class TaskFormConfig {

	private String processDefinitionId;

	private String taskName;

	private String editorId;

	private List<ProcessParameter> processParameters = new ArrayList<ProcessParameter>();

	private IConfigurationElement ic;

	public TaskFormConfig(IConfigurationElement ic) {

		this.ic = ic;

		processDefinitionId = ic.getAttribute("processDefinitionId");
		taskName = ic.getAttribute("name");
		editorId = ic.getAttribute("editorId");

		IConfigurationElement[] parameters = ic.getChildren("processParameter");
		for (int i = 0; i < parameters.length; i++) {
			IConfigurationElement cce = parameters[i];
			ProcessParameter pp = new ProcessParameter(cce);
			processParameters.add(pp);
		}
	}

	public String getTaskFormId() {

		return processDefinitionId + "@" + taskName;
	}

	public String getEditorId() {

		return editorId;
	}

	public ISingleObject getTaskFormInput(DBObject taskFormData) {

		try {
			ITaskFormInputHandler inputDelegator = (ITaskFormInputHandler) ic.createExecutableExtension("inputHandler");
			if (inputDelegator != null) {
				return inputDelegator.getTaskFormInputData(taskFormData, this);
			}
		} catch (CoreException e) {
		}
		return new SingleObject().setData(taskFormData);
	}

	public ISingleObjectEditorDialogCallback getTaskFormDialogHandler(DBObject taskFormData) {
		try {
			ISingleObjectEditorDialogCallback callback = (ISingleObjectEditorDialogCallback) ic.createExecutableExtension("saveHandler");
			return callback;
		} catch (CoreException e) {
		}
		return null;
	}
	
	public IValidationHandler getValidationHandler(){
		try {
			IValidationHandler handler = (IValidationHandler) ic.createExecutableExtension("validation");
			return handler;
		} catch (CoreException e) {
		}
		return null;
	}

	public List<ProcessParameter> getProcessParameters() {

		return processParameters;
	}

	public String[] getPersistentFields() {

		String persistentFields = ic.getAttribute("persistentFields");
		return persistentFields==null?new String[]{}:persistentFields.split(",");
	}
	
	public IWorkflowInfoProvider getWorkflowInformationProvider(){
		try {
			IWorkflowInfoProvider handler = (IWorkflowInfoProvider) ic.createExecutableExtension("workflowInfo");
			return handler;
		} catch (CoreException e) {
		}
		return null;
		
	}

	public boolean isStartForm() {

		return "start".equalsIgnoreCase(ic.getAttribute("eventType"));
	}

	public boolean isCompleteForm() {

		return "complete".equalsIgnoreCase(ic.getAttribute("eventType"));
	}

}
