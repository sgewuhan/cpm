package com.sg.cpm.myworks.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.MessageObject;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.myworks.view.WorkInBox;
import com.sg.db.DBActivator;
import com.sg.db.model.IEventListener;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.editor.SingleObjectEditor;
import com.sg.widget.editor.SingleObjectEditorInput;

public class Edit implements IWorkbenchWindowActionDelegate, IEventListener {

	private static final int DOC = 0;

	private static final int WORK = 1;

	private IWorkbenchWindow win;

	private WorkInBox currentPart;

	private DBObject doc;

	private DBCollection docCollection;

	private SingleObject data;

	private Object editingDocument;

	private int editTarget;

	private MessageObject message;

	@Override
	public void run(IAction action) {

		String editorId = null;
		if (editTarget == DOC) {
			editingDocument = doc;
			editorId = (String) doc.get(IDBConstants.FIELD_SYSTEM_EDITOR);
			data = new SingleObject(docCollection, doc);
			data.addEventListener(this);
			SingleObjectEditorInput input = new SingleObjectEditorInput(editorId, data);
			SingleObjectEditor.OPEN(input);
		} else if (editTarget == WORK) {
			editorId = UIConstants.EDITOR_WORK_READ;
			data = message.getTargetSingleObject();
			SingleObjectEditorInput input = new SingleObjectEditorInput(editorId, data);
			SingleObjectEditorDialog.getInstance(win.getShell(), editorId, input, null, false).open();
		} else {
			return;
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

		IWorkbenchPart part = win.getActivePage().getActivePart();
		if (part != null && part.getSite().getId().equals(UIConstants.VIEW_MYWORKS_WORKSINBOX)) {
			currentPart = (WorkInBox) part;
			if (selection != null && !selection.isEmpty()) {
				Object element = ((IStructuredSelection) selection).getFirstElement();
				if (element instanceof DBObject) {// 选择的是文档对象
					doc = (DBObject) element;

					message = null;
					action.setEnabled(true);
					editTarget = DOC;
				} else {// 选择的是工作通知对象
					message = (MessageObject) element;

					doc = null;
					action.setEnabled(true);
					editTarget = WORK;

				}
			}
		} else {
			doc = null;
			action.setEnabled(false);
		}
	}

	@Override
	public void dispose() {

		if (data != null) {
			data.removeEventListener(this);
		}
	}

	@Override
	public void init(IWorkbenchWindow window) {

		this.win = window;
		docCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
	}

	@Override
	public void event(String code, ISingleObject singleObject) {

		if (ISingleObject.UPDATED.equals(code)) {
			if (currentPart != null && editingDocument != null) {
				TreeViewer currentViewer = currentPart.getCurrentViewer();
				currentViewer.refresh();
			}
		}
	}

}
