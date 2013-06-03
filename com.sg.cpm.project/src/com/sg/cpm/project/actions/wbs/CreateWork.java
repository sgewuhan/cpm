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
		 * 创建任务时，需要根据当时项目的状态来设置自己的状态
		 *  如果项目状态是准备，任务的状态是NULL
		 *  如果项目状态是进行，任务的状态是准备（同时需要给出通知） 
		 *  如果项目状态是暂停，任务的状态是暂停（同时需要给出通知）
		 *  如果项目状态是终止和完成，不可新建任务
		 */
		ObjectId projectId = project.getSystemId();
		DBObject data = DataUtil.getDataObject(IDBConstants.COLLECTION_PROJECT, projectId);
		
		//************************************************************************************************************
		//只有项目经理和项目管理员有权
		if(!DataUtil.isProjectManager(data)
				&&!DataUtil.isProjectAdmin(data)){
			MessageDialog.openWarning(view.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			return;
		}
		//************************************************************************************************************
		
		
		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK));

		// 给出wbsparent的值
		ObjectId parentId = (ObjectId) currentSelection.getValue(IDBConstants.FIELD_SYSID);

		so.setValue(IDBConstants.FIELD_WBSPARENT, parentId, null, false);

		// 添加rootid
		so.setValue(IDBConstants.FIELD_ROOTID, projectId);
		
		//*************************************************************************************
		//根据项目状态设置任务的初始状态

		if(DataUtil.isInactive(data)){
			//不处理
		}else if(DataUtil.isReady(data)){
			so.setValue(IDBConstants.FIELD_PROCESS_STATUS,IDBConstants.VALUE_PROCESS_READY);
		}else if(DataUtil.isProcess(data)){
			so.setValue(IDBConstants.FIELD_PROCESS_STATUS,IDBConstants.VALUE_PROCESS_READY);
		}else if(DataUtil.isPause(data)){
			so.setValue(IDBConstants.FIELD_PROCESS_STATUS,IDBConstants.VALUE_PROCESS_PAUSE);
		}else{
			Assert.isNotNull(null, "只有当项目处于准备，进行中，暂停时可以新建任务");
		}
		//*************************************************************************************
		
		// 获得当前任务上的最大seq
		QueryExpression exp = DBActivator.getQueryExpression(IDBConstants.EXP_QUERY_WORK_BY_WBSPARENT);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(IDBConstants.FIELD_WBSPARENT, parentId);
		exp.passParamValueMap(parameters);
		exp.setSortFieldsFromString(IDBConstants.FIELD_WBSSEQ + ",-1");
		exp.setSkipAndLimit(null, "1");
		exp.setReturnFieldsFromString(IDBConstants.FIELD_WBSSEQ + ",1");

		DBCursor cursor = exp.run();
		int nextVal = 1;// 任务序号从1开始
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

			// 需要处理选择了任务负责人，并且该任务的负责人又在资源中的情况
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

			// 同步用户数据 同步负责的任务信息
			DBObject workData = so.getData();
			ObjectId id = (ObjectId) workData.get(IDBConstants.FIELD_SYSID);

			ObjectId newChargerId = null;
			if (workData != null) {
				DBObject newCharger = (DBObject) workData.get(IDBConstants.FIELD_WORK_PM);
				newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
			}


			// 同步参与的任务信息
			//*******************************************************************************************************
			/*
			 * 如果项目状态是进行，任务的状态是准备（同时需要给出通知） 
		          *  如果项目状态是暂停，任务的状态是暂停（同时需要给出通知）
		          *  其余的这些状态时不要通知的
			*/
			
			if(DataUtil.isReady(workData)||DataUtil.isPause(workData)){
				
				DataUtil.saveUserRelationInformation(null, newChargerId, IDBConstants.COLLECTION_USER_WORK_IN_CHARGED, id);
				
				BasicDBList resourceList = (BasicDBList) workData.get(IDBConstants.FIELD_WORK_RESOURCE);
				DataUtil.saveUserWorkAndProjectInformation(null, resourceList, IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED, id);
			}
			//*******************************************************************************************************

			
			// 后处理
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

		// 编辑可用性
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}

		// 考虑权限
		action.setEnabled(currentSelection != null);

		if (DataUtil.isDocumentObject(currentSelection)) {
			action.setEnabled(false);
		}


	}
}