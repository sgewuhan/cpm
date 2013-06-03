package com.sg.widget.dialog;

import com.sg.widget.editor.ISingleObjectEditorInput;

public interface ISingleObjectEditorDialogCallback {

	public abstract boolean okPressed();

	public abstract boolean saveBefore(ISingleObjectEditorInput input) ;

	public abstract boolean saveAfter(ISingleObjectEditorInput input) ;

	public abstract boolean needSave();

}