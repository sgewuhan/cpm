package com.sg.cpm.project.actions.obs;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import com.sg.common.db.DataUtil;
import com.sg.db.model.CascadeObject;


public class Edit extends OBSActions {
	@Override
	public void run(IAction action) {
		Shell shell = view.getSite().getShell();
		
		//************************************************************************************************************
		//ֻ����Ŀ�������Ŀ����Ա��Ȩ
		
		if (!hasAuth())
			return;

		//************************************************************************************************************

		
		DataUtil.editOBSItemUI(shell,currentSelection);
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
			//���Ƕ������ͣ��û������²�����
			if(DataUtil.isUserObject(currentSelection)){
				enable = false;
			}
		}
		
		//����Ȩ��
		action.setEnabled(enable);
	}
}
