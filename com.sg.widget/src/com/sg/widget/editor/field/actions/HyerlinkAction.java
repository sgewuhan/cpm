package com.sg.widget.editor.field.actions;

import org.eclipse.rap.rwt.widgets.ExternalBrowser;
import org.eclipse.ui.IEditorInput;

import com.sg.widget.editor.field.AbstractFieldPart;

public class HyerlinkAction implements IFieldActionHandler {

	@Override
	public Object run(AbstractFieldPart abstractFieldPart, IEditorInput input) {
		Object value = abstractFieldPart.getValue();
		if (value != null){
			String url = value.toString();
			ExternalBrowser.open(input.getName(), url, ExternalBrowser.LOCATION_BAR
					| ExternalBrowser.NAVIGATION_BAR | ExternalBrowser.STATUS);
		}
		return null;
	}

}
