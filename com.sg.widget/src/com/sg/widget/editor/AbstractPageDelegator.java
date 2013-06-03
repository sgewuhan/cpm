package com.sg.widget.editor;

import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;


public abstract class AbstractPageDelegator implements IPageDelegator, IFormPart {

	private IManagedForm form;
	private boolean dirty;

	

	@Override
	public IFormPart getFormPart() {

		return this;
	}

	@Override
	public void initialize(IManagedForm form) {

		this.form = form;
	}
	
	protected void fireValueChanged(){
		if(form!=null){
			form.dirtyStateChanged();
		}
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isDirty() {

		return this.dirty;
	}

	protected void setDirty(boolean b){
		this.dirty = b;
		fireValueChanged();
	}
	
	@Override
	public void commit(boolean onSave) {

		dirty = false;
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

	}
}
