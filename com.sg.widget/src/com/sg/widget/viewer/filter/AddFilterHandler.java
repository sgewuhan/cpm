package com.sg.widget.viewer.filter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


public class AddFilterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof ISimpleFilterable) {
			SimpleFilterConditionDialog sfc = new SimpleFilterConditionDialog(
					HandlerUtil.getActiveShell(event),
					((ISimpleFilterable) part).getFilterColumnNameList(),
					((ISimpleFilterable) part).getFilterColumnLabelList(),
					((ISimpleFilterable) part).getFilterColumnTypeList());
			sfc.open();
			SimpleFilterCondition condition = sfc.getResult();
			((ISimpleFilterable)part).addSimpleFilter(condition);
		}
		return null;
	}

}
