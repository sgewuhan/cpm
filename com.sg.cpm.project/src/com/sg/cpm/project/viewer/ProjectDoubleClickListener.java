package com.sg.cpm.project.viewer;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;

public class ProjectDoubleClickListener implements IDoubleClickListener {

	public ProjectDoubleClickListener() {
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
//		ISelection selection = event.getSelection();
//		if (selection != null && !selection.isEmpty()) {
//			Object element = ((IStructuredSelection) selection).getFirstElement();
//
//			// 权限处理
//			ActiveObject ao = (ActiveObject) Platform.getAdapterManager().getAdapter(element, ActiveObject.class);
//
//			// 当前选择的对象是项目
//
//			if (ao != null) {
//				hasUpdateAuthority = UserSessionContext.hasAuthority(new String[] { UserSessionContext.UPDATE_METHOD }, ao);
//				if (!hasUpdateAuthority) {
//					hasReadAuthority = UserSessionContext.hasAuthority(new String[] { UserSessionContext.READ_METHOD }, ao);
//				} else {
//					hasReadAuthority = true;
//				}
//
//				if (hasReadAuthority) {
//					ISingleObjectEditorInput input = (ISingleObjectEditorInput) Platform.getAdapterManager().getAdapter(element, ISingleObjectEditorInput.class);
//					if(input!=null){
//						SingleObjectEditorDialog.OPEN(UIConstants.EDITOR_PROJECT_EDIT,input);
//					}
//				}
//			}
//		}	
		
	
	}

}
