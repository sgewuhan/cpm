package com.sg.cpm.project.actions.wbs.assignment;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.sg.common.db.DataUtil;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;

public class AssignmentAuto extends Action {

	private TemplateMenu assignmentControl;

	public AssignmentAuto(TemplateMenu assignmentControl) {
		setText(UIConstants.TEXT_AUTO_ASSIGNMENT);
		setImageDescriptor(Resource.getImageDescriptor(Resource.AUTO_ASSIGNMENT32));
		this.assignmentControl = assignmentControl;
	}

	@Override
	public void run() {
		// 自动指派
		SingleObject project = assignmentControl.getMasterProject();
		
		Shell shell = assignmentControl.getView().getSite().getShell();
		
		//只有项目经理和项目管理员有权
		if(!DataUtil.isProjectManager(assignmentControl.getMasterProject().getData())
				&&!DataUtil.isProjectAdmin(assignmentControl.getMasterProject().getData())){
			MessageDialog.openWarning(shell, UIConstants.TEXT_PROJECT_CONTROL, UIConstants.MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS);
			return;
		}
		
		
		DataUtil.autoAssignment(project.getSystemId());

		MessageDialog.openInformation(shell, UIConstants.TEXT_AUTO_ASSIGNMENT,
				UIConstants.MESSAGE_AUTO_ASSIGNMENT_FINISHED);

		assignmentControl.getView().update();
		super.run();
	}

}
