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
			String message = "您提交评审的项目文档不符合完整性要求，请检查以下的文件后重新提交。（您也可以随时在项目导航中核对是否满足完整性要求）";
			for(int i=0;i<unCompleteness.size();i++){
				DBObject doc = (DBObject) unCompleteness.get(i)[0];
				String name = (String) doc.get(IDBConstants.FIELD_DESC);
				String reason = (String) unCompleteness.get(i)[1];
				message = message +"\n"+"文档："+name+" 问题："+reason;
			}
			MessageDialog.openWarning(view.getSite().getShell(), "项目文档完整性检查", message);
			return;
		}
		MessageDialog.openInformation(view.getSite().getShell(), "项目文档完整性检查", "项目满足完整性检查的要求");
	}

	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
		if (!selection.isEmpty()) {
			currentSelection = (CascadeObject) ((IStructuredSelection) selection).getFirstElement();
		} else {
			currentSelection = null;
		}
		//考虑权限
		action.setEnabled(currentSelection != null );
	}

}
