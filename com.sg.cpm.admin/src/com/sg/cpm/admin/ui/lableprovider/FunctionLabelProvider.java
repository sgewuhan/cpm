package com.sg.cpm.admin.ui.lableprovider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

import com.sg.cpm.admin.AdminFunction;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.viewer.labelprovider.ViewerColumnLabelProvider;

public class FunctionLabelProvider extends ViewerColumnLabelProvider {

	@Override
	public Image getImage(Object element) {
		int id = AdminFunction.getFunctionId((ISingleObject)element);
		switch (id) {
		case AdminFunction.project_template:
			return Resource.getImage(Resource.TEMPLATE16);
		case AdminFunction.project_workdelivery:
			return Resource.getImage(Resource.WORKDELIVERY16);
		case AdminFunction.project_workflow:
			return Resource.getImage(Resource.WORKFLOW16);
		case AdminFunction.auth_setting:
			return WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_OBJ_SIGNED_YES);
		case AdminFunction.auth_query:
			return WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_OBJ_SIGNED_UNKNOWN);
		case AdminFunction.org_user:
			return Resource.getImage(Resource.USER16);
		case AdminFunction.org_obs:
			return Resource.getImage(Resource.ORG16);
		case AdminFunction.workflow_console:
			return Resource.getImage(Resource.WORKFLOW16);
		case AdminFunction.workflow_reposite:
			return Resource.getImage(Resource.WORKFLOW_REPO16);
		case AdminFunction.work_template:
			return Resource.getImage(Resource.TEMPLATE16);
		case AdminFunction.server_setting:
			return WorkbenchImages.getImage(ISharedImages.IMG_DEF_VIEW);
		default:
			return WorkbenchImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
	}


}
