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
		//ֻ����Ŀ�������Ŀ����Ա��Ȩ
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
		// �¼�������ŵ���
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

		// ͬ��user��Ϣ
		if (DataUtil.isWorkObject(currentSelection)) {
			setUserInformation(currentSelection);
		}

		currentSelection.remove(true);

		view.reload(parent, false);
		// view.select(parent);
	}

	private void setUserInformation(CascadeObject currentSelection) {
		ObjectId id = currentSelection.getSystemId();

		// ����ԭ�е���������id
		ObjectId originalChargerId = null;
		DBObject originalCharger = (DBObject) currentSelection.getValue(IDBConstants.FIELD_WORK_PM);
		if (originalCharger != null) {
			originalChargerId = (ObjectId) originalCharger.get(IDBConstants.FIELD_SYSID);
		}
		// ����ԭ�еĲ�������Ϣ
		BasicDBList originalResourceList = new BasicDBList();
		BasicDBList value = (BasicDBList) currentSelection.getValue(IDBConstants.FIELD_WORK_RESOURCE);
		if(value==null){
			originalResourceList = null;
		}else{
			originalResourceList.addAll(value);
		}

		// ͬ���û���������
		DataUtil.saveUserRelationInformation(originalChargerId, null, IDBConstants.COLLECTION_USER_WORK_IN_CHARGED, id);

		// ͬ�������������Ϣ ɾ��ԭ�е�
		DataUtil.saveUserWorkAndProjectInformation(originalResourceList, null, IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED, id);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// �༭������
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		// ����Ȩ��
		action.setEnabled(currentSelection != null );

		if (!DataUtil.isWorkObject(currentSelection) && !DataUtil.isDocumentObject(currentSelection)) {
			action.setEnabled(false);
		}

	}

}
