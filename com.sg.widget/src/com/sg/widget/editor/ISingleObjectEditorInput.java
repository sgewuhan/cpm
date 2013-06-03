package com.sg.widget.editor;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;

import com.sg.db.model.ISingleObject;
import com.sg.user.IAuthorityResponseReciever;
import com.sg.widget.configuration.EditorConfiguration;

public interface ISingleObjectEditorInput extends IEditorInput,IAuthorityResponseReciever {

	EditorConfiguration getConfig();

	ISingleObject getInputData();

	boolean isEditable();

	void save(IProgressMonitor monitor);

	boolean isNewObject();

	void setEditable(boolean b);
	

}
