package com.sg.widget.editor.field.actions;

import java.util.Calendar;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;

import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldActionConfiguration;
import com.sg.widget.editor.field.AbstractFieldPart;

public class DateFieldSelectAction extends FieldActionConfiguration {
	
	public DateFieldSelectAction() {
		super();
	}

	private DateTime date;

	@Override
	public String getName() {
		return "Ñ¡ÔñÈÕÆÚ..";
	}
	@Override
	public String getId() {
		return WidgetConstants.DATE_SELECT_ACTION_ID;
	}


	@Override
	public Image getImage() {
		return Widget.getImage(IWidgetImage.IMG_DATETIME16);
	}
	@Override
	public ImageDescriptor getImageDescriptor() {
		return Widget.getImageDescriptor(IWidgetImage.IMG_DATETIME16);
	}
	
	
	@Override
	public Object run(final AbstractFieldPart fieldPart, IEditorInput input) {
		Display display = fieldPart.getControl().getDisplay();
		final Shell parent = new Shell(display,
				SWT.BORDER | SWT.APPLICATION_MODAL);// change system_modal to
													// application_modal to
													// avoid problem of data
													// selector cannot open in
													// modal dailog
		parent.setLayout(new FillLayout());
		date = new DateTime(parent, SWT.CALENDAR);
		date.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// String year = "" + date.getYear();
				// String month = (date.getMonth() + 1) > 9 ? ""
				// + (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
				// String day = date.getDay() > 9 ? "" + date.getDay() : "0"
				// + date.getDay();
				// String hours = date.getHours() > 9 ? "" + date.getHours() :
				// "0"
				// + date.getHours();
				// String minutes = date.getMinutes() > 9 ? "" +
				// date.getMinutes()
				// : "0" + date.getMinutes();
				// String seconds = date.getSeconds() > 9 ? "" +
				// date.getSeconds()
				// : "0" + date.getSeconds();
				// String datevalue = null;
				// if
				// (fieldPart.getDateFormat().equals(FieldFactory.DATEFORMAT_DATE))
				// {
				// datevalue = year + "-" + month + "-" + day;
				// } else if
				// (fieldPart.getDateFormat().equals(FieldFactory.DATEFORMAT_DATETIME))
				// {
				// datevalue = year + "-" + month + "-" + day + " " + hours
				// + ":" + minutes + ":" + seconds;
				// } else if
				// (fieldPart.getDateFormat().equals(FieldFactory.DATEFORMAT_TIME))
				// {
				// datevalue = hours + ":" + minutes + ":" + seconds;
				// }
				try {
					Calendar c = Calendar.getInstance();
					c.set(date.getYear(), date.getMonth(), date.getDay(),
							date.getHours(), date.getMinutes(),
							date.getSeconds());
					fieldPart.setValue(c.getTime());
					fieldPart.updateDataValueAndPresent();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				parent.close();
			}
		});

		parent.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				parent.dispose();
			}
		});
		parent.pack();

		Point point = fieldPart.getControl().toDisplay(0, 0);
		point.y += fieldPart.getControl().getBounds().height + 2;
		parent.setLocation(point);

		parent.open();
		return null;
	}

}

