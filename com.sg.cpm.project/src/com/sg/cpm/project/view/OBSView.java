package com.sg.cpm.project.view;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.part.NavigatableTableView;
import com.sg.widget.part.NavigatableTreeView;
import com.sg.widget.viewer.tableviewer.QueryTableViewer;

public class OBSView extends NavigatableTreeView {
	class DropTargetForSetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		}

		public void dragLeave(final DropTargetEvent event) {
		}

		public void dragOperationChanged(final DropTargetEvent event) {
		}

		public void dragOver(final DropTargetEvent event) {
		}

		public void drop(final DropTargetEvent event) {
			if(!hasEditAuth()){
				return;
			}
			
			String jsonOBS = (String) event.data;
			if (jsonOBS.equals("#")) {
				return;
			}
			CascadeObject parent = (CascadeObject) event.item.getData();
			if (parent == null)
				return;

			if (DataUtil.isUserObject(parent)) {
				MessageDialog.openWarning(getSite().getShell(), UIConstants.TEXT_ORGEDIT, UIConstants.MESSAGE_CANNOT_SETUSER_UNDER_AUSER);

				return;
			}

			DBObject user = (DBObject) JSON.parse(jsonOBS);
			
			if(user.containsField(IDBConstants.FIELD_OBSPARENT)){
				MessageDialog.openWarning(getSite().getShell(), UIConstants.TEXT_ORGEDIT, UIConstants.ONLY_MOVE_USERFROMUSERLIST);
				return;
			}

			
			ObjectId uoid = (ObjectId) user.get(IDBConstants.FIELD_SYSID);

			// 如果parent下已经有了这个用户，返回
			List<CascadeObject> children = parent.loadChildren();
			for (CascadeObject co : children) {
				if (uoid.equals(co.getValue(IDBConstants.FIELD_USEROID))) {
					MessageDialog.openWarning(getSite().getShell(), UIConstants.TEXT_ORGEDIT,
							UIConstants.MESSAGE_HAVE_A_SAME_USER_UNDER_A_ORG);

					return;
				}
			}

			// 将拖来的用户保存到OBS
			// DBObject obsUser = new BasicDBObject();
			// obsUser.put(IDBConstants.FIELD_SYSID, new ObjectId());
			// obsUser.put(IDBConstants.FIELD_USEROID, uoid);
			// obsUser.put(IDBConstants.FIELD_TEMPLATE_TYPE,
			// IDBConstants.VALUE_OBS_USERTYPE);
			// obsUser.put(IDBConstants.FIELD_OBSPARENT, parent.getSystemId());
			// collection.insert(obsUser);

			CascadeObject root = getProjectTeam();
			DBObject obsUser = DataUtil
					.createOBSItem(root.getSystemId(), parent.getSystemId(), null, uoid, IDBConstants.VALUE_OBS_USERTYPE);
			parent.createChild(IDBConstants.EXP_CASCADE_SO_OBS, obsUser, collection);
			viewer.refresh(parent, false);
			viewer.expandToLevel(parent, AbstractTreeViewer.ALL_LEVELS);
		}

		public void dropAccept(final DropTargetEvent event) {
		}

	}

	class DragSourceForSetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {
		}

		public void dragSetData(final DragSourceEvent event) {
			ISingleObject currentUser = getCurrentUser();
			if (currentUser != null) {
				event.data = JSON.serialize(currentUser.getData());
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}
		}

		public void dragStart(final DragSourceEvent event) {
		}
	}

	class DropTargetForUnsetListener implements DropTargetListener {

		public void dragEnter(final DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		}

		public void dragLeave(final DropTargetEvent event) {
		}

		public void dragOperationChanged(final DropTargetEvent event) {
		}

		public void dragOver(final DropTargetEvent event) {
		}

		public void drop(final DropTargetEvent event) {
			if(!hasEditAuth()){
				return;
			}
			
			// 删除OBS上的节点
			// String jsonOBS = (String) event.data;
			// if (jsonOBS.equals("#")) {
			// return;
			// }
			// DBObject data = (DBObject) JSON.parse(jsonOBS);
			// orgCollection.remove(new
			// BasicDBObject().append(IDBConstants.FIELD_SYSID,
			// data.get(IDBConstants.FIELD_SYSID)));
			ISingleObject currentOrg = getCurrentOrg();
			if (DataUtil.isUserObject(currentOrg)) {
				currentOrg.remove();
				viewer.setSelection(null);
			}
		}

		public void dropAccept(final DropTargetEvent event) {
		}

	}

	class DragSourceForUnsetListener implements DragSourceListener {

		public void dragFinished(final DragSourceEvent event) {
		}

		public void dragSetData(final DragSourceEvent event) {
			ISingleObject currentOrg = getCurrentOrg();
			if (DataUtil.isUserObject(currentOrg)) {
				event.data = JSON.serialize(currentOrg.getData());
			} else {
				event.data = "#";// 必须要给一个字符串，否则要在javatonative出错。
			}
		}

		public void dragStart(final DragSourceEvent event) {
			// 当选择的是根，不可以拖动，理论上可以通过event.doit来控制哪些可以拖动，但这个在RAP中不起作用
		}
	}

	private QueryTableViewer userViewer;
	private DBCollection collection;

	@Override
	public void createPartControl(Composite parent) {
		collection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_ORG);

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		super.createPartControl(sashForm);
		createUserViewer(sashForm);
		sashForm.setWeights(new int[] { 3, 2 });

		
			setupDND();

		// 加载数据
		loadInitData();
	}

	private void loadInitData() {
		NavigatableTableView view = (NavigatableTableView) getSite().getPage().findView(UIConstants.VIEW_PROJECT_NAVIGATOR);
		QueryTableViewer projectViewer = view.getViewer();
		IStructuredSelection sel = projectViewer.getSelection();
		if (sel == null || sel.isEmpty()) {
			return;
		}
		ISingleObject so = (ISingleObject) sel.getFirstElement();
		Map<String, Object> param = getParametersFromPart(so, UIConstants.VIEW_PROJECT_NAVIGATOR);
		resetData(getExpression(), param);
	}

	private boolean hasEditAuth(){
		
		NavigatableTableView masterView = (NavigatableTableView) getSite().getPage().findView(UIConstants.VIEW_PROJECT_NAVIGATOR);
		QueryTableViewer projectViewer = masterView.getViewer();
		IStructuredSelection sel = projectViewer.getSelection();
		if (sel == null || sel.isEmpty()) {
			return false;
		}
		ISingleObject so = (ISingleObject) sel.getFirstElement();
		if(!DataUtil.isProjectAdmin(so.getData())){
			return false;
		}
		
		return true;
	}
	
	private void setupDND() {

		
		
		setDragSourceForSet(userViewer.getControl());
		setDropTargetForSet(viewer.getControl());

		setDropTargetForUnset(userViewer.getControl());
		setDragSourceForUnset(viewer.getControl());
	}

	private void setDragSourceForSet(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceForSetListener());
	}
	
	private void setDropTargetForSet(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetForSetListener());
	}

	private void setDragSourceForUnset(Control dragControl) {
		DragSource dragSource = new DragSource(dragControl, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceForUnsetListener());
	}

	private void setDropTargetForUnset(Control dropControl) {
		DropTarget dropTarget = new DropTarget(dropControl, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetForUnsetListener());
	}

	public CascadeObject getProjectTeam() {
		return viewer.getExpression();
	}

	private void createUserViewer(Composite parent) {
		setActiveCollectionAdaptable(false);
		userViewer = new QueryTableViewer(parent, SWT.VIRTUAL | SWT.FULL_SELECTION,
				Widget.getQueryTableViewerConfiguration(UIConstants.VIEWER_TABLE_SITEUSER2)) {

			@Override
			protected boolean initLoad() {
				return false;
			}

		};
		QueryExpression exp = userViewer.createExpression();
		exp.setParamValue(IDBConstants.PARAM_INPUT_SITEPARENT, UserSessionContext.getSession().getSiteContextId());
		List<ISingleObject> result = DataUtil.getSiteUsers(UserSessionContext.getSession().getSiteContextId(), true);
		userViewer.runSetInput(result);
		userViewer.addPostSelectionChangedListener(this);
	}

	public ISingleObject getCurrentUser() {
		IStructuredSelection sel = userViewer.getSelection();

		if (sel == null || sel.isEmpty())
			return null;
		return (ISingleObject) sel.getFirstElement();
	}

	public ISingleObject getCurrentOrg() {
		IStructuredSelection sel = viewer.getSelection();

		if (sel == null || sel.isEmpty())
			return null;
		return (ISingleObject) sel.getFirstElement();
	}

}
