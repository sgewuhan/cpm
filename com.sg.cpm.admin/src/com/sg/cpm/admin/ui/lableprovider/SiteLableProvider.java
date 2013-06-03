package com.sg.cpm.admin.ui.lableprovider;

import org.eclipse.swt.graphics.Image;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class SiteLableProvider extends ViewerColumnLabelProvider {

	@Override
	public Image getImage(Object element) {
		ISingleObject so = (ISingleObject)element;
		if(DataUtil.isSiteObject(so)){
			if(Boolean.TRUE.equals(so.getValue(IDBConstants.FIELD_SHARESITE))){
				return Resource.getImage(Resource.SITE_SHARED16);
			}else{
				return Resource.getImage(Resource.SITE16);
			}
		}else{
			return Resource.getImage(Resource.ROOT_SITE16);
		}
	}


}
