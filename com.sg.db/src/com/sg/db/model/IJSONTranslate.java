package com.sg.db.model;

import java.util.Map;
import java.util.Set;

public interface IJSONTranslate {
	
	public String getJSONResult(Map<String, String> transferFields, Set<String> removeFields);
	
	public Object getBSONResult(Map<String, String> transferFields, Set<String> removeFields);
}
