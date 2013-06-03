package com.sg.document.editor.presentation;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public class PartyAPresentation implements IValuePresentation {

	public PartyAPresentation() {
	}

	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		DBObject projectData = (DBObject) data.getValue("project");
		if(projectData==null) return "";
		ObjectId projectId = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);
		List<DBObject> documents = BusinessService.getWorkService().getProjectDocuments(projectId);
		for (DBObject doc : documents) {
			if("com.sg.cpm.editor.JZ-QR-XG003A--1".equals(doc.get(IDBConstants.FIELD_SYSTEM_EDITOR))){
				return (String) doc.get("partya");
			}
		}
		return "";
	}

}
