package com.sg.common.workflow;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.sg.common.workflow.parameter.IProcessParameterDelegator;

public class ProcessParameter {

	private IConfigurationElement cce;

	public ProcessParameter(IConfigurationElement cce) {

		this.cce = cce;
	}

	public String getprocessParameterName() {

		return cce.getAttribute("processParameterName");
	}

	public String getTaskFormName() {

		return cce.getAttribute("taskFormFieldName");
	}

	public IProcessParameterDelegator getProcessParameterDelegator() {

		try {
			return (IProcessParameterDelegator) cce.createExecutableExtension("processParameterDelegator");
		} catch (CoreException e) {
		}
		return null;

	}
}
