package com.sg.widget.viewer;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;

public class JSONMessage implements IDoubleClickListener {

	public JSONMessage(ColumnViewer viewer) {
		if(UserSessionContext.getSession().getUserName().equalsIgnoreCase("u6")){
			viewer.addDoubleClickListener(this);
		}
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		StructuredSelection sel = (StructuredSelection) event.getSelection();
		final ISingleObject element = (ISingleObject) sel.getFirstElement();
		DBObject data = element.getData();
		String serialize = JSON.serialize(data);
		final Shell shell = new Shell(SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL);
		shell.setText(element.getCollection()==null?"无集合对象":element.getCollection().getName());
		shell.setLayout(new FillLayout());
		Text text = new Text(shell,SWT.BORDER|SWT.MULTI|SWT.WRAP);
		text.setText(serialize);
		Button button = new Button(shell,SWT.PUSH);
		button.setText("清除字段");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog input = new InputDialog(shell, "输入字段名", "字段名", "", null);
				int ok = input.open();
				if(ok==InputDialog.OK){
					element.getData().removeField(input.getValue());
					element.save();
				}
			}
		
		});
		
		shell.open();
	}

}
