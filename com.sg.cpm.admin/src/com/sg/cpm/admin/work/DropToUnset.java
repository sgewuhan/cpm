package com.sg.cpm.admin.work;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.db.IDBConstants;

public class DropToUnset implements DropTargetListener {

	private WorkTemplateEditor editor;

	public DropToUnset(WorkTemplateEditor editor) {

		this.editor = editor;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {


	}

	@Override
	public void dragLeave(DropTargetEvent event) {


	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {


	}

	@Override
	public void dragOver(DropTargetEvent event) {


	}

	@Override
	public void drop(DropTargetEvent event) {

		String jsonOBS = (String) event.data;
		if (jsonOBS.equals("#")) {
			return;
		}

		if (editor.currentTemplate == null) {
			return;
		}
		// 只处理取消设置的
		DBObject jsonData = (DBObject) JSON.parse(jsonOBS);
		if (!"unset".equals(jsonData.get("operation"))) {
			return;
		}

		String key = (String) jsonData.get("data");
		
		DBObject assDef = (DBObject) editor.currentTemplate.getValue(IDBConstants.FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION);
		if (assDef == null) {
			return;
		}
		
		assDef.removeField(key);
		editor.currentTemplate.setValue(IDBConstants.FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION, assDef);
		editor.currentTemplate.setValue(IDBConstants.FIELD_ACTIVATE, false);
		
		editor.currentTemplate.save();
		ISelection sel = editor.processViewer.getSelection();
		if(sel==null||sel.isEmpty()){
			return;
		}
		
		if(sel instanceof IStructuredSelection){
			Object element = ((IStructuredSelection)sel).getFirstElement();
			 editor.processViewer.update(element, null);
		}
		
			
	}

	@Override
	public void dropAccept(DropTargetEvent event) {


	}

}
