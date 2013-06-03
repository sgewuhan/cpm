package com.sg.cpm.admin.ui.lableprovider;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class UserLableProvider extends ViewerColumnLabelProvider {

	// @Override
	// public Image getImage(Object element) {
	// if(DataUtil.isActivatedUser((ISingleObject)element)){
	// return Resource.getImage(Resource.USER16);
	// }else{
	// return Resource.getImage(Resource.USER_D16);
	// }
	// }
	//
	// @Override
	// public String getText(Object element) {
	// return DataUtil.getUserLable((ISingleObject)element);
	// }

	@Override
	public String getText(Object element) {
		DBObject row;
		if(element instanceof ISingleObject){
			row = ((ISingleObject)element).getData();
		}else if(element instanceof DBObject){
			row = (DBObject) element;
		}else{
			return element!=null?element.toString():"";
		}
		DBObject userInfo = DataUtil.getUserInformation(row,true);

		return (String) userInfo.get(IDBConstants.FIELD_URLLABEL);

	}

}
