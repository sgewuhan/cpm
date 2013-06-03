package com.sg.widget.viewer.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class SearchNavigationControl extends WorkbenchWindowControlContribution {

	public SearchNavigationControl() {
	}

	public SearchNavigationControl(String id) {
		super(id);
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite bg = new Composite(parent,SWT.NONE);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginTop=0;
		layout.spacing = 1;
		layout.marginBottom = 0;
		layout.justify = true;
		layout.fill = true;
		bg.setLayout(layout);
		Button buttonUp = new Button(bg, SWT.PUSH|SWT.FLAT);
//		buttonUp.setImage(Resource.getImage(Resource.IMG_UP32));
//		buttonUp.setData( WidgetUtil.CUSTOM_VARIANT, UI.CUSTOM_VAR );
		buttonUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchPart part = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActivePart();
				if(part instanceof ISearchable){
					((ISearchable)part).searchPrevious();
				}
			}

		});
		Button buttonDown = new Button(bg, SWT.PUSH|SWT.FLAT);
//		buttonDown.setImage(Resource.getImage(Resource.IMG_DOWN32));
//		buttonDown.setData( WidgetUtil.CUSTOM_VARIANT, UI.CUSTOM_VAR );
		buttonDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchPart part = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActivePart();
				if(part instanceof ISearchable){
					((ISearchable)part).searchNext();
				}
			}
		});
		return bg;
	}

}
