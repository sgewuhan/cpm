package com.sg.cpm.project.actions.wbs;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.CascadeObject;

public class CheckDocument extends WBSActions {

	@Override
	public void run(IAction action) {
		ObjectId projectId = project.getSystemId();
		List<Object[]> unCompleteness = BusinessService.getWorkService().completenessCheck(projectId);
		if(unCompleteness.size()>0){
			String message = "���ύ�������Ŀ�ĵ�������������Ҫ���������µ��ļ��������ύ������Ҳ������ʱ����Ŀ�����к˶��Ƿ�����������Ҫ��";
			for(int i=0;i<unCompleteness.size();i++){
				DBObject doc = (DBObject) unCompleteness.get(i)[0];
				String name = (String) doc.get(IDBConstants.FIELD_DESC);
				String reason = (String) unCompleteness.get(i)[1];
				message = message +"\n"+"�ĵ���"+name+" ���⣺"+reason;
			}
			MessageDialog.openWarning(view.getSite().getShell(), "��Ŀ�ĵ������Լ��", message);
			return;
		}
		MessageDialog.openInformation(view.getSite().getShell(), "��Ŀ�ĵ������Լ��", "��Ŀ���������Լ���Ҫ��");
	}

	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		//����Ȩ��
		action.setEnabled(currentSelection != null );
	}

}
