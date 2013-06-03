package com.sg.document.editor;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.field.defaultvalue.IDefaultValueProvider;


public class DefualtProjectName implements IDefaultValueProvider {

	public DefualtProjectName() {

	}

	@Override
	public Object getDefaultValue(ISingleObject data, String key) {
		//从工作文档中获取项目的名称
		//_editor:com.sg.cpm.editor.JZ-QR-XG003A
		//wbsparent 为当前工作
		//templatetype 为document
		DBObject document = BusinessService.getDocumentService().getWorkDocument((ObjectId)data.getValue(IDBConstants.FIELD_SYSID),"com.sg.cpm.editor.JZ-QR-XG003A");
		if(document==null){
			return null;
		}
		return document.get(IDBConstants.FIELD_DESC);
	}

}
