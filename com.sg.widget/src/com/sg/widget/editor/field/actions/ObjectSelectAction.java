package com.sg.widget.editor.field.actions;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldActionConfiguration;
import com.sg.widget.editor.field.AbstractFieldPart;
import com.sg.widget.resource.Enumerate;

public class ObjectSelectAction extends FieldActionConfiguration {

	@Override
	public String getName() {
		return "Ñ¡Ôñ..";
	}

	@Override
	public String getId() {
		return WidgetConstants.SELECT_ACTION_ID;
	}

	@Override
	public Image getImage() {
		return Widget.getImage(IWidgetImage.IMG_SELECTOR16);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Widget.getImageDescriptor(IWidgetImage.IMG_SELECTOR16);
	}

	@Override
	public Object run(AbstractFieldPart abstractFieldPart, IEditorInput input) {
		String label = abstractFieldPart.getField().getLabel();
		List<Enumerate> list = abstractFieldPart.getOption()
				.getChildren();
		Enumerate option = FilteredOptionsSelector.openSelector(Display
				.getCurrent().getActiveShell(), "Ñ¡Ôñ" + label, list);
		abstractFieldPart.setValueFromOption(option);
		abstractFieldPart.updateDataValueAndPresent();
		return option;
	}

}
