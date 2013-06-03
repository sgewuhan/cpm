package com.sg.cpm.myworks.actions;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.common.service.MessageObject;
import com.sg.common.ui.UIConstants;
import com.sg.cpm.myworks.view.WorkInBox;
import com.sg.db.model.SingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;

public class CreateDocument implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow win;
	private MessageObject currentWorkMessage;
	private EditorConfiguration editorConfiguration;
	private IWorkbenchPart part;

	@Override
	public void run(IAction action) {
		ObjectId workId = (ObjectId) currentWorkMessage.getTargetValue(IDBConstants.FIELD_SYSID);
		final DocumentService documentService = BusinessService.getDocumentService();
		SingleObject so = documentService.prepareWorkDocument(
				workId);

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(
				editorConfiguration, so);

		ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				// 更改文件名
				String editorId = (String) input.getInputData().getValue(
						IDBConstants.FIELD_SYSTEM_EDITOR);
				String documentName = documentService.getDocumentNameFromEditor(editorId);
				input.getInputData().setValue(IDBConstants.FIELD_DESC,
						documentName, null, false);
				
				return super.saveBefore(input);
			}

		};
		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(
				win.getShell(), UIConstants.EDITOR_DELIVERDOCUMENT_CREATE,
				editInput, call, true);

		int ok = soed.open();

		if (ok == SingleObjectEditorDialog.OK) {

			if (part.getSite().getId()
					.equals(UIConstants.VIEW_MYWORKS_WORKSINBOX)) {
				// 刷新当前工作可能的选择提供者
				((WorkInBox) part).getCurrentViewer().refresh(currentWorkMessage);
			}
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// 当前激活的对象是MessageObject可用
		// 当前激活的对象的状态为ready 以及 process可用
		if (selection != null && !selection.isEmpty()) {
			Object element = ((IStructuredSelection) selection)
					.getFirstElement();
			if (element instanceof MessageObject) {
				MessageObject mo = (MessageObject) element;
				if (mo.isReady() || mo.isProcess()) {
					action.setEnabled(true);
					currentWorkMessage = mo;
					part = win.getActivePage().getActivePart();
					return;
				}
			}
		}

		action.setEnabled(false);
		currentWorkMessage = null;
		part = null;
		return;

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		win = window;
		editorConfiguration = Widget
				.getSingleObjectEditorConfiguration(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE);
	}

}
