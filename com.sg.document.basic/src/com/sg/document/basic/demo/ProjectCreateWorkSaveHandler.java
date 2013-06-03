package com.sg.document.basic.demo;

import java.util.Date;
import java.util.Iterator;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.CommonSaveHandler;
import com.sg.common.db.DataUtil;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.widget.editor.ISingleObjectEditorInput;

/**
 * ע�⣺������
 * �������û�����κεط������ã�����Ϊʵ�ֲο���ѧϰʹ�á�
 * 
 * ���������ʾͨ�����������ʱ������Ŀ�ķ��������savehandler�����ڱ�������ʱ������
 * ���ַ�ʽ�����������ر�ã���������Ϊһ��ʵ�ֵĲο�
 * ���õķ�ʽ��ʹ�����̷���ʹ�����̷�����и��õ������ԺͿ�ά���ԡ�
 * �ο�com.sg.document.basic.service.CreateProject��ʵ�ַ���
 * @author hua
 *
 */
public class ProjectCreateWorkSaveHandler extends CommonSaveHandler {

	private final DBCollection projectCollection;

	private final DBCollection workCollection;

	public ProjectCreateWorkSaveHandler() {

		super();

		projectCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_PROJECT);
		workCollection = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_WORK);
	}

	@Override
	public boolean doSave(ISingleObjectEditorInput input, IProgressMonitor monitor) {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		DBObject workdto = input.getInputData().getData();
		// �������л����Ŀ��Ҫ����Ϣ��������Ŀ
		BasicDBObject projectData = new BasicDBObject();
		Iterator<String> iter = workdto.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (key.startsWith("project_")) {// ��ʾ���Ƶ���Ŀ���ֶ�
				String projectKey = key.substring("project_".length());
				projectData.put(projectKey, workdto.get(key));
			}
		}

		// ��parent�´���һ����Ŀ��
		projectData.put(IDBConstants.FIELD_PROJECT_OBS_ROOT, new ObjectId());

		// ���ø��ļ��е�id
		projectData.put(IDBConstants.FIELD_FOLDER_ROOT, new ObjectId());

		// ��ö�Ӧ��ģ��
		DBObject pjtempltedto = (DBObject) projectData.get(IDBConstants.FIELD_TEMPLATE);

		boolean hasTemplate = (pjtempltedto != null);
		if (!hasTemplate) {// û��ģ��
			boolean ok = MessageDialog.openQuestion(shell, UIConstants.TEXT_SAVE, UIConstants.MESSAGE_QUESTION_NO_PROJECT_TEMPLATE_CONTINUE);
			if (!ok) {// ȡ������
				return true;
			}
		}

		// ����ϵͳ������Ϣ
		DataUtil.setSystemCreateInfo(projectData);

		// ������Ŀ
		projectCollection.insert(projectData);

		ISingleObject so = new SingleObject(projectCollection, projectData);
		if (!hasTemplate) {// ���û��ģ�壬����Ĭ����Ŀ��
			DataUtil.createDefaultProjectTeam(so);
		} else {
			DataUtil.createRootProjectTeam(so);
			// �����ģ�嶨�壬��Ҫ��ģ�嶨���������и��ƣ�������������
			DBObject template = (DBObject) so.getValue(IDBConstants.FIELD_TEMPLATE);
			DataUtil.importTemplateToProject((ObjectId) template.get(IDBConstants.FIELD_SYSID), (ObjectId) so.getValue(IDBConstants.FIELD_SYSID));
		}
		// ������Ŀ���ļ���
		DataUtil.createProjectFolder(projectData);

		// ͬ�����û���projectincharged
		// ����Ŀ������ͬ����user
		ObjectId id = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);

		ObjectId newChargerId = null;
		if (projectData != null) {
			DBObject newCharger = (DBObject) projectData.get(IDBConstants.FIELD_WORK_PM);
			newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
		}

		DataUtil.saveUserRelationInformation(null, newChargerId, IDBConstants.COLLECTION_USER_PROJECT_IN_CHARGED, id);

		// ��������Ѿ����
		workCollection.update(
						new BasicDBObject()
							.append(IDBConstants.FIELD_SYSID, workdto.get(IDBConstants.FIELD_SYSID)),
						new BasicDBObject()
							.append("$set", new BasicDBObject().append(IDBConstants.FIELD_PROJECT_ACTUALFINISH, new Date()))
							.append("$set", new BasicDBObject().append(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_CLOSE)));

		// ����Ϊ���Խ����������
		return true;
	}

}
