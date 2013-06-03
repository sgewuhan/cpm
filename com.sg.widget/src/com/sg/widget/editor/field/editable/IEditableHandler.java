package com.sg.widget.editor.field.editable;

import com.sg.db.model.ISingleObject;


public interface IEditableHandler {

	boolean isEditable(ISingleObject data, String key, Object value);

}
