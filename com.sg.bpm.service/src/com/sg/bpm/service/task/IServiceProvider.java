package com.sg.bpm.service.task;

import java.util.Map;


public interface IServiceProvider {

	Map<String, Object> run(Object parameter);

	void setParameters(Map<String, Object> parameters);

	void setOperation(String operation);

}
