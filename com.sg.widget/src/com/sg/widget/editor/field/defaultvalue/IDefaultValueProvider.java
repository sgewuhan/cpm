package com.sg.widget.editor.field.defaultvalue;

import com.sg.db.model.ISingleObject;

public interface IDefaultValueProvider {

	Object getDefaultValue(ISingleObject data, String key);

}
