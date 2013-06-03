package com.sg.widget.editor.field;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;
import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.configuration.ColumnConfiguration;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.dialog.DBObjectSelectorDialog;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.sorter.AbstractColumnViewerSorter;
import com.sg.widget.viewer.sorter.UniSorter;

public class TableFieldPart extends AssistantFieldPart {

	private TableViewer viewer;

	private BasicDBList inputData;

	private Button buttonAdd;

	private Button buttonRemove;

	private Button clearButton;

	private boolean isEditable;

	private boolean isDirty;

	public TableFieldPart(Composite parent, FieldConfiguration cfield, IEditorInput input) {

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
	protected void presentValue(ISingleObject data, Object value, String presValue) {

		inputData = (BasicDBList) value;
		viewer.setInput(inputData);
		Util.packTableViewer(viewer);
	}

	@Override
	protected void createControl(Composite parent) {

		// 类型检查
		// 只有选择的是DBList的可以用表格控件
		String fieldType = field.getType();
		if (!IFieldTypeConstants.FIELD_DBLIST.equals(fieldType)) {
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
		table.setHeaderVisible(field.isHeaderVisiable());
		table.setLinesVisible(field.isLineVisiable());

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

		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		createButtons(panel);

		GridData td = getControlLayoutData();
		td.horizontalSpan = 3;
		td.heightHint = field.getHeightHint() == 0 ? 160 : field.getHeightHint();
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

		clearButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
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
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.isEmpty()) {
				return;
			}

			Iterator iter = selection.iterator();
			while (iter.hasNext()) {
				DBObject item = (DBObject) iter.next();
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
				if (inputData == null) {
					inputData = new BasicDBList();
				}
				boolean b = handler.addItem(inputData);
				if(b){
					isDirty = true;
					updateDataValueAndPresent();
				}
			} else {
				// 取默认的集合选取器
				String def = field.getDataSelectorDefinition();
				String collectionName = def.substring(0, def.indexOf(":"));
				String strFields = def.substring(def.indexOf(":") + 1);
				String[] fields = strFields.split(",");
				DBObject returnFields = new BasicDBObject();
				for (int i = 0; i < fields.length; i++) {
					returnFields.put(fields[i], 1);
				}
				IStructuredSelection selection = DBObjectSelectorDialog.OPEN(getShell(), "请选择" + field.getLabel(), collectionName, null, returnFields,null);
				if (selection != null && !selection.isEmpty()) {
					Iterator iter = selection.iterator();
					while (iter.hasNext()) {
						DBObject selectedItem = (DBObject) iter.next();
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

		List<ColumnConfiguration> ccs = field.getColumnsConfigurations();

		TableViewerColumn vColumn;

		for (int i = 0; i < ccs.size(); i++) {

			ColumnConfiguration cc = ccs.get(i);
			String name = cc.getName();

			vColumn = new TableViewerColumn(viewer, cc.getStyle());

			final TableColumn cColumn = vColumn.getColumn();
			cColumn.setMoveable(cc.isMoveable());
			cColumn.setResizable(cc.isResizable());
			cColumn.setText(name);
			cColumn.setImage(cc.getImage());
			cColumn.setWidth(cc.getWidth());

			// 设置Tooltips
			cColumn.setToolTipText(cc.getToolTipText());

			// 设置LabelProvider
			ColumnLabelProvider labelProvider = cc.getLabelProvider();

			if (labelProvider == null) {
				labelProvider = new TableFieldColumnLabelProvider(cc);
			} else if (labelProvider instanceof TableFieldColumnLabelProvider) {
				((TableFieldColumnLabelProvider) labelProvider).setColumnConfigruation(cc);
			}

			vColumn.setLabelProvider(labelProvider);

			// 设置排序
			if (cc.isSorter()) {
				AbstractColumnViewerSorter sortor = cc.getSortor(viewer, cColumn, cc.getColumn());
				if (sortor == null) {
					new UniSorter(viewer, cColumn, cc.getColumn());
				}
			}

		}
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {

		return inputData;
	}

}
