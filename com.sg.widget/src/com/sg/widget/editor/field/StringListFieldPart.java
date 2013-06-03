package com.sg.widget.editor.field;

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.mongodb.BasicDBList;
import com.sg.db.model.ISingleObject;
import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.util.Util;

public class StringListFieldPart extends AssistantFieldPart {

	private TableViewer viewer;

	private BasicDBList inputData;

	private Button buttonAdd;

	private Button buttonRemove;

	private Button clearButton;

	private boolean isEditable;

	private boolean isDirty;

	public StringListFieldPart(Composite parent, FieldConfiguration cfield,
			IEditorInput input) {

		super(parent, cfield, input);
	}

	@Override
	public Control getControl() {

		return viewer.getControl();
	}

	@Override
	protected void setEditable(boolean editable) {

		this.isEditable = editable;
		if (buttonAdd != null && !buttonAdd.isDisposed())
			buttonAdd.setEnabled(editable);
		if (buttonRemove != null && !buttonRemove.isDisposed())
			buttonRemove.setEnabled(editable);
		if (clearButton != null && !clearButton.isDisposed())
			clearButton.setEnabled(editable);

	}

	@Override
	protected void presentValue(ISingleObject data, Object value,
			String presValue) {

		inputData = (BasicDBList) value;
		viewer.setInput(inputData);
		Util.packTableViewer(viewer);
	}

	@Override
	protected void createControl(Composite parent) {

		// 类型检查
		// 只有选择的是DBList的可以用表格控件
		String fieldType = field.getType();
		if (!IFieldTypeConstants.FIELD_STRINGLIST.equals(fieldType)) {
			Assert.isLegal(false, TYPE_MISMATCH + field.getId());
		}
		if (field.isLabelVisible() || hasAssist()) {

			Label blank = new Label(parent, SWT.NONE);
			blank.setLayoutData(getControlLayoutData());
			addToolbar(parent);
		}

		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout(2, false));

		viewer = new TableViewer(panel, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(false);

		// rap 1.5 m6 new feature
		if (field.isMarkupEnabled()) {
			table.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		}

		int itemHeight = field.getItemHeight();
		if (itemHeight != 0) {
			table.setData(RWT.CUSTOM_ITEM_HEIGHT, itemHeight);
		}

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		createColumns();

		viewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		createButtons(panel);

		GridData td = getControlLayoutData();
		td.horizontalSpan = 3;
		td.heightHint = field.getHeightHint() == 0 ? 160 : field
				.getHeightHint();
		panel.setLayoutData(td);
		super.createControl(parent);
	}

	private void createButtons(Composite panel) {

		buttonAdd = new Button(panel, SWT.PUSH);
		buttonAdd.setImage(Widget.getImage(IWidgetImage.IMG_ADD16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addItem();
			}

		});

		buttonRemove = new Button(panel, SWT.PUSH);
		buttonRemove.setImage(Widget.getImage(IWidgetImage.IMG_DELETE16));
		buttonRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				removeItem();
			}

		});

		clearButton = new Button(panel, SWT.PUSH);
		clearButton.setImage(Widget.getImage(IWidgetImage.IMG_CLEAR16));
		clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				clearItems();
			}

		});

		clearButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,
				false, 1, 1));
	}

	protected void clearItems() {

		if (isEditable) {

			inputData = new BasicDBList();
			isDirty = true;

			updateDataValueAndPresent();
		}
	}

	protected void removeItem() {

		if (isEditable) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.isEmpty()) {
				return;
			}

			Iterator iter = selection.iterator();
			while (iter.hasNext()) {
				Object item = iter.next();
				inputData.remove(item);
			}
			isDirty = true;
			updateDataValueAndPresent();
		}
	}

	protected void addItem() {

		if (isEditable) {

			IAddTableItemHandler handler = field.getAddEventHandler();
			if (handler != null) {
				handler.addItem(inputData);
				updateDataValueAndPresent();
			} else {
				// 取默认的集合选取器
				InputDialog id = new InputDialog(getShell(), WidgetConstants.INPUT,
						field.getTooltips(), field.getTextMessage(), null);
				int ok = id.open();
				if(ok!=Window.OK){
					return;
				}
				String selectedItem = id.getValue();
				if (selectedItem != null) {
					if (inputData == null) {
						inputData = new BasicDBList();
					}
					inputData.add(selectedItem);
					isDirty = true;
					updateDataValueAndPresent();

				}
			}
		}
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	@Override
	public void commit(boolean onSave) {

		if (onSave) {
			isDirty = false;
		}

		super.commit(onSave);
	}

	private void createColumns() {


		TableViewerColumn vColumn = new TableViewerColumn(viewer, SWT.LEFT);

		TableColumn cColumn = vColumn.getColumn();
		cColumn.setWidth(120);
		
		vColumn .setLabelProvider(new ColumnLabelProvider());
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {

		return inputData;
	}

}
