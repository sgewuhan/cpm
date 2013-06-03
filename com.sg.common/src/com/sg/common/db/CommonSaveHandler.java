package com.sg.common.db;

import org.eclipse.core.runtime.IProgressMonitor;

import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.saveHandler.IEditorSaveHandler;

public class CommonSaveHandler implements IEditorSaveHandler {

	public CommonSaveHandler() {
	}

	@Override
	public boolean doSave(ISingleObjectEditorInput input, IProgressMonitor monitor) {
		// ������Ŀ����
		ISingleObject data = input.getInputData();
		if(input.isNewObject()){
			DataUtil.setSystemCreateInfo(data);
		}else{
			DataUtil.setSystemModifyInfo(data);
		}
		
		return false;
	}
	
	
	

}
