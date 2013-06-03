package com.sg.vault.actions;

import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.widget.actions.Edit;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class EditFolderStructureItem extends Edit{

	protected boolean openInEditor() {
		return DataUtil.isDocumentObject(selected);
	}

	@Override
	protected ISingleObjectEditorInput getInput() {
		if(DataUtil.isDocumentObject(selected)){
			String editorId = (String) selected.getValue(IDBConstants.FIELD_SYSTEM_EDITOR);
			SingleObjectEditorInput input = new SingleObjectEditorInput(editorId,selected);
			return input;
		}else{
			SingleObjectEditorInput input = new SingleObjectEditorInput(selected);
			return input;
		}
	}
	
}