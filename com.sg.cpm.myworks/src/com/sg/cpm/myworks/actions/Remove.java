package com.sg.cpm.myworks.actions;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.MessageObject;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.myworks.view.WorkInBox;
import com.sg.db.DBActivator;
import com.sg.user.UserSessionContext;

public class Remove implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private Object data;
	private DBCollection workCollection;
	private DBCollection documentCollection;

	@Override
	public void run(IAction action) {
		// 3. 如果选中的是文档，任务完成了，完成的文档不可删除
		if (data instanceof DBObject) {
			ObjectId workId = (ObjectId) ((DBObject) data).get(IDBConstants.FIELD_WBSPARENT);
			if (workId != null) {
				DBObject workData = workCollection.findOne(new BasicDBObject(IDBConstants.FIELD_SYSID, workId));
				if ((workData != null) && (!DataUtil.isClose(workData))) {
					// 4.如果选中的是文档，只有任务的负责人可以删除
					DBObject chargerData = (DBObject) workData.get(IDBConstants.FIELD_WORK_PM);
					if (chargerData != null
							&& UserSessionContext.getSession().getUserOId().equals(chargerData.get(IDBConstants.FIELD_SYSID))) {
						if (MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_REMOVE_DOC,
								((DBObject) data).get(IDBConstants.FIELD_DESC) + UIConstants.MESSAGE_QUESTION_DELETE)) {
							documentCollection.remove(new BasicDBObject().append(IDBConstants.FIELD_SYSID,
									((DBObject) data).get(IDBConstants.FIELD_SYSID)));
							refreshWIB();
						}
						return;
					} else {
						MessageDialog.openWarning(window.getShell(), UIConstants.TEXT_REMOVE_DOC,
								UIConstants.MESSAGE_CANNOT_REMOVE_DOCUMENT_WHEN_NOT_CHARGER);
						return;
					}
				} else {
					MessageDialog.openWarning(window.getShell(), UIConstants.TEXT_REMOVE_DOC,
							UIConstants.MESSAGE_CANNOT_REMOVE_DOCUMENT_WHEN_TASK_FINISHED);
					return;
				}
			}
			MessageDialog.openWarning(window.getShell(), UIConstants.TEXT_REMOVE_DOC, UIConstants.MESSAGE_CANNOT_REMOVE_DOCUMENT_UNKNOWN);
			return;
		} else if (data instanceof MessageObject) {
			// 如果删除的是任务，直接放入到回收站
			((MessageObject)data).markDelete(true);
			refreshWIB();
		}
	}

	private void refreshWIB() {
		WorkInBox wib = (WorkInBox) window.getActivePage().findView(UIConstants.VIEW_MYWORKS_WORKSINBOX);
		wib.update();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		// 1. 必须是在wib中可用
		if (selection != null && !selection.isEmpty()) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			IWorkbenchPart part = window.getActivePage().getActivePart();
			if (part.getSite().getId().equals(UIConstants.VIEW_MYWORKS_WORKSINBOX)) {

				// 2. 如果选中的是任务，只有是完成的或者取消的任务才可以删除
				if (element instanceof MessageObject) {
					data = (MessageObject) element;
					if (((MessageObject) data).isCancel() || ((MessageObject) data).isClose()) {
						action.setEnabled(true);
					} else {
						action.setEnabled(false);
					}
				} else if (element instanceof DBObject) {
					data = element;
					action.setEnabled(true);
				}
			} else {
				data = null;
				action.setEnabled(false);
			}
		} else {
			data = null;
			action.setEnabled(false);
		}

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		workCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);
		documentCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
	}

}
