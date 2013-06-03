package com.sg.vault.viewer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.CascadeObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class FolderLabelprovider extends ViewerColumnLabelProvider {

	private DBCollection collection;

	public FolderLabelprovider() {
	}

	public void setCollection(String rootType){
		collection = DBActivator.getDefaultDBCollection(rootType);
	}
	
	
	@Override
	public String getText(Object element) {
		CascadeObject ca = (CascadeObject)element;
		//如果是根，需要显示根的名称
		if(ca.getParent()==null||ca.getParent().getParent()==null){
			DBObject one = collection.findOne(new BasicDBObject().append(IDBConstants.FIELD_SYSID, ca.getValue(IDBConstants.FIELD_FBSPARENT)));
			return ca.getText(IDBConstants.FIELD_DESC)+"  ["+one.get(IDBConstants.FIELD_DESC)+"]";
		}else{
			return ca.getText(IDBConstants.FIELD_DESC);
		}
	}

	@Override
	public Image getImage(Object element) {
		CascadeObject ca = (CascadeObject)element;
		
		//如果是文档，显示文档图标
		
		if(DataUtil.isMatchedObject(ca, IDBConstants.COLLECTION_DOCUMENT)){
			return Resource.getImage(Resource.DOC16);
		}
		//如果是根,显示项目根目录
		if(ca.getParent()==null||ca.getParent().getParent()==null){
			return Resource.getImage(Resource.PROJECT16);
		}else{
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
	}

}
