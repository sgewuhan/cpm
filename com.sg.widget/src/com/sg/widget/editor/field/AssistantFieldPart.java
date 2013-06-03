package com.sg.widget.editor.field;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;

import com.sg.widget.WidgetConstants;
import com.sg.widget.configuration.FieldConfiguration;
import com.sg.widget.configuration.FieldActionConfiguration;

public abstract class AssistantFieldPart extends AbstractFieldPart {

	private ToolBar toolBar;
	protected List<FieldActionConfiguration> assistActionSet;
	private Menu dropDownMenu;
	private FieldActionConfiguration fieldAction;
	private List<Item> items;

	public AssistantFieldPart(Composite parent, FieldConfiguration cfield,
			IEditorInput input) {
		super(parent, cfield, input);
	}

	@Override
	protected void createContent(Composite parent) {
		assistActionSet = new ArrayList<FieldActionConfiguration>();
		assistActionSet.addAll(field.getFieldActions());
		if(field.getId().equals("createdate")){
			System.out.println();
		}
		if (hasAssist()) {
			controlSpace--;// 如果有按钮菜单，控件的空间减去一格
		}
		super.createContent(parent);
	}
	
	GridData getControlLayoutData() {

		GridData td = new GridData(SWT.FILL, SWT.CENTER, true, false, controlSpace, 1);
		td.widthHint = field.getWidthHint() == 0 ? 80 : field.getWidthHint();
		return td;
	}

	protected boolean hasAssist() {
		return assistActionSet != null && (!assistActionSet.isEmpty());
	}

	@Override
	protected void createControl(Composite parent) {
		addToolbar(parent);
	}

	protected void addToolbar(final Composite parent) {
		if (!hasAssist())
			return;
		items = new ArrayList<Item>();
		toolBar = new ToolBar(parent, SWT.FLAT);
		toolBar.setData(RWT.CUSTOM_VARIANT,
				WidgetConstants.THEME_VIEW_TOOLBAR);

		ToolItem toolItem;
		if (assistActionSet.size() > 1) {
			toolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		} else {
			toolItem = new ToolItem(toolBar, SWT.PUSH);
		}
		toolItem.setData(RWT.CUSTOM_VARIANT,
				WidgetConstants.THEME_VIEW_TOOLBAR);
		fieldAction = assistActionSet.get(0);
		toolItem.setImage(fieldAction.getImage());
		toolItem.setData(WidgetConstants.ACTION_ID, fieldAction.getId());
		items.add(toolItem);

		toolItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (event.detail == SWT.ARROW) {
					Point point = toolBar.toDisplay(event.x, event.y);
					dropDownMenu.setLocation(point);
					dropDownMenu.setVisible(true);
				} else {
					fieldAction.run(AssistantFieldPart.this, getInput());
				}
			}
		});

		if (assistActionSet.size() > 1) {
			dropDownMenu = new Menu(toolBar.getShell(), SWT.POP_UP);
			Iterator<FieldActionConfiguration> iter = assistActionSet.iterator();
			while (iter.hasNext()) {
				final FieldActionConfiguration action = iter.next();

				MenuItem item = new MenuItem(dropDownMenu, SWT.PUSH);
				item.setText(action.getName());
				item.setImage(action.getImageDescriptor().createImage());
				item.setData(WidgetConstants.ACTION_ID, fieldAction.getId());
				items.add(item);
				item.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						action.run(AssistantFieldPart.this, getInput());
					}
				});
			}
		}
	}

	protected ToolBar getToolbar() {
		return toolBar;
	}

	protected List<Item> getAssistantItems() {
		return items;
	}

}
