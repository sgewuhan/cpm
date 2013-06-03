package com.sg.widget.editor.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import com.sg.widget.configuration.FieldConfiguration;

public class TextLimitToolTipsControl implements FocusListener, ModifyListener,
		DisposeListener {

	private ToolTip textLimitTooltips;
	private int textLimit;
	private Control control;
	private boolean showed = false;

	public TextLimitToolTipsControl(AbstractFieldPart fieldPart) {
		FieldConfiguration field = fieldPart.getField();
		control = fieldPart.getControl();
		textLimit = field.getTextLimit();

		if (textLimit != 0) {
			textLimitTooltips = new ToolTip(control.getShell(),
					SWT.ICON_INFORMATION);
			textLimitTooltips
					.setText(field.getLabel() + "字段 限定了字数" + textLimit);
			textLimitTooltips.setAutoHide(false);
		}

		control.addFocusListener(this);

		if (control instanceof Text)
			((Text) control).addModifyListener(this);
		else if (control instanceof Combo)
			((Combo) control).addModifyListener(this);

		control.addDisposeListener(this);
	}

	@Override
	public void focusGained(FocusEvent event) {
		if (textLimitTooltips != null) {
			textLimitTooltips.setLocation(getTextLimitTooltipsLocation());
		}
		showed = true;
	}

	@Override
	public void focusLost(FocusEvent event) {
		if (textLimitTooltips != null) {
			textLimitTooltips.setVisible(false);
		}
		showed = false;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		if (textLimitTooltips == null || textLimitTooltips.isDisposed())
			return;
		int inputTextCount = 0;

		if (control instanceof Text)
			inputTextCount = ((Text) control).getText().length();
		else if (control instanceof Combo)
			inputTextCount = ((Combo) control).getText().length();
		
		int left = textLimit - inputTextCount;
		textLimitTooltips.setMessage("您已经输入的文字：" + inputTextCount + "个，还可以输入"
				+ left + "个");
		if(showed){
			textLimitTooltips.setVisible(true);
		}
	}

	private Point getTextLimitTooltipsLocation() {
		Point point = control.toDisplay(0, 0);
		point.y += control.getBounds().height + 2;
		return point;
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		textLimitTooltips.dispose();
		textLimitTooltips = null;
	}

}
