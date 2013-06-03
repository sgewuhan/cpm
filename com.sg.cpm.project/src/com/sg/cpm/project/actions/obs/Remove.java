package com.sg.cpm.project.actions.obs;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.sg.common.db.DataUtil;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.CascadeObject;


public class Remove extends OBSActions {
	@Override
	public void run(IAction action) {
		Shell shell = view.getSite().getShell();
		
		//************************************************************************************************************
		//ֻ����Ŀ�������Ŀ����Ա��Ȩ
		if (!hasAuth())
			return;

		//************************************************************************************************************

		boolean ok = MessageDialog.openQuestion(shell, UIConstants.TEXT_REMOVE, "" + currentSelection + UIConstants.MESSAGE_QUESTION_DELETE);
		if (!ok)
			return;
		currentSelection.remove(true);
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		//����ѡ���Ȩ��
		boolean enable = currentSelection != null ;
		if(enable){
			//���Ƕ������ͣ���Ŀ�����²�����
			if(DataUtil.isProjectTeamObject(currentSelection)){
				enable = false;
			}
		}
		
		//����Ȩ��
		action.setEnabled(enable);
	}
}
