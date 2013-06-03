package com.sg.cpm.project.viewer;

import com.mongodb.BasicDBList;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class AssignToLabelProvider extends ViewerColumnLabelProvider {


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
