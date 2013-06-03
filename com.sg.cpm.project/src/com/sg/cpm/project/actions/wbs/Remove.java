package com.sg.cpm.project.actions.wbs;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.CascadeObject;

public class Remove extends WBSActions {

	@Override
	public void run(IAction action) {
		//************************************************************************************************************
		//只有项目经理和项目管理员有权
		ObjectId projectId = project.getSystemId();
		DBObject data = DataUtil.getDataObject(IDBConstants.COLLECTION_PROJECT, projectId);
		if(!DataUtil.isProjectManager(data)
				&&!DataUtil.isProjectAdmin(data)){
			MessageDialog.openWarning(view.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			return;
		}
		//************************************************************************************************************

		

		if (DataUtil.isWorkObject(currentSelection)) {
			boolean ok = MessageDialog.openQuestion(view.getSite().getShell(), UIConstants.TEXT_REMOVE_WORK, "" + currentSelection
					+ UIConstants.MESSAGE_QUESTION_REMOVE_TASK);
			if (!ok)
				return;
		} else if (DataUtil.isDocumentObject(currentSelection)) {
			boolean ok = MessageDialog.openQuestion(view.getSite().getShell(), UIConstants.TEXT_REMOVE_DOC, "" + currentSelection
					+ UIConstants.MESSAGE_QUESTION_REMOVE_DOC);
			if (!ok)
				return;
		}

		CascadeObject parent = currentSelection.getParent();
		// 下级任务序号调整
		int seq = ((Number) currentSelection.getValue(IDBConstants.FIELD_WBSSEQ)).intValue();

		List<CascadeObject> children = parent.getChildren();
		int index = children.indexOf(currentSelection);
		for (int i = index + 1; i < children.size(); i++) {
			CascadeObject child = children.get(i);
			if (DataUtil.isWorkObject(child)) {
				child.setValue(IDBConstants.FIELD_WBSSEQ, seq++);
				child.save();
			}
		}

		// 同步user信息
		if (DataUtil.isWorkObject(currentSelection)) {
			setUserInformation(currentSelection);
		}

		currentSelection.remove(true);

		view.reload(parent, false);
		// view.select(parent);
	}

	private void setUserInformation(CascadeObject currentSelection) {
		ObjectId id = currentSelection.getSystemId();

		// 保存原有的任务负责人id
		ObjectId originalChargerId = null;
		DBObject originalCharger = (DBObject) currentSelection.getValue(IDBConstants.FIELD_WORK_PM);
		if (originalCharger != null) {
			originalChargerId = (ObjectId) originalCharger.get(IDBConstants.FIELD_SYSID);
		}
		// 保存原有的参与者信息
		BasicDBList originalResourceList = new BasicDBList();
		BasicDBList value = (BasicDBList) currentSelection.getValue(IDBConstants.FIELD_WORK_RESOURCE);
		if(value==null){
			originalResourceList = null;
		}else{
			originalResourceList.addAll(value);
		}

		// 同步用户负责数据
		DataUtil.saveUserRelationInformation(originalChargerId, null, IDBConstants.COLLECTION_USER_WORK_IN_CHARGED, id);

		// 同步参与的任务信息 删除原有的
		DataUtil.saveUserWorkAndProjectInformation(originalResourceList, null, IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED, id);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// 编辑可用性
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		// 考虑权限
		action.setEnabled(currentSelection != null );

		if (!DataUtil.isWorkObject(currentSelection) && !DataUtil.isDocumentObject(currentSelection)) {
			action.setEnabled(false);
		}

	}

}
