package com.sg.widget.model;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mongodb.DBCollection;
import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class SingleObjectAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ISingleObject) {
			ISingleObject so = (ISingleObject) adaptableObject;
			if (adapterType == ISingleObjectEditorInput.class) {
				DBCollection collection = so.getCollection();
				if (collection != null) {
					EditorConfiguration ce = Widget.getSingleObjectEditorConfigurationByCollection(collection.getName());
					if (ce != null) {
						return new SingleObjectEditorInput(so);
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { ISingleObjectEditorInput.class };
	}

}
