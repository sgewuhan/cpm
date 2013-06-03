package com.sg.widget.dialog;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.Section;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.configuration.FieldGroupConfiguration;
import com.sg.widget.configuration.PageColumnConfiguration;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.configuration.SectionConfiguration;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
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
import com.sg.widget.editor.field.TableFieldPart;
import com.sg.widget.editor.field.TextFieldPart;
import com.sg.widget.editor.saveHandler.IEditorSaveHandler;
import com.sg.widget.util.Util;

public class SingleObjectEditorDialog extends FormEditorDialog {

	private ISingleObjectEditorDialogCallback callback;
	private EditorConfiguration editorConf;
	private boolean listFields;
	private List<PageConfiguration> cPages;

	public SingleObjectEditorDialog(Shell shell, ISingleObjectEditorInput input, EditorConfiguration editorConf, boolean listFields) {
		super(shell, input);
		this.editorConf = editorConf;
		cPages = editorConf.getPages();
		Assert.isTrue(cPages != null && cPages.size() > 0);
		this.listFields = listFields;
	}

	private void loadItemTitle(PageConfiguration cpage, CTabItem item) {
		String desc = cpage.getTitle();
//		.getDescription(input);
		if (desc != null) {
			// if (!listFields) {
			item.setText(desc);
			// }
		}

		ImageDescriptor imd = cpage.getImageDescriptor();

		if (imd != null) {
			Image img = imd.createImage();
			// if (!listFields) {
			item.setImage(img);
			// }
		}
	}

	private void loadDialogTitle() {
		String name = editorConf.getName();
		if (name != null) {
			name = input.isEditable() ? name : name + "(只读)";
			form.setText(name);
			getShell().setText(name);
		}
		ImageDescriptor imageDescription = editorConf.getImageDescription();
		if (imageDescription != null) {
			Image image = imageDescription.createImage();
			form.setImage(image);
			getShell().setImage(image);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.get().OK_LABEL, true);
		if (input.isEditable()) {

			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.get().CANCEL_LABEL, false);
		}
	}

	@Override
	protected void createContent(Composite parent) {
		loadDialogTitle();

		parent.setLayout(new FillLayout());
		CTabFolder folder = new CTabFolder(parent, SWT.TOP | SWT.FLAT);
		folder.setMRUVisible(true);

		for (int index = 0; index < cPages.size(); index++) {
			CTabItem item = new CTabItem(folder, SWT.NONE);

			PageConfiguration cpage = cPages.get(index);
			loadItemTitle(cpage, item);

			Composite panel;
			IPageDelegator ipd = cpage.getPageDelegator();
			if (ipd != null) {
				panel = ipd.createPageContent(folder, input, cpage);
			} else {
				panel = new Composite(folder, SWT.NONE);

				int columnCount = cpage.getColumns().size();
				GridLayout layout = new GridLayout(columnCount, true);
				layout.marginBottom = 0;
				layout.marginLeft = 0;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.horizontalSpacing = 0;
				layout.verticalSpacing = 0;
				panel.setLayout(layout);
				Composite column;
				for (int i = 0; i < columnCount; i++) {
					column = new Composite(panel, SWT.NONE);
					GridLayout g;
					if (listFields) {
						g = new GridLayout(3, false);
					} else {
						g = new GridLayout();
					}
					column.setLayout(g);
					column.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
					createColumn(column, cpage.getColumns().get(i));
				}
			}

			item.setControl(panel);
		}
		folder.setSelection(0);
	}

	private void createColumn(Composite parent, PageColumnConfiguration pcc) {
		List<SectionConfiguration> sec = pcc.getSections();
		for (int i = 0; i < sec.size(); i++) {
			createSection(parent, sec.get(i));
		}
	}

	private void createSection(Composite parent, SectionConfiguration csection) {

		if (listFields) {
			List<Object> fieldList = csection.getFields();
			for (int i = 0; i < fieldList.size(); i++) {

				Object fieldElement = fieldList.get(i);
				if (fieldElement instanceof FieldConfiguration) {
					createField(parent, (FieldConfiguration) fieldElement);
				} else if (fieldElement instanceof FieldGroupConfiguration) {
					createFieldGroup(parent, (FieldGroupConfiguration) fieldElement);
				}
			}

		} else {
			Section section = new Section(parent, csection.getStyle());
			section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
			section.setText(csection.getLabel());
			if (!Util.isNullOrEmptyString(csection.getDescription())) {
				section.setDescription(csection.getDescription());
			}

			Composite sectionClient = new Composite(section, SWT.NONE);

			sectionClient.setLayout(new GridLayout(3, false));

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
	}

	private Composite createFieldGroup(Composite sectionClient, FieldGroupConfiguration fieldElement) {
		Composite fieldGroup = new Composite(sectionClient, SWT.NONE);
		int columnCount = fieldElement.getColumnCount();
		GridData td = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		fieldGroup.setLayoutData(td);
		GridLayout glayout = new GridLayout(columnCount * 3, false);
		glayout.marginLeft = 0;
		glayout.horizontalSpacing = 4;
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

		if (fcc.isComputeField()) {// 如果是计算字段，直接取当行文本框
			part = new TextFieldPart(parent, fcc, input);
		} else {
			String type = fcc.getEditPart();
			if (IFieldTypeConstants.TYPE_TEXT.equals(type)) {
				part = new TextFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_SPINNER.equals(type)) {
				part = new SpinnerFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_TEXTM.equals(type)) {
				part = new MultiLineTextFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_COMBOR.equals(type)) {
				part = new ComboReadOnlyFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_COMBO.equals(type)) {
				part = new ComboFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_RADIO.equals(type)) {
				part = new RadioFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_CHECK.equals(type)) {
				part = new BooleanFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_DATE.equals(type)) {
				part = new DateFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_DATE_B.equals(type)) {
				part = new DateTimeFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_DATE_D.equals(type)) {
				part = new DateTimeFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_TIME_B.equals(type)) {
				part = new DateTimeFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_FILE.equals(type)) {
				// ****增加对文件上传的支持****zhonghua 2012/3/30**********
				part = new FileFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_FILE_MULTI.equals(type)) {
				part = new FileFieldPart(parent, fcc, input);
				// ****增加对文件上传的支持****zhonghua 2012/3/30**********
			} else if (IFieldTypeConstants.TYPE_SELECTION.equals(type)) {// todo：
				part = new SelectionFieldPart(parent, fcc, input);
			} else if (IFieldTypeConstants.TYPE_HTMLAREA.equals(type)) {
				part = new HtmlAreaFieldPart(parent, fcc, input);
			} else if(IFieldTypeConstants.TYPE_TABLE.equals(type)){
				part = new TableFieldPart(parent, fcc, input);
			}
		}
		if (part != null) {
			managedForm.addPart(part);
			part.setMessageManager(form.getMessageManager());
		}

		return part;

	}

	public void refresh() {
		if (managedForm != null) {
			managedForm.refresh();
		}
	}

	@Override
	protected void okPressed() {
		getButton(OK).setFocus();

		if (!callback.needSave()) {
			super.okPressed();
			return;
		}
		// some control need some process before save, do it now. ex:
		// fileControl
		managedForm.commit(false);
		
		
		boolean valid = saveCheck();
		if(!valid){
			MessageDialog.openError(getShell(),WidgetConstants.EDITOR_SAVE, WidgetConstants.MESSAGE_INVALID_DATA);
			return;
		}
		

		// do save before
		boolean continueSave = callback.saveBefore(input);

		// do save
		if (continueSave) {
			
			//保存历史
			saveHistory(input);

			IEditorSaveHandler saveHandler = editorConf.getSaveHandler();
			boolean saved = false;
			if (saveHandler != null) {
				saved = saveHandler.doSave(input, null);
			}

			if (!saved) {
				
				valid = saveCheck();
				if(!valid){
					MessageDialog.openError(getShell(),WidgetConstants.EDITOR_SAVE, WidgetConstants.MESSAGE_INVALID_DATA);
					return;
				}
				
				input.save(null);
			}

			// do save after
			continueSave = callback.saveAfter(input);

			if (continueSave) {
				super.okPressed();
			}
		}
		super.okPressed();
	}
	
	
	private void saveHistory(ISingleObjectEditorInput input) {
		Set<FieldConfiguration> fields = editorConf.getSaveHistoryFields();
		if(fields.isEmpty())
			return;
		ISingleObject inputData = input.getInputData();
		BasicDBList historyData = (BasicDBList) inputData.getValue("history");
		if(historyData == null){
			historyData = new BasicDBList();
		}
		Iterator<FieldConfiguration> iter = fields.iterator();
		DBObject historyItem = new BasicDBObject();
		DBObject historyFieldInfo = new BasicDBObject();
		
		while(iter.hasNext()){
			FieldConfiguration next = iter.next();
			String key = next.getName();
			String label = next.getLabel();
			Object value = inputData.getValue(key);
			historyItem.put(key, value);
			historyFieldInfo.put(key, label);
		}
		historyItem.put("lastsave", new Date());
		historyItem.put("metadata", historyFieldInfo);
		historyData.add(historyItem);

		inputData.setValue("history", historyData, null, false);
	}

	private boolean saveCheck() {
		boolean result = true;
		//清理现有的message
		IMessageManager mm = managedForm.getMessageManager();
		if(mm!=null){
			mm.removeAllMessages();
		}

		IFormPart[] parts = managedForm.getParts();
		for(int j=0;j<parts.length;j++){
			if(parts[j] instanceof AbstractFieldPart){
				boolean valid = ((AbstractFieldPart) parts[j]).checkValidOnSave();
				if(!valid){
					result = false;
				}
			}
		}
		return result;
	}

	public static SingleObjectEditorDialog getInstance(Shell parentShell, String editorConfigurationId, ISingleObjectEditorInput editInput,
			ISingleObjectEditorDialogCallback callback, boolean listFields) {
		EditorConfiguration editorConfiguration;
		if (editorConfigurationId == null) {
			Assert.isNotNull(editInput);
			editorConfiguration = editInput.getConfig();
		} else {
			editorConfiguration = Widget.getSingleObjectEditorConfiguration(editorConfigurationId);
		}

		if (editInput == null) {
			Assert.isNotNull(editorConfiguration);
			Assert.isNotNull(editorConfigurationId);
			String collectionName = editorConfiguration.getCollection();
			SingleObject so = new SingleObject(DBActivator.getDefaultDBCollection(collectionName));
			editInput = new SingleObjectEditorInput(editorConfiguration, so);
		}

		SingleObjectEditorDialog soed = new SingleObjectEditorDialog(parentShell, editInput, editorConfiguration, listFields);
		if (callback == null) {
			callback = new SingleObjectEditorDialogCallback();
		}
		soed.setCallback(callback);
		return soed;
	}

	public static int OPEN(Shell parentShell, String editorConfigurationId, ISingleObjectEditorInput editInput,
			ISingleObjectEditorDialogCallback callback, boolean listFields) {
		SingleObjectEditorDialog soed = getInstance(parentShell, editorConfigurationId, editInput, callback, listFields);
		soed.create();
		soed.getShell().pack();
		int code = soed.open();
		return code;
	}

	public static int CREATE(Shell parentShell, String editorConfigurationId) {
		return OPEN(parentShell, editorConfigurationId, null, null, false);
	}

	public static int OPEN(Shell parentShell, String editorConfigurationId, ISingleObjectEditorInput editInput) {
		return OPEN(parentShell, editorConfigurationId, editInput, null, false);
	}

	public static int OPEN(String editorconfid, ISingleObjectEditorInput editInput) {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		return OPEN(parentShell, editorconfid, editInput, null, false);
	}

	private void setCallback(ISingleObjectEditorDialogCallback callback) {
		this.callback = callback;
	}

	@Override
	public boolean close() {
		// 对话框中的managedForm不会释放formparts,如果不手工释放，可能会导致侦听器触发的widget is disposed错误
		IFormPart[] parts = managedForm.getParts();
		for (int i = 0; i < parts.length; i++) {
			parts[i].dispose();
		}
		return super.close();
	}

}
