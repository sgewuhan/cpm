package com.sg.widget.viewer.search;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


public class SearchHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if(part instanceof ISearchable){
			SearchConditionDialog scd = new SearchConditionDialog(HandlerUtil.getActiveShell(event));
			scd.open();
			((ISearchable)part).search(scd.getValue(), scd.getSearchStyle());
		}
		return null;
	}

}
