package com.sg.cpm.myworks.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.model.CompositionSelectionProvider;
import com.sg.common.service.MessageObject;
import com.sg.common.service.MessageObjectSorter;
import com.sg.common.service.ServiceException;
import com.sg.common.service.WorkflowService;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.IEventListener;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.user.UserSessionContext;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.editor.SingleObjectEditor;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.part.ColumnAutoResizer;
import com.sg.widget.part.IUpdateablePart;

public class WorkInBox extends ViewPart implements IUpdateablePart {

	private static final int COLUMNWIDTH = 350;

	private static final int ROWHEIGHT = 70;

	private TreeViewer readyBox;

//	private TreeViewer processBox;

	private TreeViewer pauseBox;

	private TreeViewer closeAndCancelBox;

	private List<MessageObject> readyList;

	private List<MessageObject> pauseList;

//	private List<MessageObject> processList;

	private List<MessageObject> closeAndCancelList;

	private DBCollection taskCollection;

	private CTabItem[] box = new CTabItem[3];

	private CTabFolder tab;

	private Job job;

	private ObjectId useroid;
	
	private String userId;

	private DBCollection inChargedMessageCollection;

	private DBCollection participatedMessageCollection;

	private MessageObjectSorter readBoxSorter;

//	private MessageObjectSorter processBoxSorter;

	private MessageObjectSorter pauseBoxSorter;

	private MessageObjectSorter closeAndCancelBoxSorter;

	public final static String[] defaultSortKeys = new String[] { "message@" + IDBConstants.FIELD_CREATE_DATE + ",-1" };

	private CompositionSelectionProvider compositeSelectionProvider = new CompositionSelectionProvider();

	private boolean workFilter = false;
	
	private final ViewerFilter myWorkFilter = new ViewerFilter() {
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			
			if(element instanceof MessageObject){
				SingleObject work = ((MessageObject)element).getTargetSingleObject();
				DBObject wfinfo = (DBObject) work.getValue(IDBConstants.FIELD_WFINFO);
				if(wfinfo!=null){
					String actorId = (String) wfinfo.get(IDBConstants.FIELD_WFINFO_ACTORID);
					return userId.equals(actorId);
				}else{
					return true;
				}
			}else{
				return true;
			}
		}

	};

	protected String actorinfo;

	private WorkflowService workflowService;

	private String kbname;


	public WorkInBox() {
	}

	private void initSetting() {
		workflowService = BusinessService.getWorkflowService();
		inChargedMessageCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER_WORK_IN_CHARGED);
		participatedMessageCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER_WORK_PARTTICIPATED);
		taskCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);

		UserSessionContext session = UserSessionContext.getSession();
		useroid = session.getUserOId();
		userId = session.getUserId();
		kbname = workflowService.getCurrentSiteKnowledgebaseName();
		actorinfo = session.getUserFullName()+"/"+session.getUserName();
	}

	@Override
	public void createPartControl(Composite parent) {
		initSetting();

		// 创建一个抽屉，包含准备状态的，正在进行的，暂停的，完成和取消的
		tab = new CTabFolder(parent, SWT.BOTTOM | SWT.FLAT);
		tab.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				readyBox.setSelection(null);
//				processBox.setSelection(null);
				pauseBox.setSelection(null);
				closeAndCancelBox.setSelection(null);
			}
		});

		createReadyBox(tab);

//		CreateProcessBox(tab);

		createPauseBox(tab);

		createCloseAndCancelBox(tab);

		setStyle();

//		loadData();

		setInput();

		tab.setSelection(0);

		setTimerUpdate();

		// 组合选择提供者
		transferSelection();
	}

	private void transferSelection() {
		readyBox.addPostSelectionChangedListener(compositeSelectionProvider);
//		processBox.addPostSelectionChangedListener(compositeSelectionProvider);
		pauseBox.addPostSelectionChangedListener(compositeSelectionProvider);
		closeAndCancelBox.addPostSelectionChangedListener(compositeSelectionProvider);

		getSite().setSelectionProvider(compositeSelectionProvider);
	}

	/**
	 * 设置定时更新
	 */
	private void setTimerUpdate() {
		job = new Job("更新工作信息") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				loadData();
				return Status.OK_STATUS;
			}

		};
		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				IWorkbenchPartSite site = getSite();
				Shell shell = site.getShell();
				if (shell == null || shell.isDisposed()) {
					return;
				}
				Display display = shell.getDisplay();
				if (display == null) {
					return;
				}
				display.asyncExec(new Runnable() {
					public void run() {
						boolean b = updateInput();
						if (b)
							job.schedule(BusinessService.getDefault().getWorkRefreshInterval()*1000);
					}
				});
			}
		});

		job.schedule();
	}

	private void setStyle() {
		tab.setData(RWT.CUSTOM_VARIANT, "inEditor");
		CTabItem[] items = tab.getItems();
		for (CTabItem item : items) {
			item.setData(RWT.CUSTOM_VARIANT, "inEditor");
		}
	}

	public void loadData() {
		//检查流程获得的任务，并同步到我的任务清单
		
		BusinessService.getWorkflowService().syncCurrentHumanTask(userId);
		
		
		
		readyList = new ArrayList<MessageObject>();
//		processList = new ArrayList<MessageObject>();
		pauseList = new ArrayList<MessageObject>();
		closeAndCancelList = new ArrayList<MessageObject>();

		DBCursor cur1 = inChargedMessageCollection.find(new BasicDBObject().append(IDBConstants.FIELD_USEROID, useroid).append(
				IDBConstants.FIELD_MARK_DELETE, new BasicDBObject().append("$ne", true)));

		while (cur1.hasNext()) {
			DBObject message = cur1.next();
			try {
				MessageObject messageObject = new MessageObject(message, inChargedMessageCollection, taskCollection, true);
				appendWorkToInput(messageObject);
			} catch (ServiceException e) {
				//删除message
				inChargedMessageCollection.remove(message);
			}
		}

		DBCursor cur2 = participatedMessageCollection.find(new BasicDBObject().append(IDBConstants.FIELD_USEROID, useroid).append(
				IDBConstants.FIELD_MARK_DELETE, new BasicDBObject().append("$ne", true)));

		while (cur2.hasNext()) {
			DBObject message = cur2.next();
			try {
				MessageObject messageObject = new MessageObject(message, participatedMessageCollection, taskCollection, false);
				appendWorkToInput(messageObject);
			} catch (ServiceException e) {
				//删除message
				participatedMessageCollection.remove(message);
			}
		}
		
		//处理通知类的活动
		updateNotice();

	}

	//处理用户通知类的活动
	private void updateNotice() {
		//这些活动在readList中
		
		HashSet<MessageObject> autoCompeletItem = new HashSet<MessageObject>();
		for(int i=0;i<readyList.size();i++){
			MessageObject readyItem = readyList.get(i);
			DBObject wfInfo = (DBObject) readyItem.getMessageValue(IDBConstants.FIELD_WFINFO);
			if(wfInfo!=null){
				//如果已经完成，就不必处理了
				if(IDBConstants.VALUE_WF_STATUS_COMPLETE.equals(wfInfo.get(IDBConstants.FIELD_WFINFO_TASKSTATUS))){
					continue;
				}
				
				//如果没有显示过，标识为显示了
				Object taskComment = wfInfo.get(IDBConstants.FIELD_WFINFO_TASKCOMMENT);
				if("isNotice".equals(taskComment)){
					
					Object noticeTaskNoticed = readyItem.getMessageValue(IDBConstants.FIELD_NOTICETASK_NOTICED);
					if(Boolean.TRUE.equals(noticeTaskNoticed)){
						//完成这项任务
						DBObject workData = readyItem.getTargetSingleObject().getData();
						ObjectId workId = (ObjectId) workData.get(IDBConstants.FIELD_SYSID);
						try {
							wfInfo = workflowService.completeTask(wfInfo,userId, workId, null,null,kbname);
							readyItem.putMessageValue(IDBConstants.FIELD_WFINFO, wfInfo);
							autoCompeletItem.add(readyItem);
						} catch (ServiceException e) {
						}
					}else{
						//标记为真
						readyItem.putMessageValue(IDBConstants.FIELD_NOTICETASK_NOTICED, Boolean.TRUE);
						readyItem.saveMessage();
					}
				}
			}
		}
		readyList.removeAll(autoCompeletItem);
	}

	private boolean updateInput() {
		if (readyBox.getControl().isDisposed() 
//				|| processBox.getControl().isDisposed() 
				|| pauseBox.getControl().isDisposed()
				|| closeAndCancelBox.getControl().isDisposed()) {
			return false;
		}
		// 记录已经展开的节点
		Object[] e1 = readyBox.getExpandedElements();
//		Object[] e2 = processBox.getExpandedElements();
		Object[] e3 = pauseBox.getExpandedElements();
		Object[] e4 = closeAndCancelBox.getExpandedElements();

		setInput();

		readyBox.setExpandedElements(e1);
//		processBox.setExpandedElements(e2);
		pauseBox.setExpandedElements(e3);
		closeAndCancelBox.setExpandedElements(e4);

		return true;
	}

	private void setInput() {
		readyBox.setInput(readyList);
//		processBox.setInput(processList);
		pauseBox.setInput(pauseList);
		closeAndCancelBox.setInput(closeAndCancelList);
	}

	private void appendWorkToInput(MessageObject messageModel) {
		if (messageModel.isReady()) {
			if (!readyList.contains(messageModel))
				readyList.add(messageModel);
		} else if (messageModel.isProcess()) {
			if (!readyList.contains(messageModel))
				readyList.add(messageModel);
		} else if (messageModel.isPause()) {
			if (!pauseList.contains(messageModel))
				pauseList.add(messageModel);
		} else if (messageModel.isClose()) {
			if (!closeAndCancelList.contains(messageModel))
				closeAndCancelList.add(messageModel);
		} else if (messageModel.isCancel()) {
			if (!closeAndCancelList.contains(messageModel))
				closeAndCancelList.add(messageModel);
		}
	}

	private void createReadyBox(CTabFolder parent) {
		box[0] = new CTabItem(parent, SWT.NONE, 0);
		box[0].setText(UIConstants.TEXT_READY_BOX);
		box[0].setImage(Resource.getImage(Resource.WORK_READY16));
		
		
		
		final Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());

		readyBox = new TreeViewer(panel, SWT.FULL_SELECTION);
		readyBox.setUseHashlookup(true);
		readyBox.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		readyBox.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT, ROWHEIGHT);
		readBoxSorter = new MessageObjectSorter(readyBox, defaultSortKeys);
		readyBox.setContentProvider(new WorkboxContentProvider());

		final TreeViewerColumn col = new TreeViewerColumn(readyBox, SWT.NONE);
		col.getColumn().setWidth(COLUMNWIDTH);
		col.setLabelProvider(new WorkLabelProvider());

		new NavigatorPanel(this, readyBox);
		readyBox.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection != null && !selection.isEmpty()) {
					Object element = ((IStructuredSelection) selection).getFirstElement();
					edit(readyBox,element);
				}
			}
			
		});
		
		new ColumnAutoResizer(panel,col.getColumn());

		box[0].setControl(panel);
	}

//	private void CreateProcessBox(CTabFolder parent) {
//		box[1] = new CTabItem(parent, SWT.NONE, 1);
//		box[1].setText(UIConstants.TEXT_PROCESS_BOX);
//		box[1].setImage(Resource.getImage(Resource.WORK_PROCESS16));
//		Composite panel = new Composite(parent, SWT.NONE);
//		panel.setLayout(new FillLayout());
//
//		processBox = new TreeViewer(panel, SWT.FULL_SELECTION);
//		processBox.setUseHashlookup(true);
//		processBox.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
//		processBox.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT, ROWHEIGHT);
//		processBoxSorter = new MessageObjectSorter(processBox, defaultSortKeys);
//		processBox.setContentProvider(new WorkboxContentProvider());
//
//		TreeViewerColumn col = new TreeViewerColumn(processBox, SWT.NONE);
//		col.getColumn().setWidth(COLUMNWIDTH);
//		col.setLabelProvider(new WorkLabelProvider());
//
//		new NavigatorPanel(this, processBox);
//		box[1].setControl(panel);
//	}

	protected void edit(final TreeViewer viewer, final Object element) {
		if (element instanceof DBObject) {// 选择的是文档对象
			DBObject doc = (DBObject) element;
			String editorId = (String) doc.get(IDBConstants.FIELD_SYSTEM_EDITOR);
			DBCollection docCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT);
			SingleObject data = new SingleObject(docCollection, doc);
			data.addEventListener(new IEventListener(){

				@Override
				public void event(String code, ISingleObject singleObject) {
					if (ISingleObject.UPDATED.equals(code)) {
						viewer.update(element, null);
					}					
				}
			}
			);
			SingleObjectEditorInput input = new SingleObjectEditorInput(editorId, data);
			SingleObjectEditor.OPEN(input);

		} else {// 选择的是工作通知对象
			MessageObject message = (MessageObject) element;
			String editorId = UIConstants.EDITOR_WORK_READ;
			SingleObject data = message.getTargetSingleObject();
			SingleObjectEditorInput input = new SingleObjectEditorInput(editorId, data);
			SingleObjectEditorDialog.getInstance(getSite().getShell(), editorId, input, null, false).open();
		}

	}

	private void createPauseBox(CTabFolder parent) {
		box[1] = new CTabItem(parent, SWT.NONE, 1);
		box[1].setText(UIConstants.TEXT_PAUSE_BOX);
		box[1].setImage(Resource.getImage(Resource.WORK_STOP16));
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());

		pauseBox = new TreeViewer(panel, SWT.FULL_SELECTION);
		pauseBox.setUseHashlookup(true);
		pauseBox.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		pauseBox.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT, ROWHEIGHT);
		pauseBoxSorter = new MessageObjectSorter(pauseBox, defaultSortKeys);

		pauseBox.setContentProvider(new WorkboxContentProvider());

		TreeViewerColumn col = new TreeViewerColumn(pauseBox, SWT.NONE);
		col.getColumn().setWidth(COLUMNWIDTH);
		col.setLabelProvider(new WorkLabelProvider());

		new NavigatorPanel(this, pauseBox);

		box[1].setControl(panel);
	}

	private void createCloseAndCancelBox(CTabFolder parent) {
		box[2] = new CTabItem(parent, SWT.NONE, 2);
		box[2].setText(UIConstants.TEXT_CLOSENCANCEL_BOX);
		box[2].setImage(Resource.getImage(Resource.WORK_CLOSE16));
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FillLayout());

		closeAndCancelBox = new TreeViewer(panel, SWT.FULL_SELECTION);
		closeAndCancelBox.setUseHashlookup(true);
		closeAndCancelBox.getTree().setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		closeAndCancelBox.getTree().setData(RWT.CUSTOM_ITEM_HEIGHT, ROWHEIGHT);

		closeAndCancelBoxSorter = new MessageObjectSorter(closeAndCancelBox, defaultSortKeys);
		closeAndCancelBox.setContentProvider(new WorkboxContentProvider());

		TreeViewerColumn col = new TreeViewerColumn(closeAndCancelBox, SWT.NONE);
		col.getColumn().setWidth(COLUMNWIDTH);
		col.setLabelProvider(new WorkLabelProvider());

		new NavigatorPanel(this, pauseBox);

		box[2].setControl(panel);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean needUpdate() {
		return true;
	}

	@Override
	public void update() {
		loadData();
		updateInput();
	}

	public void messageStarted(MessageObject messageObject, TreeViewer viewer) {
		viewer.refresh(messageObject);
//		List<MessageObject> list = (List<MessageObject>) viewer.getInput();
//		list.remove(messageObject);
//		viewer.remove(messageObject);
//		processList.add(messageObject);
//		processBox.refresh();
	}

	public void messageFinished(MessageObject messageObject, TreeViewer viewer) {
		List<MessageObject> list = (List<MessageObject>) viewer.getInput();
		list.remove(messageObject);
		viewer.remove(messageObject);

		closeAndCancelList.add(messageObject);
		closeAndCancelBox.refresh();
	}

	public void setSort(int direct, String key) {
		readBoxSorter.newSort(new String[] { key + "," + direct });
//		processBoxSorter.newSort(new String[] { key + "," + direct });
		pauseBoxSorter.newSort(new String[] { key + "," + direct });
		closeAndCancelBoxSorter.newSort(new String[] { key + "," + direct });
	}

	public void unSort() {
		readBoxSorter.newSort(null);
//		processBoxSorter.newSort(null);
		pauseBoxSorter.newSort(null);
		closeAndCancelBoxSorter.newSort(null);
	}

	public TreeViewer getReadyBox() {
		return readyBox;
	}

//	public TreeViewer getProcessBox() {
//		return processBox;
//	}

	public TreeViewer getPauseBox() {
		return pauseBox;
	}

	public TreeViewer getCloseAndCancelBox() {
		return closeAndCancelBox;
	}

	public TreeViewer getCurrentViewer() {
		int index = tab.getSelectionIndex();

//		switch (index) {
//		case 0:
//			return readyBox;
//		case 1:
//			return processBox;
//		case 2:
//			return pauseBox;
//		case 3:
//			return closeAndCancelBox;
//		default:
//			return null;
//		}
		
		switch (index) {
		case 0:
			return readyBox;
		case 1:
			return pauseBox;
		case 2:
			return closeAndCancelBox;
		default:
			return null;
		}
	}

	public boolean switchProcessFilter() {
		workFilter  = !workFilter;
		
		if(workFilter){
			readyBox.setFilters(new ViewerFilter[]{myWorkFilter});
		}else{
			readyBox.removeFilter(myWorkFilter);
		}
		
		return workFilter;
	}

}
