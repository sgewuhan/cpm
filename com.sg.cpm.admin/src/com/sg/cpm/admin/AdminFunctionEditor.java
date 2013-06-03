package com.sg.cpm.admin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.sg.common.ui.UIConstants;
import com.sg.widget.part.IUpdateablePart;

public abstract class AdminFunctionEditor extends EditorPart implements IPerspectiveListener, IUpdateablePart{

	@Override
	public void createPartControl(Composite parent) {
		getSite().getWorkbenchWindow().addPerspectiveListener(this);
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().removePerspectiveListener(this);
		super.dispose();
	}

	@Override
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		//如果不是自己所在的透视图，关闭之
		if(!UIConstants.PERSPECTIVE_ID_ADMIN.equals(perspective.getId())){
			page.closeAllEditors(true);
		}
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setSite(site);
	}
	
	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {
	}


	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
	}
	
	@Override
	public boolean needUpdate() {
		return true;
	}

}
