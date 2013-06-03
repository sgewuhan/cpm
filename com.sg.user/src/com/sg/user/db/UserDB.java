package com.sg.user.db;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;

public class UserDB {
	
	public static final String COLLECTION_AUTHORITY = "authority";
	
	
	public static final String FIELD_CONTEXTID="contextId";
	
	public static final String FIELD_TOKENID="tokenId";
	
	public static final String FIELD_TARGETTYPE="targetType";
	
	public static final String FIELD_TARGETID="targetId";
	
	public static final String FIELD_AUTHVALUE="authValue";

	public static final String VALUE_USER = "user";

	public static final String VALUE_ROLE = "role";

	public static final String VALUE_TEAM = "team";

	public static final String COLLECTION_OBS = "obs";
	
	public static final String FIELD_USEROID = "useroid";
	
	public static final String FIELD_OBS_TYPE = "templateType";
	
	public static final String FIELD_OBS_PARENT = "obsparent";
	
	public static final String FIELD_PROJECT_ROOTOBS = "rootid";


	public static final String FIELD__ID = "_id";


	
	



	@Deprecated
	/**
	 * �����������ȡ��ֻ��������֯�µĽ�ɫ�е��û�����˲�һ���ܹ���ȫ����û����е���
	 * @param userId
	 * @return
	 */
	public static List<ObjectId> getParentTeamIdList(ObjectId userId) {
		return getParentOBSIdList(userId,VALUE_TEAM);
	}
	
	
	public static List<ObjectId> getParentRoleIdList(ObjectId userId) {
		return getParentOBSIdList(userId,VALUE_ROLE);
	}

	public static void getParentRoleAndTeam(ObjectId userId, BasicDBList roleIdList,BasicDBList teamIdList){
		//���׼����д�������Ƿ��ʼ��
		Assert.isNotNull(roleIdList);
		Assert.isNotNull(teamIdList);
		
		//obs�ļ���
		DBCollection obsCol = DBActivator.getDefaultDBCollection(COLLECTION_OBS);
		
		
		//��ѯ���û���OBSPARENT
		DBObject ref = new BasicDBObject()
							.append(FIELD_USEROID, userId)
							.append(FIELD_PROJECT_ROOTOBS, 
									new BasicDBObject().append("$exists", false));//���������������Ŀ��������֯�е��û�������
							
		DBObject keys = new BasicDBObject()
							.append(FIELD_OBS_PARENT, 1);
		
		DBCursor cur = obsCol.find(ref, keys);//��ѯ���е���OBS�����е�USER�ڵ�
		
		
		
		//����Щ�ڵ��IDȡ������
		BasicDBList parentIdList = new BasicDBList();
		
		while(cur.hasNext()){
			Object parentId = cur.next().get(FIELD_OBS_PARENT);
			if(!parentIdList.contains(parentId)){
				parentIdList.add(parentId);
			}
		}
		
		
		
		//��ѯ����Щobs�ϵĽڵ�
		cur = obsCol.find(new BasicDBObject().append(FIELD__ID, new BasicDBObject().append("$in", parentIdList)));
		while(cur.hasNext()){
			DBObject obsItem = cur.next();
			Object id = obsItem.get(FIELD__ID);
			if(VALUE_ROLE.equals(obsItem.get(FIELD_OBS_TYPE))){
				
				//����ǽ�ɫ����Ҫ�ж������ɫ�Ƿ��Ѿ����뵽�˽�ɫ�б�
				if(roleIdList.contains(id)){
					continue;
				}else{
					
					//����ɫ���뵽��ɫ�б�
					roleIdList.add(id);
				
					//��һ����ѯ����obsprent�Ի����֯
					//��ɫ���ϼ�һ������֯
					Object parentTeamId = obsItem.get(FIELD_OBS_PARENT);
					if(!teamIdList.contains(parentTeamId)){
						teamIdList.add(parentTeamId);
					}
				}

			}else if(VALUE_TEAM.equals(obsItem.get(FIELD_OBS_TYPE))){

				if(!teamIdList.contains(id)){
					teamIdList.add(id);
				}
				
			}
		}
		
		
	}
	
	private static List<ObjectId> getParentOBSIdList(ObjectId userId,String obstype) {
		DBCollection obsCol = DBActivator.getDefaultDBCollection(COLLECTION_OBS);
		
		
		
		//��ѯ���û���OBSPARENT
		DBObject ref = new BasicDBObject()
							.append(FIELD_USEROID, userId);
							
		DBObject keys = new BasicDBObject()
							.append(FIELD_OBS_PARENT, 1)
							.append(FIELD_PROJECT_ROOTOBS, 1);
		
		List<ObjectId> result = new ArrayList<ObjectId>();
		DBCursor cur = obsCol.find(ref, keys);
		while(cur.hasNext()){
			DBObject obsItem = cur.next();
			if(obsItem.get(FIELD_PROJECT_ROOTOBS)!=null){
				//���������������Ŀ��������֯�е��û�������
				continue;
			}
			
			DBObject one = obsCol.findOne(new BasicDBObject()
								.append(FIELD_OBS_TYPE, obstype)
								.append(FIELD__ID, obsItem.get(FIELD_OBS_PARENT)),
							new BasicDBObject()
								.append(FIELD__ID, 1));
			if(one!=null && !result.contains(one)){
				result.add((ObjectId) one.get(FIELD__ID));
			}
		}
		
		
		return result;
	}


	public static DBObject getAuthorityData(ObjectId contextId, ObjectId targetId, String tokenId, String targetType, 
			boolean authValue) {
		DBCollection col = DBActivator.getDefaultDBCollection(COLLECTION_AUTHORITY);
		DBObject one = col.findOne(
				new BasicDBObject()
					.append(FIELD_AUTHVALUE, authValue)
					.append(FIELD_TARGETID, targetId)
					.append(FIELD_TOKENID, tokenId)
					.append(FIELD_TARGETTYPE, targetType)
					.append(FIELD_CONTEXTID, contextId));
		return one;
	}
	

	/**
	 * �õ��������֯�������о���Ȩ�޵�Ȩ�޶��� 
	 * @param teamList ��֯������
	 * @param targetList �û����߽�ɫ���б�ֻ����ID list
	 * @param tokenId Ȩ��ID
	 * @param targetType ��Ȩ����user����role
	 * @param authValue ��Ȩֵ����ȨΪtrue,����Ϊfalse
	 * @return ͨ��Ȩ�޵���֯�б�
	 */
	@Deprecated
	public static List<DBObject> getTeamContextAuthorityData(BasicDBList teamList, BasicDBList targetList, String tokenId, String targetType,
			boolean authValue) {
		DBCollection col = DBActivator.getDefaultDBCollection(COLLECTION_AUTHORITY);

		List<DBObject> result = new ArrayList<DBObject>();
		
		
		DBCursor cur = col.find(new BasicDBObject()
				.append(FIELD_AUTHVALUE, authValue)
				.append(FIELD_TOKENID, tokenId)
				.append(FIELD_TARGETTYPE, targetType)
				.append(FIELD_TARGETID, new BasicDBObject().append("$in", targetList)));
		
		while(cur.hasNext()){
			DBObject one = cur.next();
			ObjectId contextId = (ObjectId)one.get(FIELD_CONTEXTID);
			if(teamList.contains(contextId)){
				result.add(one);
			}
		}
		
		return result;
	}
	
	/**
	 * �õ��û�������Ȩ�޵�Ȩ�޶��� 
	 * @param targetList �û����߽�ɫ���б�ֻ����ID list
	 * @param tokenId Ȩ��ID
	 * @param targetType ��Ȩ����user����role
	 * @param authValue ��Ȩֵ����ȨΪtrue,����Ϊfalse
	 * @return ͨ��Ȩ�޵���֯�б�
	 */
	public static List<DBObject> getAuthorityData(BasicDBList targetList, String tokenId, String targetType,
			boolean authValue) {
		DBCollection col = DBActivator.getDefaultDBCollection(COLLECTION_AUTHORITY);

		List<DBObject> result = new ArrayList<DBObject>();
		
		
		DBCursor cur = col.find(new BasicDBObject()
				.append(FIELD_AUTHVALUE, authValue)
				.append(FIELD_TOKENID, tokenId)
				.append(FIELD_TARGETTYPE, targetType)
				.append(FIELD_TARGETID, new BasicDBObject().append("$in", targetList)));
		
		while(cur.hasNext()){
			DBObject one = cur.next();
			result.add(one);
		}
		
		return result;
	}


}
