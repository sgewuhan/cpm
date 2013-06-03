package com.sg.cpm.project.actions.obs;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.sg.common.db.DataUtil;
import com.sg.db.model.CascadeObject;


public class Edit extends OBSActions {
	@Override
	public void run(IAction action) {
		Shell shell = view.getSite().getShell();
		
		//************************************************************************************************************
		//只有项目经理和项目管理员有权
		
		if (!hasAuth())
			return;

		//************************************************************************************************************

		
		DataUtil.editOBSItemUI(shell,currentSelection);
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
			//考虑对象类型，用户类型下不可用
			if(DataUtil.isUserObject(currentSelection)){
				enable = false;
			}
		}
		
		//考虑权限
		action.setEnabled(enable);
	}
}
