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
		if (DataUtil.isDocumentObject(selected)) {// ѡ�е����ĵ�
			// ������ĵ�������Ĺ����ĵ�������ɾ��
			if (selected.getValue(IDBConstants.FIELD_WBSPARENT) != null) {
				MessageDialog.openInformation(window.getShell(), UIConstants.TEXT_REMOVE_DOC+ ":" + selected, UIConstants.MESSAGE_CANNOT_WORK_DEL);
				return;
			}
			// ��ʾ�Ƿ�Ҫɾ��
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
				// ��ʾ�Ƿ�Ҫɾ��
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
		// ��һ���ǲ�ѯ��
		// �ڶ�������Ŀ��Ŀ¼
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
				if (selected == root) {// ѡ�е��Ǹ�
					action.setEnabled(false);
				} else {
					super.updateActionStatus(action);
				}
			}
		}
	}

}