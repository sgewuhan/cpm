package com.sg.vault.view;

import com.mongodb.BasicDBList;
import com.sg.common.db.DataUtil;
import com.sg.user.UserSessionContext;

public class OrgDocumentNavigatorView extends DocumentNavigatorView {

	@Override
	public String getFolderManagementAuthCode() {
		return  UserSessionContext.FOLDER_ORG_MANAGEMENT;
	}

	@Override
	protected BasicDBList getRootIdList() {
		return DataUtil.getContextControlFolderIdList();
	}

}
