package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.IServiceProvider;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;

public class LockDocument extends ServiceProvider implements IServiceProvider {

	public LockDocument() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String editorId = getOperation();
		if("all".equalsIgnoreCase(editorId)){
			BusinessService.getWorkService().lockDocumentsOfWork(new ObjectId((String) parameter), null,true);
		}else{
			BusinessService.getWorkService().lockDocumentsOfWork(new ObjectId((String) parameter), editorId,true);
		}
		return null;
	}

}
