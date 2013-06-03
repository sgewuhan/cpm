package com.sg.common.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.WorkService;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class ProjectOfCurrentUserAsPMOption implements IOptionProvider {

	public ProjectOfCurrentUserAsPMOption() {
	}

	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,
			ISingleObject data, String key, Object value) {
		List<Enumerate> project = new ArrayList<Enumerate>();
		Enumerate e = new Enumerate(key, data.toString(), data, project);
		WorkService ws = BusinessService.getWorkService();
		DBObject user = BusinessService.getOrganizationService().getCurrentUserData();
		ObjectId userOid = (ObjectId) user.get(IDBConstants.FIELD_SYSID);
		List<DBObject> result = ws.getProjectAsProjectManager(userOid,IDBConstants.VALUE_PROCESS_PROCESS);
		for(int i=0;i<result.size();i++){
			DBObject projectData = result.get(i);
			project.add(new Enumerate((String)projectData.get(IDBConstants.FIELD_ID), (String)projectData.get(IDBConstants.FIELD_DESC), projectData, null));
		}
		return e;
	}

}
