package com.sg.common.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.common.BusinessService;
import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.SingleObject;
import com.sg.user.UserSessionContext;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;

public class DocumentService extends CommonService {

	/**
	 * 
	 * <strong>
	 * <p>
	 * Prepare Document to create
	 * </p>
	 * </strong>
	 * <p>
	 * There two types work can add delivery document:
	 * <li><strong>Project work: </strong> the document need to save information
	 * of the project and add to the project folder</li>
	 * <li><strong>Standlone work: </strong> the document need to add to the
	 * organization folder</li> all of these document need to store work as its
	 * <code>wbsparent</code>
	 * </p>
	 * <br/>
	 * 
	 * <p>
	 * Ex: A document produce by <strong>project work </strong>
	 * </p>
	 * <code>
	 * { "_id" : ObjectId("4fbce40ff7e6cfc912d65d50"), <strong>"wbsparent" :
	 * ObjectId("4fbce40ff7e6cfc912d65d4f")</strong>, 
	 * "templateType" : "document", "_editor" :
	 * "com.sg.cpm.editor.JZ-QR-XG003A", "desc" : "DOCUMENT NAME", "creator" :
	 * "20102069", "creator_desc" : "gpp", "owner" : "20102069", "owner_desc" :
	 * "gpp", "createdate" : ISODate("2012-05-23T13:20:15.476Z"), <strong>"rootid" :
	 * ObjectId("4fbce40ff7e6cfc912d65d4e")</strong>,  <strong>"fbsparent" :
	 * ObjectId("4fbce40ff7e6cfc912d65d51")</strong> }
	 * </code>
	 * 
	 * <p>
	 * <li>field: wbsparent,store parent work id</li>
	 * <li>field: rootid, store project id</li>
	 * <li>field: fbsparent, store parent folder id</li>
	 * </p>
	 * <br/>
	 * <p>
	 * Another example is a document produce by a <strong>stanlone
	 * work</strong>, standlone work means a work launched by some user, not by
	 * project
	 * </p>
	 * <p>
	 * <li>field: wbsparent,store parent work id</li>
	 * <li>field: rootid, store folder root id of team, pay more attention to
	 * this, it's a mistake, rootid to store team id is much better, but I
	 * havn't change it</li>
	 * <li>field: fbsparent, store parent folder id</li>
	 * </p>
	 * 
	 * 
	 * @since 1.0
	 * @author hua
	 * @param workId
	 * @return document SingleObject
	 */
	public SingleObject prepareWorkDocument(ObjectId workId) {

		SingleObject so = new SingleObject(docCollection);

		// get work data
		DBObject parentWorkData = getDBObject(workCollection, workId);
		Assert.isNotNull(parentWorkData);

		// set document parent work
		so.setValue(FIELD_WBSPARENT, workId, null, false);

		// get work's project data
		ObjectId projectId = (ObjectId) parentWorkData.get(FIELD_ROOTID);
		DBObject projectData = null;
		ObjectId parentFolderId = null;

		if (projectId != null) {// a document produced by project work
			// set document parent project
			so.setValue(FIELD_ROOTID, projectId);

			// get project folder id
			projectData = getDBObject(projectCollection, projectId);
			parentFolderId = (ObjectId) projectData.get(FIELD_FOLDER_ROOT);
		} else {// a document produced by standlone work
				// get current user's organization's folder
			ObjectId userId = UserSessionContext.getSession().getUserOId();
			BasicDBList list = BusinessService.getOrganizationService().getTeamOfUser(userId, OrganizationService.TEAM_NOPROJECT);
			if (!list.isEmpty()) {
				DBObject team = (DBObject) list.get(0);
				parentFolderId = (ObjectId) team.get(FIELD_FOLDER_ROOT);

				// it's a mistake but we have not change it;
				// here we store parentFolderId to rootid
				so.setValue(FIELD_ROOTID, parentFolderId);

			}

		}
		if (parentFolderId != null) {
			// set document parent folder
			so.setValue(FIELD_FBSPARENT, parentFolderId);
		}
		return so;
	}

	/**
	 * <p>
	 * Create document from a template
	 * </p>
	 * 
	 * @author hua
	 * @since 1.0
	 * @param workId
	 * @param templateId
	 */
	public void createDocumentFromTemplate(ObjectId workId, ObjectId templateId) {



		DBObject template = getDBObject(workTemplateCollection, templateId);
		if (template == null) {
			return;
		}

		String editorId = (String) template.get(FIELD_SYSTEM_EDITOR);
		if (editorId == null) {
			return;
		}
		
		DBObject createSystemInformation = getSessionUserCreateInfo();

		createDocumentFromEditorId(workId,editorId,createSystemInformation);

	}

	public DBObject createDocumentFromEditorId(ObjectId workId, String editorId,DBObject createInformation) {
		DBObject work = getDBObject(workCollection, workId);
		if (work == null) {
			return null;
		}
		String documentName = getDocumentNameFromEditor(editorId);

		BasicDBObject doc = new BasicDBObject();
		ObjectId docId = new ObjectId();
		doc.put(FIELD_SYSID, docId);
		doc.put(FIELD_WBSPARENT, workId);
		doc.put(FIELD_TEMPLATE_TYPE, VALUE_WBS_DOCUMENT_TYPE);
		doc.put(FIELD_SYSTEM_EDITOR, editorId);
		doc.put(FIELD_DESC, documentName);

		// if the work is a project work, rootid field will be null
		// otherwise the work is a standlone work

		ObjectId projectId = (ObjectId) work.get(FIELD_ROOTID);
		if (projectId != null) {// project work

			// set the document to project document;
			doc.put(FIELD_ROOTID, projectId);

			// set the document into project folder
			ObjectId projectFolderId = getProjectRootFolder(projectId);
			doc.put(FIELD_FBSPARENT, projectFolderId);

		} else {// standlone work
			DBObject actor = (DBObject) work.get(FIELD_WORK_PM);
			ObjectId userId = (ObjectId) actor.get(FIELD_SYSID);
			BasicDBList list = BusinessService.getOrganizationService().getTeamOfUser(userId, OrganizationService.TEAM_NOPROJECT);
			if (!list.isEmpty()) {
				DBObject team = (DBObject) list.get(0);

				ObjectId parentFolderId = (ObjectId) team.get(FIELD_FOLDER_ROOT);
				doc.put(FIELD_FBSPARENT, parentFolderId);

				// it's a mistake but we have not change it;
				// here we store parentFolderId to rootid
				doc.put(FIELD_ROOTID, parentFolderId);
			}

		}

		if(createInformation!=null){
			Iterator<String> iter = createInformation.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				doc.put(key, createInformation.get(key));
			}
		}

		docCollection.insert(doc);
		return doc;
	}

	/**
	 * <p>
	 * <strong>get editor name from editorId</strong>
	 * </p>
	 * 
	 * @author hua
	 * @since 1.0
	 * @param editorId
	 * @return
	 */
	public String getDocumentNameFromEditor(String editorId) {

		Set<EditorConfiguration> editorSet = Widget.listSingleObjectEditorConfigurationByCollection(IDBConstants.COLLECTION_DOCUMENT);
		Iterator<EditorConfiguration> iter = editorSet.iterator();
		while (iter.hasNext()) {
			EditorConfiguration ec = iter.next();
			if (ec.getId().equals(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE)) {
				// basic document,
				// use to create document manuly
				continue;
			}
			if (ec.getId().equals(editorId)) {
				return ec.getName();
			}
		}

		return null;
	}

	public ObjectId getProjectRootFolder(ObjectId projectId) {

		DBObject project = getDBObject(projectCollection, projectId);
		return (ObjectId) project.get(FIELD_FOLDER_ROOT);
	}

	public ObjectId getOrganizationRootFolder(ObjectId organizationId) {

		DBObject organization = getDBObject(orgCollection, organizationId);
		return (ObjectId) organization.get(FIELD_FOLDER_ROOT);
	}

	/**
	 * <strong>获得某个工作的下级文档</strong>
	 * 
	 * @param workId
	 * @param editortype
	 * @return
	 */
	public DBObject getWorkDocument(ObjectId workId, String editortype) {
		return docCollection.findOne(new BasicDBObject().append(FIELD_WBSPARENT, workId).append(FIELD_SYSTEM_EDITOR, editortype));
	}

	public List<DBObject> getWorkDocument(ObjectId workId) {
		return docCollection.find(new BasicDBObject().append(FIELD_WBSPARENT, workId)).toArray();
	}
	
	
	public List<DBObject> getSubFolder(ObjectId parentFolderId,String folderName) {
		DBCursor cur = folderCollection.find(new BasicDBObject().append(FIELD_FBSPARENT,parentFolderId).append(FIELD_DESC, folderName));
		return cur.toArray();
	}

	public void saveDocument(DBObject doc) {
		docCollection.save(doc);
	}

//	public void createDocumentForWork(ObjectId workId, String editorId) {
//		String documentName = getDocumentNameFromEditor(editorId);
//
//		BasicDBObject doc = new BasicDBObject();
//		ObjectId docId = new ObjectId();
//		doc.put(FIELD_SYSID, docId);
//		doc.put(FIELD_WBSPARENT, workId);
//		doc.put(FIELD_TEMPLATE_TYPE, VALUE_WBS_DOCUMENT_TYPE);
//		doc.put(FIELD_SYSTEM_EDITOR, editorId);
//		doc.put(FIELD_DESC, documentName);
//		
//		
//		DBObject work = BusinessService.getWorkService().getWorkObject(workId);
//		// 创建者 创建时间
//		doc.put(IDBConstants.FIELD_CREATER, work.get(IDBConstants.FIELD_CREATER));
//		
//		doc.put(IDBConstants.FIELD_CREATER_NAME, work.get(IDBConstants.FIELD_CREATER_NAME));
//		
//		doc.put(IDBConstants.FIELD_OWNER, work.get(IDBConstants.FIELD_OWNER));
//		
//		doc.put(IDBConstants.FIELD_OWNER_NAME, work.get(IDBConstants.FIELD_OWNER_NAME));
//		
//		doc.put(IDBConstants.FIELD_CREATE_DATE, new Date());
//		
//		//文档与工作关联
//		
//	}
}
