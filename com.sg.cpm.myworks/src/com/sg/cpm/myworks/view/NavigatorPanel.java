package com.sg.cpm.myworks.view;

import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.service.MessageObject;
import com.sg.common.service.ServiceException;
import com.sg.common.service.WorkflowService;
import com.sg.common.ui.UIConstants;
import com.sg.common.workflow.IValidationHandler;
import com.sg.common.workflow.IWorkflowInfoProvider;
import com.sg.common.workflow.TaskForm;
import com.sg.common.workflow.TaskFormConfig;
import com.sg.resource.Resource;

public class NavigatorPanel implements MouseListener {

	private static final String NAVIMENU = "navimenu";

	private TreeViewer viewer;

	private Button startWorkButton;

	private Button markReadButton;

	private Button markStarButton;

	private Button finishWorkButton;

	private Button closeButton;

	private Shell navi;

	private boolean enable;

	private MessageObject messageObject;

	private WorkInBox box;

	private Button startTaskButton;

	private Button finishTaskButton;

	private WorkflowService workflowService;

	public NavigatorPanel(WorkInBox workInBox, TreeViewer viewer) {

		this.viewer = viewer;
		viewer.getTree().addMouseListener(this);
		box = workInBox;
		workflowService = BusinessService.getWorkflowService();
		
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {

	}

	@Override
	public void mouseDown(MouseEvent e) {

	}

	@Override
	public void mouseUp(MouseEvent e) {

		if (e.button == 3) {
			activeNavigator(e);
		}
	}

	private void activeNavigator(MouseEvent e) {

		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if (selection == null || selection.isEmpty()) {
			setEnable(false);
			return;
		} else {
			setEnable(true);
		}

		// 如果选中的是工作，不显示
		Object element = selection.getFirstElement();
		if (!(element instanceof MessageObject)) {
			setEnable(false);
			return;
		}

		if (enable) {
			messageObject = (MessageObject) element;

			createNavi();
			Rectangle b = navi.getBounds();
			Control control = (Control) e.widget;
			Point point = control.toDisplay(e.x, e.y);
			point.x = point.x - b.width / 2;
			point.y = point.y - b.height / 2;
			navi.setLocation(point);
			navi.open();
		}
	}

	private void createNavi() {

		// 显示导航
		navi = new Shell(SWT.NONE | SWT.APPLICATION_MODAL);
		GridLayout layout = new GridLayout(3, true);
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		navi.setLayout(layout);

		if (messageObject.isWorkflowData()) {
			//如果是流程中的，判断流程是否是在准备状态时，显示工作启动按钮，否则显示流程启动按钮
			if(messageObject.isReady()){
				createStartWorkButton();
			}else{
				createStartTaskButton();
			}
		} else {
			createStartWorkButton();
		}

		createStarMarkButton();

		createClosePanelButton();

		if (messageObject.isWorkflowData()) {
			//如果是流程中的，判断流程是否是在准备状态时，显示工作启动按钮，否则显示流程启动按钮
			if(messageObject.isReady()){
				createFinishWorkButton();
			}else{
				createFinishTaskButton();
			}
		} else {
			createFinishWorkButton();
		}

		createReadMarkButton();

		navi.setDefaultButton(closeButton);
		closeButton.setFocus();

		navi.pack();
		navi.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		navi.addShellListener(new ShellListener() {

			@Override
			public void shellDeactivated(ShellEvent e) {

				navi.close();
			}

			@Override
			public void shellClosed(ShellEvent e) {

			}

			@Override
			public void shellActivated(ShellEvent e) {

			}
		});

		updateStatus();
	}

	private void createReadMarkButton() {

		markReadButton = new Button(navi, SWT.PUSH);
		markReadButton.setImage(Resource.getImage(Resource.V_MARKREAD32));
		markReadButton.setToolTipText(UIConstants.TEXT_READWORK);
		markReadButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		markReadButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		markReadButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				markRead();
			}

		});
	}

	private void createFinishWorkButton() {

		finishWorkButton = new Button(navi, SWT.PUSH);
		finishWorkButton.setImage(Resource.getImage(Resource.V_FINISH32));
		if (messageObject.isWorkflowData()) {
			finishWorkButton.setToolTipText(UIConstants.TEXT_FINISHWFWORK);
		} else {
			finishWorkButton.setToolTipText(UIConstants.TEXT_FINISHWORK);
		}
		finishWorkButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		finishWorkButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		finishWorkButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				finishWork();
			}

		});
	}

	private void createClosePanelButton() {

		closeButton = new Button(navi, SWT.PUSH);
		closeButton.setImage(Resource.getImage(Resource.V_NAVICLOSE32));
		closeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		closeButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		closeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				closePressed();
			}

		});
	}

	private void createStarMarkButton() {

		markStarButton = new Button(navi, SWT.PUSH);
		markStarButton.setImage(Resource.getImage(Resource.V_MARKSTAR32));
		markStarButton.setToolTipText(UIConstants.TEXT_STARWORK);
		markStarButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		markStarButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		markStarButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				markStar();
			}

		});
	}

	private void createStartWorkButton() {

		startWorkButton = new Button(navi, SWT.PUSH);
		startWorkButton.setImage(Resource.getImage(Resource.START32));
		startWorkButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		startWorkButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		startWorkButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				startWork();
			}

		});
	}

	private void createFinishTaskButton() {

		finishTaskButton = new Button(navi, SWT.PUSH);
		finishTaskButton.setImage(Resource.getImage(Resource.M_COMPLETE32));
		finishTaskButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		finishTaskButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		finishTaskButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				finishTask();
			}

		});
	}

	private void createStartTaskButton() {

		startTaskButton = new Button(navi, SWT.PUSH);
		startTaskButton.setImage(Resource.getImage(Resource.M_START32));
		startTaskButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		startTaskButton.setData(RWT.CUSTOM_VARIANT, NAVIMENU);
		startTaskButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				startTask();
			}

		});
	}

	protected void startTask() {

		try {
			DBObject wfInfo = (DBObject) messageObject.getMessageValue(IDBConstants.FIELD_WFINFO);
			ObjectId workId = (ObjectId) messageObject.getTargetValue(IDBConstants.FIELD_SYSID);

			wfInfo = workflowService.startTask(wfInfo, workId,null);
			
			messageObject.putMessageValue(IDBConstants.FIELD_WFINFO, wfInfo);
			viewer.update(messageObject, null);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		navi.dispose();
	}

	protected void finishTask() {
		navi.dispose();

		DBObject wfInfo = (DBObject) messageObject.getMessageValue(IDBConstants.FIELD_WFINFO);
		String processDefinitionId =(String) wfInfo.get(IDBConstants.FIELD_WFINFO_PROCESSID);
		String taskName = (String) wfInfo.get(IDBConstants.FIELD_WFINFO_TASKNAME);
		
		TaskFormConfig tfc = BusinessService.getDefault().getTaskCompleteFormConfig(processDefinitionId, taskName);

		Map<String,Object> inputParameter = null;
		DBObject workData = messageObject.getTargetSingleObject().getData();

		
		DBObject tfData = null;
		if(tfc!=null){
			//执行校验
			IValidationHandler vh = tfc.getValidationHandler();
			if(vh!=null){
				boolean v = vh.validateBeforeOpen(workData);
				if(!v){
					Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					MessageDialog.openError(parent, UIConstants.TEXT_VALIDATION_TASK, vh.getMessage());
					return;
				}
			}
			
			//显示表单
			TaskForm tf = new TaskForm(tfc);
			int ok = tf.open(workData);
			if(ok == TaskForm.OK){
				inputParameter = tf.getInputParameter();
				tfData = tf.getTaskFromData();
			}else if(ok== TaskForm.CANCEL){
				return;
			}
		}
		try {
			ObjectId workId = (ObjectId) workData.get(IDBConstants.FIELD_SYSID);
			
			
			//将流程信息保存到工作中
			BasicDBObject rec = new BasicDBObject();
			if(tfc!=null&&tfData!=null){//如果表单不为空，提取表单的信息
				String[] keys = tfc.getPersistentFields();
				for(int i=0;i<keys.length;i++){
					Object value = tfData.get(keys[i]);
					rec.put(keys[i], value);
				}
			}
			
			if(tfc!=null){
				IWorkflowInfoProvider informationProvider = tfc.getWorkflowInformationProvider();
				if(informationProvider!=null){
					Object info = informationProvider.getWorkflowInformation(tfData);
					if(info!=null){
						rec.put(IDBConstants.FIELD_WFINFO_ADDITIONAL, info);
					}
				}
			}
			
			wfInfo = workflowService.completeTask(wfInfo, workId, inputParameter,rec);
			
			
			messageObject.putMessageValue(IDBConstants.FIELD_WFINFO, wfInfo);
			viewer.update(messageObject, null);
		} catch (ServiceException e) {
		}
		
//		// 判断有没有任务流程模板的定义，如果有，显示该表单，如果没有就跳过
//		// 模板的定义根据com.tmt.ProjectPlanApproval.Review名称自动匹配
//
//		String formId = BusinessService.getWorkflowService().getTaskFormEditorId(wfInfo);
//		DBObject inputData = null;
//		if (formId != null) {
//			EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(formId);
//			if (ec != null) {
//				ISingleObjectEditorInput input = new SingleObjectEditorInput(messageObject.getTargetSingleObject());
//				int ok = SingleObjectEditorDialog.getInstance(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), formId, input, null, false)
//						.open();
//				if (ok != SingleObjectEditorDialog.OK) {
//					return;
//				} else {
//					inputData = input.getInputData().getData();
//				}
//			}
//
//		}
//
//		try {
//			ObjectId workId = (ObjectId) messageObject.getTargetValue(IDBConstants.FIELD_SYSID);
//			wfInfo = BusinessService.getWorkflowService().completeTask(wfInfo, workId, inputData);
//			messageObject.putMessageValue(IDBConstants.FIELD_WFINFO, wfInfo);
//			viewer.update(messageObject, null);
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}

	}

	private void updateStatus() {

		if (messageObject.isMarkDelete()) {
			if (startWorkButton != null && (!startWorkButton.isDisposed()))
				startWorkButton.setEnabled(false);// 只有准备状态以及暂停状态的可以开始

			if (finishWorkButton != null && (!finishWorkButton.isDisposed()))
				finishWorkButton.setEnabled(false);

			if (startTaskButton != null && (!startTaskButton.isDisposed()))
				startTaskButton.setEnabled(false);// 只有准备状态以及暂停状态的可以开始

			if (finishTaskButton != null && (!finishTaskButton.isDisposed()))
				finishTaskButton.setEnabled(false);

			markReadButton.setEnabled(false);
			markStarButton.setEnabled(false);
		} else {
			if (startWorkButton != null && (!startWorkButton.isDisposed()))
				startWorkButton.setEnabled(messageObject.isReady() || messageObject.isPause());// 只有准备状态以及暂停状态的可以开始
			if (finishWorkButton != null && (!finishWorkButton.isDisposed()))
				finishWorkButton.setEnabled((!messageObject.isWorkflowData()) && (messageObject.isProcess() || messageObject.isPause()));
			if (startTaskButton != null && (!startTaskButton.isDisposed()))
				startTaskButton.setEnabled(messageObject.canStartTask());

			if (finishTaskButton != null && (!finishTaskButton.isDisposed()))
				finishTaskButton.setEnabled(messageObject.canFinishTask());

			markReadButton.setEnabled(true);
			markStarButton.setEnabled(true);

			markReadButton.setImage(messageObject.isMarkRead() ? Resource.getImage(Resource.V_UNMARKREAD32) : Resource.getImage(Resource.V_MARKREAD32));
			markStarButton.setImage(messageObject.isMarkStar() ? Resource.getImage(Resource.V_UNMARKSTAR32) : Resource.getImage(Resource.V_MARKSTAR32));
		}
	}

	public void setEnable(boolean b) {

		this.enable = b;
	}

	protected void startWork() {

		messageObject.start();
		// 通知WorkInBox更新
		box.messageStarted(messageObject, viewer);
		navi.dispose();
	}

	protected void markStar() {

		messageObject.markStar();
		viewer.update(messageObject, null);
		navi.dispose();
	}

	protected void markRead() {

		messageObject.markRead();
		viewer.update(messageObject, null);
		navi.dispose();
	}

	protected void finishWork() {

		messageObject.finish();
		box.messageFinished(messageObject, viewer);
		navi.dispose();
	}

	protected void closePressed() {

		navi.dispose();
	}

}
