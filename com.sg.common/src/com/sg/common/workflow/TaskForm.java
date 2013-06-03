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
		//�����ж���û�ж���ı༭��
		if(tfc==null){
			return NO_FORM;
		}
		//��ñ༭����Id
		String taskFormEditorId = tfc.getEditorId();
		EditorConfiguration ec = Widget.getSingleObjectEditorConfiguration(taskFormEditorId);
		if(ec==null){
			return NO_FORM;
		}
		//��ʼ����������
		taskFormData = new BasicDBObject();
		taskFormData.putAll(workData);
		
		//���Ϊ�����������inputHandler�������޸���������������ݶ���
		ISingleObject taskFormInputData = tfc.getTaskFormInput(taskFormData);
		SingleObjectEditorInput taskFormInput = new SingleObjectEditorInput(ec,taskFormInputData);
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		//���Դ���չ����������ĶԻ��������
		ISingleObjectEditorDialogCallback handler = tfc.getTaskFormDialogHandler(taskFormData);

		//���û�л�öԻ����������ϵͳʹ��Ĭ�ϵĿ�����
		if(handler == null){
			handler = new SingleObjectEditorDialogCallback(){

				@Override
				public boolean saveBefore(ISingleObjectEditorInput input) {
//					doSave(input);
					return false;//����������
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
	 * ������Ϣ�ƶ��������������ʱ��ȷ���κ�״̬�¶����̵Ĳ��������Ա���¼
	 * @param input
	 */
	@Deprecated
	protected void doSave(ISingleObjectEditorInput input) {

//		//��������Ϣ���浽������
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
//		//���뵽work��������ʷ��
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
			if(pd!=null){//���������ȡֵ����
				try{
					Object value = pd.getValue(processParameter,taskDatakey,taskFormData);
					result.put(processParameter, value);
				}catch(Exception e){
					System.out.println(tfc.getTaskFormId()+"����ȡֵ����"+pd.getClass().getName());
					e.printStackTrace();
				}
			}else{//���û�����ô���ֱ�Ӵӱ�ȡֵ
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
