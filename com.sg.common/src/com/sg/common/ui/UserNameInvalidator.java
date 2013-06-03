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

public class UserNameInvalidator implements IInputValidator {

	private DBCollection cUser;

	public UserNameInvalidator() {
		cUser = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER);
	}

	@Override
	public boolean validate(ISingleObject data, FieldConfiguration field, Object valueForUpdate, IMessageManager messageManager,
			Control control) {
		if (valueForUpdate != null) {
			// 验证用户名不可包含@
			if (valueForUpdate.toString().contains("@")) {
				messageManager.addMessage(field.getId(), UIConstants.MESSAGE_USERNAME_INVALID_CANNOT_CONTAIN_AT, null,
						IMessageProvider.ERROR, control);
				return false;
			}

			// 验证用户名不可全部是数字
			try {
				Integer.parseInt(valueForUpdate.toString());
				messageManager.addMessage(field.getId(), UIConstants.MESSAGE_USERNAME_INVALID_CANNOT_ALL_NUMBER, null,
						IMessageProvider.ERROR, control);
				return false;
			} catch (Exception e) {
			}

			// 验证用户名不得重复
			Object uoid = data.getValue(IDBConstants.FIELD_SYSID);
			
			DBCursor cur = cUser.find(new BasicDBObject()
							.append(IDBConstants.FIELD_SYSID, new BasicDBObject().append("$ne", uoid))
							.append(IDBConstants.FIELD_DESC, valueForUpdate.toString().toLowerCase()) ,
						new BasicDBObject()
							.append(IDBConstants.FIELD_SYSID, 1));
			if (cur.hasNext()) {
				messageManager.addMessage(field.getId(), UIConstants.MESSAGE_USERNAME_DUPLICATED, null, IMessageProvider.ERROR, control);
				return false;
			}

		}//
		return true;
	}

}
