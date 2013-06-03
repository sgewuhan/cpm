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
 * 注意：！！！
 * 这个程序没有在任何地方被调用，仅作为实现参考和学习使用。
 * 
 * 这个程序演示通过任务表单保存时产生项目的方法，这个savehandler用于在保存任务时被调用
 * 这种方式看起来不是特别好，但可以作为一种实现的参考
 * 更好的方式是使用流程服务，使用流程服务具有更好的重用性和可维护性。
 * 参考com.sg.document.basic.service.CreateProject的实现方法
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
		// 从任务中获得项目必要的信息，创建项目
		BasicDBObject projectData = new BasicDBObject();
		Iterator<String> iter = workdto.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (key.startsWith("project_")) {// 表示复制到项目的字段
				String projectKey = key.substring("project_".length());
				projectData.put(projectKey, workdto.get(key));
			}
		}

		// 在parent下创建一个项目组
		projectData.put(IDBConstants.FIELD_PROJECT_OBS_ROOT, new ObjectId());

		// 设置根文件夹的id
		projectData.put(IDBConstants.FIELD_FOLDER_ROOT, new ObjectId());

		// 获得对应的模板
		DBObject pjtempltedto = (DBObject) projectData.get(IDBConstants.FIELD_TEMPLATE);

		boolean hasTemplate = (pjtempltedto != null);
		if (!hasTemplate) {// 没有模板
			boolean ok = MessageDialog.openQuestion(shell, UIConstants.TEXT_SAVE, UIConstants.MESSAGE_QUESTION_NO_PROJECT_TEMPLATE_CONTINUE);
			if (!ok) {// 取消保存
				return true;
			}
		}

		// 设置系统创建信息
		DataUtil.setSystemCreateInfo(projectData);

		// 创建项目
		projectCollection.insert(projectData);

		ISingleObject so = new SingleObject(projectCollection, projectData);
		if (!hasTemplate) {// 如果没有模板，创建默认项目组
			DataUtil.createDefaultProjectTeam(so);
		} else {
			DataUtil.createRootProjectTeam(so);
			// 如果有模板定义，需要把模板定义的任务进行复制，并产生交付物
			DBObject template = (DBObject) so.getValue(IDBConstants.FIELD_TEMPLATE);
			DataUtil.importTemplateToProject((ObjectId) template.get(IDBConstants.FIELD_SYSID), (ObjectId) so.getValue(IDBConstants.FIELD_SYSID));
		}
		// 创建项目根文件夹
		DataUtil.createProjectFolder(projectData);

		// 同步到用户的projectincharged
		// 将项目负责人同步到user
		ObjectId id = (ObjectId) projectData.get(IDBConstants.FIELD_SYSID);

		ObjectId newChargerId = null;
		if (projectData != null) {
			DBObject newCharger = (DBObject) projectData.get(IDBConstants.FIELD_WORK_PM);
			newChargerId = (ObjectId) newCharger.get(IDBConstants.FIELD_SYSID);
		}

		DataUtil.saveUserRelationInformation(null, newChargerId, IDBConstants.COLLECTION_USER_PROJECT_IN_CHARGED, id);

		// 标记任务已经完成
		workCollection.update(
						new BasicDBObject()
							.append(IDBConstants.FIELD_SYSID, workdto.get(IDBConstants.FIELD_SYSID)),
						new BasicDBObject()
							.append("$set", new BasicDBObject().append(IDBConstants.FIELD_PROJECT_ACTUALFINISH, new Date()))
							.append("$set", new BasicDBObject().append(IDBConstants.FIELD_PROCESS_STATUS, IDBConstants.VALUE_PROCESS_CLOSE)));

		// 返回为真以结束保存过程
		return true;
	}

}
