package com.sg.widget.editor.field.actions;

import org.eclipse.ui.IEditorInput;

import com.sg.widget.editor.field.AbstractFieldPart;

public interface IFieldActionHandler {

	Object run(AbstractFieldPart abstractFieldPart, IEditorInput input);

}
