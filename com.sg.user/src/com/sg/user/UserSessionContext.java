package com.sg.user;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.rap.rwt.SingletonUtil;

import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.expression.query.QueryExpression;
import com.sg.user.db.UserDB;

public class UserSessionContext  {

	public static final String UPDATE_METHOD = "update";

	public static final String READ_METHOD = "read";

	public static final String CREATE_METHOD = "create";

	public static final String REMOVE_METHOD = "remove";

	public static final String PROJECT_MANAGER = "project.manager";

	public static final String FOLDER_PROJECT_MANAGEMENT = "folder.project.management";

	public static final String FOLDER_ORG_MANAGEMENT = "folder.org.management";

	public static final String FOLDER_ORG_CREATE_DOC = "folder.org.createdocument";

	public static final String FOLDER_PROJECT_CREATE_DOC = "folder.project.createdocument";

	public static final String FOLDER_PROJECT_EDIT_DOC = "folder.project.editdocument";
	
	public static final String TOKEN_ORG_PROJECT_MANAGER = "org.projectmanager";
	
	public static final String TOKEN_ORG_DEPT_MANAGER = "org.deptmanager";

	public static final String TOKEN_ORG_PROJECT_ADMIN = "org.projectadmin";
	
	public static final String TOKEN_ORG_DOCUMENT_MANAGER = "org.documentmanager";
	
	public static final String TOKEN_ORG_BUSINESS_MANAGER = "org.businessmanager";

	public static final int OBJECT_EDIT = 1<<1;
	
	public static final int OBJECT_REMOVE = 1<<2;
	
	public static final int OBJECT_READ = 1<<3;
	
	public static final int OBJECT_NONE = 1<<4;

	private String userId;

	private String userName;

	public String sessionConextId;

	private Date loginDate;

	private boolean login;

	private ListenerList listeners = new ListenerList();

	private ObjectId siteId;

	private String siteName;

	private ObjectId userOid;

	private String userFullName;

	public UserSessionContext() {
		super();
		sessionConextId = new ObjectId().toString();
	}

	public static UserSessionContext getSession() {
		return (UserSessionContext) SingletonUtil.getSessionInstance(UserSessionContext.class);
	}

	public String login(String loginId, String password) {
		//����û�����Ϣ
		QueryExpression userExp = DBActivator.getQueryExpression("com.sg.user.query");

		//�ж��û���¼�õ�id��ʲô
		//�ж��Ƿ�ʹ�������½
		int ix = loginId.indexOf("@");
		if(ix>0&&loginId.substring(ix).contains(".")){//����@����
			userExp.setParamValue("email", Pattern.compile(loginId,Pattern.CASE_INSENSITIVE )).setParamValue("password", password);
		}else{
			try{
				Integer.parseInt(loginId);
				//ʹ��uid��¼
				userExp.setParamValue("uid", loginId).setParamValue("password", password);
				
			}catch(Exception e){
				//ʹ���û�����¼Pattern.compile(loginId,Pattern.CASE_INSENSITIVE )
				userExp.setParamValue("desc",loginId.toLowerCase() ).setParamValue("password", password);
			}
		}
		
		DBCursor userCursor = userExp.run();
		if(userCursor.hasNext()){
			DBObject user = userCursor.next();
			//����û��Ƿ�ʧЧ
			boolean isActivate = Boolean.TRUE.equals(user.get("activate"));
			if(isActivate){
				userId = (String) user.get("uid");
				userOid = (ObjectId)user.get("_id");
				userName = (String) user.get("desc");
				userFullName = (String)user.get("name");
				siteId = (ObjectId) user.get("siteparent");
				if(siteId!=null){
					//��ѯվ����Ϣ
					QueryExpression siteExp = DBActivator.getQueryExpression("com.sg.site.query");
					siteExp.setParamValue("_id", siteId);
					DBCursor siteCursor = siteExp.run();
					if(siteCursor.hasNext()){
						DBObject site = siteCursor.next();
						siteName = (String) site.get("desc");
					}
				}else{
					siteName = DBActivator.getDatabase().getName();
				}
			}else{
				//��ǰ�û�û�м���
				
				return "��������û�ID��û�м������ϵ����Ա���������ʻ���";
			}
		}else{
			
			
			return "��������û�ID�������벻��ͨ����֤������������";
		}
		

		// ����û������Ϣ

		this.loginDate = new Date();
		login = true;
		return null;
	}

	public void logout() {
		this.login = false;
		this.userName = null;
		this.userId = null;
		this.sessionConextId = null;
	}

	public boolean isLogin() {
		return login;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return this.userName;
	}

	
	public String getUserFullName(){
		return this.userFullName;
	}
	public String getSessionId() {
		return sessionConextId;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void addUserSessionListner(IUserSessionEventListener listener) {
		listeners.add(listener);
	}

	public void removeUserSessionListner(IUserSessionEventListener listener) {
		listeners.remove(listener);
	}

	public void fireUserSessionEvent(String method, String arg, ISessionAuthorityControl sc) {
		Object[] lis = listeners.getListeners();
		for (int i = 0; i < lis.length; i++) {
			((IUserSessionEventListener) lis[i]).fireUserSessionEvent(method, arg, sc);
		}
	}

	/**
	 * ��ȡ��ǰ��վ��������
	 * @return
	 */
	public ObjectId getSiteContextId() {
		return siteId;
	}

	public String getSiteContextName() {
		return siteName;
	}
	
	public ObjectId getUserOId(){
		return userOid;
	}
	
	public void getUserRoleAndTeam(BasicDBList roleIdList,BasicDBList teamIdList){
		UserDB.getParentRoleAndTeam(userOid, roleIdList, teamIdList);
	}
	
	public BasicDBList getUserTeam() {
		BasicDBList result = new BasicDBList();
		BasicDBList role = new BasicDBList();
		getUserRoleAndTeam(role,result);
		return result;
	}


	public static boolean hasTokenAuthority(String tokenId,IAuthorityResponse resp) {
		UserSessionContext s = getSession();
		String name = s.getUserName();
		if(name.equals(UserService.SUPERUSER_DESC)){
			return true;
		}
		
		ObjectId userId = s.getUserOId();

		if(tokenId.startsWith("site.")){//վ��������Ȩ��
			ObjectId currentSiteId = s.getSiteContextId();
			//վ�������ĵ�Ȩ��ֻ�����õ�վ����û�
			DBObject one = UserDB.getAuthorityData(currentSiteId,userId,tokenId,UserDB.VALUE_USER,true);
			
			if(resp!=null){
				resp.setPermission(one!=null);
			}
			return one!=null;
		}
			
		if(tokenId.startsWith("org.")){//��֯�����ĵ�Ȩ��

			//����û������еĽ�ɫ
			List<ObjectId> roleIdList = UserDB.getParentRoleIdList(userId);
			
			List<DBObject> contextAuthorityListByRole = null;
			List<DBObject> contextAuthorityListByUser = null;
			
			if((roleIdList != null)&&(!roleIdList.isEmpty())){
				
				BasicDBList targetList = new BasicDBList();
				targetList.addAll(roleIdList);
				//����û���ɫ����Щ��֯�������о���Ȩ��
//				contextAuthorityListByRole = UserDB.getTeamContextAuthorityData(contextList, targetList, tokenId, UserDB.VALUE_ROLE, true);
				contextAuthorityListByRole = UserDB.getAuthorityData(targetList, tokenId, UserDB.VALUE_ROLE, true);
				
				if((resp==null)&&(!isNullOrEmptyList(contextAuthorityListByRole))){
					return true;
				}
			}
			
			BasicDBList targetList = new BasicDBList();
			targetList.add(userId);
			//����û�����Щ��֯����������Ȩ��
			contextAuthorityListByUser = UserDB.getAuthorityData( targetList, tokenId, UserDB.VALUE_USER, true);

			if((resp==null)&&(!isNullOrEmptyList(contextAuthorityListByUser))){
				return true;
			}

			
			if(resp!=null){
				//�ϲ�����д��resp
				if(isNullOrEmptyList(contextAuthorityListByRole)&&isNullOrEmptyList(contextAuthorityListByUser)){
					resp.setPermission(false, IAuthorityResponse.MESSAGE_USER_AND_HIS_ROLE_HAVENOT_PERMISSION);
					return false;
				}else{
					BasicDBList result = new BasicDBList();
					if(!isNullOrEmptyList(contextAuthorityListByRole)){
						for(DBObject item:contextAuthorityListByRole){
							ObjectId contextId = (ObjectId) item.get(UserDB.FIELD_CONTEXTID);
							if(!result.contains(contextId)){
								result.add(contextId);
							}
						}
					}
					
					if(!isNullOrEmptyList(contextAuthorityListByUser)){
						for(DBObject item:contextAuthorityListByUser){
							ObjectId contextId = (ObjectId) item.get(UserDB.FIELD_CONTEXTID);
							if(!result.contains(contextId)){
								result.add(contextId);
							}
						}
					}
					
					resp.setPermissionContextList(result);
					return true;
				}
			}else{
				if(isNullOrEmptyList(contextAuthorityListByRole)&&isNullOrEmptyList(contextAuthorityListByUser)){
					return false;
				}else{
					return true;
				}
			}
			
			
			
//			//******************************************************************
//			//ע�ͱ��� ����Ȩ�޿������ ��Ҫ����û�һ����ĳ����֯��
//			//******************************************************************
//
//			//����û������еĽ�ɫ
//			BasicDBList targetList = new BasicDBList();
//
//			//����û���������֯������
//			BasicDBList contextList = new BasicDBList();
//			
//			UserDB.getParentRoleAndTeam(userId, targetList, contextList);
//			
//			if(contextList.isEmpty()){
//				//�û��������κ���֯
//				if(resp!=null){
//					resp.setPermission(false,IAuthorityResponse.MESSAGE_USER_NOT_BELONG_ANY_ORG);
//				}
//				return false;
//			}
//
//			List<DBObject> contextAuthorityListByRole = null;
//			List<DBObject> contextAuthorityListByUser = null;
//			
//			if((targetList != null)&&(!targetList.isEmpty())){
//				
//				//����û���ɫ����Щ��֯�������о���Ȩ��
////				contextAuthorityListByRole = UserDB.getTeamContextAuthorityData(contextList, targetList, tokenId, UserDB.VALUE_ROLE, true);
//				contextAuthorityListByRole = UserDB.getAuthorityData(targetList, tokenId, UserDB.VALUE_ROLE, true);
//				
//				if((resp==null)&&(!isNullOrEmptyList(contextAuthorityListByRole))){
//					return true;
//				}
//			}
//			
//			targetList = new BasicDBList();
//			targetList.add(userId);
//			//����û�����Щ��֯����������Ȩ��
//			contextAuthorityListByUser = UserDB.getAuthorityData( targetList, tokenId, UserDB.VALUE_USER, true);
//
//			if((resp==null)&&(!isNullOrEmptyList(contextAuthorityListByUser))){
//				return true;
//			}
//
//			
//			if(resp!=null){
//				//�ϲ�����д��resp
//				if(isNullOrEmptyList(contextAuthorityListByRole)&&isNullOrEmptyList(contextAuthorityListByUser)){
//					resp.setPermission(false, IAuthorityResponse.MESSAGE_USER_AND_HIS_ROLE_HAVENOT_PERMISSION);
//					return false;
//				}else{
//					BasicDBList result = new BasicDBList();
//					if(!isNullOrEmptyList(contextAuthorityListByRole)){
//						for(DBObject item:contextAuthorityListByRole){
//							ObjectId contextId = (ObjectId) item.get(UserDB.FIELD_CONTEXTID);
//							if(!result.contains(contextId)){
//								result.add(contextId);
//							}
//						}
//					}
//					
//					if(!isNullOrEmptyList(contextAuthorityListByUser)){
//						for(DBObject item:contextAuthorityListByUser){
//							ObjectId contextId = (ObjectId) item.get(UserDB.FIELD_CONTEXTID);
//							if(!result.contains(contextId)){
//								result.add(contextId);
//							}
//						}
//					}
//					
//					resp.setPermissionContextList(result);
//					return true;
//				}
//			}else{
//				return false;
//			}
//			//******************************************************************
//			//ע�ͱ��� ����Ȩ�޿������ ��Ҫ����û�һ����ĳ����֯��
//			//******************************************************************

		}
		return false;
	}

	private static boolean isNullOrEmptyList(List<?> list){
		return list==null||list.isEmpty();
	}
}
