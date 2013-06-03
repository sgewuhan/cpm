package com.sg.common.ui;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;

import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.DocumentService;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class DocumentTaskFormDefaultPage implements IPageDelegator {

	private TableViewer viewer;
	protected DocumentService documentService;

	public DocumentTaskFormDefaultPage() {
		documentService = BusinessService.getDocumentService();
	}

	@Override
	public Composite createPageContent(Composite parent,
			ISingleObjectEditorInput input, PageConfiguration conf) {
		viewer = new TableViewer(parent,SWT.NONE);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				DBObject doc = (DBObject) element;
				String docName = (String) doc.get(IDBConstants.FIELD_DESC);
				String docType = (String) doc.get(IDBConstants.FIELD_SYSTEM_EDITOR);
				String docTypeName = documentService.getDocumentNameFromEditor(docType);
				return docTypeName+":"+docName;
			}
			
		});
		
		ObjectId workOid = (ObjectId) input.getInputData().getValue(IDBConstants.FIELD_SYSID);
		List<DBObject> documents = documentService.getWorkDocument(workOid);
		viewer.setInput(documents);
		return viewer.getTable();
	}

	@Override
	public IFormPart getFormPart() {
		return null;
	}

}
