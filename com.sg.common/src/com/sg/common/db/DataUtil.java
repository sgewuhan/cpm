package com.sg.common.db;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.sg.common.ui.UIConstants;
import com.sg.db.DBActivator;
import com.sg.db.DBConstants;
import com.sg.db.Util;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.model.CascadeObject;
import com.sg.db.model.ISingleObject;
import com.sg.db.model.SingleObject;
import com.sg.resource.Resource;
import com.sg.user.AuthorityResponse;
import com.sg.user.IAuthorityResponse;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.dialog.ISingleObjectEditorDialogCallback;
import com.sg.widget.dialog.SingleObjectEditorDialog;
import com.sg.widget.dialog.SingleObjectEditorDialogCallback;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.SingleObjectEditorInput;
import com.sg.widget.util.FileUtil;

public class DataUtil implements IDBConstants, UIConstants {

	public static String createUID(char c, int length) {

		String format = "";
		for (int i = 0; i < length; i++) {
			format = format + c;
		}

		int id = Util.getIncreasedID(DBActivator.getDefaultDBCollection(DBConstants.IDS), COLLECTION_USER);
		DecimalFormat df1 = new DecimalFormat("000000");

		return df1.format(id);
	}

	public static void setSystemCreateInfo(ISingleObject data) {
		setSystemCreateInfo(data.getData());
	}

	public static void setSystemCreateInfo(DBObject data) {

		String createUserId;
		String createUserName;
		try {
			// 创建者 创建时间
			createUserId = UserSessionContext.getSession().getUserId();
			createUserName = UserSessionContext.getSession().getUserName();
		} catch (Exception e) {
			createUserId = "internal";
			createUserName = "internal";
		}
		data.put(FIELD_CREATER, createUserId);
		// 因为创建者的名字字段不显示
		data.put(FIELD_CREATER_NAME, createUserName);

		String ownerUserId = createUserId;
		data.put(FIELD_OWNER, ownerUserId);

		String ownerUserName = createUserName;
		data.put(FIELD_OWNER_NAME, ownerUserName);

		Date createDate = new Date();
		data.put(FIELD_CREATE_DATE, createDate);
	}

	public static void setSystemModifyInfo(ISingleObject data) {

		Date modifydate = new Date();
		data.setValue(FIELD_MODIFY_DATE, modifydate, null, true);
	}

	// **************************************************************************************
	// 判断数据类型的工具方法
	public static boolean isMatchedObject(ISingleObject so, String collectionName) {

		if (so == null)
			return false;
		DBCollection collection = so.getCollection();
		if (collection != null) {
			String name = collection.getName();
			if (collectionName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isProjectObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_PROJECT);
	}

	public static boolean isWorkObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_WORK);
	}

	public static boolean isDocumentObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_DOCUMENT);
	}

	public static boolean isProjectTemplateObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_PROJECT_TEMPLATE);
	}

	public static boolean isWorkTemplateObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_WORK_TEMPLATE) && (VALUE_WBS_TASK_TYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE)));
	}

	public static boolean isDeliveryTemplateObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_WORK_TEMPLATE) && (VALUE_WBS_DOCUMENT_TYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE)));
	}

	public static boolean isInactive(DBObject data) {

		return data.get(FIELD_PROCESS_STATUS) == null;
	}

	public static boolean isWorkReady(ISingleObject so) {

		return VALUE_PROCESS_READY.equals(so.getValue(FIELD_PROCESS_STATUS));
	}

	public static boolean isReady(DBObject data) {

		return VALUE_PROCESS_READY.equals(data.get(FIELD_PROCESS_STATUS));
	}

	public static boolean isWorkProcess(ISingleObject so) {

		return VALUE_PROCESS_PROCESS.equals(so.getValue(FIELD_PROCESS_STATUS));
	}

	public static boolean isProcess(DBObject data) {

		return VALUE_PROCESS_PROCESS.equals(data.get(FIELD_PROCESS_STATUS));
	}

	public static boolean isWorkCancel(ISingleObject so) {

		return VALUE_PROCESS_CANCEL.equals(so.getValue(FIELD_PROCESS_STATUS));
	}

	public static boolean isCancel(DBObject data) {

		return VALUE_PROCESS_CANCEL.equals(data.get(FIELD_PROCESS_STATUS));
	}

	public static boolean isWorkClose(ISingleObject so) {

		return VALUE_PROCESS_CLOSE.equals(so.getValue(FIELD_PROCESS_STATUS));
	}

	public static boolean isClose(DBObject data) {

		return VALUE_PROCESS_CLOSE.equals(data.get(FIELD_PROCESS_STATUS));
	}

	public static boolean isWorkStop(ISingleObject so) {

		return VALUE_PROCESS_PAUSE.equals(so.getValue(FIELD_PROCESS_STATUS));
	}

	public static boolean isPause(DBObject data) {

		return VALUE_PROCESS_PAUSE.equals(data.get(FIELD_PROCESS_STATUS));
	}

	public static boolean isRoleTemplateObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_ORG_TEMPLATE) && (VALUE_OBS_ROLETYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE)));
	}

	public static boolean isTeamTemplateObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_ORG_TEMPLATE) && (VALUE_OBS_TEAMTYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE)));
	}

	public static boolean isRoleObject(ISingleObject so) {

		return so != null && VALUE_OBS_ROLETYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE));
	}

	public static boolean isTeamObject(ISingleObject so) {

		return so != null && VALUE_OBS_TEAMTYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE));
	}

	public static boolean isUserObject(ISingleObject so) {

		return so != null && VALUE_OBS_USERTYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE));
	}

	public static boolean isProjectTeamObject(ISingleObject so) {

		return so != null && VALUE_OBS_PJTEAMTYPE.equals(so.getValue(FIELD_TEMPLATE_TYPE));
	}

	public static boolean isSiteObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_SITE);
	}

	public static boolean isFolderObject(ISingleObject so) {

		return isMatchedObject(so, COLLECTION_FOLDER);
	}

	// **************************************************************************************

	public static String getWorkWBSCode(CascadeObject so) {

		String wbsCode = "";
		while (so.getParent().getParent() != null) {
			Object seq = so.getValue(FIELD_WBSSEQ);
			if (seq == null)
				seq = 1;
			if (wbsCode.equals("")) {
				wbsCode = "" + seq;
			} else {
				wbsCode = "" + seq + "." + wbsCode;
			}
			so = so.getParent();
		}

		return wbsCode;
	}

	public static String getListLabel(BasicDBList list, String fieldName) {

		if (list.isEmpty()) {
			return null;
		}
		String result = "";
		Iterator<Object> iter = list.iterator();
		while (iter.hasNext()) {
			DBObject dbo = (DBObject) iter.next();
			result = result + dbo.get(fieldName);
			if (iter.hasNext()) {
				result = result + ",";
			}
		}
		return result;
	}

	public static Comparator<CascadeObject> getWBSSorter() {

		Comparator<CascadeObject> c = new Comparator<CascadeObject>() {

			@Override
			public int compare(CascadeObject o1, CascadeObject o2) {

				if (isWorkObject(o1) && isDocumentObject(o2)) {
					return -1;
				} else if (isDocumentObject(o1) && isWorkObject(o2)) {
					return 1;
				} else if ((isWorkObject(o1) && isWorkObject(o2)) || ((isDocumentObject(o1) && isDocumentObject(o2)))) {
					Number seq1 = (Number) o1.getValue(FIELD_WBSSEQ);
					Number seq2 = (Number) o2.getValue(FIELD_WBSSEQ);
					return seq1.intValue() - seq2.intValue();
				}
				return 0;
			}

		};
		return c;
	}

	/**
	 * 将模板拷贝到项目中
	 * 
	 * @param templateId
	 * @param project
	 */
	public static void importTemplateToProject(ObjectId templateId, ObjectId projectId) {

		// 取出项目的OBS根
		QueryExpression exp = DBActivator.getQueryExpression(EXP_QUERY_PROJECT);
		exp.setParamValue(PARAM_INPUT_ID, projectId);
		DBCursor cur = exp.run();
		Assert.isTrue(cur.hasNext());
		ObjectId obsId = (ObjectId) cur.next().get(FIELD_PROJECT_OBS_ROOT);

		//

		// 第一步复制组织OBS,并建立映射关系,查询OBS模板
		CascadeObject obs_exp = DBActivator.getCascadeObject(EXP_CASCADE_OBS_TEMPLATE);
		obs_exp.setParamValue(FIELD_SYSID, templateId);

		CascadeObject obs_template_root = obs_exp.getChildren().get(0);

		List<CascadeObject> obs_children = obs_template_root.getChildren();
		Map<String, DBObject> obs_srcId_tgtData = copyOBSStructure(obs_children, FIELD_OBSPARENT, obsId, obsId);

		// 第二步复制WBS
		CascadeObject wbs_exp = DBActivator.getCascadeObject(EXP_CASCADE_WBS_TEMPLATE);
		wbs_exp.setParamValue(FIELD_SYSID, templateId);
		CascadeObject wbs_template_root = wbs_exp.getChildren().get(0);

		List<CascadeObject> wbs_children = wbs_template_root.getChildren();
		ArrayList<DBObject> worksTobeCreated = new ArrayList<DBObject>();
		ArrayList<DBObject> documentsTobeCreated = new ArrayList<DBObject>();

		copyWBSStructure(wbs_children, projectId, projectId, worksTobeCreated, documentsTobeCreated, obs_srcId_tgtData);

		// 清除project关联的组织
		removeOBSofProject(obsId);
		// 清除project管理的任务和文档
		removeWBSofProject(projectId);
		// 保存OBS
		//

		DBCollection obsCol = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		obsCol.insert((DBObject[]) obs_srcId_tgtData.values().toArray(new DBObject[] {}), WriteConcern.NORMAL);
		// 保存WBS
		DBCollection workCol = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		workCol.insert(worksTobeCreated, WriteConcern.NORMAL);
		DBCollection docCol = DBActivator.getDefaultDBCollection(COLLECTION_DOCUMENT);
		docCol.insert(documentsTobeCreated, WriteConcern.NORMAL);
	}

	private static void copyWBSStructure(List<CascadeObject> children, ObjectId parentId, ObjectId rootId, ArrayList<DBObject> worksTobeCreated,
			ArrayList<DBObject> documentsTobeCreated, Map<String, DBObject> obs_srcId_tgtData) {

		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				CascadeObject child = children.get(i);
				DBObject srcData = child.getData();

				DBObject dbo = Util.translateBSON(srcData, null, null, null, false);

				// 设置系统字段
				setSystemCreateInfo(dbo);
				// 设置新的id
				ObjectId tgtId = new ObjectId();
				dbo.put(FIELD_SYSID, tgtId);
				// 设置wbsparent字段
				dbo.put(FIELD_WBSPARENT, parentId);
				// 设置rootid字段
				dbo.put(FIELD_ROOTID, rootId);
				// 设置参与者字段
				BasicDBList participateListFromTemplate = (BasicDBList) srcData.get(FIELD_PARTICIPATE);
				if (participateListFromTemplate != null && participateListFromTemplate.size() > 0) {
					BasicDBList participateListForTarget = new BasicDBList();
					for (int j = 0; j < participateListFromTemplate.size(); j++) {
						DBObject participateFromTemplate = (DBObject) participateListFromTemplate.get(j);
						ObjectId participateIdFromTemplate = (ObjectId) participateFromTemplate.get(FIELD_SYSID);
						DBObject tgtData = obs_srcId_tgtData.get(participateIdFromTemplate.toString());
						if (tgtData != null) {
							DBObject obsData = getRefData(tgtData, DATA_OBS_BASIC);
							participateListForTarget.add(obsData);
						}
					}
					dbo.put(FIELD_PARTICIPATE, participateListForTarget);
				}
				// 判断是工作还是交付物
				Object type = srcData.get(FIELD_TEMPLATE_TYPE);
				if (VALUE_WBS_TASK_TYPE.equals(type)) {
					worksTobeCreated.add(dbo);
				} else if (VALUE_WBS_DOCUMENT_TYPE.equals(type)) {
					documentsTobeCreated.add(dbo);
				}
				copyWBSStructure(child.getChildren(), tgtId, rootId, worksTobeCreated, documentsTobeCreated, obs_srcId_tgtData);
			}
		}
	}

	/**
	 * children的对象复制一个新对象，并将wbsparent设置为target的id
	 * 
	 * @param children
	 *            要被复制的记录
	 * @param parentId
	 *            复制到的集合
	 * @param targetcollection
	 *            复制到的集合
	 */
	private static Map<String, DBObject> copyOBSStructure(List<CascadeObject> children, String parentFieldName, ObjectId parentId, ObjectId rootId) {

		Map<String, DBObject> result = new HashMap<String, DBObject>();

		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				CascadeObject child = children.get(i);
				DBObject srcData = child.getData();
				String srcId = srcData.get(FIELD_SYSID).toString();

				DBObject dbo = Util.translateBSON(srcData, null, null, null, false);

				setSystemCreateInfo(dbo);
				ObjectId tgtId = new ObjectId();
				dbo.put(FIELD_SYSID, tgtId);
				dbo.put(parentFieldName, parentId);
				dbo.put(FIELD_ROOTID, rootId);

				result.put(srcId, dbo);

				Map<String, DBObject> childMap = copyOBSStructure(child.getChildren(), parentFieldName, tgtId, rootId);
				result.putAll(childMap);
			}
		}
		return result;
	}

	public static void removeWBSofProject(ObjectId projectId) {

		// CascadeObject exp =
		// DBActivator.getCascadeObject(EXP_CASCADE_WBS);
		// exp.setParamValue(FIELD_SYSID, parentId);
		// CascadeObject parent = exp.getChildren().get(0);
		//
		// List<CascadeObject> children = new ArrayList<CascadeObject>();
		// children.addAll(parent.getChildren());
		// for(int i=0;i<children.size();i++){
		// children.get(i).remove(true);
		// }

		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		// ***********************************************************************************
		// 任务删除时，需要处理任务指派给的用户上的缓存数据
		/*
		 * 2012/5/13 处理逻辑改变，项目在使用模板功能的时候处于准备状态，此时不向用户发送Message 所以本段完全注释
		 * 
		 * DBCursor cur = workCollection.find(new
		 * BasicDBObject().append(FIELD_ROOTID, projectId));
		 * while(cur.hasNext()){ DBObject workObject = cur.next(); ObjectId
		 * workId = (ObjectId)workObject;
		 * 
		 * 
		 * //取出任务的负责人 DBObject chargerData = (DBObject)
		 * workObject.get(FIELD_WORK_PM); if(chargerData!=null){ ObjectId
		 * originalChargerId = (ObjectId) chargerData.get(FIELD_SYSID); //同步user
		 * saveUserRelationInformation
		 * (originalChargerId,null,COLLECTION_USER_WORK_IN_CHARGED,workId); }
		 * 
		 * //取出任务的资源 BasicDBList resourcelist =
		 * (BasicDBList)workObject.get(FIELD_WORK_RESOURCE);
		 * if(resourcelist!=null){ Iterator<Object> iter =
		 * resourcelist.iterator(); while(iter.hasNext()){ DBObject userData =
		 * (DBObject) iter.next(); //同步user
		 * saveUserRelationInformation((ObjectId)
		 * userData.get(FIELD_SYSID),null,
		 * COLLECTION_USER_WORK_PARTTICIPATED,(ObjectId)
		 * workObject.get(FIELD_SYSID)); } } }
		 */
		// ***********************************************************************************
		workCollection.remove(new BasicDBObject().append(FIELD_ROOTID, projectId));

		DBActivator.getDefaultDBCollection(COLLECTION_DOCUMENT).remove(new BasicDBObject().append(FIELD_ROOTID, projectId));

	}

	public static void removeOBSofProject(ObjectId rootOBSId) {

		// CascadeObject wbs_exp =
		// DBActivator.getCascadeObject(EXP_CASCADE_OBS);
		// wbs_exp.setParamValue(FIELD_SYSID, parentId);
		// CascadeObject parent = wbs_exp.getChildren().get(0);
		//
		// List<CascadeObject> children = new ArrayList<CascadeObject>();
		// children.addAll(parent.getChildren());
		// for(int i=0;i<children.size();i++){
		// children.get(i).remove(true);
		// }

		DBActivator.getDefaultDBCollection(COLLECTION_ORG).remove(new BasicDBObject().append(FIELD_ROOTID, rootOBSId));
	}

	public static boolean isActivatedUser(ISingleObject element) {

		return Boolean.TRUE.equals(element.getValue(FIELD_ACTIVATE));
	}

	public static String getUserLable(ISingleObject user) {

		return getUserLable(user.getData());
	}

	public static String getUserLable(DBObject user) {

		user = getUserInformation(user, true);
		return user.get(FIELD_NAME) + " " + user.get(FIELD_DESC);
	}

	public static String getUserLable2(DBObject user) {

		return user.get(FIELD_NAME) + " " + user.get(FIELD_DESC);
	}

	public static DBObject getDataObject(String collectName, ObjectId id) {

		return DBActivator.getDefaultDBCollection(collectName).findOne(new BasicDBObject().append(FIELD_SYSID, id));
	}

	/**
	 * 创建根项目组
	 * 
	 * @param inputData
	 * @return
	 */
	public static ObjectId createRootProjectTeam(ISingleObject inputData) {

		ObjectId parentId = (ObjectId) inputData.getValue(FIELD_OBSPARENT);
		ObjectId teamId = (ObjectId) inputData.getValue(FIELD_PROJECT_OBS_ROOT);
		Assert.isNotNull(parentId);
		// 需要考虑当选择了项目负责人字段的时候需要添加的角色

		// 在parent组下创建项目组
		ObjectId pjTeamId = (ObjectId) createOBSItem(null, parentId, teamId, inputData.getText(FIELD_DESC), VALUE_OBS_PJTEAMTYPE).get(FIELD_SYSID);
		return pjTeamId;
	}

	public static ObjectId createDefaultProjectTeam(ISingleObject inputData) {

		ObjectId parentId = (ObjectId) inputData.getValue(FIELD_OBSPARENT);
		ObjectId teamId = (ObjectId) inputData.getValue(FIELD_PROJECT_OBS_ROOT);
		Assert.isNotNull(parentId);
		// 需要考虑当选择了项目负责人字段的时候需要添加的角色

		// 在parent组下创建项目组
		ObjectId pjTeamId = (ObjectId) createOBSItem(null, parentId, teamId, inputData.getText(FIELD_DESC), VALUE_OBS_PJTEAMTYPE).get(FIELD_SYSID);

		// 为项目组添加默认的角色
		// 添加默认项目经理角色
		ObjectId pjManagerId = (ObjectId) createOBSItem(teamId, pjTeamId, null, VALUE_ROLE_PROJECTMANAGER, VALUE_OBS_ROLETYPE).get(FIELD_SYSID);
		createOBSItem(teamId, pjManagerId, null, UserSessionContext.getSession().getUserOId(), VALUE_OBS_USERTYPE);

		// 添加默认的项目管理员角色
		ObjectId pjAdminId = (ObjectId) createOBSItem(teamId, pjTeamId, null, VALUE_ROLE_PROJECTADMIN, VALUE_OBS_ROLETYPE).get(FIELD_SYSID);
		// 将当前用户添加到项目管理员
		createOBSItem(teamId, pjAdminId, null, UserSessionContext.getSession().getUserOId(), VALUE_OBS_USERTYPE);

		// 添加默认的项目观察者角色
		createOBSItem(teamId, pjTeamId, null, VALUE_ROLE_PROJECTOBSERVER, VALUE_OBS_ROLETYPE);

		return pjTeamId;
	}

	/**
	 * 
	 * Create OBS节点
	 * 这个方法已经被组织服务的同名方法取代
	 * @param rootId
	 *            项目id项目组通常作为根组织存在 ,如果不为空，将向obs的rootid字段传递根组织的id,创建根组织本身时这个将被忽略
	 * @param parentId
	 *            上级id
	 * @param itemId
	 *            本级id,可以传null，传空时返回id
	 * @param desc
	 *            ,当创建用户时，传递用户的oid,其他情况传描述
	 * @param obstype
	 *            ，传递obs的类型 VALUE_OBS_ROLETYPE, VALUE_OBS_USERTYPE
	 *            ,VALUE_OBS_PJTEAMTYPE ,VALUE_OBS_TEAMTYPE
	 * @return 该obs节点的oid
	 */
	@Deprecated
	public static DBObject createOBSItem(ObjectId rootId, ObjectId parentId, ObjectId itemId, Object desc, String obstype) {

		DBCollection obsCollection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		if (itemId == null) {
			itemId = new ObjectId();
		}
		DBObject obsItem = new BasicDBObject();
		if (rootId != null && (!Util.equals(rootId, itemId))) {
			obsItem.put(FIELD_ROOTID, rootId);
		}
		obsItem.put(FIELD_SYSID, itemId);
		obsItem.put(FIELD_TEMPLATE_TYPE, obstype);
		obsItem.put(FIELD_OBSPARENT, parentId);

		if (VALUE_OBS_USERTYPE.equals(obstype)) {
			obsItem.put(FIELD_USEROID, desc);
		} else {
			obsItem.put(FIELD_DESC, desc);
		}
		DataUtil.setSystemCreateInfo(obsItem);

		obsCollection.insert(obsItem);

		return obsItem;
	}

	/**
	 * 返回某个站点Id下所有的用户
	 * 
	 * @param siteContextId
	 * @return
	 */
	public static List<ISingleObject> getSiteUsers(ObjectId siteId, boolean cascade) {

		List<ISingleObject> userList = new ArrayList<ISingleObject>();
		// 查询当前站点
		QueryExpression siteExp = DBActivator.getQueryExpression(EXP_QUERY_SITE);
		siteExp.setParamValue(PARAM_INPUT_ID, siteId);
		DBCursor curorCurrentSite = siteExp.run();
		if (!curorCurrentSite.hasNext()) {
			return userList;
		}

		// 当前站点
		DBObject currentSiteData = curorCurrentSite.next();
		Object currentOID = currentSiteData.get(FIELD_SYSID);
		// 获得上级站点 id
		Object parentSiteId = currentSiteData.get(FIELD_SITEPARENT);

		siteExp.clean();
		siteExp.setParamValue(PARAM_INPUT_SITEPARENT, parentSiteId);
		DBCursor cursorBrother = siteExp.run();
		while (cursorBrother.hasNext()) {
			DBObject siteData = cursorBrother.next();
			Object siteOID = siteData.get(FIELD_SYSID);
			if (Util.equals(currentOID, siteOID)) {
				listSiteUsers(siteData, userList, cascade);
			} else if (Boolean.TRUE.equals(siteData.get(FIELD_SHARESITE))) {
				listSiteUsers(siteData, userList, cascade);
			}
		}

		return userList;

	}

	private static void listSiteUsers(DBObject siteData, List<ISingleObject> userList, boolean cascade) {

		String siteName = (String) siteData.get(FIELD_DESC);
		ObjectId siteOId = (ObjectId) siteData.get(FIELD_SYSID);

		// 查询当前站点下级的用户
		QueryExpression userExp = DBActivator.getQueryExpression(EXP_QUERY_USER);
		userExp.setParamValue(PARAM_INPUT_SITEPARENT, siteOId);
		DBCursor cur2 = userExp.run();
		if (cur2.hasNext()) {
			DBCollection useCol = DBActivator.getDefaultDBCollection(COLLECTION_USER);
			while (cur2.hasNext()) {
				SingleObject so = new SingleObject(useCol, cur2.next());
				so.setValue("siteName", siteName);
				userList.add(so);
			}
		}

		// 查询下级站点
		if (cascade) {
			QueryExpression subSiteExp = DBActivator.getQueryExpression(EXP_QUERY_SITE);
			subSiteExp.setParamValue(PARAM_INPUT_SITEPARENT, siteOId);
			DBCursor cur3 = subSiteExp.run();
			while (cur3.hasNext()) {
				DBObject subSiteData = cur3.next();
				listSiteUsers(subSiteData, userList, true);
			}
		}
	}

	public static int createOBSItemUI(Shell shell, CascadeObject currentOrg, String obsType) {

		String editorConfId = null;
		if (VALUE_OBS_ROLETYPE.equals(obsType)) {
			editorConfId = EDITOR_ROLE;
		} else if (VALUE_OBS_TEAMTYPE.equals(obsType)) {
			editorConfId = EDITOR_TEAM;
		}
		EditorConfiguration editorConfiguration = Widget.getSingleObjectEditorConfiguration(editorConfId);

		DBCollection collection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		SingleObject so = new SingleObject(collection);

		// 给出obsparent的值
		ObjectId parentId = (ObjectId) currentOrg.getValue(FIELD_SYSID);
		so.setValue(FIELD_OBSPARENT, parentId, null, false);

		// 创建组织文件夹
		if (VALUE_OBS_TEAMTYPE.equals(obsType)) {
			so.setValue(FIELD_FOLDER_ROOT, new ObjectId(), null, false);
		}

		so.setValue(FIELD_TEMPLATE_TYPE, obsType, null, false);
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);

		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(shell, editorConfId, editInput, null, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();

			// 创建组织文件夹
			if (VALUE_OBS_TEAMTYPE.equals(obsType)) {
				createOrgFolder(data.getData());
			}

			// reload
			currentOrg.createChild(EXP_CASCADE_SO_OBS, data.getData(), collection);
		}
		return ok;
	}

	/**
	 * 进行任务的自动指派
	 * 
	 * @param masterProject
	 */
	@Deprecated
	public static void autoAssignment(ObjectId projectId) {

		// 取出下级
		CascadeObject cascadeWBS = DBActivator.getCascadeObject(EXP_CASCADE_WBS_WITH_DOC);
		cascadeWBS.setParamValue(FIELD_SYSID, projectId);
		List<CascadeObject> children = cascadeWBS.getChildren();// 获得级联查询根
		if (children.size() < 1) {
			return;
		}
		CascadeObject project = children.get(0);// 获得项目
		autoAssignmentChildren(project.getChildren());// 对项目的每个子节点进行指派
	}

	private static void autoAssignmentChildren(List<CascadeObject> list) {

		for (CascadeObject co : list) {
			assignmentWBSItemByDefault(co);// 指派某一个wbs节点
		}
	}

	private static void assignmentWBSItemByDefault(CascadeObject work) {

		BasicDBList participates = (BasicDBList) work.getValue(FIELD_PARTICIPATE);// 获取该节点的参与者定义
		if (participates != null && participates.size() > 0) {

			BasicDBList parentList = new BasicDBList();// 初始化参与者定义的列表，用于存放参与者定义的id(参与者定义是，角色或者组织，角色和组织在项目的OBS建立时已经建立)
			for (int i = 0; i < participates.size(); i++) {
				DBObject p = (DBObject) participates.get(i);
				ObjectId pid = (ObjectId) p.get(FIELD_SYSID);
				parentList.add(pid);
			}

			// 在obs上查询对应的user节点
			DBCollection collection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
			DBObject query = new BasicDBObject();
			query.put(FIELD_OBSPARENT, new BasicDBObject().append("$in", parentList));
			query.put(FIELD_TEMPLATE_TYPE, VALUE_OBS_USERTYPE);
			DBCursor cursor = collection.find(query);

			// 从obs上获得useroid
			BasicDBList userIdList = new BasicDBList();

			while (cursor.hasNext()) {
				DBObject obsItem = cursor.next();
				userIdList.add(obsItem.get(FIELD_USEROID));
			}

			// 从user中获得user的详细信息
			DBCollection userCollection = DBActivator.getDefaultDBCollection(COLLECTION_USER);
			DBObject userQuery = new BasicDBObject();
			userQuery.put(FIELD_SYSID, new BasicDBObject().append("$in", userIdList));
			DBCursor userCursor = userCollection.find(userQuery);

			BasicDBList resource = new BasicDBList();// 初始化资源，准备将这个插入到wbs节点的资源字段
			while (userCursor.hasNext()) {
				DBObject data = userCursor.next();
				resource.add(getRefData(data, DATA_USER_BASIC));
			}

			if (resource.size() > 0) {
				// 同步user信息
				if (isWorkObject(work)) {
					DBObject chargerData = (DBObject) resource.get(0);
					work.setValue(FIELD_WORK_PM, chargerData);
					resource.remove(0);
					work.setValue(FIELD_WORK_RESOURCE, resource);
					work.save();
					/*
					 * 自动指派任务时，项目必须处于准备状态，此时，不向用户发出Message, 本段完全注释 ObjectId id =
					 * work.getSystemId(); //同步user任务负责人
					 * saveUserRelationInformation(null, (ObjectId)
					 * chargerData.get(FIELD_SYSID),
					 * COLLECTION_USER_WORK_IN_CHARGED, id); //同步user任务参与者
					 * Iterator<Object> iter = resource.iterator();
					 * while(iter.hasNext()){ DBObject user = (DBObject)
					 * iter.next(); saveUserRelationInformation(null, (ObjectId)
					 * user.get(FIELD_SYSID),
					 * COLLECTION_USER_WORK_PARTTICIPATED, id); }
					 */
				}
			}
		}

		autoAssignmentChildren(work.getChildren());
	}

	public static void editOBSItemUI(Shell shell, CascadeObject currentOrg) {

		String editorConfId = null;
		if (DataUtil.isRoleObject(currentOrg)) {
			editorConfId = EDITOR_ROLE;
		} else if (DataUtil.isTeamObject(currentOrg)) {
			editorConfId = EDITOR_TEAM;
		}
		SingleObjectEditorInput editInput = new SingleObjectEditorInput(currentOrg);
		SingleObjectEditorDialog.OPEN(shell, editorConfId, editInput, null, false);
	}

	public static DBObject simpleQuery(String exp, String fieldName, Object value) {

		QueryExpression queryOrg = DBActivator.getQueryExpression(exp);
		queryOrg.setParamValue(fieldName, value);
		DBCursor cur = queryOrg.run();
		if (cur.hasNext()) {
			return cur.next();
		} else {
			return null;
		}
	}

	public static String getProcessStatus(ISingleObject row) {

		Object statusCode = row.getValue(FIELD_PROCESS_STATUS);
		if (VALUE_PROCESS_CANCEL.equals(statusCode)) {
			return "已取消";
		}
		if (VALUE_PROCESS_CLOSE.equals(statusCode)) {
			return "已完成";
		}
		if (VALUE_PROCESS_PAUSE.equals(statusCode)) {
			return "暂停中";
		}
		if (VALUE_PROCESS_PROCESS.equals(statusCode)) {
			return "进行中";
		}
		return "准备中";
	}

	public static String getProcessStatusImageURL(ISingleObject row) {

		Object statusCode = row.getValue(FIELD_PROCESS_STATUS);
		String key = null;
		if (VALUE_PROCESS_CANCEL.equals(statusCode)) {
			key = Resource.IMAGE_CANCEL16;
		} else if (VALUE_PROCESS_CLOSE.equals(statusCode)) {
			key = Resource.IMAGE_CLOSE16;
		} else if (VALUE_PROCESS_PAUSE.equals(statusCode)) {
			key = Resource.IMAGE_PAUSE16;
		} else if (VALUE_PROCESS_PROCESS.equals(statusCode)) {
			key = Resource.IMAGE_PROCESS16;
		} else {
			key = Resource.IMAGE_READY16;
		}
		return FileUtil.getImageLocationFromInputStream(key, Resource.getDefault().getImageInputStream(key));
	}

	public static DBObject getUserInformation(DBObject input, boolean sync) {

		BasicDBObject userInfor = new BasicDBObject();
		String userId = (String) input.get(FIELD_UID);
		String userDesc = (String) input.get(FIELD_DESC);
		String userName = (String) input.get(FIELD_NAME);
		String email = (String) input.get(FIELD_EMAIL);

		if (sync && (userId == null || userDesc == null || userName == null || email == null)) {
			ObjectId oid = (ObjectId) input.get(FIELD_SYSID);
			if (oid != null) {
				// 用户基本信息需要进行同步
				input = getDataObject(COLLECTION_USER, oid);
				DBObject data = getUserInformation(input, false);
				userId = (String) data.get(FIELD_UID);
				userDesc = (String) data.get(FIELD_DESC);
				;
				userName = (String) data.get(FIELD_NAME);
				;
				email = (String) data.get(FIELD_EMAIL);
				;
			}
		}

		String siteName = (String) input.get(FIELD_SITENAME);
		;
		if (sync && (siteName == null)) {
			// 用户站点信息需要同步
			ObjectId siteId = (ObjectId) input.get(FIELD_SITEPARENT);
			if (siteId != null)
				siteName = (String) getDataObject(COLLECTION_SITE, siteId).get(FIELD_DESC);
		}

		String activate;
		String activeUserImageUrl;
		String userImageUrl = getUserImageURL(input);
		boolean activtvate = Boolean.TRUE.equals(input.get(FIELD_ACTIVATE));

		if (activtvate) {
			activate = "激活";
			activeUserImageUrl = getUserActiveImageURL(true);
		} else {
			activate = "未激活";
			activeUserImageUrl = getUserActiveImageURL(false);
		}

		String url = (String) userInfor.get(FIELD_URLLABEL);

		if (url == null) {

			StringBuilder builder = new StringBuilder();

			builder.append("<img src=\"");
			builder.append(userImageUrl);
			builder.append("\" style=\"float:left;padding:5px\" width=\"64\" height=\"64\" />");
			builder.append("<b>");
			builder.append(userName);
			builder.append("(");
			builder.append(userDesc);
			builder.append(")");
			builder.append("</b>");

			builder.append("<small><br/><i>");

			builder.append(email == null ? "" : email);
			builder.append("<br/>");

			if (!Util.isNullorEmpty(siteName)) {

				builder.append("站点: ");
				builder.append(siteName);
				builder.append("<br/>");
			}

			builder.append("<img src='" + activeUserImageUrl + "' width='10' height='10' style='padding-right:5px'/>");
			builder.append(activate);

			builder.append("</i><br/></small>");
			url = builder.toString();
		}

		userInfor.put(FIELD_UID, userId);
		userInfor.put(FIELD_DESC, userDesc);
		userInfor.put(FIELD_NAME, userName);
		userInfor.put(FIELD_SITENAME, siteName);
		userInfor.put(FIELD_EMAIL, email);
		userInfor.put(FIELD_ACTIVATE, activtvate);
		userInfor.put(FIELD_URLLABEL, url);

		if (sync) {
			input.put(FIELD_UID, userId);
			input.put(FIELD_DESC, userDesc);
			input.put(FIELD_NAME, userName);
			input.put(FIELD_SITENAME, siteName);
			input.put(FIELD_EMAIL, email);
			input.put(FIELD_ACTIVATE, activtvate);
			input.put(FIELD_URLLABEL, url);
		}

		return userInfor;
	}

	private static String getUserActiveImageURL(boolean active) {

		if (active) {
			return FileUtil.getImageLocationFromInputStream(Resource.IMAGE_ACTIVE16, Resource.getDefault().getImageInputStream(Resource.IMAGE_ACTIVE16));
		} else {
			return FileUtil.getImageLocationFromInputStream(Resource.IMAGE_DISACTIVE16, Resource.getDefault().getImageInputStream(Resource.IMAGE_DISACTIVE16));
		}
	}

	public static Image getUserImage(DBObject dbObject) {

		BasicDBList thumbList = (BasicDBList) dbObject.get(FIELD_THUMB);

		Image image = null;
		if (thumbList != null && !thumbList.isEmpty()) {
			ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
			DBObject dbo = (DBObject) thumbList.iterator().next();
			String namespace = (String) dbo.get("namespace");
			String fileObjectid = ((ObjectId) dbo.get("_id")).toString();
			//首先从注册文件中找
			String key = "cpm_" + namespace + "." + fileObjectid;
			image = imageRegistry.get(key);
			//没有注册的情况下，从数据库读取
			if (image == null) {
				image = FileUtil.getImageFileFromGridFS(namespace, fileObjectid);
			}
			
			//对image进行放缩到64X64大小
			if(image!=null){
				Rectangle b = image.getBounds();
				if(b.width!=64||b.height!=64){
					int disWidth = 0;
					int disHeight = 0;
					if(b.width>b.height){
						disWidth = 64;
						disHeight = b.height*64/b.width;
					}else{
						disHeight = 64;
						disWidth = b.width*64/b.height;
					}
						
					ImageData imageData = image.getImageData();
					image = new Image(null, imageData.scaledTo(disWidth,disHeight));
					imageRegistry.put(key, image);
				}
			}
		}

		if(image == null){
			image = Resource.getImage(Resource.IMAGE_DEFAULT_USER64);
		}
		
		return image;
	}

	public static String getUserImageURL(DBObject userData) {

		BasicDBList thumbList = (BasicDBList) userData.get(FIELD_THUMB);

		String imgLocation = null;
		if (thumbList != null && !thumbList.isEmpty()) {
			DBObject dbo = (DBObject) thumbList.iterator().next();

			String namespace = (String) dbo.get("namespace");
			ObjectId fileObjectid = (ObjectId) dbo.get("_id");

			imgLocation = FileUtil.getImageLocationFromDatabase(namespace, fileObjectid);
		}

		if (imgLocation == null) {
			if (Boolean.TRUE.equals(userData.get(FIELD_ACTIVATE))) {
				imgLocation = FileUtil.getImageLocationFromInputStream(Resource.IMAGE_DEFAULT_USER64,
						Resource.getDefault().getImageInputStream(Resource.IMAGE_DEFAULT_USER64));
			} else {
				imgLocation = FileUtil.getImageLocationFromInputStream(Resource.IMAGE_DEFAULT_USER_D64,
						Resource.getDefault().getImageInputStream(Resource.IMAGE_DEFAULT_USER_D64));
			}
		}
		if (imgLocation != null)
			return FileUtil.getImageUrl(imgLocation);
		return null;
	}

	/**
	 * 创建项目的根文件夹
	 * 
	 * @param projectData
	 */
	public static void createProjectFolder(DBObject projectData) {

		// 是否需要按wbs建立项目目录结构
		boolean createFBSasWBS = Boolean.TRUE.equals(projectData.get(FIELD_PROJECT_FOLDER_CREATE_FBS_AS_WBS));

		ObjectId givenId = (ObjectId) projectData.get(FIELD_FOLDER_ROOT);
		String folderName = (String) projectData.get(FIELD_DESC);
		ObjectId fbsParentId = (ObjectId) projectData.get(FIELD_SYSID);

		createProjectFolderStructure(givenId, folderName, fbsParentId, createFBSasWBS);
	}

	public static void createOrgFolder(DBObject orgData) {

		DBObject folder = getFolder(orgData.get(FIELD_FOLDER_ROOT), orgData.get(FIELD_SYSID), orgData.get(FIELD_DESC));
		DBCollection collection = DBActivator.getDefaultDBCollection(COLLECTION_FOLDER);
		collection.insert(folder);
	}

	public static DBObject getFolder(Object givenId, Object fbsParentId, Object folderName) {

		DBObject folderData = new BasicDBObject();
		folderData.put(FIELD_SYSID, givenId);
		folderData.put(FIELD_FBSPARENT, fbsParentId);
		folderData.put(FIELD_DESC, folderName);
		setSystemCreateInfo(folderData);
		return folderData;
	}

	public static DBObject createProjectFolderStructure(ObjectId givenId, String folderName, ObjectId fbsParentId, boolean createFBSasWBS) {

		DBCollection documentCollection = DBActivator.getDefaultDBCollection(COLLECTION_DOCUMENT);
		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		DBCollection folderCollection = DBActivator.getDefaultDBCollection(COLLECTION_FOLDER);

		DBObject folderData = getFolder(givenId, fbsParentId, folderName);

		// 如果需要按照wbs创建fbs
		if (createFBSasWBS) {
			// work上挂的wbsprentid,新的目录对象
			Map<String, DBObject> workId_FolderMap = new HashMap<String, DBObject>();
			workId_FolderMap.put(fbsParentId.toString(), folderData);// 添加项目根

			// 查询rootid下的所有工作
			DBCursor workCur = workCollection.find(new BasicDBObject().append(FIELD_ROOTID, fbsParentId),
					new BasicDBObject().append(FIELD_SYSID, 1).append(FIELD_DESC, 1).append(FIELD_WBSPARENT, 1));
			// 为对象重新分配id
			while (workCur.hasNext()) {
				DBObject work = workCur.next();
				DBObject folder = getFolder(new ObjectId(), work.get(FIELD_WBSPARENT), work.get(FIELD_DESC));
				ObjectId workId = (ObjectId) work.get(FIELD_SYSID);
				workId_FolderMap.put(workId.toString(), folder);
			}

			// 更新wbsparent到fbsparent
			Collection<DBObject> folders = workId_FolderMap.values();
			Iterator<DBObject> iter = folders.iterator();
			while (iter.hasNext()) {
				DBObject folder = iter.next();
				// 排除根目录
				if (givenId.equals(folder.get(FIELD_SYSID)))
					continue;
				ObjectId wbsParentId = (ObjectId) folder.get(FIELD_FBSPARENT);
				DBObject parentFolder = workId_FolderMap.get(wbsParentId.toString());
				if (parentFolder == null)
					continue;// 不应该为空
				folder.put(FIELD_FBSPARENT, parentFolder.get(FIELD_SYSID));
			}

			folderCollection.insert((DBObject[]) folders.toArray(new DBObject[] {}), WriteConcern.NORMAL);

			// 更新文档
			DBCursor docCur = documentCollection.find(new BasicDBObject().append(FIELD_ROOTID, fbsParentId));
			while (docCur.hasNext()) {
				DBObject doc = docCur.next();
				ObjectId wbsParentId = (ObjectId) doc.get(FIELD_WBSPARENT);
				DBObject parentFolder = workId_FolderMap.get(wbsParentId.toString());
				if (parentFolder == null)
					continue;// 不应该为空
				doc.put(FIELD_FBSPARENT, parentFolder.get(FIELD_SYSID));
				documentCollection.save(doc);
			}

		} else {
			// 直接把文档挂在项目的根目录
			documentCollection.update(new BasicDBObject().append(FIELD_ROOTID, fbsParentId),
					new BasicDBObject().append("$set", new BasicDBObject().append(FIELD_FBSPARENT, givenId)), false, true);
			// 创建项目根目录对象
			folderCollection.insert(folderData);
		}

		return folderData;

	}

	/**
	 * 获得当前上下文的项目列表，id
	 * 
	 * @return
	 */
	@Deprecated
	public static BasicDBList getContextControlProjectIdList() {

		QueryExpression exp = DBActivator.getQueryExpression(EXP_QUERY_PROJECT);
		BasicDBList result = new BasicDBList();
		DBCursor cur = exp.run();
		while (cur.hasNext()) {
			result.add(cur.next().get(FIELD_SYSID));
		}
		return result;
	}

	/**
	 * 获得当前上下文的组织列表, id
	 * 
	 * @return
	 */
	@Deprecated
	public static BasicDBList getContextControlOrgIdList() {

		ObjectId uid = UserSessionContext.getSession().getUserOId();
		// 在OBS上查询用户的记录
		QueryExpression obs = DBActivator.getQueryExpression(EXP_QUERY_OBS);
		obs.setParamValue(PARAM_INPUT_USEROID, uid);
		DBCursor cur = obs.run();
		BasicDBList result = new BasicDBList();
		while (cur.hasNext()) {
			DBObject userInObs = cur.next();
			DBObject team = getParentTeamOfUser(userInObs);
			if (team != null) {
				result.add(team.get(FIELD_SYSID));
				// 补丁方法，开发版本中存在一些组织没有根目录的情况，这个补丁程序解决这个问题，为没有根目录的组织添加根目录
				if (team.get(FIELD_FOLDER_ROOT) == null) {
					team.put(FIELD_FOLDER_ROOT, new ObjectId());
					DBActivator.getDefaultDBCollection(COLLECTION_ORG).save(team);
					createOrgFolder(team);
				}

			}
		}
		return result;
	}

	// 获得obs 用户上级的一个team
	private static DBObject getParentTeamOfUser(DBObject obsItem) {

		Object parentId = obsItem.get(FIELD_OBSPARENT);
		if (parentId != null) {
			QueryExpression obs = DBActivator.getQueryExpression(EXP_QUERY_OBS);
			obs.setParamValue(PARAM_INPUT_ID, parentId);
			DBCursor cur = obs.run();
			if (cur.hasNext()) {
				DBObject parent = cur.next();
				if (VALUE_OBS_TEAMTYPE.equals(parent.get(FIELD_TEMPLATE_TYPE))) {// 如果是组织，可以返回
					return parent;
				} else if (VALUE_OBS_ROLETYPE.equals(parent.get(FIELD_TEMPLATE_TYPE))) {// 如果是角色，继续上一级
					return getParentTeamOfUser(parent);
				}
			}
		}

		return null;

	}

	public static int createFolderItemUI(Shell shell, CascadeObject fbsParent) {

		EditorConfiguration editorConfiguration = Widget.getSingleObjectEditorConfiguration(EDITOR_FOLDER);

		DBCollection collection = DBActivator.getDefaultDBCollection(COLLECTION_FOLDER);
		SingleObject so = new SingleObject(collection);

		// 给出fbsparent的值
		ObjectId parentId = (ObjectId) fbsParent.getValue(FIELD_SYSID);
		so.setValue(FIELD_FBSPARENT, parentId);

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);

		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(shell, EDITOR_FOLDER, editInput, null, true);
		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();
			// reload
			fbsParent.createChild(EXP_CASCADE_SO_FBS, data.getData(), collection);
		}
		return ok;

	}

	public static int createDocumentInFolderItemUI(Shell shell, CascadeObject fbsParent, ObjectId rootId) {

		EditorConfiguration editorConfiguration = Widget.getSingleObjectEditorConfiguration(EDITOR_DELIVERDOCUMENT_CREATE);

		DBCollection collection = DBActivator.getDefaultDBCollection(COLLECTION_DOCUMENT);
		SingleObject so = new SingleObject(collection);

		// 给出fbsparent的值
		ObjectId parentId = (ObjectId) fbsParent.getValue(FIELD_SYSID);

		so.setValue(FIELD_FBSPARENT, parentId);

		// 添加rootid
		so.setValue(FIELD_ROOTID, rootId);

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);
		ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {

				// 更改文件名
				Set<EditorConfiguration> editorSet = Widget.listSingleObjectEditorConfigurationByCollection(COLLECTION_DOCUMENT);
				String id = (String) input.getInputData().getValue(FIELD_SYSTEM_EDITOR);
				Iterator<EditorConfiguration> iter = editorSet.iterator();
				while (iter.hasNext()) {
					EditorConfiguration ec = iter.next();
					if (ec.getId().equals(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE)) {// 排除基本文档
						continue;
					}
					if (ec.getId().equals(id)) {
						input.getInputData().setValue(FIELD_DESC, ec.getName(), null, false);
					}
				}

				return super.saveBefore(input);
			}

		};
		// create
		SingleObjectEditorDialog soed = SingleObjectEditorDialog.getInstance(shell, EDITOR_DELIVERDOCUMENT_CREATE, editInput, call, true);

		int ok = soed.open();
		if (ok == SingleObjectEditorDialog.OK) {
			ISingleObject data = soed.getInputData();
			// reload
			fbsParent.createChild(EXP_CASCADE_SO_FBS, data.getData(), collection);
		}
		return ok;
	}

	/**
	 * 获得用于其他集合应用的参考数据
	 * 
	 * @param userData
	 * @return
	 */
	public static DBObject getRefData(DBObject data, String[] refFields) {

		DBObject result = new BasicDBObject();
		for (String fieldName : refFields) {
			Object object = data.get(fieldName);
			if (object != null) {
				result.put(fieldName, object);
			}
		}
		return result;
	}

	// 将数据同步保存至用户的负责的项目清单中，冗余保存以提高性能
	// public static void saveUserProjectincharged(ObjectId originalChargerId,
	// DBObject projectData) {
	// ObjectId newChargerId ;
	// if(projectData!=null){
	// DBObject newCharger = (DBObject) projectData.get(FIELD_WORK_PM);
	// newChargerId = (ObjectId) newCharger.get(FIELD_SYSID);
	// }else{
	// newChargerId = null;
	// }
	//
	// if(Util.equals(newChargerId, originalChargerId)){
	// return;
	// }
	// ObjectId id = (ObjectId) projectData.get(FIELD_SYSID);
	//
	// saveUserWorkAndProjectInformation(originalChargerId, newChargerId,
	// FIELD_USER_PROJECT_IN_CHARGED, id);
	//
	// }

	// public static void saveUserWorkincharged(ObjectId originalChargerId,
	// DBObject workData) {
	// ObjectId newChargerId ;
	// if(workData!=null){
	// DBObject newCharger = (DBObject) workData.get(FIELD_WORK_PM);
	// newChargerId = (ObjectId) newCharger.get(FIELD_SYSID);
	// }else{
	// newChargerId = null;
	// }
	//
	// if(Util.equals(newChargerId, originalChargerId)){
	// return;
	// }
	//
	// ObjectId id = (ObjectId) workData.get(FIELD_SYSID);
	//
	// saveUserWorkAndProjectInformation(originalChargerId, newChargerId,
	// FIELD_USER_WORK_IN_CHARGED, id);
	//
	// }

	public static void saveUserRelationInformation(ObjectId originalUserId, ObjectId newUserId, String typeName, ObjectId idValue) {

		saveUserRelationInformation(originalUserId, newUserId, typeName, idValue, false);
	}

	public static void saveUserRelationInformation(ObjectId originalUserId, ObjectId newUserId, String typeName, ObjectId idValue, boolean replace) {

		if (Util.equals(originalUserId, newUserId)) {
			return;
		}

		// 关联关系表
		// _id, useroid,valueId,systeminfo
		DBCollection refCollection = DBActivator.getDefaultDBCollection(typeName);

		if (originalUserId != null) {
			BasicDBObject query = new BasicDBObject().append(FIELD_USEROID, originalUserId).append(FIELD_ID, idValue);
			if (replace) {
				refCollection.remove(query);
			} else {
				refCollection.update(query, new BasicDBObject().append("$set", new BasicDBObject().append(FIELD_MARK_DELETE, true)));
			}

			// DBObject oUser = userCollection.findOne(new
			// BasicDBObject().append(FIELD_SYSID, originalUserId));
			// BasicDBList list = (BasicDBList) oUser.get(typeName);
			// //查询包含 _id = value的记录
			// BasicDBList shouldBeDeleteList = new BasicDBList();
			// if(list!=null&&!list.isEmpty()){
			// for(int i=0;i<list.size();i++){
			// DBObject message = (DBObject) list.get(i);
			// if(message.get(FIELD_SYSID).equals(idValue)){
			// shouldBeDeleteList.add(message);
			// }
			// }
			// }
			// if(shouldBeDeleteList.size()>0){
			// //更新原来用户的message清单，从中移去
			// userCollection.update(
			// new BasicDBObject().append(FIELD_SYSID, newUserId),
			// new BasicDBObject().append("$pullAll",
			// new BasicDBObject().append(typeName, shouldBeDeleteList)));
			// }

		}

		if (newUserId != null) {
			// 添加到新的用户中
			DBObject message = new BasicDBObject();
			message.put(FIELD_USEROID, newUserId);
			message.put(FIELD_ID, idValue);
			setSystemCreateInfo(message);

			refCollection.insert(message);

			// DBObject messageData = new BasicDBObject();
			// messageData.put(FIELD_SYSID, idValue);
			//
			// userCollection.update(
			// new BasicDBObject().append(FIELD_SYSID,originalUserId),
			// new BasicDBObject().append("$addToSet",
			// new BasicDBObject().append(typeName, idValue)));
		}

	}

	public static void saveUserWorkAndProjectInformation(BasicDBList originalUserList, BasicDBList newUserList, String fieldName, ObjectId id) {

		if (originalUserList != null) {
			Iterator<Object> iter = originalUserList.iterator();
			while (iter.hasNext()) {
				DBObject user = (DBObject) iter.next();
				ObjectId userId = (ObjectId) user.get(FIELD_SYSID);
				saveUserRelationInformation(userId, null, fieldName, id);
			}
		}

		if (newUserList != null) {
			Iterator<Object> iter = newUserList.iterator();
			while (iter.hasNext()) {
				DBObject user = (DBObject) iter.next();
				ObjectId userId = (ObjectId) user.get(FIELD_SYSID);
				saveUserRelationInformation(null, userId, fieldName, id);
			}
		}
	}

	/*
	 * 项目启动，任务的状态改为准备（同步用户任务消息数据） 项目暂停，任务的状态改为暂停 项目终止，任务的状态改为终止
	 * 项目完成，任务的状态改为终止（如果任务当前的状态是完成，则不改）
	 */
	@Deprecated
	public static void projectStart(SingleObject master) {

		master.setValue(FIELD_PROCESS_STATUS, VALUE_PROCESS_PROCESS);
		master.setValue(FIELD_PROJECT_ACTUALSTART, new Date());

		master.save();

		// *************************************************************************
		// 项目启动，任务的状态改为准备（同步用户任务消息数据）
		ObjectId projectId = master.getSystemId();
		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		// workCollection.update(
		// new BasicDBObject()
		// .append(FIELD_ROOTID, projectId),
		// new BasicDBObject()
		// .append(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY)
		// , false, true);

		// 保存任务消息
		DBCursor cur = workCollection.find(new BasicDBObject().append(FIELD_ROOTID, projectId));
		DBObject work, chargerData;
		BasicDBList resource;
		while (cur.hasNext()) {
			work = cur.next();
			if(VALUE_PROCESS_CLOSE.equals(work.get(FIELD_PROCESS_STATUS))){
				continue;
			}
			if(VALUE_PROCESS_CANCEL.equals(work.get(FIELD_PROCESS_STATUS))){
				continue;
			}
			
			// 更改任务的状态
			work.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY);
			workCollection.save(work);

			ObjectId id = (ObjectId) work.get(FIELD_SYSID);
			chargerData = (DBObject) work.get(FIELD_WORK_PM);
			resource = (BasicDBList) work.get(FIELD_WORK_RESOURCE);

			// 如果这个任务是先被暂停后被使用开始恢复的，那么是已经有了任务通知
			// 需要先删除，后新增
			cleanWorkInformation(id);

			// 同步user任务负责人
			if (chargerData != null) {
				DataUtil.saveUserRelationInformation(null, (ObjectId) chargerData.get(FIELD_SYSID), COLLECTION_USER_WORK_IN_CHARGED, id);
			}

			// 同步user任务参与者
			if (resource != null) {
				Iterator<Object> iter = resource.iterator();
				while (iter.hasNext()) {
					DBObject user = (DBObject) iter.next();
					DataUtil.saveUserRelationInformation(null, (ObjectId) user.get(FIELD_SYSID), COLLECTION_USER_WORK_PARTTICIPATED, id);
				}
			}
		}

		// *************************************************************************

	}

	private static void cleanWorkInformation(ObjectId id) {

		DBCollection participate = DBActivator.getDefaultDBCollection(COLLECTION_USER_WORK_PARTTICIPATED);
		DBCollection charge = DBActivator.getDefaultDBCollection(COLLECTION_USER_WORK_IN_CHARGED);
		participate.remove(new BasicDBObject().append(FIELD_ID, id));
		charge.remove(new BasicDBObject().append(FIELD_ID, id));
	}

	/*
	 * 项目启动，任务的状态改为准备（同步用户任务消息数据） 项目暂停，任务的状态改为暂停 项目终止，任务的状态改为终止
	 * 项目完成，任务的状态改为终止（如果任务当前的状态是完成，则不改）
	 */
	public static void projectStop(SingleObject master) {

		master.setValue(FIELD_PROCESS_STATUS, VALUE_PROCESS_PAUSE);
		master.save();

		// *************************************************************************
		ObjectId projectId = master.getSystemId();
		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		workCollection.update(new BasicDBObject().append(FIELD_ROOTID, projectId).append(FIELD_PROCESS_STATUS, new BasicDBObject().append("$ne", VALUE_PROCESS_CLOSE)),
				new BasicDBObject().append("$set", new BasicDBObject().append(FIELD_PROCESS_STATUS, VALUE_PROCESS_PAUSE)), false, true);

	}

	/*
	 * 项目启动，任务的状态改为准备（同步用户任务消息数据） 项目暂停，任务的状态改为暂停 项目终止，任务的状态改为终止
	 * 项目完成，任务的状态改为终止（如果任务当前的状态是完成，则不改）
	 */
	public static void projectCancel(SingleObject master) {

		master.setValue(FIELD_PROCESS_STATUS, VALUE_PROCESS_CANCEL);
		master.save();

		// *************************************************************************
		ObjectId projectId = master.getSystemId();
		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		workCollection.update(new BasicDBObject().append(FIELD_ROOTID, projectId).append(FIELD_PROCESS_STATUS, new BasicDBObject().append("$ne", VALUE_PROCESS_CLOSE)),
				new BasicDBObject().append("$set", new BasicDBObject().append(FIELD_PROCESS_STATUS, VALUE_PROCESS_CANCEL)), false, true);
	}

	/*
	 * 项目启动，任务的状态改为准备（同步用户任务消息数据） 项目暂停，任务的状态改为暂停 项目终止，任务的状态改为终止
	 * 项目完成，任务的状态改为终止（如果任务当前的状态是完成，则不改）
	 */
	public static void projectClose(SingleObject master) {

		master.setValue(FIELD_PROCESS_STATUS, VALUE_PROCESS_CLOSE);
		master.setValue(FIELD_PROJECT_ACTUALFINISH, new Date());
		master.save();

		// *************************************************************************
		ObjectId projectId = master.getSystemId();
		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		workCollection.update(
				new BasicDBObject().append(FIELD_ROOTID, projectId).append(FIELD_PROCESS_STATUS, new BasicDBObject().append("$ne", VALUE_PROCESS_CLOSE)),
				new BasicDBObject().append("$set", new BasicDBObject().append(FIELD_PROCESS_STATUS, VALUE_PROCESS_CANCEL)), false, true);
	}

	/**
	 * 获得站点下的所有用户
	 * 
	 * @param siteId
	 * @return
	 */
	public static List<DBObject> getUserOfSite(ObjectId siteId) {

		DBCollection userCollection = DBActivator.getDefaultDBCollection(COLLECTION_USER);
		DBObject ref = new BasicDBObject().append(FIELD_SITEPARENT, siteId);
		BasicDBObject keys = new BasicDBObject().append(FIELD_DESC, 1).append(FIELD_UID, 1).append(FIELD_NAME, 1);
		DBCursor cur = userCollection.find(ref, keys);
		return cur.toArray();
	}

	/**
	 * 获得站点下面所有的角色
	 * 
	 * @param siteId
	 * @return
	 */
	public static List<DBObject> getRoleOfTeam(ObjectId teamId) {

		DBCollection obsCollection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		DBObject ref = new BasicDBObject().append(FIELD_OBSPARENT, teamId).append(FIELD_TEMPLATE_TYPE, VALUE_OBS_ROLETYPE);
		DBObject keys = new BasicDBObject().append(FIELD_DESC, 1).append(FIELD_TEMPLATE_TYPE, 1);
		DBCursor cur = obsCollection.find(ref, keys);
		return cur.toArray();
	}

	/**
	 * 获取某个OBS节点的所属站点数据
	 * 
	 * @param obsItemId
	 * @return
	 */
	public static DBObject getSiteofOBSItem(ObjectId obsItemId) {

		DBCollection obsCollection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		DBObject obsItem = obsCollection.findOne(new BasicDBObject().append(FIELD_SYSID, obsItemId));
		if (obsItem != null) {
			ObjectId parentId = (ObjectId) obsItem.get(FIELD_OBSPARENT);
			return getSiteofOBSItem(parentId);
		} else {
			DBCollection siteCollection = DBActivator.getDefaultDBCollection(COLLECTION_SITE);
			return siteCollection.findOne(new BasicDBObject().append(FIELD_SYSID, obsItemId));
		}
	}

	/**
	 * 返回权限控制下可以查看的根文件夹清单
	 * 
	 * @return
	 */
	public static BasicDBList getContextControlFolderIdList() {

		// 获得权限下的组织，进而获得文件夹
		IAuthorityResponse editResp = new AuthorityResponse();
		IAuthorityResponse viewResp = new AuthorityResponse();

		boolean hasAuthEdit = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_DOCUMENT_MANAGER, editResp);
		boolean hasAuthView = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_DEPT_MANAGER, viewResp);

		BasicDBList teamIdList = new BasicDBList();

		if (hasAuthEdit || hasAuthView) {
			// 得到可以编辑或查看所有组织内项目的这些组织
			BasicDBList contextList = editResp.getContextList();
			if (contextList != null && contextList.size() > 0)
				teamIdList.addAll(contextList);
			contextList = viewResp.getContextList();
			if (contextList != null && contextList.size() > 0)
				teamIdList.addAll(contextList);
		}

		return teamIdList;
	}

	/**
	 * 返回权限控制下的可以查看的项目清单
	 * 
	 * @return
	 */
	public static List<ISingleObject> getContextControlProjectList() {

		DBCollection cProject = DBActivator.getDefaultDBCollection(COLLECTION_PROJECT);
		DBCollection cWork = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		DBCollection cObs = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		ObjectId uoid = UserSessionContext.getSession().getUserOId();
		String uid = UserSessionContext.getSession().getUserId();

		BasicDBObject q = new BasicDBObject();

		BasicDBList condition = new BasicDBList();

		// ******************************************************************************************************************************
		// ******************************************************************************************************************************
		// 如果负责人是当前用户的项目可以显示
		condition.add(new BasicDBObject().append(FIELD_PROJECT_PM + "." + FIELD_SYSID, uoid));
		// 如果创建者是当前用户的项目可以显示
		condition.add(new BasicDBObject().append(FIELD_CREATER, uid));

		// 如果是项目某个工作的负责人可以显示
		DBCursor cur = cWork.find(new BasicDBObject().append(FIELD_PROJECT_PM + "." + FIELD_SYSID, uoid), new BasicDBObject().append(FIELD_ROOTID, 1));

		BasicDBList list1 = new BasicDBList();
		list1.addAll(cur.toArray());
		if (list1.size() > 0)
			condition.add(new BasicDBObject().append(FIELD_SYSID, new BasicDBObject().append("$in", list1)));
		// ******************************************************************************************************************************
		// ******************************************************************************************************************************

		// ******************************************************************************************************************************
		// ******************************************************************************************************************************
		BasicDBList projectTeamRootList = new BasicDBList();
		BasicDBObject condition2 = new BasicDBObject();
		BasicDBList list2 = new BasicDBList();

		// 如果得到了组织授权的也可以显示
		IAuthorityResponse editResp = new AuthorityResponse();
		IAuthorityResponse viewResp = new AuthorityResponse();
		boolean hasAuthEdit = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_PROJECT_ADMIN, editResp);
		boolean hasAuthView = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_DEPT_MANAGER, viewResp);
		if (hasAuthEdit || hasAuthView) {
			// 得到可以编辑或查看所有组织内项目的这些组织
			BasicDBList teamIdList = new BasicDBList();
			BasicDBList contextList = editResp.getContextList();
			if (contextList != null && contextList.size() > 0)
				teamIdList.addAll(contextList);
			contextList = viewResp.getContextList();
			if (contextList != null && contextList.size() > 0)
				teamIdList.addAll(contextList);

			// 查询所有组织内的项目组织根
			list2.add(new BasicDBObject().append(FIELD_TEMPLATE_TYPE, VALUE_OBS_PJTEAMTYPE).append(FIELD_OBSPARENT,
					new BasicDBObject().append("$in", teamIdList)));
		}

		// 查询项目参与者，项目下各个任务的的资源包含当前用户的项目
		// 在obs里面查找，rootid不为空, templateType 为user,useroid为当前用户的rootid
		list2.add(new BasicDBObject().append(FIELD_TEMPLATE_TYPE, VALUE_OBS_USERTYPE).append(FIELD_USEROID, uoid)
				.append(FIELD_ROOTID, new BasicDBObject().append("$exists", true)));

		condition2.put("$or", list2);

		DBObject fields = new BasicDBObject().append(FIELD_SYSID, 1).append(FIELD_ROOTID, 1);
		cur = cObs.find(condition2, fields);
		while (cur.hasNext()) {
			DBObject pjobs = cur.next();
			Object rootId = pjobs.get(FIELD_ROOTID);
			if (rootId != null) {
				projectTeamRootList.add(rootId);
				continue;
			} else {
				projectTeamRootList.add(pjobs.get(FIELD_SYSID));
			}
		}
		// ******************************************************************************************************************************
		// ******************************************************************************************************************************

		// 将条件加入到项目查询条件中
		condition.add(new BasicDBObject().append(FIELD_PROJECT_OBS_ROOT, new BasicDBObject().append("$in", projectTeamRootList)));

		q.append("$or", condition);

		cur = cProject.find(q);
		List<ISingleObject> input = new ArrayList<ISingleObject>();
		while (cur.hasNext()) {
			SingleObject so = new SingleObject(cProject, cur.next());
			input.add(so);
		}
		return input;
	}

	/**
	 * 获取某个组织下的用户，递归他的角色
	 * 
	 * @param teamOrSiteId
	 * @return
	 */
	public static List<DBObject> getUserOfTeam(ObjectId teamId) {

		DBCollection cObs = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		DBCollection cUser = DBActivator.getDefaultDBCollection(COLLECTION_USER);

		BasicDBList dbl = new BasicDBList();
		dbl.add(new BasicDBObject().append(FIELD_TEMPLATE_TYPE, VALUE_OBS_USERTYPE));
		dbl.add(new BasicDBObject().append(FIELD_TEMPLATE_TYPE, VALUE_OBS_ROLETYPE));

		DBObject ref = new BasicDBObject().append(FIELD_OBSPARENT, teamId).append("$or", dbl);

		DBObject keys = new BasicDBObject().append(FIELD_SYSID, 1).append(FIELD_OBSPARENT, 1).append(FIELD_TEMPLATE_TYPE, 1).append(FIELD_USEROID, 1);

		DBCursor cur = cObs.find(ref, keys);

		BasicDBList dbluser = new BasicDBList();
		BasicDBList dblrole = new BasicDBList();
		while (cur.hasNext()) {
			DBObject dbo = cur.next();
			Object uoid = dbo.get(FIELD_USEROID);
			if (uoid != null) {
				dbluser.add(uoid);
			} else {
				dblrole.add(dbo.get(FIELD_SYSID));
			}
		}

		cur = cObs.find(new BasicDBObject().append(FIELD_OBSPARENT, new BasicDBObject().append("$in", dblrole)), new BasicDBObject().append(FIELD_USEROID, 1));
		while (cur.hasNext()) {
			DBObject dbo = cur.next();
			Object uoid = dbo.get(FIELD_USEROID);
			if (uoid != null) {
				dbluser.add(uoid);
			}
		}

		cur = cUser.find(new BasicDBObject().append(FIELD_SYSID, new BasicDBObject().append("$in", dbluser)));
		return cur.toArray();
	}

	public static int getProjectContextAuthority(DBObject dbObject) {

		// 首先判断是否是pm
		if (isProjectManager(dbObject)) {
			return UserSessionContext.OBJECT_EDIT;
		} else if (isProjectCreator(dbObject)) {
			// 然后判断是否是项目的创建者
			return UserSessionContext.OBJECT_EDIT;
		} else if (isProjectAdmin(dbObject)) {
			// 判断项目所属的组织是否包含在内
			return UserSessionContext.OBJECT_EDIT;
		} else {
			return UserSessionContext.OBJECT_READ;
		}

	}

	public static boolean isProjectManager(DBObject dbObject) {

		DBObject pmObject = (DBObject) dbObject.get(FIELD_PROJECT_PM);
		return UserSessionContext.getSession().getUserOId().equals(pmObject.get(FIELD_SYSID));
	}

	public static boolean isProjectAdmin(DBObject dbObject) {

		// 得到组织的项目权限
		AuthorityResponse resp = new AuthorityResponse();
		boolean hasAuthority = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_PROJECT_ADMIN, resp);
		if (hasAuthority) {
			BasicDBList teamIdList = resp.getContextList();
			Object parentTeamId = dbObject.get(FIELD_OBSPARENT);
			if (teamIdList != null && teamIdList.contains(parentTeamId)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isProjectCreator(DBObject dbObject) {

		return UserSessionContext.getSession().getUserId().equals(dbObject.get(FIELD_CREATER));
	}

	// /**
	// * 获得我发起的工作
	// * @return
	// */
	// public static List<ISingleObject> getMyLaunchedWorkList() {
	// List<ISingleObject> result = new ArrayList<ISingleObject>();
	//
	//
	//
	// DBCollection workCollection =
	// DBActivator.getDefaultDBCollection(COLLECTION_WORK);
	// BasicDBObject query = new BasicDBObject()
	// .append(FIELD_CREATER, UserSessionContext.getSession().getUserId())
	// .append(FIELD_WBSPARENT, null);//非项目中的任务
	// DBCursor cur = workCollection.find(query);
	// cur.sort(new BasicDBObject().append(FIELD_CREATE_DATE, 1));
	// while(cur.hasNext()){
	// result.add(new SingleObject(workCollection, cur.next()));
	// }
	//
	// return result;
	// }

	/**
	 * 得到当前用户 所在组织以及下级组织
	 * 
	 * @return
	 */
	public static BasicDBList getAvilebleWorkTemplate() {

		BasicDBList teamIdList = new BasicDBList();
		UserSessionContext.getSession().getUserRoleAndTeam(new BasicDBList(), teamIdList);
		BasicDBList result = new BasicDBList();

		if (teamIdList == null || teamIdList.isEmpty()) {
			return result;
		}

		for (int i = 0; i < teamIdList.size(); i++) {
			DBCollection obs = DBActivator.getDefaultDBCollection(COLLECTION_ORG);

			DBObject data = obs.findOne(new BasicDBObject().append(FIELD_SYSID, teamIdList.get(i)));

			addTeamAndSuperTeam(result, data);
		}

		return result;
	}

	private static void addTeamAndSuperTeam(BasicDBList result, DBObject data) {

		DBCollection obs = DBActivator.getDefaultDBCollection(COLLECTION_ORG);

		DBObject parent = obs.findOne(new BasicDBObject().append(FIELD_SYSID, data.get(FIELD_OBSPARENT)));

		if (parent != null) {
			addTeamAndSuperTeam(result, parent);
			data.put(DUMMY_FIELD_PARENT, parent);
		}

		// 取模板

		DBCollection workt = DBActivator.getDefaultDBCollection(COLLECTION_WORK_TEMPLATE);
		DBCursor cur = workt.find(new BasicDBObject().append(FIELD_OBSPARENT, data.get(FIELD_SYSID)));
		BasicDBList list = new BasicDBList();
		while (cur.hasNext()) {
			list.add(cur.next());
		}
		if (!list.isEmpty()) {
			data.put(DUMMY_FIELD_TEAMPLATE, list);
			result.add(data);
		}
	}

	/**
	 * 将工作流里面的#{}变量参数取出里面的变量
	 * 
	 * @param param
	 * @return
	 */
	public static String getParameterName(String param) {

		int start = param.indexOf("#{");
		int end = param.indexOf("}");
		if (start != 0 || end == -1) {
			return null;
		} else {
			return param.substring(2, end);
		}
	}

}
