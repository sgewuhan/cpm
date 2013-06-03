package com.sg.cpm.project.actions.wbs;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class CreateWork extends WBSActions {

	private EditorConfiguration editorConfiguration;

	@Override
	public void run(IAction action) {
		/*
		 * ��������ʱ����Ҫ���ݵ�ʱ��Ŀ��״̬�������Լ���״̬
		 *  �����Ŀ״̬��׼���������״̬��NULL
		 *  �����Ŀ״̬�ǽ��У������״̬��׼����ͬʱ��Ҫ����֪ͨ�� 
		 *  �����Ŀ״̬����ͣ�������״̬����ͣ��ͬʱ��Ҫ����֪ͨ��
		 *  �����Ŀ״̬����ֹ����ɣ������½�����
		 */
		ObjectId projectId = project.getSystemId();
		DBObject data = DataUtil.getDataObject(IDBConstants.COLLECTION_PROJECT, projectId);
		
		//************************************************************************************************************
		//ֻ����Ŀ�������Ŀ����Ա��Ȩ
		if(!DataUtil.isProjectManager(data)
				&&!DataUtil.isProjectAdmin(data)){
			MessageDialog.openWarning(view.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			return;
		}
		//************************************************************************************************************
		
		
		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK));

		// ����wbsparent��ֵ
		ObjectId parentId = (ObjectId) currentSelection.getValue(IDBConstants.FIELD_SYSID);

		so.setValue(IDBConstants.FIELD_WBSPARENT, parentId, null, false);

		// ���rootid
		so.setValue(IDBConstants.FIELD_ROOTID, projectId);
		
		//*************************************************************************************
		//������Ŀ״̬��������ĳ�ʼ״̬

		if(DataUtil.isInactive(data)){
			//������
		}else if(DataUtil.isReady(data)){
			so.setValue(IDBConstants.FIELD_PROCESS_STATUS,IDBConstants.VALUE_PROCESS_READY);
		}else if(DataUtil.isProcess(data)){
			so.setValue(IDBConstants.FIELD_PROCESS_STATUS,IDBConstants.VALUE_PROCESS_READY);
		}else if(DataUtil.isPause(data)){
			so.setValue(IDBConstants.FIELD_PROCESS_STATUS,IDBConstants.VALUE_PROCESS_PAUSE);
		}else{
			Assert.isNotNull(null, "ֻ�е���Ŀ����׼���������У���ͣʱ�����½�����");
		}
		//*************************************************************************************
		
		// ��õ�ǰ�����ϵ����seq
		QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_WORK_BY_WBSPARENT);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(IDBConstants.FIELD_WBSPARENT, parentId);
		exp.passParamValueMap(parameters);
		exp.setSortFieldsFromString(IDBConstants.FIELD_WBSSEQ + ",-1");
		exp.setSkipAndLimit(null, "1");
		exp.setReturnFieldsFromString(IDBConstants.FIELD_WBSSEQ + ",1");

		DBCursor cursor = exp.run();
		int nextVal = 1;// ������Ŵ�1��ʼ
		if (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			Object maxSeq = dbo.get(IDBConstants.FIELD_WBSSEQ);
			if (maxSeq instanceof Number) {
				nextVal = ((Number) maxSeq).intValue() + 1;
			}
		}

		so.setValue(IDBConstants.FIELD_WBSSEQ, nextVal, null, false);

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);

		ISingleObjectEditorDialogCallback saveCallback = new SingleObjectEditorDialogCallback() {

			// ��Ҫ����ѡ�����������ˣ����Ҹ�����ĸ�����������Դ�е����
			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				DBObject charger = (DBObject) input.getInputData().getValue(IDBConstants.FIELD_WORK_PM);

				if (charger == null) {
					return true;
				}

				ObjectId pmoid = (ObjectId) charger.get(IDBConstants.FIELD_SYSID);

				if (pmoid == null) {
					return true;
				}

				BasicDBList resource = (BasicDBList) input.getInputData().getValue(IDBConstants.FIELD_WORK_RESOURCE);
				if (resource == null) {
					return true;
				}

				for (int i = 0; i < resource.size(); i++) {
					DBObject res = (DBObject) resource.get(i);
					if (pmoid.equals(res.get(IDBConstants.FIELD_SYSID))) {
						resource.remove(res);
						return true;
					}
				}

				return super.saveBefore(input);
			}

		};
		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(view.getSite().getShell(), UIConstants.EDITOR_WORK_CREATE,
				editInput, saveCallback, true);

		int ok = soed.open();

		if (ok == SingleObjectEditorDialog.OK) {

			// ͬ���û����� ͬ�������������Ϣ
			DBObject workData = so.getData();
			ObjectId id = (ObjectId) workData.get(IDBConstants.FIELD_SYSID);

			ObjectId newChargerId = null;
			if (workData != null) {
				DBObject newCharger = (DBObject) workData.get(IDBConstants.FIELD_WORK_PM);
				newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
			}


			// ͬ�������������Ϣ
			//*******************************************************************************************************
			/*
			 * �����Ŀ״̬�ǽ��У������״̬��׼����ͬʱ��Ҫ����֪ͨ�� 
		          *  �����Ŀ״̬����ͣ�������״̬����ͣ��ͬʱ��Ҫ����֪ͨ��
		          *  �������Щ״̬ʱ��Ҫ֪ͨ��
			*/
			
			if(DataUtil.isReady(workData)||DataUtil.isPause(workData)){
				
				DataUtil.saveUserRelationInformation(null, newChargerId, IDBConstants.COLLECTION_USER_WORK_IN_CHARGED, id);
				
				BasicDBList resourceList = (BasicDBList) workData.get(IDBConstants.FIELD_WORK_RESOURCE);
				DataUtil.saveUserWorkAndProjectInformation(null, resourceList, IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED, id);
			}
			//*******************************************************************************************************

			
			// ����
			currentSelection.createChild(IDBConstants.EXP_CASCADE_SO_WBS, soed.getInputData().getData(),
					DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK));

			currentSelection.sortChildren(DataUtil.getWBSSorter());

			view.getViewer().refresh(currentSelection, false);

			view.getViewer().expandToLevel(currentSelection, 1);

		}
	}

	@Override
	public void init(IViewPart view) {

		editorConfiguration = Widget.getSingleObjectEditorConfiguration(UIConstants.EDITOR_WORK_CREATE);

		super.init(view);
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

		if (DataUtil.isDocumentObject(currentSelection)) {
			action.setEnabled(false);
		}


	}
}