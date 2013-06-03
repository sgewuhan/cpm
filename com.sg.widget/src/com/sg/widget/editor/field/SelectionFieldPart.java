package com.sg.widget.editor.field;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.editor.field.actions.ClearSelectionAction;
import com.sg.widget.editor.field.actions.ObjectSelectAction;
import com.sg.widget.util.Util;
import com.sg.db.model.ISingleObject;

public class SelectionFieldPart extends AssistantFieldPart {

	private Text text;

	public SelectionFieldPart(Composite parent, FieldConfiguration fcc, IEditorInput input) {

		super(parent, fcc, input);
	}

	@Override
	protected void createControl(Composite parent) {

		text = new Text(parent, SWT.BORDER);
		text.setEditable(false);
		text.setLayoutData(getControlLayoutData());
		
		//…Ë÷√±‡º≠Ã· æ
		String textMessage = field.getTextMessage();
		if(!Util.isNullOrEmptyString(textMessage)){
			text.setMessage(textMessage);
		}
		super.createControl(parent);
	}

	@Override
	protected boolean hasAssist() {

		return true;
	}

	@Override
	protected void addToolbar(Composite parent) {

		assistActionSet.add(new ObjectSelectAction());
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

		super.valueChanged(key, oldValue, newValue);

	}

	@Override
	protected void presentValue(ISingleObject data, Object value, String presentValue) {

		if (text.isDisposed())
			return;
		text.setText(presentValue);
	}

	@Override
	protected void setEditable(boolean editable) {

		List<Item> itms = getAssistantItems();
		if (itms != null) {

			Item item;
			for (int i = 0; i < itms.size(); i++) {
				item = itms.get(i);
				if (WidgetConstants.CLEAR_ACTION_ID.equals(item.getData(WidgetConstants.ACTION_ID))
						|| WidgetConstants.SELECT_ACTION_ID.equals(item.getData(WidgetConstants.ACTION_ID))) {
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
