package com.sg.vault.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sg.common.db.DataUtil;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.CascadeObject;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.part.NavigatableTreeView;

public class CreateDocument implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private String currentPartId;
	private CascadeObject selectedRoot;
	private CascadeObject selected;
	private NavigatableTreeView part;
	@Override
	public void run(IAction action) {
		int ok = DataUtil.createDocumentInFolderItemUI(window.getShell(),selected,selectedRoot.getSystemId());
		
		if (ok == SingleObjectEditorDialog.OK) {
			part.getViewer().refresh(selected, false);
			part.getViewer().expandToLevel(selected, AbstractTreeViewer.ALL_LEVELS);
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IWorkbenchPart part = window.getActivePage().getActivePart();
		if(part==null){
			return;
		}
		if(!part.getSite().getId().equals(UIConstants.VIEW_PROJECT_FOLDER_NAVIGATOR)
				&&!part.getSite().getId().equals(UIConstants.VIEW_ORG_FOLDER_NAVIGATOR)){
			return;
		}
		
		if(part!=null){
			currentPartId = part.getSite().getId();
		}
		// 当前的部件是项目文件夹视图
		if (currentPartId==null||selection == null || selection.isEmpty()) {
			selectedRoot = null;
			selected = null;
			this.part = null;
			return;
		} else {
			selected = (CascadeObject) ((StructuredSelection) selection).getFirstElement();
			selectedRoot = getRootFolder(selected);
			this.part = (NavigatableTreeView) part;
		}
		updateActionStatus(action);

	}

	
	private void updateActionStatus(IAction action) {
		if (selectedRoot == null) {
			action.setEnabled(false);
		} else {
			// 判断当前选择的是文件还是目录
			if (DataUtil.isDocumentObject(selected)) {
				action.setEnabled(false);
			} else {
				action.setEnabled(true);
			}
		}

	}
	
	private CascadeObject getRootFolder(CascadeObject cascadeObject) {
		// 第一级是查询根
		// 第二级是项目根目录
		if (cascadeObject.getParent().getParent() != null) {
			return getRootFolder(cascadeObject.getParent());
		}
		return cascadeObject;
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
