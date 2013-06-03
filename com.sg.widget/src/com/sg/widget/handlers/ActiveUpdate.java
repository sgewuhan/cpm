package com.sg.widget.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sg.widget.part.IUpdateablePart;


public class ActiveUpdate extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IUpdateablePart part = (IUpdateablePart) HandlerUtil.getActivePart(event);
		part.update();
		return null;
	}


}
