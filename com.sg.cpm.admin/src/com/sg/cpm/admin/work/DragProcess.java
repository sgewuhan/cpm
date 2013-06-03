package com.sg.cpm.admin.work;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jbpm.workflow.core.node.HumanTaskNode;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.sg.common.BusinessService;
import com.sg.common.service.NodeAssignment;
import com.sg.common.service.ServiceException;

public class DragProcess implements DragSourceListener {

	private WorkTemplateEditor editor;

	public DragProcess(WorkTemplateEditor editor) {

		this.editor = editor;
	}

	@Override
	public void dragStart(DragSourceEvent event) {

	}

	@Override
	public void dragSetData(DragSourceEvent event) {

		DragSource src = (DragSource) event.getSource();
		Table control = (Table) src.getControl();
		TableItem[] selectedItems = control.getSelection();
		if (selectedItems == null || selectedItems.length < 1) {
			event.data = "#";
			return;
		}

		HumanTaskNode node = (HumanTaskNode) selectedItems[0].getData();

		try {
			NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(editor.currentTemplate.getData(), node);
			if (!nass.isAlreadyAssignment()) {
				event.data = "#";
				return;
			}
			String parameter = nass.getNodeActorParameter();
			event.data = JSON.serialize(new BasicDBObject().append("operation", "unset").append("data", parameter));
		} catch (ServiceException e) {
			event.data = "#";
		}

	}

	@Override
	public void dragFinished(DragSourceEvent event) {

	}

}
