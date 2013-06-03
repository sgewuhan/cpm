package com.sg.widget.editor.saveHandler;

import org.eclipse.core.runtime.IProgressMonitor;

import com.sg.widget.editor.ISingleObjectEditorInput;


public interface IEditorSaveHandler {

	public boolean doSave(ISingleObjectEditorInput input, IProgressMonitor monitor);

}
