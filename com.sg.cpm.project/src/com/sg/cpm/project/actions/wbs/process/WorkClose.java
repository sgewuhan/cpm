package com.sg.cpm.project.actions.wbs.process;

import org.eclipse.jface.action.Action;

import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;

public class WorkClose extends Action {

	private ControlMenu controler;

	public WorkClose(ControlMenu controlMenu) {
		setText(UIConstants.TEXT_CLOSE_WORK);
		setImageDescriptor(Resource.getImageDescriptor(Resource.CLOSE32));
		controler = controlMenu;
	}

	@Override
	public void run() {
		SingleObject master = controler.getMasterWork();
		master.setValue(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_CLOSE);
		master.save();
		controler.checkStatus();
	}
}