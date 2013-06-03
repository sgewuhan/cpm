package com.sg.common.ui;

import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Image;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class OBSLabelProvider extends ViewerColumnLabelProvider {

	@Override
	public Image getImage(Object element) {
		if (DataUtil.isProjectTemplateObject((ISingleObject) element)) {
			return Resource.getImage(Resource.PROJECT16);
		} else if (DataUtil.isTeamObject((ISingleObject) element)) {
			return Resource.getImage(Resource.TEAM16);
		} else if (DataUtil.isRoleObject((ISingleObject) element)) {
			return Resource.getImage(Resource.ROLE16);
		} else if (DataUtil.isUserObject((ISingleObject) element)) {
			return Resource.getImage(Resource.USER16);
		} else if(DataUtil.isProjectTeamObject((ISingleObject)element)){
			return Resource.getImage(Resource.PROJECT_TEAM16);
		}
		return Resource.getImage(Resource.SITE16);
	}

	@Override
	public String getText(Object element) {
		if (DataUtil.isUserObject((SingleObject) element)) {
			SingleObject user = (SingleObject) element;
			DBObject dbo = DataUtil.getDataObject(IDBConstants.COLLECTION_USER,(ObjectId) user.getValue(IDBConstants.FIELD_USEROID));
			return DataUtil.getUserLable(dbo);
		}
		return super.getText(element);
	}

}