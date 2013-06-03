package com.sg.bpm.service.task;

import java.util.Map;

public abstract class ServiceProvider implements IServiceProvider {

	private Map<String, Object> parameters;
	private String operation;

	@Override
	public void setParameters(Map<String, Object> parameters) {

		this.parameters = parameters;
	}

	public Object getInputValue(String parameter) {

		if (parameters != null)
			return parameters.get(parameter);
		return null;
	}

	@Override
	public void setOperation(String operation) {

		this.operation = operation;
	}
	
	protected String getOperation(){
		return operation;
	}
	

}
