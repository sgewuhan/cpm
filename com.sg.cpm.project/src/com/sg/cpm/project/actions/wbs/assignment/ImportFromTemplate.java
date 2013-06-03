package com.sg.cpm.project.actions.wbs.assignment;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.ISingleObject;
import com.sg.resource.Resource;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;

public class ImportFromTemplate extends Action {

	private TemplateMenu assignmentControl;

	public ImportFromTemplate(TemplateMenu assignmentControl) {
		setText(UIConstants.TEXT_IMPORT_PROJECT_TEMPLATE);
		setImageDescriptor(Resource.getImageDescriptor(Resource.V_IMPORT_PROJECT32));
		this.assignmentControl = assignmentControl;
	}

	@Override
	public void run() {
		Shell shell = assignmentControl.getView().getSite().getShell();

		//只有项目经理和项目管理员有权
		if(!DataUtil.isProjectManager(assignmentControl.getMasterProject().getData())
				&&!DataUtil.isProjectAdmin(assignmentControl.getMasterProject().getData())){
			MessageDialog.openWarning(shell, UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			return;
		}
		
		
		// 首先显示项目模板选择的对话框
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(shell,
				UIConstants.EDITOR_SELECT_TEMPLATE, null, new SingleObjectEditorDialogCallback(){

					@Override
					public boolean needSave() {
						return false;
					}
					
			
		}, false);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject so = soed.getInputData();
			DBObject template = (DBObject) so.getValue(IDBConstants.FIELD_TEMPLATE);
			DataUtil.importTemplateToProject( (ObjectId)template.get(IDBConstants.FIELD_SYSID), (ObjectId) assignmentControl.getMasterProject().getValue(IDBConstants.FIELD_SYSID));
		
			MessageDialog.openInformation(shell, UIConstants.TEXT_IMPORT_PROJECT_TEMPLATE, UIConstants.MESSAGE_IMPORT_PROJECT_TEMPLATE_FINISHED);
			assignmentControl.getView().update();
		}
		
		
	}
}
