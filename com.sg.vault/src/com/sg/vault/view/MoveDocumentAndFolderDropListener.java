package com.sg.vault.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Shell;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.CascadeObject;

public 	class MoveDocumentAndFolderDropListener implements DropTargetListener {
	
	private DocumentNavigatorView docNavi;

	public MoveDocumentAndFolderDropListener(DocumentNavigatorView docNavi){
		this.docNavi = docNavi;
	}

	public void dragEnter(final DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
	}

	public void dragLeave(final DropTargetEvent event) {
	}

	public void dragOperationChanged(final DropTargetEvent event) {
	}

	public void dragOver(final DropTargetEvent event) {
	}

	public void drop(final DropTargetEvent event) {
		String jsonOBS = (String) event.data;
		if (jsonOBS.equals("#")) {
			return;
		}
		if (docNavi.dragItem == null) {
			return;
		}
		
		if(event.item==null||event.item.getData()==null){
			return;
		}
		
		CascadeObject targetParent = (CascadeObject) event.item.getData();
		
		Shell shell = docNavi.getSite().getShell();
		if (DataUtil.isDocumentObject(targetParent)) {
			MessageDialog.openError(shell, UIConstants.TEXT_MOVE + ":" + docNavi.dragItem,
					UIConstants.MESSAGE_CANNOT_MOVE_TO_FILE);
			return;
		}

		// 如果是不同的根，不可移动
		CascadeObject sourceRoot = getRootFolder(docNavi.dragItem);
		CascadeObject targetRoot = getRootFolder(targetParent);
		if (sourceRoot != targetRoot) {
			MessageDialog.openError(shell, UIConstants.TEXT_MOVE + ":" + docNavi.dragItem,
					UIConstants.MESSAGE_CANNOT_MOVE_TO_ANOTHER_ROOT);
			return;
		}
		
		//如果当前用户没有该项目的文档管理的权限不可以拖动
		//如果当前用户没有权限管理文件夹，不可拖动
//		boolean hasAuthority = UserSessionContext.hasAuthority(new String[] { docNavi.getFolderManagementAuthCode() },
//				new SingleActiveObject(sourceRoot));
//		if(!hasAuthority){
//			MessageDialog.openError(shell, UIConstants.TEXT_MOVE + ":" + docNavi.dragItem,
//					UIConstants.MESSAGE_NOT_AUTH+UIConstants.MESSAGE_CHANGE_FBS);
//			return;
//		}
		
		

		// 不能把本级别的移动到下级别
		if (isCascadeChild(targetParent, docNavi.dragItem)) {
			MessageDialog.openError(shell, UIConstants.TEXT_MOVE + ":" + docNavi.dragItem,
					UIConstants.MESSAGE_CANNOT_MOVE_TO_CHILD);
			return;
		}

		CascadeObject oldParent = docNavi.dragItem.getParent();
		oldParent.removeChild(docNavi.dragItem);
		docNavi.dragItem.setValue(IDBConstants.FIELD_FBSPARENT, targetParent.getSystemId());
		docNavi.dragItem.save();
		targetParent.addChild(docNavi.dragItem);
		docNavi.getViewer().refresh(oldParent);
		docNavi.getViewer().refresh(targetRoot);
	}

	public void dropAccept(final DropTargetEvent event) {
	}
	
	/**
	 * 判断sourceItem是否是targetParent的父
	 * 
	 * @param targetParent
	 * @param sourceItem
	 * @return
	 */
	private boolean isCascadeChild(CascadeObject targetParent, CascadeObject sourceItem) {
		if (targetParent == null) {
			return false;
		} else {

			if (targetParent.getParent() == sourceItem) {
				return true;
			} else {
				return isCascadeChild(targetParent.getParent(), sourceItem);
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

}