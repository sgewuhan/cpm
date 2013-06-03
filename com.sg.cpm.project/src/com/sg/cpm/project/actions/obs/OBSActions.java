package com.sg.cpm.project.actions.obs;

import org.eclipse.core.internal.commands.util.Util;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.sg.common.db.DataUtil;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.project.view.OBSView;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.widget.part.IMasterChangeListener;
import com.sg.widget.part.NavigatableTableView;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;
import com.sg.widget.viewer.treeviewer.QueryTreeViewer;

public class OBSActions implements IViewActionDelegate, IMasterChangeListener {

	protected CascadeObject currentSelection;
	protected OBSView view;
	protected CascadeObject master;

	@Override
	public void run(IAction action) {

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		//编辑可用性
		
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		//考虑权限
		action.setEnabled(currentSelection != null );
	}

	@Override
	public void init(IViewPart view) {
		this.view = (OBSView) view;
		this.view.addMasterChangeListener(this);
	}

	@Override
	public void masterChanged(CascadeObject oldMaster, CascadeObject newMaster) {
		if((master==null&&newMaster==null)||(master!=null&&newMaster!=null&&Util.equals(master.getSystemId(), newMaster.getSystemId()))){
			return;
		}
		this.master = newMaster;
	}
	
	
	protected QueryTreeViewer getViewer(){
		return view.getViewer();
	}
	
	protected boolean hasAuth(){
		NavigatableTableView masterView = (NavigatableTableView) view.getSite().getPage().findView(UIConstants.VIEW_PROJECT_NAVIGATOR);
		QueryTableViewer projectViewer = masterView.getViewer();
		IStructuredSelection sel = projectViewer.getSelection();
		if (sel == null || sel.isEmpty()) {
			return false;
		}
		ISingleObject so = (ISingleObject) sel.getFirstElement();
		
//		//************************************************************************************************************
//		//只有项目经理和项目管理员有权
//		if(!DataUtil.isProjectManager(so.getData())
//				&&!DataUtil.isProjectAdmin(so.getData())){
//			MessageDialog.openWarning(masterView.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
//			return false;
//		}
//		return true;
//		//************************************************************************************************************

		//**********************************************修改权限逻辑，只有项目管理员有权
		if(!DataUtil.isProjectAdmin(so.getData())){
			MessageDialog.openWarning(masterView.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_TEAM);
			return false;
		}
		return true;
		
	}

}
