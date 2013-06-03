package com.sg.widget.editor.field;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.editor.field.actions.ClearSelectionAction;
import com.sg.widget.editor.field.actions.DateFieldSelectAction;
import com.sg.widget.util.Util;
import com.sg.db.model.ISingleObject;

public class DateFieldPart extends AssistantFieldPart {

	private Text text;

	public DateFieldPart(Composite parent, FieldConfiguration fcc, IEditorInput input) {

		super(parent, fcc, input);
	}

	@Override
	protected void createControl(Composite parent) {

		// 检查数据类型不匹配的字段
		Assert.isLegal(field.getType().equals(IFieldTypeConstants.FIELD_DATE), TYPE_MISMATCH + field.getId());

		text = new Text(parent, SWT.BORDER);
		text.setEditable(false);
		text.setLayoutData(getControlLayoutData());

		super.createControl(parent);
	}

	@Override
	protected boolean hasAssist() {

		return true;
	}

	@Override
	protected void addToolbar(Composite parent) {

		assistActionSet.add(new DateFieldSelectAction());
		if (!field.isRequired()) {
			assistActionSet.add(new ClearSelectionAction());
		}
		super.addToolbar(parent);
	}

	@Override
	public Control getControl() {

		return text;
	}

	@Override
	public void valueChanged(String key, Object oldValue, Object newValue) {

		// TODO Auto-generated method stub
		super.valueChanged(key, oldValue, newValue);

	}

	@Override
	protected void presentValue(ISingleObject data, Object value, String presentValue) {

		if (text.isDisposed()) {
			return;
		}
		if (isValuePresented()) {
			text.setText(presentValue);
		} else {
			if (value == null) {
				text.setText("");
			} else {
				if (value instanceof Date) {
					text.setText(Util.getDateFormat(Util.SDF_YYYY__MM__DD).format(((Date) value)));
				} else {
					Assert.isLegal(false, TYPE_MISMATCH + field.getId());
				}
			}
		}
	}

	@Override
	protected void setEditable(boolean editable) {

		List<Item> itms = getAssistantItems();
		if (itms != null) {

			Item item;
			for (int i = 0; i < itms.size(); i++) {
				item = itms.get(i);
				if (WidgetConstants.CLEAR_ACTION_ID.equals(item.getData(WidgetConstants.ACTION_ID))
						|| WidgetConstants.DATE_SELECT_ACTION_ID.equals(item.getData(WidgetConstants.ACTION_ID))) {
					if (item instanceof ToolItem) {
						((ToolItem) item).setEnabled(editable);
					}
					if (item instanceof MenuItem) {
						((MenuItem) item).setEnabled(editable);
					}
				}
			}
		}

//		Display display = text.getDisplay();
//		text.setForeground(editable ? display.getSystemColor(SWT.COLOR_BLACK) : display.getSystemColor(SWT.COLOR_GRAY));
	}

	@Override
	protected Object getValueForUpdate(IMessageManager messageManager) {

		return getValue();
	}

}
