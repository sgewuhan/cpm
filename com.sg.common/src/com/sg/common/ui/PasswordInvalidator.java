package com.sg.common.ui;

import org.eclipse.core.internal.commands.util.Util;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.editor.field.validator.IInputValidator;

public class PasswordInvalidator implements IInputValidator {

	@Override
	public boolean validate(ISingleObject data, FieldConfiguration field, Object valueForUpdate, IMessageManager messageManager,
			Control control) {
		String key = field.getName();
		// 密码的长度验证
		if (!UserSessionContext.getSession().getUserName().equals("admin")) {
			boolean lengthEnough = false;
			if (valueForUpdate != null && valueForUpdate.toString().length() >= 8) {
				lengthEnough = true;
			}
			if (!lengthEnough) {
				messageManager.addMessage(field.getId(), UIConstants.MESSAGE_PASSWORD_NOT_ENOUGH_LENGTH, null, IMessageProvider.ERROR,
						control);
				return false;
			}
		}

		// 两次输入一致性检查
		boolean twiseInputIsSame = false;
		Object anothnerValue = null;
		if (IDBConstants.FIELD_PASSWORD.equals(key)) {
			anothnerValue = data.getValue(IDBConstants.FIELD_PASSWORD_REPEAT);
		} else if (IDBConstants.FIELD_PASSWORD_REPEAT.equals(key)) {
			anothnerValue = data.getValue(IDBConstants.FIELD_PASSWORD);
		}
		if (anothnerValue == null || Util.equals(valueForUpdate, anothnerValue)) {
			twiseInputIsSame = true;
		}
		if (!twiseInputIsSame) {
			messageManager.addMessage(field.getId(), UIConstants.MESSAGE_PASSWORD_NOT_MATCHED, null, IMessageProvider.ERROR, control);
			return false;
		}

		return true;
	}

}
