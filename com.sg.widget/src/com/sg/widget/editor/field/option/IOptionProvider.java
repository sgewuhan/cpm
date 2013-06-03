package com.sg.widget.editor.field.option;

import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.resource.Enumerate;

public interface IOptionProvider {

	Enumerate getOption(ISingleObjectEditorInput input,ISingleObject data, String key, Object value);

}
