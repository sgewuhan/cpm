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
		// 保存项目数据
		ISingleObject data = input.getInputData();
		if(input.isNewObject()){
			// 1. 项目的id需要自动生成
			String number = new DecimalFormat("000000").format(Util.getIncreasedID(DBConstants.getIDSCollection(),
					IDBConstants.COLLECTION_PROJECT));
			data.setValue(IDBConstants.FIELD_ID, number, null, true);
			// 2. 保存用户创建信息
			DataUtil.setSystemCreateInfo(data);
		}else{

			DataUtil.setSystemModifyInfo(data);
		}

		return false;
	}

}
