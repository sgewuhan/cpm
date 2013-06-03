package com.sg.document.basic.service;

import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.bpm.service.task.ServiceProvider;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;

public class CreateDocument extends ServiceProvider {

	public CreateDocument() {
	}

	@Override
	public Map<String, Object> run(Object parameter) {
		String _workId = (String) getInputValue("workId");
		ObjectId workId = new ObjectId(_workId);

		String editorId = getOperation();
		
		
		DBObject work = BusinessService.getWorkService().getWorkObject(workId);

		BasicDBObject createInfo = new BasicDBObject();
		// 创建者 创建时间
		createInfo.put(IDBConstants.FIELD_CREATER, work.get(IDBConstants.FIELD_CREATER));
		// 因为创建者的名字字段不显示
		createInfo.put(IDBConstants.FIELD_CREATER_NAME, work.get(IDBConstants.FIELD_CREATER_NAME));

		createInfo.put(IDBConstants.FIELD_OWNER, work.get(IDBConstants.FIELD_OWNER));

		createInfo.put(IDBConstants.FIELD_OWNER_NAME, work.get(IDBConstants.FIELD_OWNER_NAME));

		createInfo.put(IDBConstants.FIELD_CREATE_DATE, new Date());
		
		BusinessService.getDocumentService().createDocumentFromEditorId(workId, editorId,createInfo);
		
		return null;
	}

}
