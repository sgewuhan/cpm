package com.sg.cpm.admin.ui.lableprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;

public class WBSTemplateStatusIconProvider extends ColumnLabelProvider {

	@Override
	public Image getImage(Object element) {

		if (element instanceof ISingleObject) {
			if (DataUtil.isWorkTemplateObject((ISingleObject) element) || DataUtil.isProjectTemplateObject((ISingleObject) element)) {
				if (Boolean.TRUE.equals(((ISingleObject) element).getValue(IDBConstants.FIELD_ACTIVATE))) {// 启用
					return Resource.getImage(Resource.ACTIVE16);
				} else {// 已经停用
					return Resource.getImage(Resource.DISACTIVE16);
				}
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {

		return "";
	}

}
