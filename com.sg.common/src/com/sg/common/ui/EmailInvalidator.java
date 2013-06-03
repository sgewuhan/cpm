package com.sg.common.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessageManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.editor.field.validator.IInputValidator;

public class EmailInvalidator implements IInputValidator {

	private DBCollection cUser;

	public EmailInvalidator() {
		cUser = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER);
	}

	@Override
	public boolean validate(ISingleObject data, FieldConfiguration field, Object valueForUpdate, IMessageManager messageManager,
			Control control) {
		if (valueForUpdate != null) {
			// 验证必须是一个邮箱地址
			int ix = valueForUpdate.toString().indexOf("@");
			if(ix<0||(!valueForUpdate.toString().substring(ix).contains("."))){//包含@符号
				messageManager.addMessage(field.getId(), UIConstants.MESSAGE_INVALID_EMAIL_ADDRESS, null,
						IMessageProvider.ERROR, control);
				return false;
			}
			
			//邮箱不可以重复
			Object uoid = data.getValue(IDBConstants.FIELD_SYSID);
			
			DBCursor cur = cUser.find(new BasicDBObject()
							.append(IDBConstants.FIELD_SYSID, new BasicDBObject().append("$ne", uoid))
							.append(IDBConstants.FIELD_EMAIL, valueForUpdate) ,
						new BasicDBObject()
							.append(IDBConstants.FIELD_SYSID, 1));
			if (cur.hasNext()) {
				messageManager.addMessage(field.getId(), UIConstants.MESSAGE_EMAIL_DUPLICATED, null, IMessageProvider.ERROR, control);
				return false;
			}
		}//
		
		return true;
	}

}
