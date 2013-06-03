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
		//Ȩ������ʱ���ƴ���
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
				//������ѡ�����֯����Ŀ��
				//��parent�´���һ����Ŀ��
				ISingleObject projectData = input.getInputData();
				projectData.setValue(IDBConstants.FIELD_PROJECT_OBS_ROOT, new ObjectId(), null, false);
				//���ø��ļ��е�id
				projectData.setValue(IDBConstants.FIELD_FOLDER_ROOT, new ObjectId(), null, false);
				
				if (getTemplateFromInput(input) == null) {//û��ģ��
					boolean ok = MessageDialog.openQuestion(window.getShell(), UIConstants.TEXT_SAVE, UIConstants.MESSAGE_QUESTION_NO_PROJECT_TEMPLATE_CONTINUE);
					if (!ok){//ȡ������
						return false;
					}else{//��������
						hasTemplate = false;
					}
				}
				
				return super.saveBefore(input);
			}
			
			

			@Override
			public boolean saveAfter(ISingleObjectEditorInput input) {
				ISingleObject so = input.getInputData();
				if(!hasTemplate){//���û��ģ�壬����Ĭ����Ŀ��
					DataUtil.createDefaultProjectTeam(so);
				}else{
					DataUtil.createRootProjectTeam(so);
					// �����ģ�嶨�壬��Ҫ��ģ�嶨���������и��ƣ�������������
					DBObject template = (DBObject) so.getValue(IDBConstants.FIELD_TEMPLATE);
					DataUtil.importTemplateToProject( (ObjectId)template.get(IDBConstants.FIELD_SYSID), (ObjectId) so.getValue(IDBConstants.FIELD_SYSID));
				}
				//������Ŀ���ļ���
				DBObject projectData = so.getData();
				DataUtil.createProjectFolder(projectData);

				//ͬ�����û���projectincharged
				//����Ŀ������ͬ����user
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
		//Ȩ������ʱ���ƴ���
		//��Ȩ�޷������뵽����
		//��ѡ�������ĵ�ѡ���п��Կ���
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
			// û��ѡ��ģ��
			return;
		}
		
		//��ȡ��Ŀģ��ķֽ�ṹ
		Object projectTemplateId = pjt.get(IDBConstants.FIELD_SYSID);
		
		CascadeObject exp = DBActivator.getCascadeObject(IDBConstants.EXP_CASCADE_WBS_TEMPLATE);
		
		Map<String, Object> para = new HashMap<String, Object>();
		para.put(IDBConstants.FIELD_SYSID, projectTemplateId);
		exp.passParamValueMap(para);
		CascadeObject templateProject = exp.getChildren().get(0);
		copyWBSFromTemplate(input.getInputData().getData(),templateProject);
	}

	private void copyWBSFromTemplate(DBObject target, CascadeObject source) {
		//��ǰ������¼��ڵ�
		List<CascadeObject> d = source.getChildren();
		int docseq = 1;
		for(CascadeObject child:d){
			//������Ӧ������
			DBObject srcData = child.getData();
			DBObject trgData = new BasicDBObject();
			//��Ϊid���滹��Ҫ���ڼ�����ѯ���ݲ��������Ը��ƶ���
			Iterator<String> iter = srcData.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();

				//�ų����ֶ�
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
		// ����Ѿ��������Ŀ����
		ISingleObject pjdto = input.getInputData();
		// ��ö�Ӧ��ģ��
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
