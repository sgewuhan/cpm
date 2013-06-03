package com.sg.cpm.admin.ui.lableprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.mongodb.BasicDBList;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;

public class WBSTemplateParticipateLabelProvider extends ColumnLabelProvider {

	public WBSTemplateParticipateLabelProvider() {
	}
	@Override
	public String getText(Object element) {
		Object participate = ((ISingleObject) element).getValue(IDBConstants.FIELD_PARTICIPATE);
		if (participate != null && participate instanceof BasicDBList) {
			String listText = DataUtil.getListLabel((BasicDBList) participate, IDBConstants.FIELD_DESC);
			if (listText != null) {
				return listText;
			}
		}
		return "";
	}
}
