package com.sg.widget.dialog;

import com.sg.widget.editor.ISingleObjectEditorInput;

public class SingleObjectEditorDialogCallback implements ISingleObjectEditorDialogCallback {

	@Override
	public boolean okPressed() {
		return true;
	}

	@Override
	public boolean saveBefore(ISingleObjectEditorInput input) {
		return true;
	}

	@Override
	public boolean saveAfter(ISingleObjectEditorInput input) {
		return true;
	}

	@Override
	public boolean needSave() {
		return true;
	}

}
