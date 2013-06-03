package com.sg.cpm.project.actions.wbs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.user.AuthorityResponse;
import com.sg.user.UserSessionContext;
import com.sg.user.ui.AuthorityUI;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.part.NavigatableTableView;

public class CreateProject implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		
		//*******************************************************************************************
		//权限运行时控制代码
		AuthorityResponse resp = new AuthorityResponse();
		boolean hasAuthority = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_PROJECT_MANAGER,resp );
		if(!hasAuthority){
			AuthorityUI.SHOW_NOT_PERMISSION(resp);
			return;
		}
		//*******************************************************************************************
		
		
		ISingleObjectEditorDialogCallback callback = new SingleObjectEditorDialogCallback() {

			boolean hasTemplate = true;
			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {
				//创建在选择的组织下项目组
				//在parent下创建一个项目组
				ISingleObject projectData = input.getInputData();
				projectData.setValue(IDBConstants.FIELD_PROJECT_OBS_ROOT, new ObjectId(), null, false);
				//设置根文件夹的id
				projectData.setValue(IDBConstants.FIELD_FOLDER_ROOT, new ObjectId(), null, false);
				
				if (getTemplateFromInput(input) == null) {//没有模板
					boolean ok = MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_SAVE, UIConstants.MESSAGE_QUESTION_NO_PROJECT_TEMPLATE_CONTINUE);
					if (!ok){//取消保存
						return false;
					}else{//继续保存
						hasTemplate = false;
					}
				}
				
				return super.saveBefore(input);
			}
			
			

			@Override
			public boolean saveAfter(ISingleObjectEditorInput input) {
				ISingleObject so = input.getInputData();
				if(!hasTemplate){//如果没有模板，创建默认项目组
					DataUtil.createDefaultProjectTeam(so);
				}else{
					DataUtil.createRootProjectTeam(so);
					// 如果有模板定义，需要把模板定义的任务进行复制，并产生交付物
					DBObject template = (DBObject) so.getValue(IDBConstants.FIELD_TEMPLATE);
					DataUtil.importTemplateToProject( (ObjectId)template.get(IDBConstants.FIELD_SYSID), (ObjectId) so.getValue(IDBConstants.FIELD_SYSID));
				}
				//创建项目根文件夹
				DBObject projectData = so.getData();
				DataUtil.createProjectFolder(projectData);

				//同步到用户的projectincharged
				//将项目负责人同步到user
				ObjectId id = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);

				ObjectId newChargerId = null ;
				if(projectData!=null){
					DBObject newCharger = (DBObject) projectData.get(IDBConstants.FIELD_WORK_PM);
					newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
				}
				
				DataUtil.saveUserRelationInformation(null, newChargerId, IDBConstants.COLLECTION_USER_PROJECT_IN_CHARGED, id);

				
				
				return super.saveAfter(input);
			}

		};
		
		EditorConfiguration editorConfiguration = Widget.getSingleObjectEditorConfiguration(UIConstants.EDITOR_PROJECT_CREATE);
		SingleObject s = new SingleObject(DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_PROJECT));
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, s);
		//*******************************************************************************************
		//权限运行时控制代码
		//将权限反馈传入到界面
		//在选择上下文的选框中可以控制
		editInput.setAuthorityResponse(resp);
				
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(window.getShell(), UIConstants.EDITOR_PROJECT_CREATE, editInput, callback,false);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject so = soed.getInputData();
			NavigatableTableView view = (NavigatableTableView) window.getActivePage().findView(UIConstants.VIEW_PROJECT_NAVIGATOR);
			if (view != null) {
				view.addDataObject(so);
			}
		}
	}


	protected void createWBSFromInputTemplate(ISingleObjectEditorInput input) {
		DBObject pjt = getTemplateFromInput(input);
		if (pjt == null) {
			// 没有选择模板
			return;
		}
		
		//读取项目模板的分解结构
		Object projectTemplateId = pjt.get(IDBConstants.FIELD_SYSID);
		
		CascadeObject exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS_TEMPLATE);
		
		Map<String, Object> para = new HashMap<String, Object>();
		para.put(IDBConstants.FIELD_SYSID, projectTemplateId);
		exp.passParamValueMap(para);
		CascadeObject templateProject = exp.getChildren().get(0);
		copyWBSFromTemplate(input.getInputData().getData(),templateProject);
	}

	private void copyWBSFromTemplate(DBObject target, CascadeObject source) {
		//当前级别的下级节点
		List<CascadeObject> d = source.getChildren();
		int docseq = 1;
		for(CascadeObject child:d){
			//建立对应的任务
			DBObject srcData = child.getData();
			DBObject trgData = new BasicDBObject();
			//因为id后面还需要用于级联查询传递参数，所以复制对象
			Iterator<String> iter = srcData.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();

				//排除的字段
				if(IDBConstants.FIELD_DBSSEQ.equals(key)||IDBConstants.FIELD_TEMPLATE_TYPE.equals(key)){//
					continue;
				}
				
				Object value ;

				if(IDBConstants.FIELD_SYSID.equals(key)) {
					value = new ObjectId();
				}else if(IDBConstants.FIELD_WBSPARENT.equals(key)){
					value = target.get(IDBConstants.FIELD_SYSID);
				}else{
					value = srcData.get(key);
				}
				trgData.put(key, value);
			}
			
			DataUtil.setSystemCreateInfo(trgData);
			
			if(DataUtil.isWorkTemplateObject(child)){
				DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK).insert(trgData);
			}else if(DataUtil.isDeliveryTemplateObject(child)){
				trgData.put(IDBConstants.FIELD_WBSSEQ, docseq++);
				DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_DOCUMENT).insert(trgData);
			}
			
			copyWBSFromTemplate(trgData,child);
		}
		
	}

	protected DBObject getTemplateFromInput(ISingleObjectEditorInput input) {
		// 获得已经保存的项目数据
		ISingleObject pjdto = input.getInputData();
		// 获得对应的模板
		DBObject pjtdto = (DBObject) pjdto.getValue(IDBConstants.FIELD_TEMPLATE);
		return pjtdto;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
		
//		hasAuthority = UserSessionContext.hasAuthority(new String[] { UserSessionContext.CREATE_METHOD, IDBConstants.COLLECTION_PROJECT },
//				new ActiveCollection() {
//
//					@Override
//					public int getObjectType() {
//						return TYPE_COLLECTION;
//					}
//
//					@Override
//					public String getDisplayText() {
//						return IDBConstants.COLLECTION_PROJECT;
//					}
//
//					@Override
//					public String getCollectionName() {
//						return IDBConstants.COLLECTION_PROJECT;
//					}
//
//				});
//		action.setEnabled(hasAuthority);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
