package com.sg.cpm.project.actions.wbs;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.util.Util;

public class EditProject implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private ISingleObjectEditorInput input;
	
	
	@Override
	public void run(IAction action) {
		if (input == null) {
			return;
		}
		
		
		//*******************************************************************************************
		//权限运行时控制代码
		int authEditReadorNot = getAuthority();
		input.setEditable(!((authEditReadorNot&UserSessionContext.OBJECT_EDIT)==0));
		
		//*******************************************************************************************

		ObjectId originalChargerId;
		DBObject originalCharger = (DBObject) input.getInputData().getValue(IDBConstants.FIELD_PROJECT_PM);
		if (originalCharger != null) {
			originalChargerId = (ObjectId) originalCharger.get(IDBConstants.FIELD_SYSID);
		} else {
			originalChargerId = null;
		}

		int ok = SingleObjectEditorDialog.OPEN(window.getShell(), UIConstants.EDITOR_PROJECT_EDIT, input);
		if (ok == SingleObjectEditorDialog.OK) {

			// 将项目负责人同步到user
			DBObject projectData = input.getInputData().getData();
			ObjectId id = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);

			ObjectId newChargerId = null;
			if (projectData != null) {
				DBObject newCharger = (DBObject) projectData.get(IDBConstants.FIELD_WORK_PM);
				newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
			}

			DataUtil.saveUserRelationInformation(originalChargerId, newChargerId, IDBConstants.COLLECTION_USER_PROJECT_IN_CHARGED, id, true);
		}
	}

	private int getAuthority() {
		return DataUtil.getProjectContextAuthority(input.getInputData().getData());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

		if (Util.isNullOrEmptySelection(selection)) {
			input = null;
			action.setEnabled(false);
			return;
		} else {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof ISingleObject) {
				if (DataUtil.isProjectObject((ISingleObject) element)) {
					input = new SingleObjectEditorInput(UIConstants.EDITOR_PROJECT_EDIT, (ISingleObject) element);
					action.setEnabled(true);
					return;
				}
			}
		}
		input = null;
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
