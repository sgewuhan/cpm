package com.sg.document.editor;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;


public class DefualtProjectManager implements IDefaultValueProvider {

	public DefualtProjectManager() {

	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {

		//ȡ�������ϵĸ�������ΪĬ�ϵ���Ŀ������
		return data.getValue(IDBConstants.FIELD_WORK_PM);
	}

}
