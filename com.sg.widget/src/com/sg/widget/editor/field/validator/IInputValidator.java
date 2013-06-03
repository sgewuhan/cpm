package com.sg.widget.editor.field.validator;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.db.model.ISingleObject;
import com.sg.widget.configuration.FieldConfiguration;

public interface IInputValidator {

	boolean validate(ISingleObject data, FieldConfiguration field, Object valueForUpdate, IMessageManager messageManager, Control control);

}
