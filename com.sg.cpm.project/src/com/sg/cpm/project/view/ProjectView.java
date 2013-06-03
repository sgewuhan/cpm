package com.sg.cpm.project.view;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.sg.common.db.DataUtil;
import com.sg.db.model.ISingleObject;
import com.sg.widget.part.NavigatableTableView;

public class ProjectView extends NavigatableTableView {


	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		update();
	}

	@Override
	public void update() {
		List<ISingleObject> input = DataUtil.getContextControlProjectList();
		getViewer().setInput(input);
	}


}
