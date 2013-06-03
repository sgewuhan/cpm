package com.sg.vault.viewer;

import com.sg.common.db.IDBConstants;


public class OrgFolderLabelProvider extends FolderLabelprovider {

	public OrgFolderLabelProvider() {
		setCollection(IDBConstants.COLLECTION_ORG);
	}

}
