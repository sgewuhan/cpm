package com.sg.widget.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;

import com.sg.widget.configuration.PageConfiguration;

public interface IPageDelegator {

	Composite createPageContent(Composite parent, ISingleObjectEditorInput input, PageConfiguration conf);

	IFormPart getFormPart();

}
