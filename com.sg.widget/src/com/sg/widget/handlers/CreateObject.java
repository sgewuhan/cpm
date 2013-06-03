package com.sg.widget.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sg.widget.editor.SingleObjectEditor;
import com.sg.widget.part.IDocumentCreator;

public class CreateObject extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof IDocumentCreator) {
			IDocumentCreator documentCreator = (IDocumentCreator) part;

			String editorConf = documentCreator.getEditorConfigruation();
			SingleObjectEditor.CREATE(editorConf);
		}
		return null;
	}

}
