package com.sg.cpm.admin.navigator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.AuthorityResponse;
import com.sg.user.IAuthorityResponse;
import com.sg.user.UserSessionContext;
import com.sg.user.ui.AuthorityUI;
import com.sg.widget.part.QueryTreeView;

public class FunctionNavigatorView extends QueryTreeView {

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setActiveCollectionAdaptable(false);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				openSelection((IStructuredSelection) event.getSelection());
			}
		});

//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//
//			@Override
//			public void doubleClick(DoubleClickEvent event) {
//				openSelection((IStructuredSelection) event.getSelection());
//			}
//		});
	}

	protected void openSelection(IStructuredSelection selection) {
		
		if (selection == null || selection.isEmpty())
			return;
		
		

		ISingleObject so = (ISingleObject) selection.getFirstElement();
		//*****************************************************
		//添加权限判断

		String tokenId = (String) so.getValue(IDBConstants.FIELD_TOKENID);
		//获得上下文
		if(tokenId == null){
			return;
		}
		IAuthorityResponse resp = new AuthorityResponse();
		boolean has = UserSessionContext.hasTokenAuthority(tokenId,resp );
		
		if(!has){
			AuthorityUI.SHOW_NOT_PERMISSION();
			return;
		}
		String editorId = (String) so.getValue(IDBConstants.FIELD_SYSTEM_EDITOR);

		if (editorId == null)
			return;

		FunctionEditorInput editorInput = new FunctionEditorInput(so, editorId,resp);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, editorId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}