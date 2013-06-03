package com.sg.common.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.workflow.parameter.IProcessParameterDelegator;
import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;


public class TaskForm {

	public static final int OK = 0;
	public static final int NO_FORM = 1;
	public static final int CANCEL = 2;
	
	private BasicDBObject taskFormData;
	private TaskFormConfig tfc;

	public TaskForm(TaskFormConfig taskFormConfig){
		this.tfc = taskFormConfig;
	}
	
	public int open(DBObject workData){
		//首先判断有没有定义的编辑器
		if(tfc==null){
			return NO_FORM;
		}
		//获得编辑器的Id
		String taskFormEditorId = tfc.getEditorId();
		EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(taskFormEditorId);
		if(ec==null){
			return NO_FORM;
		}
		//初始化表单的数据
		taskFormData = new BasicDBObject();
		taskFormData.putAll(workData);
		
		//如果为任务表单定义了inputHandler，可以修改任务表单的输入数据对象
		ISingleObject taskFormInputData = tfc.getTaskFormInput(taskFormData);
		SingleObjectEditorInput taskFormInput = new SingleObjectEditorInput(ec,taskFormInputData);
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		//可以从扩展点获得任务表单的对话框控制器
		ISingleObjectEditorDialogCallback handler = tfc.getTaskFormDialogHandler(taskFormData);

		//如果没有获得对话框控制器，系统使用默认的控制器
		if(handler == null){
			handler = new SingleObjectEditorDialogCallback(){

				@Override
				public boolean saveBefore(ISingleObjectEditorInput input) {
//					doSave(input);
					return false;//不继续保存
				}
				
			};
		}
		int ok = SingleObjectEditorDialog.getInstance(shell, taskFormEditorId, taskFormInput, handler, false).open();
		if (ok != SingleObjectEditorDialog.OK) {
			return CANCEL;
		} else {
			return OK;
		}
	}

	/**
	 * 保存信息移动到启动完成任务时，确保任何状态下对流程的操作都可以被记录
	 * @param input
	 */
	@Deprecated
	protected void doSave(ISingleObjectEditorInput input) {

//		//将流程信息保存到工作中
//		BasicDBObject rec = new BasicDBObject();
//		rec.put(IDBConstants.FIELD_WFINFO_TASKSTATUS, IDBConstants.VALUE_WF_STATUS_COMPLETE);
//		
//		ISingleObject workso = input.getInputData();
//		
//		String[] keys = tfc.getPersistentFields();
//		for(int i=0;i<keys.length;i++){
//			Object value = workso.getValue(keys[i]);
//			rec.put(keys[i], value);
//		}
//		
//		ObjectId workId = (ObjectId) workso.getValue(IDBConstants.FIELD_SYSID);
//		//插入到work的流程历史上
//		BusinessService.getWorkflowService().saveProcessHistory(workId,rec);
		
	}

	public Map<String,Object> getInputParameter() {
		if(tfc==null){
			return null;
		}
		List<ProcessParameter> ps = tfc.getProcessParameters();
		if(ps.size()==0){
			return null;
		}
		Map<String,Object > result = new HashMap<String,Object>();
		for(int i = 0 ; i < ps.size() ; i ++){
			ProcessParameter pi = ps.get(i);
			String processParameter = pi.getprocessParameterName();
			String taskDatakey = pi.getTaskFormName();
			IProcessParameterDelegator pd = pi.getProcessParameterDelegator();
			if(pd!=null){//如果设置了取值代理
				try{
					Object value = pd.getValue(processParameter,taskDatakey,taskFormData);
					result.put(processParameter, value);
				}catch(Exception e){
					System.out.println(tfc.getTaskFormId()+"参数取值错误。"+pd.getClass().getName());
					e.printStackTrace();
				}
			}else{//如果没有设置代理，直接从表单取值
				if(taskFormData!=null){
					
					Object value = taskFormData.get(taskDatakey);
					result.put(processParameter, value);
				}
			}
		}
		
		return result;
		
	}
	
	
	public DBObject getTaskFromData(){
		return taskFormData;
	}
}
