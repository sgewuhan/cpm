package com.sg.widget.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditor;

public class EditObject extends AbstractHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection sel = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if(sel!=null){
			Iterator iter = sel.iterator();
			while(iter.hasNext()){
				Object element = iter.next();
				
				ISingleObjectEditorInput input = (ISingleObjectEditorInput) Platform.getAdapterManager().getAdapter(
						element, ISingleObjectEditorInput.class);
				
				if(input == null){
					continue;
				}
				
				SingleObjectEditor.OPEN(input);
			}
		}
		
		return null;
	}

}
