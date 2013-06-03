package com.sg.cpm.admin.ui.lableprovider;

import org.eclipse.swt.graphics.Image;

import com.sg.common.db.DataUtil;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class WBSTemplateLabelProvider extends ViewerColumnLabelProvider {

	@Override
	public Image getImage(Object element) {
		if (DataUtil.isProjectTemplateObject((ISingleObject) element)) {
			return Resource.getImage(Resource.PROJECT16);
		} else if (DataUtil.isWorkTemplateObject((ISingleObject) element)) {
			return Resource.getImage(Resource.TASK16);
		} else if (DataUtil.isDeliveryTemplateObject((ISingleObject) element)) {
			return Resource.getImage(Resource.DOC16);
		} else if (DataUtil.isTeamTemplateObject((ISingleObject) element)) {
			return Resource.getImage(Resource.TEAM16);
		}
		return null;
	}

//	@Override
//	public String getText(Object element) {
//		String taskName = super.getText(element);
//		Object participate = ((ISingleObject) element).getValue(IDBConstants.FILED_PARTICIPATE);
//		if (participate != null && participate instanceof BasicDBList) {
//			String listText = DataUtil.getListLabel((BasicDBList) participate, IDBConstants.FIELD_DESC);
//			if (listText != null) {
//				taskName = taskName + "(" + listText + ")";
//			}
//		}
//		return taskName;
//	}

}
