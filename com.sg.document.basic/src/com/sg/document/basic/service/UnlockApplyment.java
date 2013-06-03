package com.sg.document.basic.service;

import java.util.Map;

import org.bson.types.ObjectId;

import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;


public class UnlockApplyment extends ServiceProvider {
	
	private static final String EDITOR_ID = "com.sg.cpm.editor.JZ-QR-XG003A";// 项目立项申请表

	@Override
	public Map<String, Object> run(Object parameter) {

		// 传入的parameter是workid
		BusinessService.getWorkService().unlockDocumentsOfWork(new ObjectId((String) parameter),EDITOR_ID);
		return null;
	}


}
