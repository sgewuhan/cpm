package com.sg.cpm.project.viewer;

import org.eclipse.swt.graphics.Image;

import com.sg.common.db.DataUtil;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class WorkIconColumnLabelProvider extends ViewerColumnLabelProvider {

	@Override
	public Image getImage(Object element) {
		if(DataUtil.isProjectObject((ISingleObject) element)){
			return Resource.getImage(Resource.PROJECT16);
			
		}else if(DataUtil.isWorkObject((ISingleObject) element)){
			if(DataUtil.isWorkReady((ISingleObject) element)){
				return Resource.getImage(Resource.WORK_READY16);
			}
			if(DataUtil.isWorkProcess((ISingleObject) element)){
				return Resource.getImage(Resource.WORK_PROCESS16);
			}
			if(DataUtil.isWorkCancel((ISingleObject) element)){
				return Resource.getImage(Resource.WORK_CANCEL16);
			}
			if(DataUtil.isWorkClose((ISingleObject) element)){
				return Resource.getImage(Resource.WORK_CLOSE16);
			}
			if(DataUtil.isWorkStop((ISingleObject) element)){
				return Resource.getImage(Resource.WORK_STOP16);
			}
			
			return Resource.getImage(Resource.TASK16);
		}else{
			return Resource.getImage(Resource.DOC16);
		}
	}
}
