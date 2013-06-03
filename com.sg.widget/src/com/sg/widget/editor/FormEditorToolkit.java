package com.sg.widget.editor;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class FormEditorToolkit {

	
	public static void decorateFormHeading(IManagedForm mform) {
		FormToolkit toolkit = mform.getToolkit();
		FormColors colors = toolkit.getColors();
		Color top = colors.createColor(
				IFormColors.H_GRADIENT_START, 236, 241, 255);
		Color bot = colors.createColor(IFormColors.H_GRADIENT_END,
				255, 255, 255);

		mform.getForm().getForm().setTextBackground(new Color[] { top, bot },
				new int[] { 80,80 }, true);
		
		mform.getForm().getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE1, bot);
		mform.getForm().getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE2, bot);
		mform.getForm().getForm().setHeadColor(IFormColors.H_HOVER_LIGHT, colors
				.getColor(IFormColors.H_HOVER_LIGHT));
		mform.getForm().getForm().setHeadColor(IFormColors.H_HOVER_FULL, colors
				.getColor(IFormColors.H_HOVER_FULL));
		mform.getForm().getForm().setHeadColor(IFormColors.TB_TOGGLE, colors
				.getColor(IFormColors.TB_TOGGLE));
		mform.getForm().getForm().setHeadColor(IFormColors.TB_TOGGLE_HOVER, colors
				.getColor(IFormColors.TB_TOGGLE_HOVER));
	}
}
