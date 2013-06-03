package com.sg.widget.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.sg.widget.configuration.PageConfiguration;

public abstract class Webpage implements IPageDelegator, IFormPart {

	private Browser browser;
	private ISingleObjectEditorInput input;

	public Webpage() {
	}

	@Override
	public Composite createPageContent(Composite parent,
			ISingleObjectEditorInput input, PageConfiguration conf) {
	    browser = new Browser( parent, SWT.NONE );
	    this.input = input;
	    browser.setUrl(getURL(input));
		
		return browser;
	}

	protected abstract String getURL(ISingleObjectEditorInput input);

	@Override
	public IFormPart getFormPart() {
		return this;
	}

	@Override
	public void initialize(IManagedForm form) {
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
		
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	    browser.setUrl(getURL(input));
	}

}
