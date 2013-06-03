package com.sg.cpm.admin.workflow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

import com.sg.cpm.admin.Activator;
import com.sg.cpm.admin.AdminFunctionEditor;


public class Guvnor extends AdminFunctionEditor {

	
	private Browser browser;

	@Override
	public void createPartControl(Composite parent) {

		browser = new Browser(parent, SWT.NONE);
		
		update();
		super.createPartControl(parent);
	}

	@Override
	public void update() {
		browser.setUrl(Activator.getGuvnorHost());
	}

	
	
}