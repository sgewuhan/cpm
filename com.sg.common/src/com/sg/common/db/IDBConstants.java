package com.sg.common.db;

public interface IDBConstants {

	public static final String COLLECTION_AUTH = "authority";

	public static final String COLLECTION_DOCUMENT = "document";

	public static final String COLLECTION_FOLDER = "folder";

	public static final String COLLECTION_KSESSION = "ksession";

	public static final String COLLECTION_ORG = "obs";

	public static final String COLLECTION_ORG_TEMPLATE = "obs_t";

	public static final String COLLECTION_PROJECT = "project";

	public static final String COLLECTION_PROJECT_TEMPLATE = "project_t";

	public static final String COLLECTION_SITE = "site";

	public static final String COLLECTION_USER = "user";

	public static final String COLLECTION_USER_PROJECT_IN_CHARGED = "userref_projectincharged";

	public static final String COLLECTION_USER_WORK_IN_CHARGED = "userref_workincharged";

	public static final String COLLECTION_USER_WORK_PARTTICIPATED = "userref_workparticipated";

	public static final String COLLECTION_WORK = "work";
	
	public static final String COLLECTION_NOTICE = "notice";
	
	public static final String FIELD_WFINFO_PARENTTASKID = "parentTaskId";

	public static final String COLLECTION_WORK_TEMPLATE = "work_t";

	public static final String[] DATA_OBS_BASIC = new String[] { "_id", "obsparent", "templateType", "desc", "rootid" };

	public static final String[] DATA_USER_BASIC = new String[] { "_id", "activate", "desc", "name", "uid", "email", "siteName", "thumb" };

	public static final String DUMMY_FIELD_PARENT = "display_parent";

	public static final String DUMMY_FIELD_TEAMPLATE = "display_template";

	public static final String EXP_CASCADE_AUTH_ORG = "com.sg.cpm.auth.org";

	/**
	 * CASCADE，com.sg.admin，传递参数：项目模板的_id，根据项目模板的ID查询下级的obs_t集合
	 */
	public static final String EXP_CASCADE_AUTH_SITE = "com.sg.cpm.auth.site";

	public static final String EXP_CASCADE_OBS = "com.sg.cpm.currentProjectOBS";

	public static final String EXP_CASCADE_OBS_TEMPLATE = "com.sg.cpm.currentProjectOBSTemplate";

	public static final String EXP_CASCADE_SITE = "com.sg.common.site.root";

	public static final String EXP_CASCADE_SO_FBS = "com.sg.folder";

	public static final String EXP_CASCADE_SO_OBS = "com.sg.cpm.obs.parent";

	public static final String EXP_CASCADE_SO_OBS_TEMPLATE = "com.sg.cpm.obsTemplate.parent";

	// public static final String FIELD_PROJECT_DEPT = "dept";

	public static final String EXP_CASCADE_SO_SITE = "com.sg.common.site";

	public static final String EXP_CASCADE_SO_WBS = "com.sg.cpm.wbs.parent";

	public static final String EXP_CASCADE_SO_WBS_TEMPLATE = "com.sg.cpm.wbsTemplate.parent";

	public static final String EXP_CASCADE_SO_WBS_WITH_DOC = "com.sg.cpm.wbs.withdoc.parent";

	public static final String EXP_CASCADE_WBS = "com.sg.cpm.currentProject";

	public static final String EXP_CASCADE_WBS_TEMPLATE = "com.sg.cpm.currentProjectWBSTemplate";

	public static final String EXP_CASCADE_WBS_WITH_DOC = "com.sg.cpm.currentProjectWithDoc";

	public static final String EXP_OBS_ROOT_BY_USER = "com.sg.common.user.org.root";

	public static final String EXP_QUERY_AUTH = "com.sg.common.query.authority";

	public static final String EXP_QUERY_DOCUMENT = "com.sg.cpm.query.document";

	public static final String EXP_QUERY_OBS = "com.sg.common.query.obs";

	public static final String EXP_QUERY_PROJECT = "com.sg.cpm.query.project";

	public static final String EXP_QUERY_PROJECT_TEMPLATE = "com.sg.cpm.query.projectTemplate";

	public static final String EXP_QUERY_SITE = "com.sg.common.query.site";

	public static final String EXP_QUERY_USER = "com.sg.cpm.query.user";

	public static final String EXP_QUERY_WORK = "com.sg.cpm.query.work";

	public static final String EXP_QUERY_WORK_BY_WBSPARENT = "com.sg.cpm.query.work.bywbsparent";

	public static final String EXP_QUERY_WORKTEMPLATE_BY_WBSPARENT = "com.sg.cpm.query.workTemplate.bywbsparent";

	public static final String FIELD_ACTIVATE = "activate";

	public static final String FIELD_AUTHVALUE = "authValue";

	public static final String FIELD_CONTEXTID = "contextId";

	public static final String FIELD_CREATE_DATE = "createdate";

	public static final String FIELD_CREATER = "creator";

	public static final String FIELD_CREATER_NAME = "creator_desc";

	public static final String FIELD_DBSSEQ = "dbsseq";

	public static final String FIELD_DESC = "desc";

	public static final String FIELD_EMAIL = "email";

	public static final String FIELD_FBSPARENT = "fbsparent";

	public static final String FIELD_FOLDER_ROOT = "folderRoot";

	public static final String FIELD_FUNCTION_TOOLTIPS = "tooltips";

	public static final String FIELD_HISTORY = "history";

	public static final String FIELD_ID = "id";

	public static final String FIELD_KBNAME = "kbname";

	public static final String FIELD_KNOWLEDGEBASE = "knowledgebase";

	public static final String FIELD_LOCKBY = "lockedby";

	public static final String FIELD_LOCKMARK = "lockmark";

	public static final String FIELD_LOCKREASON = "lockreason";

	public static final String FIELD_MARK_DELETE = "deletemark";

	public static final String FIELD_MARK_READ = "readmark";

	public static final String FIELD_MARK_STAR = "starmark";

	public static final String FIELD_MODIFY_DATE = "modifydate";

	public static final String FIELD_NAME = "name";

	public static final String FIELD_OBSPARENT = "obsparent";

	public static final String FIELD_OWNER = "owner";

	public static final String FIELD_OWNER_NAME = "owner_desc";

	public static final String FIELD_PASSWORD = "password";

	public static final String FIELD_PASSWORD_REPEAT = "password_repeat";

	public static final String FIELD_PROCESS_DEFINITION_ID = "processDefinitionId";

	public static final String FIELD_PROCESS_STATUS = "procstatus";

	public static final String FIELD_PROCESSHISTORY = "processHistory";

	public static final String FIELD_PROJECT_ACTUALFINISH = "actualfinish";

	public static final String FIELD_PROJECT_ACTUALSTART = "actualstart";

	public static final String FIELD_PROJECT_DIRECTION = "direction";

	public static final String FIELD_PROJECT_FOLDER_CREATE_FBS_AS_WBS = "createfbsfromwbs";

	public static final String FIELD_PROJECT_OBS_ROOT = "obsroot";

	public static final String FIELD_PROJECT_PLANFINISH = "planfinish";

	public static final String FIELD_PROJECT_PLANSTART = "planstart";

	public static final String FIELD_PROJECT_PM = "pm";

	public static final String FIELD_ROOTID = "rootid";

	public static final String FIELD_SHARESITE = "sharesite";

	public static final String FIELD_SITENAME = "sitename";

	public static final String FIELD_SITEPARENT = "siteparent";

	public static final String FIELD_SYSID = "_id";

	public static final String FIELD_SYSTEM_EDITOR = "_editor";

	public static final String FIELD_SYSTEM_TYPE = "_type";

	public static final String FIELD_TARGETID = "targetId";

	public static final String FIELD_TARGETTYPE = "targetType";

	public static final String FIELD_TEMPLATE = "template";// 保存在project的模板字段

	public static final String FIELD_TEMPLATE_TYPE = "templateType";

	public static final String FIELD_THUMB = "thumb";

	public static final String FIELD_TOKENID = "tokenId";

	public static final String FIELD_UID = "uid";

	public static final String FIELD_URLLABEL = "urllabel";

	public static final String FIELD_USEROID = "useroid";

	public static final String FIELD_WBSPARENT = "wbsparent";

	public static final String FIELD_WBSSEQ = "wbsseq";

	public static final String FIELD_WF_HISTORY_ACTOR = "actor";

	public static final String FIELD_WF_HISTORY_CHOICE = "choice";

	public static final String FIELD_WF_HISTORY_COMMENT = "comment";

	public static final String FIELD_WF_HISTORY_OPEN_DATE = "opendate";

	public static final String FIELD_WF_HISTORY_CLOSE_DATE = "closedate";

	public static final String FIELD_WF_HISTORY_TASK_OPERATION = "taskOperation";

	public static final String FIELD_WFINFO = "wfinfo";

	public static final String FIELD_WFINFO_ACTORID = "actorId";

	public static final String FIELD_WFINFO_ACTORNAME = "actorName";

	public static final String FIELD_WFINFO_PROCESSID = "processId";

	public static final String FIELD_WFINFO_PROCESSINSTANCEID = "processInstanceId";

	public static final String FIELD_WFINFO_TASKCOMMENT = "taskComment";

	public static final String FIELD_WFINFO_TASKID = "taskId";

	public static final String FIELD_WFINFO_TASKNAME = "taskName";

	public static final String FIELD_WFINFO_TASKSTATUS = "taskStatus";

	public static final String FIELD_WORK_PM = "pm";// 代表工作负责人，考虑简化与项目负责人字段同名

	public static final String FIELD_WORK_PROCESS_ASSINGMENT_DEFINITION = "assignmentdef";

	public static final String FIELD_WORK_RESOURCE = "resource";

	public static final String FIELD_PARTICIPATE = "participate";
	
	public static final String FIELD_NOTICE_TIME = "noticetime";

	public static final String PARAM_INPUT_DESC = "input.desc";

	public static final String PARAM_INPUT_EMAIL = "input.email";

	public static final String PARAM_INPUT_FBSPARENT_LIST = "input.fbsparent.list";

	public static final String PARAM_INPUT_ID = "input._id";

	public static final String PARAM_INPUT_OBSPARENT = "input.obsparent";

	public static final String PARAM_INPUT_OBSTYPE = "input.templateType";

	public static final String PARAM_INPUT_PASSWORD = "input.password";

	public static final String PARAM_INPUT_ROOTID = "input.rootid";

	public static final String PARAM_INPUT_SITEPARENT = "input.siteparent";

	public static final String PARAM_INPUT_UID = "input.uid";

	public static final String PARAM_INPUT_USEROID = "input.useroid";

	public static final String PARAM_INPUT_WBSPARENT = "input.wbsparent";

	public static final String PARAM_PROJECT_OBS_ROOT = "input.obsroot";

	public static final String VALUE_LOCKREASON_PROCESS = "process";

	public static final String VALUE_OBS_PJTEAMTYPE = "project";

	public static final String VALUE_OBS_ROLETYPE = "role";

	public static final String VALUE_OBS_TEAMTYPE = "team";

	public static final String VALUE_OBS_USERTYPE = "user";

	public static final String VALUE_PROCESS_CANCEL = "process.cancel";

	public static final String VALUE_PROCESS_CLOSE = "process.close";

	public static final String VALUE_PROCESS_PAUSE = "process.pause";

	public static final String VALUE_PROCESS_PROCESS = "process.process";

	public static final String VALUE_PROCESS_READY = "process.ready";

	public static final String VALUE_ROLE_PROJECTADMIN = "项目管理员";

	public static final String VALUE_ROLE_PROJECTMANAGER = "项目经理";

	public static final String VALUE_ROLE_PROJECTOBSERVER = "项目查看者";

	public static final String VALUE_USER_ADMIN = "admin";

	public static final String VALUE_WBS_DOCUMENT_TYPE = "document";
	
	public static final String FIELD_REQUIRED_DOCUMENT = "require";

	public static final String VALUE_WBS_TASK_TYPE = "work";

	public static final String VALUE_WF_STATUS_COMPLETE = "Completed";

	public static final String VALUE_WF_STATUS_CREATED = "Created";

	public static final String VALUE_WF_STATUS_INPROGRESS = "InProgress";

	public static final String VALUE_WF_STATUS_READY = "Ready";

	public static final String VALUE_WF_STATUS_RESERVED = "Reserved";

	public static final String VALUE_WF_TASK_OPER_COMPLETE = "完成";

	public static final String VALUE_WF_TASK_OPER_START = "开始";

	public static final String WF_ACTOR = "ActorId";

	public static final String WF_GROUP = "GroupId";

	public static final String FIELD_AMOUNT = "amount";

	public static final String FIELD_SPEC = "spec";

	public static final String FIELD_QTY = "qty";

	public static final String FIELD_SOLUTION = "solution";

	public static final String FIELD_USAGE = "usage";
	
	public static final String FIELD_DATE = "udate";

	public static final String FIELD_DOCUMENT_WORKS = "historyWork";

	public static final String FIELD_WFINFO_ADDITIONAL = "wfdata";

	public static final String FIELD_BUDGET = "budget";

	public static final String FIELD_NOTICETASK_NOTICED = "noticetasknoticed";


}
