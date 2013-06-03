package com.sg.widget.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.sg.db.model.ISingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;

public abstract class FormEditorDialog extends FormDialog {

	protected IManagedForm managedForm;
	protected ScrolledForm form;
	protected ISingleObjectEditorInput input;
	protected Menu messagePopup;

	public FormEditorDialog(Shell shell,ISingleObjectEditorInput input) {
		super(shell);
		this.input = input;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		this.managedForm = managedForm;
		form = managedForm.getForm();
		
		managedForm.setInput(input);

		addFormMessageLisener(managedForm);

		// 创建page body
		createContent(form.getBody());
		form.reflow(true);
	}
	
	protected abstract void createContent(Composite body);

	private void addFormMessageLisener(IManagedForm managedForm) {
		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				showMessagePopup(e);
			}
		});
	}

	private void showMessagePopup(HyperlinkEvent e) {
		if (messagePopup != null && !messagePopup.isDisposed()) {
			messagePopup.dispose();
		}
		messagePopup = new Menu(form.getShell(), SWT.POP_UP);
		IMessage[] messages = form.getForm().getChildrenMessages();
		for (int i = 0; i < messages.length; i++) {
			final IMessage message = messages[i];
			MenuItem item = new MenuItem(messagePopup, SWT.PUSH);
			item.setText(message.getPrefix() + " " + message.getMessage());
			item.setImage(getMessageImage(message.getMessageType()));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Control c = message.getControl();
					if (c != null && !c.isDisposed()) {
						c.setFocus();
					}
				}
			});
		}
		Point hl = ((Control) e.widget).toDisplay(0, 0);
		hl.y += 16;
		messagePopup.setLocation(hl);
		messagePopup.setVisible(true);

	}

	/**
	 * 根据类型获取图标
	 * 
	 * @param type
	 * @return
	 */
	private Image getMessageImage(int type) {
		Display display = form.getShell().getDisplay();
		switch (type) {
		case IMessageProvider.ERROR:
			return display.getSystemImage(SWT.ICON_ERROR);
		case IMessageProvider.WARNING:
			return display.getSystemImage(SWT.ICON_WARNING);
		case IMessageProvider.INFORMATION:
			return display.getSystemImage(SWT.ICON_INFORMATION);
		}
		return null;
	}

	public ISingleObject getInputData() {
		return input.getInputData();
	}
}
