package com.sg.document.tmt.editor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.PageConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.AbstractPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.util.FileUtil;
import com.sg.widget.util.Util;

public class ReviewCommentPage extends AbstractPageDelegator implements
		ISelectionChangedListener {

	private static final String REPLY_EDITOR_ID = "com.tmt.projectreview2.reviewreply";
	private static final String REVIEW_EDITOR_ID = "com.tmt.projectreview2.review";
	private ISingleObjectEditorInput input;
	private TreeViewer list;
	private HashMap<ObjectId, DBObject> questionMap;
	private HashMap<ObjectId, List<DBObject>> replyMap;
	private Button addReply;
	private Button removeReply;
	private Button editReply;
	private boolean showEditButtons;
	private Text box;

	public ReviewCommentPage() {
	}

	@Override
	public Composite createPageContent(Composite parent,
			ISingleObjectEditorInput input, PageConfiguration conf) {
		this.input = input;

		// 判断当前的用户是不是项目负责人，如果是，显示按钮，如果不是不显示按钮
		showEditButtons = isCurrentProjectManager();

		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FormLayout());

		if (showEditButtons) {
			createToolbar(panel);
		}
//
		createContent(panel);
//		createCommentTree(panel);

		refresh();
		return panel;
	}

	private void createContent(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		
		
		createCommentTree(sashForm);
		createCommentBox(sashForm);
		sashForm.setWeights(new int[] {3,2 });
		
		FormData fd = new FormData();
		sashForm.setLayoutData(fd);
		if (showEditButtons) {
			fd.top = new FormAttachment(addReply, 4);
		} else {
			fd.top = new FormAttachment(0, 0);
		}
		fd.left = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(100, 0);
		fd.right = new FormAttachment(100, 0);

	}

	private void createCommentBox(Composite panel) {
		box = new Text(panel,SWT.MULTI|SWT.WRAP|SWT.READ_ONLY);
	}

	private void createCommentTree(Composite panel) {
		list = new TreeViewer(panel, SWT.FULL_SELECTION);
		list.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		list.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		list.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf(80));
		list.setContentProvider(new ITreeContentProvider() {
			
			@Override
			public void dispose() {
			}
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object inputElement) {
				return ((Collection<ObjectId>) inputElement).toArray();
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof ObjectId) {
					ObjectId id = (ObjectId) parentElement;
					return replyMap.get(id).toArray();
				}
				return null;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof ObjectId) {
					return replyMap.get((ObjectId) element) != null
							&& replyMap.get((ObjectId) element).size() > 0;
				}
				return false;
			}
			
		});
		
		TreeViewerColumn col = new TreeViewerColumn(list, SWT.NONE);
		col.setLabelProvider(new ColumnLabelProvider(){
			
			@Override
			public String getText(Object element) {
				if (element instanceof ObjectId) {
					return getReviewCommentLabel(questionMap
							.get((ObjectId) element));
				} else if (element instanceof DBObject) {
					return getReplyCommentLabel((DBObject) element);
				}
				
				return "";
			}
		});
		col.getColumn().setWidth(624);
		
		list.addSelectionChangedListener(this);
		// TODO Auto-generated method stub
		
	}

	protected String getReplyCommentLabel(DBObject element) {
		String comment = (String) element.get("comment");
		comment = Util.getLimitedString(comment, 160);
		BasicDBList comment_attachment = (BasicDBList) element
				.get("comment_attachment");
		Date _date = (Date) element.get("date");
		String actor = (String) element.get("actor");
		SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YY_MM_DD);
		String date = sdf.format(_date);

		StringBuffer sb = new StringBuffer();
		sb.append("<span style=\" word-break:normal; width:"
				+ 600
				+ "; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;\">");
		sb.append("<strong>" + date + " 项目负责人:" + actor + "  </strong>");
		sb.append("<br/>");
		sb.append("<b>回复：</b>" + comment);
		sb.append("<br/>");
		sb.append(getDownloadLink(comment_attachment));
		sb.append("</span>");
		String html = sb.toString();
		return html;
	}

	protected String getReviewCommentLabel(DBObject element) {
		String comment = (String) element.get("comment");
		comment = Util.getLimitedString(comment, 160);
		BasicDBList comment_attachment = (BasicDBList) element
				.get("comment_attachment");
		Date _date = (Date) element.get("date");
		String actor = (String) element.get("actor");
		SimpleDateFormat sdf = Util.getDateFormat(Util.SDF_YY_MM_DD);
		String date = sdf.format(_date);

		StringBuffer sb = new StringBuffer();
		sb.append("<span style=\" word-break:normal; width:"
				+ 600
				+ "; display:block; white-space:pre-wrap;word-wrap : break-word ;overflow: hidden ;\">");

		sb.append("<strong>" + date + " 评审专家:" + actor + "  </strong>");
		sb.append("<br/>");
		sb.append("<b>意见：</b>" + comment);
		sb.append("<br/>");
		sb.append(getDownloadLink(comment_attachment));
		sb.append("</span>");
		String html = sb.toString();
		return html;
	}

	private String getDownloadLink(BasicDBList attachments) {
		if (attachments == null || attachments.size() < 1) {
			return "";
		}

		String result = "附件:";

		for (int i = 0; i < attachments.size(); i++) {
			DBObject item = (DBObject) attachments.get(i);
			String namespace = (String) item.get("namespace");
			String fileName = (String) item.get("fileName");
			ObjectId fid = (ObjectId) item.get("_id");
			String url = FileUtil.getGridfsDownloadUrl(namespace,
					fid.toString(), fileName);
			url = url.replaceAll("&", "&amp;");
			result = result + " <a href=\"" + url + "\"  target=\"_blank\">"
					+ fileName + "</a>";
		}

		return result;
	}

	private void createToolbar(Composite panel) {
		addReply = new Button(panel, SWT.PUSH);
		addReply.setText("回复专家意见");
		addReply.setImage(Resource.getImage(Resource.CREATE_DELIVERY32));
		addReply.setData(RWT.CUSTOM_VARIANT,
				UIConstants.WIDGET_CSS_IN_EDITOR);

		addReply.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAddReply();
			}

		});
		FormData fd = new FormData();
		addReply.setLayoutData(fd);
		fd.top = new FormAttachment(0, 4);
		fd.height = 38;
		fd.left = new FormAttachment(0, 4);

		removeReply = new Button(panel, SWT.PUSH);
		removeReply.setText("删除回复");
		removeReply.setImage(Resource.getImage(Resource.REMOVE_DELIVERY32));
		removeReply.setData(RWT.CUSTOM_VARIANT,
				UIConstants.WIDGET_CSS_IN_EDITOR);

		removeReply.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemoveReply();
			}

		});
		fd = new FormData();
		removeReply.setLayoutData(fd);
		fd.top = new FormAttachment(0, 4);
		fd.height = 38;
		fd.left = new FormAttachment(addReply, 4);

		editReply = new Button(panel, SWT.PUSH);
		editReply.setText("查看编辑");
		editReply.setImage(Resource.getImage(Resource.EDIT_PROP32));
		editReply.setData(RWT.CUSTOM_VARIANT,
				UIConstants.WIDGET_CSS_IN_EDITOR);
		editReply.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEditReply();
			}

		});
		fd = new FormData();
		editReply.setLayoutData(fd);
		fd.top = new FormAttachment(0, 4);
		fd.height = 38;
		fd.left = new FormAttachment(removeReply, 4);
	}

	private boolean isCurrentProjectManager() {
		DBObject currentUser = BusinessService.getOrganizationService()
				.getCurrentUserData();
		DBObject project = (DBObject) input.getInputData().getValue("project");
		DBObject pm = (DBObject) project.get(IDBConstants.FIELD_PROJECT_PM);
		ObjectId userOid = (ObjectId) pm.get(IDBConstants.FIELD_SYSID);
		return currentUser.get(IDBConstants.FIELD_SYSID).equals(userOid);
	}

	protected void handleEditReply() {
		IStructuredSelection sel = (IStructuredSelection) list.getSelection();
		if (sel == null || sel.isEmpty()) {
			return;
		}
		final Object element = sel.getFirstElement();

		EditorConfiguration ec = null;
		ISingleObjectEditorInput seinput = null;
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		if (element instanceof ObjectId) {
			ec = Widget.getSingleObjectEditorConfiguration(REVIEW_EDITOR_ID);
			seinput = new SingleObjectEditorInput(ec,
					new SingleObject().setData(questionMap.get(element)));
			seinput.setEditable(false);
			SingleObjectEditorDialog.getInstance(shell, REVIEW_EDITOR_ID,
					seinput, null, false).open();
		} else if (element instanceof DBObject) {
			ec = Widget.getSingleObjectEditorConfiguration(REPLY_EDITOR_ID);
			seinput = new SingleObjectEditorInput(ec,
					new SingleObject().setData((DBObject) element));
			ISingleObjectEditorDialogCallback handler = new SingleObjectEditorDialogCallback() {

				@Override
				public boolean saveBefore(ISingleObjectEditorInput input) {
					
					ObjectId docId = (ObjectId) ReviewCommentPage.this.input
							.getInputData().getValue(IDBConstants.FIELD_SYSID);
					DBCollection docCollection = DBActivator
							.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);

					
					ObjectId reviewId = (ObjectId) ((DBObject)element).get("reviewId");
					List<DBObject> replyList = replyMap.get(reviewId);
					
					docCollection.update(new BasicDBObject().append(
							IDBConstants.FIELD_SYSID, docId),
							new BasicDBObject().append("$set",
									new BasicDBObject().append("replycomment",
											replyList)));
					
					list.update(element, null);
					return false;
				}

			};

			int ok = SingleObjectEditorDialog.getInstance(shell,
					REPLY_EDITOR_ID, seinput, handler, false).open();
			if (ok == SingleObjectEditorDialog.OK) {
				setDirty(true);
			} else {
			}
		} else {
			return;
		}

	}

	protected void handleRemoveReply() {
		IStructuredSelection sel = (IStructuredSelection) list.getSelection();
		if (sel == null || sel.isEmpty()) {
			return;
		}
		Object element = sel.getFirstElement();
		if (element instanceof ObjectId) {
			return;
		}

		DBObject replyData = (DBObject) element;
		BasicDBList replyDataList = (BasicDBList) input.getInputData()
				.getValue("replycomment");
		if (replyDataList == null)
			return;
		for (int i = 0; i < replyDataList.size(); i++) {
			DBObject item = (DBObject) replyDataList.get(i);
			Object _id = item.get(IDBConstants.FIELD_SYSID);
			if (replyData.get(IDBConstants.FIELD_SYSID).equals(_id)) {
				replyDataList.remove(item);
			}
		}

		ObjectId reviewId = (ObjectId) replyData.get("reviewId");
		List<DBObject> replyItems = replyMap.get(reviewId);
		replyItems.remove(replyData);
		list.refresh(reviewId);

		ObjectId docId = (ObjectId) input.getInputData().getValue(
				IDBConstants.FIELD_SYSID);
		DBCollection docCollection = DBActivator
				.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
		docCollection.update(new BasicDBObject().append(
				IDBConstants.FIELD_SYSID, docId), new BasicDBObject().append(
				"$set",
				new BasicDBObject().append("replycomment", replyDataList)));

	}

	protected void handleAddReply() {
		IStructuredSelection sel = (IStructuredSelection) list.getSelection();
		if (sel == null || sel.isEmpty()) {
			return;
		}
		final Object element = sel.getFirstElement();
		if (!(element instanceof ObjectId)) {
			return;
		}
		// 显示对话框
		SingleObjectEditorDialogCallback handler = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				DBObject replyData = input.getInputData().getData();
				replyData.put("reviewId", element);
				replyData.put("_id", new ObjectId());

				ObjectId docId = (ObjectId) ReviewCommentPage.this.input
						.getInputData().getValue(IDBConstants.FIELD_SYSID);
				DBCollection docCollection = DBActivator
						.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
				List<DBObject> replyList = replyMap.get(element);
				if (replyList == null) {
					replyList = new ArrayList<DBObject>();
				}

				replyList.add(replyData);
				docCollection.update(new BasicDBObject().append(
						IDBConstants.FIELD_SYSID, docId), new BasicDBObject()
				.append("$push", new BasicDBObject().append(
						"replycomment", replyData)));
				replyMap.put((ObjectId) element, replyList);
				list.refresh(element);
				return false;
			}

		};
		EditorConfiguration ec = Widget
				.getSingleObjectEditorConfiguration(REPLY_EDITOR_ID);
		ISingleObjectEditorInput taskInput = new SingleObjectEditorInput(ec,
				new SingleObject().setData(new BasicDBObject()));
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		int ok = SingleObjectEditorDialog.getInstance(shell, REPLY_EDITOR_ID,
				taskInput, handler, false).open();
		if (ok == SingleObjectEditorDialog.OK) {
			setDirty(true);
		} else {
		}

	}

	@Override
	public IFormPart getFormPart() {
		return this;
	}

	@Override
	public void initialize(IManagedForm form) {

	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {

	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {

	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		BasicDBList reviewcomment = (BasicDBList) input.getInputData()
				.getValue("reviewcomment");
		BasicDBList replycomment = (BasicDBList) input.getInputData().getValue(
				"replycomment");

		// 构造一个树结构
		questionMap = new HashMap<ObjectId, DBObject>();
		replyMap = new HashMap<ObjectId, List<DBObject>>();

		if (reviewcomment != null) {
			for (int i = 0; i < reviewcomment.size(); i++) {
				DBObject item = (DBObject) reviewcomment.get(i);
				questionMap.put((ObjectId) item.get(IDBConstants.FIELD_SYSID),
						item);
			}
		}

		if (replycomment != null) {
			for (int i = 0; i < replycomment.size(); i++) {
				DBObject item = (DBObject) replycomment.get(i);
				ObjectId reviewId = (ObjectId) item.get("reviewId");
				List<DBObject> replyList = replyMap.get(reviewId);
				if (replyList == null) {
					replyList = new ArrayList<DBObject>();
				}
				replyList.add(item);
				replyMap.put(reviewId, replyList);
			}
		}

		list.setInput(questionMap.keySet());
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection is = (IStructuredSelection) event.getSelection();
		if (is != null && !is.isEmpty()) {
			Object element = is.getFirstElement();
			if (showEditButtons) {
				if (element instanceof ObjectId) {
					addReply.setEnabled(true);
					removeReply.setEnabled(false);
				} else {
					addReply.setEnabled(false);
					removeReply.setEnabled(true);
				}
			}
			
			DBObject data = null;
			if (element instanceof ObjectId) {
				data = questionMap.get(element);
			} else if(element instanceof DBObject){
				data = (DBObject) element;
			}
			if(data!=null){
				String comment = (String) data.get("comment");
				if(comment==null){
					box.setText("");
				}else{
					box.setText(comment);
				}
			}
		}
	}

}
