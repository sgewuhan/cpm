package com.sg.widget.editor.field.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldActionConfiguration;
import com.sg.widget.editor.field.AbstractFieldPart;

public class ClearSelectionAction extends FieldActionConfiguration {



	public ClearSelectionAction() {
		super();
	}

	@Override
	public String getId() {
		return WidgetConstants.CLEAR_ACTION_ID;
	}

	@Override
	public String getName() {
		return "清除所选内容";
	}

	@Override
	public Image getImage() {
		return Widget.getImage(IWidgetImage.IMG_CLEAR16);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Widget.getImageDescriptor(IWidgetImage.IMG_CLEAR16);
	}

	@Override
	public Object run(AbstractFieldPart abstractFieldPart, IEditorInput input) {
		abstractFieldPart.setValue(null);
		abstractFieldPart.updateDataValueAndPresent();
		return null;
	}




	

}
