package com.sg.document.tmt.projectreport.editor.presentation;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.presentation.IValuePresentation;

public abstract class AbstractProjectDSDPresentation implements IValuePresentation {
	
	protected ObjectId projectId = null;
	protected String pValue = "";
	
	@Override
	public String getPresentValue(String key, ISingleObject data, Object value,
			String format) {
		DBObject editorData = data.getData();
		
		ObjectId newProjectId = getProjectId(editorData);
		if(org.eclipse.jface.util.Util.equals(projectId, newProjectId)){
			return pValue;
		}else{
			projectId = newProjectId;
		}
		
		pValue = getValue(editorData,projectId,format);
		return pValue;
	}
	
	
	protected abstract String getValue(DBObject editorData, ObjectId projectId, String format);


	protected ObjectId getProjectId(DBObject editorData){
		DBObject projectData = (DBObject) editorData.get("project");
		if (projectData == null)
			return null;

		return (ObjectId) projectData
				.get(IDBConstants.FIELD_SYSID);
		
	}

	protected DBObject getDocument(ObjectId projectId) {
		
		WorkService workService = BusinessService.getWorkService();

		List<DBObject> docs = workService.getProjectDocuments(projectId,
				"com.sg.cpm.editor.JZ-QR-XG004A");
		if (docs.isEmpty()) {
			docs = workService.getProjectDocuments(projectId,
					"com.sg.cpm.editor.JZ-QR-XG003A--1");
		}
		if (docs.isEmpty()) {
			return null;
		}

		DBObject doc = docs.get(0);
		return doc;
	}
	
	protected Object getDataFromDocument(ObjectId projectId,String key){
		DBObject doc = getDocument(projectId);
		if(doc==null) return null;
		return doc.get(key);
	}
}
