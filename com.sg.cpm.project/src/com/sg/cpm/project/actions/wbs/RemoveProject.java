package com.sg.cpm.project.actions.wbs;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.util.Util;

public class RemoveProject implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private Object element;
	private ISingleObject project;

	@Override
	public void run(IAction action) {
		if (project == null) {
			return;
		}
		//不是初始化的项目不能删除
		DBObject projectData = project.getData();
		if(!DataUtil.isInactive(projectData)&&!DataUtil.isInactive(projectData)){
			MessageDialog.openWarning(window.getShell(),  UIConstants.TEXT_REMOVE_PROJECT, UIConstants.MESSAGE_CANNOT_DELETE_PROJECT_NOT_INIT_OR_READY);
			return;
		}
		
		//项目管理员和项目创建者可以删除
		if(!DataUtil.isProjectCreator(projectData)&&!DataUtil.isProjectAdmin(projectData)){
			MessageDialog.openWarning(window.getShell(),  UIConstants.TEXT_REMOVE_PROJECT, UIConstants.MESSAGE_CANNOT_DELETE_PROJECT_NOT_AUTH);
			return;
		}
		
		boolean ok = MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_REMOVE_PROJECT,
				"" + project.getText(IDBConstants.FIELD_DESC) + UIConstants.MESSAGE_QUESTION_REMOVE_PROJECT);
		if (!ok)
			return;
		removeUserInformation();
		project.remove();
	}

	private void removeUserInformation() {
		if (element instanceof ISingleObject) {
			ISingleObject so = (ISingleObject) element;
			DBObject projectData = so.getData();
			ObjectId id = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);
			DBObject charger = (DBObject) projectData.get(IDBConstants.FIELD_PROJECT_PM);

			DataUtil.saveUserRelationInformation((ObjectId) charger.get(IDBConstants.FIELD_SYSID), null,
					IDBConstants.COLLECTION_USER_PROJECT_IN_CHARGED, id);

		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (Util.isNullOrEmptySelection(selection)) {
			project = null;
			action.setEnabled(false);
			return;
		} else {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof ISingleObject) {
				if (DataUtil.isProjectObject((ISingleObject) element)) {
					project = (ISingleObject) element;
					action.setEnabled(true);
					return;
				}
			}
		}
		project = null;
		action.setEnabled(false);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
