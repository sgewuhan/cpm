package com.sg.common.db;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.saveHandler.IEditorSaveHandler;

public class StandloneWorkSaveHandler implements IEditorSaveHandler {

	public StandloneWorkSaveHandler() {

	}

	@Override
	public boolean doSave(ISingleObjectEditorInput input, IProgressMonitor monitor) {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		ISingleObject so = input.getInputData();
		DBObject workData = so.getData();
		

		// 是否直接启动任务
		boolean q = MessageDialog.openQuestion(shell, UIConstants.TEXT_CREATE_WORK, UIConstants.MESSAGE_START_WORK_IMMEDIETELY);
		if (q) {
			BusinessService.getWorkService().createWork(workData,true);
		} else {
			BusinessService.getWorkService().createWork(workData,false);
		}


		return true;
	}


}
