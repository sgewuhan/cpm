package com.sg.widget.editor;

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.configuration.FieldGroupConfiguration;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.configuration.PageColumnConfiguration;
import com.sg.widget.configuration.SectionConfiguration;
import com.sg.widget.editor.field.AbstractFieldPart;
import com.sg.widget.editor.field.BooleanFieldPart;
import com.sg.widget.editor.field.ComboFieldPart;
import com.sg.widget.editor.field.ComboReadOnlyFieldPart;
import com.sg.widget.editor.field.DateFieldPart;
import com.sg.widget.editor.field.DateTimeFieldPart;
import com.sg.widget.editor.field.FileFieldPart;
import com.sg.widget.editor.field.HtmlAreaFieldPart;
import com.sg.widget.editor.field.IFieldTypeConstants;
import com.sg.widget.editor.field.MultiLineTextFieldPart;
import com.sg.widget.editor.field.RadioFieldPart;
import com.sg.widget.editor.field.SelectionFieldPart;
import com.sg.widget.editor.field.SpinnerFieldPart;
import com.sg.widget.editor.field.StringListFieldPart;
import com.sg.widget.editor.field.TableFieldPart;
import com.sg.widget.editor.field.TextFieldPart;
import com.sg.widget.util.Util;

public class SingleObjectEditorFormPage extends FormPage {

	private PageConfiguration cpage;

	private ScrolledForm form;

	private FormToolkit toolkit;

	private Menu messagePopup;

	public SingleObjectEditorFormPage(String id, String title, PageConfiguration cpage) {

		super(id, title);
		this.cpage = cpage;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {

		form = managedForm.getForm();
		ISingleObjectEditorInput input = (ISingleObjectEditorInput) getEditorInput();
		managedForm.setInput(input);

		toolkit = managedForm.getToolkit();

		FormEditorToolkit.decorateFormHeading(managedForm);

		loadTitle();

		addFormMessageLisener(managedForm);

		// 创建page body
		createPageFromContent();
		form.reflow(true);
	}

	private void loadTitle() {

		ISingleObjectEditorInput input = (ISingleObjectEditorInput) getEditorInput();

		String desc = cpage.getDescription(input);
		if (desc != null) {
			form.setText(desc);
		}

		ImageDescriptor image = cpage.getImageDescriptor();

		if (image != null) {
			form.setImage(image.createImage());
		}

	}

	protected void createPageFromContent() {

		Composite parent = form.getBody();
		parent.setBackgroundMode(SWT.INHERIT_DEFAULT);//解决背景透明的问题
		IPageDelegator ipd = cpage.getPageDelegator();
		if (ipd != null) {
			parent.setLayout(new FillLayout());
			IEditorInput editorInput = getEditorInput();
			ipd.createPageContent(parent, (ISingleObjectEditorInput) editorInput, cpage);
			IFormPart formPart = ipd.getFormPart();
			if (formPart != null)
				getManagedForm().addPart(formPart);
		} else {
			int columnCount = cpage.getColumns().size();
			GridLayout layout = new GridLayout(columnCount, true);
			parent.setLayout(layout);
			Composite panel;
			for (int i = 0; i < columnCount; i++) {
				panel = new Composite(parent, SWT.NONE);
				panel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
				layout = new GridLayout();
				panel.setLayout(layout);
				createColumn(panel, cpage.getColumns().get(i));
			}
		}
	}

	private void createColumn(Composite parent, PageColumnConfiguration pcc) {

		List<SectionConfiguration> sec = pcc.getSections();
		for (int i = 0; i < sec.size(); i++) {
			createSection(parent, sec.get(i));
		}
	}

	private void createSection(Composite parent, SectionConfiguration csection) {

		Section section = toolkit.createSection(parent, csection.getStyle());
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		section.setText(csection.getLabel());
		if (!Util.isNullOrEmptyString(csection.getDescription())) {
			section.setDescription(csection.getDescription());
		}

		Composite sectionClient = toolkit.createComposite(section);

		GridLayout glayout = new GridLayout(3, false);
		sectionClient.setLayout(glayout);

		List<Object> fieldList = csection.getFields();
		for (int i = 0; i < fieldList.size(); i++) {

			Object fieldElement = fieldList.get(i);
			if (fieldElement instanceof FieldConfiguration) {
				createField(sectionClient, (FieldConfiguration) fieldElement);
			} else if (fieldElement instanceof FieldGroupConfiguration) {
				createFieldGroup(sectionClient, (FieldGroupConfiguration) fieldElement);
			}
		}
		section.setClient(sectionClient);
	}

	private Composite createFieldGroup(Composite sectionClient, FieldGroupConfiguration fieldElement) {

		Composite fieldGroup = toolkit.createComposite(sectionClient);
		int columnCount = fieldElement.getColumnCount();
		GridData td = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		fieldGroup.setLayoutData(td);
		GridLayout glayout = new GridLayout(columnCount * 3, false);
		glayout.horizontalSpacing = 5;
		fieldGroup.setLayout(glayout);

		List<FieldConfiguration> fieldList = fieldElement.getFields();
		for (int i = 0; i < fieldList.size(); i++) {

			FieldConfiguration fcc = fieldList.get(i);
			createField(fieldGroup, fcc);
		}
		return fieldGroup;
	}

	private AbstractFieldPart createField(Composite parent, FieldConfiguration fcc) {

		AbstractFieldPart part = null;

		IEditorInput editorInput = getEditorInput();
		if (fcc.isComputeField()) {// 如果是计算字段，直接取当行文本框
			part = new TextFieldPart(parent, fcc, editorInput);
		} else {
			String type = fcc.getEditPart();
			if (IFieldTypeConstants.TYPE_TEXT.equals(type)) {
				part = new TextFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_SPINNER.equals(type)) {
				part = new SpinnerFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_TEXTM.equals(type)) {
				part = new MultiLineTextFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_COMBOR.equals(type)) {
				part = new ComboReadOnlyFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_COMBO.equals(type)) {
				part = new ComboFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_RADIO.equals(type)) {
				part = new RadioFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_CHECK.equals(type)) {
				part = new BooleanFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_DATE.equals(type)) {
				part = new DateFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_DATE_B.equals(type)) {
				part = new DateTimeFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_DATE_D.equals(type)) {
				part = new DateTimeFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_TIME_B.equals(type)) {
				part = new DateTimeFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_FILE.equals(type)) {
				// ****增加对文件上传的支持****zhonghua 2012/3/30**********
				part = new FileFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_FILE_MULTI.equals(type)) {
				part = new FileFieldPart(parent, fcc, editorInput);
				// ****增加对文件上传的支持****zhonghua 2012/3/30**********
			} else if (IFieldTypeConstants.TYPE_SELECTION.equals(type)) {// todo：
				part = new SelectionFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_HTMLAREA.equals(type)) {
				part = new HtmlAreaFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_TABLE.equals(type)) {
				part = new TableFieldPart(parent, fcc, editorInput);
			} else if (IFieldTypeConstants.TYPE_LIST.equals(type)) {
				part = new StringListFieldPart(parent, fcc, editorInput);
			}
		}
		if (part != null) {
			getManagedForm().addPart(part);
			part.setMessageManager(form.getMessageManager());
		}

		return part;

	}

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

	public void refresh() {

		IManagedForm mf = getManagedForm();
		if (mf != null) {
			mf.refresh();
			loadTitle();
		}
	}

	@Override
	public boolean isDirty() {

		// TODO Auto-generated method stub
		return super.isDirty();
	}

}
