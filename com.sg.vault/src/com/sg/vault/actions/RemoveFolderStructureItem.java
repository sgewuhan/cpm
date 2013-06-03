package com.sg.vault.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.CascadeObject;
import com.sg.widget.actions.Remove;
import com.sg.widget.part.NavigatableTreeView;

public class RemoveFolderStructureItem extends Remove {

	private CascadeObject root;

	@Override
	public void run(IAction action) {
		if (DataUtil.isDocumentObject(selected)) {// 选中的是文档
			// 如果该文档是任务的关联文档，不可删除
			if (selected.getValue(IDBConstants.FIELD_WBSPARENT) != null) {
				MessageDialog.openInformation(window.getShell(), UIConstants.TEXT_REMOVE_DOC+ ":" + selected, UIConstants.MESSAGE_CANNOT_WORK_DEL);
				return;
			}
			// 提示是否要删除
			boolean ok = MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_REMOVE_DOC + ":" + selected,
					UIConstants.MESSAGE_QUESTION_DELETE);
			if (ok) {
				selected.remove();
			}
			return;
		} else if (DataUtil.isFolderObject(selected)) {
			CascadeObject cs = (CascadeObject) selected;
			List<CascadeObject> children = cs.getChildren();
			if (children.size() == 0) {
				boolean ok = MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_REMOVE_FOLDER + ":" + selected,
						UIConstants.MESSAGE_QUESTION_DELETE);
				if (ok)
					cs.remove();
			} else {
				// 提示是否要删除
				boolean ok = MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_REMOVE_FOLDER + ":" + selected,
						UIConstants.MESSAGE_QUESTION_DELETE_FOLDER_MOVE_DOC);
				if (ok) {
					removeFolderCascade(cs);
					NavigatableTreeView part = (NavigatableTreeView) window.getActivePage().getActivePart();
					root.loadChildren();
					part.getViewer().refresh(root);
				}
			}
			return;
		}
	}

	private void removeFolderCascade(CascadeObject cs) {
		if (root == null) {
			root = getRootFolder((CascadeObject) selected);
		}

		CascadeObject[] children = cs.getChildren().toArray(new CascadeObject[] {});

		for (CascadeObject child : children) {
			if (DataUtil.isDocumentObject(child)) {
				child.setValue(IDBConstants.FIELD_FBSPARENT, root.getSystemId());
				child.save();
			} else {
				removeFolderCascade(child);
			}
		}
		cs.remove();
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
	protected void updateActionStatus(IAction action) {
		if(selected==null){
			root = null;
		}else{
			if(selected instanceof CascadeObject){
				root = getRootFolder((CascadeObject) selected);
				if (selected == root) {// 选中的是根
					action.setEnabled(false);
				} else {
					super.updateActionStatus(action);
				}
			}
		}
	}

}