package com.sg.cpm.admin.work;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.jbpm.workflow.core.node.HumanTaskNode;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.NodeAssignment;
import com.sg.common.service.ServiceException;

public class DropToProcess implements DropTargetListener {

	private WorkTemplateEditor editor;

	public DropToProcess(WorkTemplateEditor editor) {

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
		// 只处理设置的
		DBObject jsonData = (DBObject) JSON.parse(jsonOBS);
		if (!"set".equals(jsonData.get("operation"))) {
			return;
		}
		Object obsid = jsonData.get("data");

		Object target = event.item.getData();
		if (target instanceof HumanTaskNode) {
			// 将obsitem的对象oid写到任务上
			try {
				NodeAssignment nass = BusinessService.getWorkflowService().getNodeAssignment(editor.currentTemplate.getData(), (HumanTaskNode) target);
				if (nass.isNotNeedAssignment()) {
					return;
				}

				DBObject assDef = (DBObject) editor.currentTemplate.getValue(IDBConstants.FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION);
				if (assDef == null) {
					assDef = new BasicDBObject();
				}

				String parameter = nass.getNodeActorParameter();
				assDef.put(parameter, obsid);

				editor.currentTemplate.setValue(IDBConstants.FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION, assDef);
				editor.currentTemplate.setValue(IDBConstants.FIELD_ACTIVATE, false);
				editor.currentTemplate.save();
				editor.processViewer.update(target, null);
			} catch (ServiceException e) {
			}
		}

	}

	@Override
	public void dropAccept(DropTargetEvent event) {

	}

}
