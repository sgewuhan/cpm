package com.sg.common.db;

import java.text.DecimalFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com.sg.db.DBConstants;
import com.sg.db.Util;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.saveHandler.IEditorSaveHandler;

public class IDAutoGenCommonSaveHandler implements IEditorSaveHandler {

	@Override
	public boolean doSave(ISingleObjectEditorInput input, IProgressMonitor monitor) {
		// ������Ŀ����
		ISingleObject data = input.getInputData();
		if(input.isNewObject()){
			// 1. ��Ŀ��id��Ҫ�Զ�����
			String number = new DecimalFormat("000000").format(Util.getIncreasedID(DBConstants.getIDSCollection(),
					IDBConstants.COLLECTION_PROJECT));
			data.setValue(IDBConstants.FIELD_ID, number, null, true);
			// 2. �����û�������Ϣ
			DataUtil.setSystemCreateInfo(data);
		}else{

			DataUtil.setSystemModifyInfo(data);
		}

		return false;
	}

}
