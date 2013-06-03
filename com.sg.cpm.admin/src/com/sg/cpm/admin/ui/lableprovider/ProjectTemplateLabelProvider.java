package com.sg.cpm.admin.ui.lableprovider;

import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Image;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class ProjectTemplateLabelProvider extends ViewerColumnLabelProvider {

	

	@Override
	public Image getImage(Object element) {
		return Resource.getImage(Resource.TEMPLATE16);
	}

	@Override
	public String getText(Object element) {
		if(element instanceof ISingleObject){
			Object contextId = ((ISingleObject) element).getValue(IDBConstants.FIELD_OBSPARENT);
			if(contextId!=null){
				DBObject obsItem = DataUtil.getDataObject(IDBConstants.COLLECTION_ORG, (ObjectId) contextId);
				String obsDesc = (String) obsItem.get(IDBConstants.FIELD_DESC);
				return super.getText(element)+"["+obsDesc+"]";
			}
		}
		// TODO Auto-generated method stub
		return super.getText(element);
	}
	
	

}
