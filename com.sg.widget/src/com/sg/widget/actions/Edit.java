package com.sg.widget.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sg.db.model.ISingleObject;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditor;

public class Edit implements IWorkbenchWindowActionDelegate {

	protected ISingleObject selected;
	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		ISingleObjectEditorInput input = getInput();
		if(input!=null){
			if(openInEditor()){
				SingleObjectEditor.OPEN(input);
			}else{
				SingleObjectEditorDialog.OPEN(window.getShell(), null, input);
			}
		}
	}
	
	protected boolean openInEditor() {
		return false;
	}

	protected ISingleObjectEditorInput getInput(){
		if(selected == null) return null;
		return (ISingleObjectEditorInput) Platform.getAdapterManager().getAdapter(
				selected, ISingleObjectEditorInput.class);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection == null || selection.isEmpty()) {
			selected = null;
			return;
		} else {
			Object element = ((StructuredSelection) selection).getFirstElement();
			if(element instanceof ISingleObject){
				selected  = (ISingleObject) element;
			}else{
				selected = null;
			}
		}
		updateActionStatus(action);
	}

	private void updateActionStatus(IAction action) {
		if (selected == null) {
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}

	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}