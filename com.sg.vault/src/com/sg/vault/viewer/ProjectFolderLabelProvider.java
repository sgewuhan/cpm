package com.sg.vault.viewer;

import com.sg.common.db.IDBConstants;


public class ProjectFolderLabelProvider extends FolderLabelprovider {

	public ProjectFolderLabelProvider() {
		setCollection(IDBConstants.COLLECTION_PROJECT);
	}

}
