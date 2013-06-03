package com.sg.cpm.project.actions.wbs;

import java.util.Iterator;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class CreateDoc extends WBSActions {

	private EditorConfiguration editorConfiguration;
	
	@Override
	public void run(IAction action) {
		ObjectId projectId = project.getSystemId();
		DBObject projectData = DataUtil.getDataObject(IDBConstants.COLLECTION_PROJECT, projectId);

		//************************************************************************************************************
		//只有项目经理和项目管理员有权
		if(!DataUtil.isProjectManager(projectData)
				&&!DataUtil.isProjectAdmin(projectData)){
			MessageDialog.openWarning(view.getSite().getShell(), UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			return;
		}
		//************************************************************************************************************
		
		

		SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT));

		// 给出wbsparent的值
		ObjectId parentId = (ObjectId) currentSelection.getValue(IDBConstants.FIELD_SYSID);
		
		so.setValue(IDBConstants.FIELD_WBSPARENT, parentId, null, false);
		
		so.setValue(IDBConstants.FIELD_WBSSEQ, 1, null, false);
		
		//添加rootid
		so.setValue(IDBConstants.FIELD_ROOTID, projectId);

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);

		//******************************************************************************************
		//在任务上创建的文档需要挂在项目的文件夹中
		//获得项目的文件夹
		ObjectId rootFolder = (ObjectId) projectData.get(IDBConstants.FIELD_FOLDER_ROOT);
		Assert.isNotNull(rootFolder);
		so.setValue(IDBConstants.FIELD_FBSPARENT, rootFolder);
		
		//******************************************************************************************
		
		
		
		ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback(){

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				//更改文件名
				Set<EditorConfiguration> editorSet = Widget.listSingleObjectEditorConfigurationByCollection(IDBConstants.COLLECTION_DOCUMENT);
				String id = (String) input.getInputData().getValue(IDBConstants.FIELD_SYSTEM_EDITOR);
				Iterator<EditorConfiguration> iter = editorSet.iterator();
				while(iter.hasNext()){
					EditorConfiguration ec = iter.next();
					if(ec.getId().equals(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE)){//排除基本文档
						continue;
					}
					if(ec.getId().equals(id)){
						input.getInputData().setValue(IDBConstants.FIELD_DESC, ec.getName(), null, false);
					}
				}

				
				return super.saveBefore(input);
			}
			
		};

		
		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(view.getSite().getShell(), UIConstants.EDITOR_DELIVERDOCUMENT_CREATE, editInput, call,true);
		
		int ok = soed.open();
		
		if (ok == SingleObjectEditorDialog.OK) {
			
			currentSelection.createChild(IDBConstants.EXP_CASCADE_SO_WBS_WITH_DOC, soed.getInputData().getData(),
					DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT));
			
			currentSelection.sortChildren(DataUtil.getWBSSorter());

			view.getViewer().refresh(currentSelection, false);
			
			view.getViewer().expandToLevel(currentSelection, 1);
			
		}
	}

	@Override
	public void init(IViewPart view) {
		
		editorConfiguration = Widget.getSingleObjectEditorConfiguration(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE);
		
		super.init(view);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(!view.isShowDocument()){
			action.setEnabled(false);
			return;
		}
		
		//编辑可用性
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		
		//考虑权限
		action.setEnabled(currentSelection != null );
		
		if(!DataUtil.isWorkObject(currentSelection)){
			action.setEnabled(false);
		}
		
		
	}
}