package com.sg.cpm.project.actions.obs;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;
import com.sg.widget.dialog.SingleObjectEditorDialog;

public class CreateTeam extends OBSActions {

	@Override
	public void run(IAction action) {


		if (currentSelection == null)
			return;

		
		//************************************************************************************************************
		if (!hasAuth())
			return;

		//************************************************************************************************************

		
		int ok = DataUtil.createOBSItemUI(view.getSite().getShell(), currentSelection, IDBConstants.VALUE_OBS_TEAMTYPE);

		if (ok == SingleObjectEditorDialog.OK) {
			getViewer().refresh(currentSelection, false);
			getViewer().expandToLevel(currentSelection, AbstractTreeViewer.ALL_LEVELS);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		//考虑选项和权限
		boolean enable = currentSelection != null ;
		if(enable){
			//考虑对象类型，在角色类型、用户类型下不可用
			if(DataUtil.isUserObject(currentSelection)||DataUtil.isRoleObject(currentSelection)){
				enable = false;
			}
		}
		
		//考虑权限
		action.setEnabled(enable);
	}

}
