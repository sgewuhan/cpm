package com.sg.cpm.myworks.view;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.MessageObject;
import com.sg.common.service.ServiceException;
import com.sg.db.DBActivator;
import com.sg.user.UserSessionContext;

public class Recyclebin extends ViewPart {

	private static final int COLUMNWIDTH = 350;

	private static final int ROWHEIGHT = 64;

	private TreeViewer recycleBox;

	private List<MessageObject> messageObjectList = new ArrayList<MessageObject>();

	private DBCollection inChargedMessageCollection;

	private DBCollection participatedMessageCollection;

	private DBCollection taskCollection;

	private ObjectId useroid;

	public Recyclebin() {

	}

	private void initSetting() {

		inChargedMessageCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER_WORK_IN_CHARGED);
		participatedMessageCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED);
		taskCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);

		useroid = UserSessionContext.getSession().getUserOId();

	}

	@Override
	public void createPartControl(Composite parent) {

		initSetting();

		recycleBox = new TreeViewer(parent, SWT.FULL_SELECTION);
		recycleBox.setUseHashlookup(true);
		recycleBox.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		recycleBox.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT, ROWHEIGHT);
		recycleBox.setContentProvider(new WorkboxContentProvider());

		TreeViewerColumn col = new TreeViewerColumn(recycleBox, SWT.NONE);
		col.getColumn().setWidth(COLUMNWIDTH);
		col.setLabelProvider(new WorkLabelProvider());

		DBCursor cur1 = inChargedMessageCollection.find(new BasicDBObject().append(IDBConstants.FIELD_USEROID, useroid).append(IDBConstants.FIELD_MARK_DELETE,
				true));

		while (cur1.hasNext()) {
			DBObject message = cur1.next();

			try {
				MessageObject messageObject = new MessageObject(message, inChargedMessageCollection, taskCollection, true);
				appendWorkToInput(messageObject);
			} catch (ServiceException e) {
				inChargedMessageCollection.remove(message);
			}
		}

		DBCursor cur2 = participatedMessageCollection.find(new BasicDBObject().append(IDBConstants.FIELD_USEROID, useroid).append(
				IDBConstants.FIELD_MARK_DELETE, true));

		while (cur2.hasNext()) {
			DBObject message = cur2.next();
			try {
				MessageObject messageObject = new MessageObject(message, participatedMessageCollection, taskCollection, false);
				appendWorkToInput(messageObject);
			} catch (ServiceException e) {
				participatedMessageCollection.remove(message);
			}
		}

		recycleBox.setInput(messageObjectList);

	}

	private void appendWorkToInput(MessageObject messageObject) {

		if (!messageObjectList.contains(messageObject)) {
			messageObjectList.add(messageObject);
		}
	}

	@Override
	public void setFocus() {

	}

}
