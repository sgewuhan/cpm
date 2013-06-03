package com.sg.cpm.myworks.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.service.WorkService;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.part.NavigatableTableView;

public class MyLaunchedWork extends NavigatableTableView {

	@Override
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
		update();
	}

	@Override
	public void update() {

		WorkService service = BusinessService.getWorkService();
		BasicDBList result = service.getUserLaunchedWork(UserSessionContext.getSession().getUserId(), false);
		List<ISingleObject> input = new ArrayList<ISingleObject>();

		for (int i = 0; i < result.size(); i++) {
			input.add(new SingleObject(DBActivator.getDefaultDBCollection(WorkService.COLLECTION_WORK), (DBObject) result.get(i)));
		}

		getViewer().setInput(input);
	}

}
