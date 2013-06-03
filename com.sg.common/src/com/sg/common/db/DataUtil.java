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
			// ������ ����ʱ��
			createUserId = UserSessionContext.getSession().getUserId();
			createUserName = UserSessionContext.getSession().getUserName();
		} catch (Exception e) {
			createUserId = "internal";
			createUserName = "internal";
		}
		data.put(FIELD_CREATER, createUserId);
		// ��Ϊ�����ߵ������ֶβ���ʾ
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
	// �ж��������͵Ĺ��߷���
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
	 * ��ģ�忽������Ŀ��
	 * 
	 * @param templateId
	 * @param project
	 */
	public static void importTemplateToProject(ObjectId templateId, ObjectId projectId) {

		// ȡ����Ŀ��OBS��
		QueryExpression exp = DBActivator.getQueryExpression(EXP_QUERY_PROJECT);
		exp.setParamValue(PARAM_INPUT_ID, projectId);
		DBCursor cur = exp.run();
		Assert.isTrue(cur.hasNext());
		ObjectId obsId = (ObjectId) cur.next().get(FIELD_PROJECT_OBS_ROOT);

		//

		// ��һ��������֯OBS,������ӳ���ϵ,��ѯOBSģ��
		CascadeObject obs_exp = DBActivator.getCascadeObject(EXP_CASCADE_OBS_TEMPLATE);
		obs_exp.setParamValue(FIELD_SYSID, templateId);

		CascadeObject obs_template_root = obs_exp.getChildren().get(0);

		List<CascadeObject> obs_children = obs_template_root.getChildren();
		Map<String, DBObject> obs_srcId_tgtData = copyOBSStructure(obs_children, FIELD_OBSPARENT, obsId, obsId);

		// �ڶ�������WBS
		CascadeObject wbs_exp = DBActivator.getCascadeObject(EXP_CASCADE_WBS_TEMPLATE);
		wbs_exp.setParamValue(FIELD_SYSID, templateId);
		CascadeObject wbs_template_root = wbs_exp.getChildren().get(0);

		List<CascadeObject> wbs_children = wbs_template_root.getChildren();
		ArrayList<DBObject> worksTobeCreated = new ArrayList<DBObject>();
		ArrayList<DBObject> documentsTobeCreated = new ArrayList<DBObject>();

		copyWBSStructure(wbs_children, projectId, projectId, worksTobeCreated, documentsTobeCreated, obs_srcId_tgtData);

		// ���project��������֯
		removeOBSofProject(obsId);
		// ���project�����������ĵ�
		removeWBSofProject(projectId);
		// ����OBS
		//

		DBCollection obsCol = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
		obsCol.insert((DBObject[]) obs_srcId_tgtData.values().toArray(new DBObject[] {}), WriteConcern.NORMAL);
		// ����WBS
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

				// ����ϵͳ�ֶ�
				setSystemCreateInfo(dbo);
				// �����µ�id
				ObjectId tgtId = new ObjectId();
				dbo.put(FIELD_SYSID, tgtId);
				// ����wbsparent�ֶ�
				dbo.put(FIELD_WBSPARENT, parentId);
				// ����rootid�ֶ�
				dbo.put(FIELD_ROOTID, rootId);
				// ���ò������ֶ�
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
				// �ж��ǹ������ǽ�����
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
	 * children�Ķ�����һ���¶��󣬲���wbsparent����Ϊtarget��id
	 * 
	 * @param children
	 *            Ҫ�����Ƶļ�¼
	 * @param parentId
	 *            ���Ƶ��ļ���
	 * @param targetcollection
	 *            ���Ƶ��ļ���
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
		// ����ɾ��ʱ����Ҫ��������ָ�ɸ����û��ϵĻ�������
		/*
		 * 2012/5/13 �����߼��ı䣬��Ŀ��ʹ��ģ�幦�ܵ�ʱ����׼��״̬����ʱ�����û�����Message ���Ա�����ȫע��
		 * 
		 * DBCursor cur = workCollection.find(new
		 * BasicDBObject().append(FIELD_ROOTID, projectId));
		 * while(cur.hasNext()){ DBObject workObject = cur.next(); ObjectId
		 * workId = (ObjectId)workObject;
		 * 
		 * 
		 * //ȡ������ĸ����� DBObject chargerData = (DBObject)
		 * workObject.get(FIELD_WORK_PM); if(chargerData!=null){ ObjectId
		 * originalChargerId = (ObjectId) chargerData.get(FIELD_SYSID); //ͬ��user
		 * saveUserRelationInformation
		 * (originalChargerId,null,COLLECTION_USER_WORK_IN_CHARGED,workId); }
		 * 
		 * //ȡ���������Դ BasicDBList resourcelist =
		 * (BasicDBList)workObject.get(FIELD_WORK_RESOURCE);
		 * if(resourcelist!=null){ Iterator<Object> iter =
		 * resourcelist.iterator(); while(iter.hasNext()){ DBObject userData =
		 * (DBObject) iter.next(); //ͬ��user
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
	 * ��������Ŀ��
	 * 
	 * @param inputData
	 * @return
	 */
	public static ObjectId createRootProjectTeam(ISingleObject inputData) {

		ObjectId parentId = (ObjectId) inputData.getValue(FIELD_OBSPARENT);
		ObjectId teamId = (ObjectId) inputData.getValue(FIELD_PROJECT_OBS_ROOT);
		Assert.isNotNull(parentId);
		// ��Ҫ���ǵ�ѡ������Ŀ�������ֶε�ʱ����Ҫ��ӵĽ�ɫ

		// ��parent���´�����Ŀ��
		ObjectId pjTeamId = (ObjectId) createOBSItem(null, parentId, teamId, inputData.getText(FIELD_DESC), VALUE_OBS_PJTEAMTYPE).get(FIELD_SYSID);
		return pjTeamId;
	}

	public static ObjectId createDefaultProjectTeam(ISingleObject inputData) {

		ObjectId parentId = (ObjectId) inputData.getValue(FIELD_OBSPARENT);
		ObjectId teamId = (ObjectId) inputData.getValue(FIELD_PROJECT_OBS_ROOT);
		Assert.isNotNull(parentId);
		// ��Ҫ���ǵ�ѡ������Ŀ�������ֶε�ʱ����Ҫ��ӵĽ�ɫ

		// ��parent���´�����Ŀ��
		ObjectId pjTeamId = (ObjectId) createOBSItem(null, parentId, teamId, inputData.getText(FIELD_DESC), VALUE_OBS_PJTEAMTYPE).get(FIELD_SYSID);

		// Ϊ��Ŀ�����Ĭ�ϵĽ�ɫ
		// ���Ĭ����Ŀ�����ɫ
		ObjectId pjManagerId = (ObjectId) createOBSItem(teamId, pjTeamId, null, VALUE_ROLE_PROJECTMANAGER, VALUE_OBS_ROLETYPE).get(FIELD_SYSID);
		createOBSItem(teamId, pjManagerId, null, UserSessionContext.getSession().getUserOId(), VALUE_OBS_USERTYPE);

		// ���Ĭ�ϵ���Ŀ����Ա��ɫ
		ObjectId pjAdminId = (ObjectId) createOBSItem(teamId, pjTeamId, null, VALUE_ROLE_PROJECTADMIN, VALUE_OBS_ROLETYPE).get(FIELD_SYSID);
		// ����ǰ�û���ӵ���Ŀ����Ա
		createOBSItem(teamId, pjAdminId, null, UserSessionContext.getSession().getUserOId(), VALUE_OBS_USERTYPE);

		// ���Ĭ�ϵ���Ŀ�۲��߽�ɫ
		createOBSItem(teamId, pjTeamId, null, VALUE_ROLE_PROJECTOBSERVER, VALUE_OBS_ROLETYPE);

		return pjTeamId;
	}

	/**
	 * 
	 * Create OBS�ڵ�
	 * ��������Ѿ�����֯�����ͬ������ȡ��
	 * @param rootId
	 *            ��Ŀid��Ŀ��ͨ����Ϊ����֯���� ,�����Ϊ�գ�����obs��rootid�ֶδ��ݸ���֯��id,��������֯����ʱ�����������
	 * @param parentId
	 *            �ϼ�id
	 * @param itemId
	 *            ����id,���Դ�null������ʱ����id
	 * @param desc
	 *            ,�������û�ʱ�������û���oid,�������������
	 * @param obstype
	 *            ������obs������ VALUE_OBS_ROLETYPE, VALUE_OBS_USERTYPE
	 *            ,VALUE_OBS_PJTEAMTYPE ,VALUE_OBS_TEAMTYPE
	 * @return ��obs�ڵ��oid
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
	 * ����ĳ��վ��Id�����е��û�
	 * 
	 * @param siteContextId
	 * @return
	 */
	public static List<ISingleObject> getSiteUsers(ObjectId siteId, boolean cascade) {

		List<ISingleObject> userList = new ArrayList<ISingleObject>();
		// ��ѯ��ǰվ��
		QueryExpression siteExp = DBActivator.getQueryExpression(EXP_QUERY_SITE);
		siteExp.setParamValue(PARAM_INPUT_ID, siteId);
		DBCursor curorCurrentSite = siteExp.run();
		if (!curorCurrentSite.hasNext()) {
			return userList;
		}

		// ��ǰվ��
		DBObject currentSiteData = curorCurrentSite.next();
		Object currentOID = currentSiteData.get(FIELD_SYSID);
		// ����ϼ�վ�� id
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

		// ��ѯ��ǰվ���¼����û�
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

		// ��ѯ�¼�վ��
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

		// ����obsparent��ֵ
		ObjectId parentId = (ObjectId) currentOrg.getValue(FIELD_SYSID);
		so.setValue(FIELD_OBSPARENT, parentId, null, false);

		// ������֯�ļ���
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

			// ������֯�ļ���
			if (VALUE_OBS_TEAMTYPE.equals(obsType)) {
				createOrgFolder(data.getData());
			}

			// reload
			currentOrg.createChild(EXP_CASCADE_SO_OBS, data.getData(), collection);
		}
		return ok;
	}

	/**
	 * ����������Զ�ָ��
	 * 
	 * @param masterProject
	 */
	@Deprecated
	public static void autoAssignment(ObjectId projectId) {

		// ȡ���¼�
		CascadeObject cascadeWBS = DBActivator.getCascadeObject(EXP_CASCADE_WBS_WITH_DOC);
		cascadeWBS.setParamValue(FIELD_SYSID, projectId);
		List<CascadeObject> children = cascadeWBS.getChildren();// ��ü�����ѯ��
		if (children.size() < 1) {
			return;
		}
		CascadeObject project = children.get(0);// �����Ŀ
		autoAssignmentChildren(project.getChildren());// ����Ŀ��ÿ���ӽڵ����ָ��
	}

	private static void autoAssignmentChildren(List<CascadeObject> list) {

		for (CascadeObject co : list) {
			assignmentWBSItemByDefault(co);// ָ��ĳһ��wbs�ڵ�
		}
	}

	private static void assignmentWBSItemByDefault(CascadeObject work) {

		BasicDBList participates = (BasicDBList) work.getValue(FIELD_PARTICIPATE);// ��ȡ�ýڵ�Ĳ����߶���
		if (participates != null && participates.size() > 0) {

			BasicDBList parentList = new BasicDBList();// ��ʼ�������߶�����б����ڴ�Ų����߶����id(�����߶����ǣ���ɫ������֯����ɫ����֯����Ŀ��OBS����ʱ�Ѿ�����)
			for (int i = 0; i < participates.size(); i++) {
				DBObject p = (DBObject) participates.get(i);
				ObjectId pid = (ObjectId) p.get(FIELD_SYSID);
				parentList.add(pid);
			}

			// ��obs�ϲ�ѯ��Ӧ��user�ڵ�
			DBCollection collection = DBActivator.getDefaultDBCollection(COLLECTION_ORG);
			DBObject query = new BasicDBObject();
			query.put(FIELD_OBSPARENT, new BasicDBObject().append("$in", parentList));
			query.put(FIELD_TEMPLATE_TYPE, VALUE_OBS_USERTYPE);
			DBCursor cursor = collection.find(query);

			// ��obs�ϻ��useroid
			BasicDBList userIdList = new BasicDBList();

			while (cursor.hasNext()) {
				DBObject obsItem = cursor.next();
				userIdList.add(obsItem.get(FIELD_USEROID));
			}

			// ��user�л��user����ϸ��Ϣ
			DBCollection userCollection = DBActivator.getDefaultDBCollection(COLLECTION_USER);
			DBObject userQuery = new BasicDBObject();
			userQuery.put(FIELD_SYSID, new BasicDBObject().append("$in", userIdList));
			DBCursor userCursor = userCollection.find(userQuery);

			BasicDBList resource = new BasicDBList();// ��ʼ����Դ��׼����������뵽wbs�ڵ����Դ�ֶ�
			while (userCursor.hasNext()) {
				DBObject data = userCursor.next();
				resource.add(getRefData(data, DATA_USER_BASIC));
			}

			if (resource.size() > 0) {
				// ͬ��user��Ϣ
				if (isWorkObject(work)) {
					DBObject chargerData = (DBObject) resource.get(0);
					work.setValue(FIELD_WORK_PM, chargerData);
					resource.remove(0);
					work.setValue(FIELD_WORK_RESOURCE, resource);
					work.save();
					/*
					 * �Զ�ָ������ʱ����Ŀ���봦��׼��״̬����ʱ�������û�����Message, ������ȫע�� ObjectId id =
					 * work.getSystemId(); //ͬ��user��������
					 * saveUserRelationInformation(null, (ObjectId)
					 * chargerData.get(FIELD_SYSID),
					 * COLLECTION_USER_WORK_IN_CHARGED, id); //ͬ��user���������
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
			return "��ȡ��";
		}
		if (VALUE_PROCESS_CLOSE.equals(statusCode)) {
			return "�����";
		}
		if (VALUE_PROCESS_PAUSE.equals(statusCode)) {
			return "��ͣ��";
		}
		if (VALUE_PROCESS_PROCESS.equals(statusCode)) {
			return "������";
		}
		return "׼����";
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
				// �û�������Ϣ��Ҫ����ͬ��
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
			// �û�վ����Ϣ��Ҫͬ��
			ObjectId siteId = (ObjectId) input.get(FIELD_SITEPARENT);
			if (siteId != null)
				siteName = (String) getDataObject(COLLECTION_SITE, siteId).get(FIELD_DESC);
		}

		String activate;
		String activeUserImageUrl;
		String userImageUrl = getUserImageURL(input);
		boolean activtvate = Boolean.TRUE.equals(input.get(FIELD_ACTIVATE));

		if (activtvate) {
			activate = "����";
			activeUserImageUrl = getUserActiveImageURL(true);
		} else {
			activate = "δ����";
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

				builder.append("վ��: ");
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
			//���ȴ�ע���ļ�����
			String key = "cpm_" + namespace + "." + fileObjectid;
			image = imageRegistry.get(key);
			//û��ע�������£������ݿ��ȡ
			if (image == null) {
				image = FileUtil.getImageFileFromGridFS(namespace, fileObjectid);
			}
			
			//��image���з�����64X64��С
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
	 * ������Ŀ�ĸ��ļ���
	 * 
	 * @param projectData
	 */
	public static void createProjectFolder(DBObject projectData) {

		// �Ƿ���Ҫ��wbs������ĿĿ¼�ṹ
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

		// �����Ҫ����wbs����fbs
		if (createFBSasWBS) {
			// work�Ϲҵ�wbsprentid,�µ�Ŀ¼����
			Map<String, DBObject> workId_FolderMap = new HashMap<String, DBObject>();
			workId_FolderMap.put(fbsParentId.toString(), folderData);// �����Ŀ��

			// ��ѯrootid�µ����й���
			DBCursor workCur = workCollection.find(new BasicDBObject().append(FIELD_ROOTID, fbsParentId),
					new BasicDBObject().append(FIELD_SYSID, 1).append(FIELD_DESC, 1).append(FIELD_WBSPARENT, 1));
			// Ϊ�������·���id
			while (workCur.hasNext()) {
				DBObject work = workCur.next();
				DBObject folder = getFolder(new ObjectId(), work.get(FIELD_WBSPARENT), work.get(FIELD_DESC));
				ObjectId workId = (ObjectId) work.get(FIELD_SYSID);
				workId_FolderMap.put(workId.toString(), folder);
			}

			// ����wbsparent��fbsparent
			Collection<DBObject> folders = workId_FolderMap.values();
			Iterator<DBObject> iter = folders.iterator();
			while (iter.hasNext()) {
				DBObject folder = iter.next();
				// �ų���Ŀ¼
				if (givenId.equals(folder.get(FIELD_SYSID)))
					continue;
				ObjectId wbsParentId = (ObjectId) folder.get(FIELD_FBSPARENT);
				DBObject parentFolder = workId_FolderMap.get(wbsParentId.toString());
				if (parentFolder == null)
					continue;// ��Ӧ��Ϊ��
				folder.put(FIELD_FBSPARENT, parentFolder.get(FIELD_SYSID));
			}

			folderCollection.insert((DBObject[]) folders.toArray(new DBObject[] {}), WriteConcern.NORMAL);

			// �����ĵ�
			DBCursor docCur = documentCollection.find(new BasicDBObject().append(FIELD_ROOTID, fbsParentId));
			while (docCur.hasNext()) {
				DBObject doc = docCur.next();
				ObjectId wbsParentId = (ObjectId) doc.get(FIELD_WBSPARENT);
				DBObject parentFolder = workId_FolderMap.get(wbsParentId.toString());
				if (parentFolder == null)
					continue;// ��Ӧ��Ϊ��
				doc.put(FIELD_FBSPARENT, parentFolder.get(FIELD_SYSID));
				documentCollection.save(doc);
			}

		} else {
			// ֱ�Ӱ��ĵ�������Ŀ�ĸ�Ŀ¼
			documentCollection.update(new BasicDBObject().append(FIELD_ROOTID, fbsParentId),
					new BasicDBObject().append("$set", new BasicDBObject().append(FIELD_FBSPARENT, givenId)), false, true);
			// ������Ŀ��Ŀ¼����
			folderCollection.insert(folderData);
		}

		return folderData;

	}

	/**
	 * ��õ�ǰ�����ĵ���Ŀ�б�id
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
	 * ��õ�ǰ�����ĵ���֯�б�, id
	 * 
	 * @return
	 */
	@Deprecated
	public static BasicDBList getContextControlOrgIdList() {

		ObjectId uid = UserSessionContext.getSession().getUserOId();
		// ��OBS�ϲ�ѯ�û��ļ�¼
		QueryExpression obs = DBActivator.getQueryExpression(EXP_QUERY_OBS);
		obs.setParamValue(PARAM_INPUT_USEROID, uid);
		DBCursor cur = obs.run();
		BasicDBList result = new BasicDBList();
		while (cur.hasNext()) {
			DBObject userInObs = cur.next();
			DBObject team = getParentTeamOfUser(userInObs);
			if (team != null) {
				result.add(team.get(FIELD_SYSID));
				// ���������������汾�д���һЩ��֯û�и�Ŀ¼��������������������������⣬Ϊû�и�Ŀ¼����֯��Ӹ�Ŀ¼
				if (team.get(FIELD_FOLDER_ROOT) == null) {
					team.put(FIELD_FOLDER_ROOT, new ObjectId());
					DBActivator.getDefaultDBCollection(COLLECTION_ORG).save(team);
					createOrgFolder(team);
				}

			}
		}
		return result;
	}

	// ���obs �û��ϼ���һ��team
	private static DBObject getParentTeamOfUser(DBObject obsItem) {

		Object parentId = obsItem.get(FIELD_OBSPARENT);
		if (parentId != null) {
			QueryExpression obs = DBActivator.getQueryExpression(EXP_QUERY_OBS);
			obs.setParamValue(PARAM_INPUT_ID, parentId);
			DBCursor cur = obs.run();
			if (cur.hasNext()) {
				DBObject parent = cur.next();
				if (VALUE_OBS_TEAMTYPE.equals(parent.get(FIELD_TEMPLATE_TYPE))) {// �������֯�����Է���
					return parent;
				} else if (VALUE_OBS_ROLETYPE.equals(parent.get(FIELD_TEMPLATE_TYPE))) {// ����ǽ�ɫ��������һ��
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

		// ����fbsparent��ֵ
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

		// ����fbsparent��ֵ
		ObjectId parentId = (ObjectId) fbsParent.getValue(FIELD_SYSID);

		so.setValue(FIELD_FBSPARENT, parentId);

		// ���rootid
		so.setValue(FIELD_ROOTID, rootId);

		SingleObjectEditorInput editInput = new SingleObjectEditorInput(editorConfiguration, so);
		ISingleObjectEditorDialogCallback call = new SingleObjectEditorDialogCallback() {

			@Override
			public boolean saveBefore(ISingleObjectEditorInput input) {

				// �����ļ���
				Set<EditorConfiguration> editorSet = Widget.listSingleObjectEditorConfigurationByCollection(COLLECTION_DOCUMENT);
				String id = (String) input.getInputData().getValue(FIELD_SYSTEM_EDITOR);
				Iterator<EditorConfiguration> iter = editorSet.iterator();
				while (iter.hasNext()) {
					EditorConfiguration ec = iter.next();
					if (ec.getId().equals(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE)) {// �ų������ĵ�
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
	 * ���������������Ӧ�õĲο�����
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

	// ������ͬ���������û��ĸ������Ŀ�嵥�У����ౣ�����������
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

		// ������ϵ��
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
			// //��ѯ���� _id = value�ļ�¼
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
			// //����ԭ���û���message�嵥��������ȥ
			// userCollection.update(
			// new BasicDBObject().append(FIELD_SYSID, newUserId),
			// new BasicDBObject().append("$pullAll",
			// new BasicDBObject().append(typeName, shouldBeDeleteList)));
			// }

		}

		if (newUserId != null) {
			// ��ӵ��µ��û���
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
	 * ��Ŀ�����������״̬��Ϊ׼����ͬ���û�������Ϣ���ݣ� ��Ŀ��ͣ�������״̬��Ϊ��ͣ ��Ŀ��ֹ�������״̬��Ϊ��ֹ
	 * ��Ŀ��ɣ������״̬��Ϊ��ֹ���������ǰ��״̬����ɣ��򲻸ģ�
	 */
	@Deprecated
	public static void projectStart(SingleObject master) {

		master.setValue(FIELD_PROCESS_STATUS, VALUE_PROCESS_PROCESS);
		master.setValue(FIELD_PROJECT_ACTUALSTART, new Date());

		master.save();

		// *************************************************************************
		// ��Ŀ�����������״̬��Ϊ׼����ͬ���û�������Ϣ���ݣ�
		ObjectId projectId = master.getSystemId();
		DBCollection workCollection = DBActivator.getDefaultDBCollection(COLLECTION_WORK);
		// workCollection.update(
		// new BasicDBObject()
		// .append(FIELD_ROOTID, projectId),
		// new BasicDBObject()
		// .append(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY)
		// , false, true);

		// ����������Ϣ
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
			
			// ���������״̬
			work.put(FIELD_PROCESS_STATUS, VALUE_PROCESS_READY);
			workCollection.save(work);

			ObjectId id = (ObjectId) work.get(FIELD_SYSID);
			chargerData = (DBObject) work.get(FIELD_WORK_PM);
			resource = (BasicDBList) work.get(FIELD_WORK_RESOURCE);

			// �������������ȱ���ͣ��ʹ�ÿ�ʼ�ָ��ģ���ô���Ѿ���������֪ͨ
			// ��Ҫ��ɾ����������
			cleanWorkInformation(id);

			// ͬ��user��������
			if (chargerData != null) {
				DataUtil.saveUserRelationInformation(null, (ObjectId) chargerData.get(FIELD_SYSID), COLLECTION_USER_WORK_IN_CHARGED, id);
			}

			// ͬ��user���������
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
	 * ��Ŀ�����������״̬��Ϊ׼����ͬ���û�������Ϣ���ݣ� ��Ŀ��ͣ�������״̬��Ϊ��ͣ ��Ŀ��ֹ�������״̬��Ϊ��ֹ
	 * ��Ŀ��ɣ������״̬��Ϊ��ֹ���������ǰ��״̬����ɣ��򲻸ģ�
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
	 * ��Ŀ�����������״̬��Ϊ׼����ͬ���û�������Ϣ���ݣ� ��Ŀ��ͣ�������״̬��Ϊ��ͣ ��Ŀ��ֹ�������״̬��Ϊ��ֹ
	 * ��Ŀ��ɣ������״̬��Ϊ��ֹ���������ǰ��״̬����ɣ��򲻸ģ�
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
	 * ��Ŀ�����������״̬��Ϊ׼����ͬ���û�������Ϣ���ݣ� ��Ŀ��ͣ�������״̬��Ϊ��ͣ ��Ŀ��ֹ�������״̬��Ϊ��ֹ
	 * ��Ŀ��ɣ������״̬��Ϊ��ֹ���������ǰ��״̬����ɣ��򲻸ģ�
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
	 * ���վ���µ������û�
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
	 * ���վ���������еĽ�ɫ
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
	 * ��ȡĳ��OBS�ڵ������վ������
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
	 * ����Ȩ�޿����¿��Բ鿴�ĸ��ļ����嵥
	 * 
	 * @return
	 */
	public static BasicDBList getContextControlFolderIdList() {

		// ���Ȩ���µ���֯����������ļ���
		IAuthorityResponse editResp = new AuthorityResponse();
		IAuthorityResponse viewResp = new AuthorityResponse();

		boolean hasAuthEdit = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_DOCUMENT_MANAGER, editResp);
		boolean hasAuthView = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_DEPT_MANAGER, viewResp);

		BasicDBList teamIdList = new BasicDBList();

		if (hasAuthEdit || hasAuthView) {
			// �õ����Ա༭��鿴������֯����Ŀ����Щ��֯
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
	 * ����Ȩ�޿����µĿ��Բ鿴����Ŀ�嵥
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
		// ����������ǵ�ǰ�û�����Ŀ������ʾ
		condition.add(new BasicDBObject().append(FIELD_PROJECT_PM + "." + FIELD_SYSID, uoid));
		// ����������ǵ�ǰ�û�����Ŀ������ʾ
		condition.add(new BasicDBObject().append(FIELD_CREATER, uid));

		// �������Ŀĳ�������ĸ����˿�����ʾ
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

		// ����õ�����֯��Ȩ��Ҳ������ʾ
		IAuthorityResponse editResp = new AuthorityResponse();
		IAuthorityResponse viewResp = new AuthorityResponse();
		boolean hasAuthEdit = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_PROJECT_ADMIN, editResp);
		boolean hasAuthView = UserSessionContext.hasTokenAuthority(UserSessionContext.TOKEN_ORG_DEPT_MANAGER, viewResp);
		if (hasAuthEdit || hasAuthView) {
			// �õ����Ա༭��鿴������֯����Ŀ����Щ��֯
			BasicDBList teamIdList = new BasicDBList();
			BasicDBList contextList = editResp.getContextList();
			if (contextList != null && contextList.size() > 0)
				teamIdList.addAll(contextList);
			contextList = viewResp.getContextList();
			if (contextList != null && contextList.size() > 0)
				teamIdList.addAll(contextList);

			// ��ѯ������֯�ڵ���Ŀ��֯��
			list2.add(new BasicDBObject().append(FIELD_TEMPLATE_TYPE, VALUE_OBS_PJTEAMTYPE).append(FIELD_OBSPARENT,
					new BasicDBObject().append("$in", teamIdList)));
		}

		// ��ѯ��Ŀ�����ߣ���Ŀ�¸�������ĵ���Դ������ǰ�û�����Ŀ
		// ��obs������ң�rootid��Ϊ��, templateType Ϊuser,useroidΪ��ǰ�û���rootid
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

		// ���������뵽��Ŀ��ѯ������
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
	 * ��ȡĳ����֯�µ��û����ݹ����Ľ�ɫ
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

		// �����ж��Ƿ���pm
		if (isProjectManager(dbObject)) {
			return UserSessionContext.OBJECT_EDIT;
		} else if (isProjectCreator(dbObject)) {
			// Ȼ���ж��Ƿ�����Ŀ�Ĵ�����
			return UserSessionContext.OBJECT_EDIT;
		} else if (isProjectAdmin(dbObject)) {
			// �ж���Ŀ��������֯�Ƿ��������
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

		// �õ���֯����ĿȨ��
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
	// * ����ҷ���Ĺ���
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
	// .append(FIELD_WBSPARENT, null);//����Ŀ�е�����
	// DBCursor cur = workCollection.find(query);
	// cur.sort(new BasicDBObject().append(FIELD_CREATE_DATE, 1));
	// while(cur.hasNext()){
	// result.add(new SingleObject(workCollection, cur.next()));
	// }
	//
	// return result;
	// }

	/**
	 * �õ���ǰ�û� ������֯�Լ��¼���֯
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

		// ȡģ��

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
	 * �������������#{}��������ȡ������ı���
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
