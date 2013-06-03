package com.sg.bpm.service.task;

import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class CommonServiceTaskHandler implements WorkItemHandler {

	public IServiceProvider getServiceProvider(String id, String operation) {

		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint("com.sg.bpm.service",
				"serviceTask");
		if (ePnt == null)
			return null;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("service".equals(confs[j].getName())) {
					String confId = confs[j].getAttribute("interface");
					String confOperation = confs[j].getAttribute("operation");
					if (confId.equals(id) && confOperation.equals(operation)) {
						try {
							return (IServiceProvider) confs[j]
									.createExecutableExtension("serviceProvider");
						} catch (CoreException e) {
							return null;
						}
					}
				}
			}
		}

		return null;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String id = (String) workItem.getParameter("Interface");
		String operation = (String) workItem.getParameter("Operation");
		IServiceProvider isp = getServiceProvider(id, operation);
		Map<String, Object> result = null;

		if (isp != null) {
			//2013/1/10 解决有可能出错的服务造成的不一致问题
			try {
				Object parameter = workItem.getParameter("Parameter");
				isp.setParameters(workItem.getParameters());
				isp.setOperation(operation);
				result = isp.run(parameter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		manager.completeWorkItem(workItem.getId(), result);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

		// Do nothing, cannot be aborted
	}

}
