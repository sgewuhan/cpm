package com.sg.vault.view;

import java.util.List;

import com.mongodb.BasicDBList;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;

public class ProjectDocumentNavigatorView extends DocumentNavigatorView {
	
	@Override
	public String getFolderManagementAuthCode() {
		return  UserSessionContext.FOLDER_PROJECT_MANAGEMENT;
	}

	@Override
	protected BasicDBList getRootIdList() {
		
		BasicDBList result = new BasicDBList();
		List<ISingleObject> list = DataUtil.getContextControlProjectList();
		for(ISingleObject so:list){
			result.add(so.getValue(IDBConstants.FIELD_SYSID));
		}
		return result;
	}

}
