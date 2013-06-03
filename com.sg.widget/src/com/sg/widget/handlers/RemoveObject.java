package com.sg.widget.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sg.db.model.ISingleObject;

public class RemoveObject extends AbstractHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection sel = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if(sel!=null){
			Iterator iter = sel.iterator();
			while(iter.hasNext()){
				Object element = iter.next();
				if(element instanceof ISingleObject){
					ISingleObject so = (ISingleObject)element;
					try{
						so.remove();
					}catch(Exception e){
						
					}
				}
			}
		}
		
		return null;
	}

}
