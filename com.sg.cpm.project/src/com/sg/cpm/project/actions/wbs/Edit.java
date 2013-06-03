package com.sg.cpm.project.actions.wbs;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Platform;
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
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class Edit extends WBSActions {

	@Override
	public void run(IAction action) {
		//************************************************************************************************************
		//ֻ����Ŀ�������Ŀ����Ա��Ȩ
		boolean editable = true;
		ObjectId projectId = project.getSystemId();
		DBObject data = DataUtil.getDataObject(IDBConstants.COLLECTION_PROJECT, projectId);
		if(!DataUtil.isProjectManager(data)
				&&!DataUtil.isProjectAdmin(data)){
//			MessageDialog.openWarning(view.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			editable = false;
//			return;
		}
		//************************************************************************************************************

		
		
		if (currentSelection != null) {
			ISingleObjectEditorInput input = (ISingleObjectEditorInput) Platform.getAdapterManager().getAdapter(currentSelection,
					ISingleObjectEditorInput.class);
			input.setEditable(editable);
			if (DataUtil.isWorkObject(currentSelection)) {
				// ����ԭ�е���������id
				ObjectId originalChargerId = null;
				DBObject originalCharger = (DBObject) input.getInputData().getValue(IDBConstants.FIELD_WORK_PM);
				if (originalCharger != null) {
					originalChargerId = (ObjectId) originalCharger.get(IDBConstants.FIELD_SYSID);
				}
				// ����ԭ�еĲ�������Ϣ
				BasicDBList originalResourceList = new BasicDBList();
				BasicDBList value = (BasicDBList) input.getInputData().getValue(IDBConstants.FIELD_WORK_RESOURCE);
				if (value != null){
					originalResourceList.addAll(value);
				}else {
					originalResourceList = null;
				}

				ISingleObjectEditorDialogCallback saveCallback = new SingleObjectEditorDialogCallback(){

					//��Ҫ����ѡ�����������ˣ����Ҹ�����ĸ�����������Դ�е����
					@Override
					public boolean saveBefore(ISingleObjectEditorInput input) {
						DBObject charger = (DBObject) input.getInputData().getValue(IDBConstants.FIELD_WORK_PM);

						if(charger==null){
							return true;
						}
						
						ObjectId pmoid = (ObjectId) charger.get(IDBConstants.FIELD_SYSID);
						
						if(pmoid==null){
							return true;
						}
						
						BasicDBList resource = (BasicDBList)input.getInputData().getValue(IDBConstants.FIELD_WORK_RESOURCE);
						if(resource==null){
							return true;
						}

						for(int i=0;i<resource.size();i++){
							DBObject res = (DBObject) resource.get(i);
							if(pmoid.equals(res.get(IDBConstants.FIELD_SYSID))){
								resource.remove(res);
								return true;
							}
						}
						
						return super.saveBefore(input);
					}
					
				};
				
				int ok = SingleObjectEditorDialog.OPEN(view.getSite().getShell(), UIConstants.EDITOR_WORK_EDIT, input,saveCallback,false);
				if (ok == SingleObjectEditorDialog.OK) {
					// ͬ���û���������
					DBObject workData = input.getInputData().getData();
					ObjectId id = (ObjectId) workData.get(IDBConstants.FIELD_SYSID);

					ObjectId newChargerId = null;
					if (workData != null) {
						DBObject newCharger = (DBObject) workData.get(IDBConstants.FIELD_WORK_PM);
						if(newCharger!=null){
							newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
						}
					}

					//*******************************************************************************************************
					/*
					 * �����Ŀ״̬�ǽ��������״̬��׼����ͬʱ��Ҫ����֪ͨ�� 
				          *  �����Ŀ״̬����ͣ�������״̬����ͣ��ͬʱ��Ҫ����֪ͨ��
				          *  �������Щ״̬ʱ��Ҫͬ��
					*/
					
					if(DataUtil.isReady(workData)||DataUtil.isPause(workData)){
						
						DataUtil.saveUserRelationInformation(originalChargerId, newChargerId, IDBConstants.COLLECTION_USER_WORK_IN_CHARGED, id);
	
						// ͬ�������������Ϣ ɾ��ԭ�е�
						BasicDBList resourceList = (BasicDBList) workData.get(IDBConstants.FIELD_WORK_RESOURCE);
	
						DataUtil.saveUserWorkAndProjectInformation(originalResourceList, null, IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED, id);
						DataUtil.saveUserWorkAndProjectInformation(null, resourceList, IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED, id);
					}
					//*******************************************************************************************************
				}

			} else if (DataUtil.isDocumentObject(currentSelection)) {
				Object editorId = currentSelection.getValue(IDBConstants.FIELD_SYSTEM_EDITOR);
				if (editorId != null) {
					SingleObjectEditorDialog.OPEN(view.getSite().getShell(), (String) editorId, input);
				}
			}
		}
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
		action.setEnabled(currentSelection != null);

		if (!DataUtil.isWorkObject(currentSelection) && !DataUtil.isDocumentObject(currentSelection)) {
			action.setEnabled(false);
		}

	}
}
