package com.sg.common.ui;

public interface UIConstants {

	public static final String PERSPECTIVE_ID_MYWORKSPACE = "com.sg.cpm.perspectives.workspace";

	public static final String PERSPECTIVE_ID_DOCUMENT = "com.sg.cpm.perspectives.document";

	public static final String PERSPECTIVE_ID_PROJECT = "com.sg.cpm.perspectives.project";

	public static final String PERSPECTIVE_ID_ADMIN = "com.sg.cpm.perspectives.admin";

	public static final String ID_TOOLBAR = "main";

	public static final String ID_TOOLBAR_GROUP_PERSPECTIVE = "pers";

	public static final String ID_TOOLBAR_GROUP_COMMON = "common";

	public static final String WIDGET_CSS_IN_EDITOR = "inEditor";

	public static final String TEXT_SAVE = "����";

	public static final String TEXT_REMOVE = "ɾ��";

	public static final String TEXT_EDIT = "�༭";

	public static final String TEXT_SAVEALL = "����ȫ��";

	public static final String TEXT_CLOSE = "�رձ༭��";

	public static final String TEXT_CLOSEALL = "�رձ༭";

	public static final String TEXT_WBS_WITH_DOC = "�����ֽ�ͽ����ĵ�";

	public static final String TEXT_OBS_WITH_ROLE = "��Ŀ��ṹ�ͽ�ɫ";

	public static final String TEXT_CREATE_PROJECT_TEMPLATE = "������Ŀģ��";

	public static final String TEXT_CREATE_DOC_TEMPLATE = "��ӽ����ĵ�����";

	public static final String TEXT_CREATE_WORK_TEMPLATE = "��ӹ�������";

	public static final String TEXT_CREATE_TEAM_TEMPLATE = "����ŶӶ���";

	public static final String TEXT_CREATE_ROLE_TEMPLATE = "��ӽ�ɫ����";

	public static final String TEXT_REMOVE_WORK = "ɾ������";

	public static final String TEXT_REMOVE_PROJECT = "ɾ����Ŀ";

	public static final String TEXT_REMOVE_TEAM = "ɾ���Ŷ�";

	public static final String TEXT_REMOVE_ROLE = "ɾ����ɫ";

	public static final String TEXT_REMOVE_DOC = "ɾ���ĵ�";

	public static final String TEXT_CREATE_TEAM = "�����֯/�Ŷ�";

	public static final String TEXT_PROJECT_TEMPLATE = "��Ŀģ��";

	public static final String TEXT_CREATE_ROLE = "��ӽ�ɫ";

	public static final String TEXT_CREATE_USER = "����û�";

	public static final String TEXT_UNAVILABLE_TOOLTIPS = "(��ͬʱ��ʾ�����ĵ�ʱ������)";

	public static final String TEXT_UNAVILABLE_TOOLTIPS_NO_AUTH = "(û��ִ�в�����Ȩ��)";

	public static final String TEXT_UNAVILABLE_TOOLTIPS_NO_SELECTION = "(û��ѡ����Ŀ�͹���)";

	public static final String TEXT_UNAVILABLE_TOOLTIPS_PROJECT_NOT_WIP = "(��Ŀ����״̬�²ſ�ִ��������̲���)";

	public static final String TEXT_SITE = "վ��ṹ";

	public static final String TEXT_USER = "վ���û�";

	public static final String TEXT_ACTIVATE_USER = "�����û�";

	public static final String TEXT_DISACTIVATE_USER = "ʹ�û�ʧЧ";

	public static final String TEXT_CREATE_SITE = "������վ��";

	public static final String TEXT_REMOVE_USER = "����ɾ���û�";

	public static final String TEXT_REMOVE_SITE = "����ɾ��վ��";

	public static final String TEXT_OBS = "��֯�ṹ";

	public static final String TEXT_START_WORK = "��������";

	public static final String TEXT_STOP_WORK = "��ͣ����";

	public static final String TEXT_CLOSE_WORK = "��ɹ���";

	public static final String TEXT_CANCEL_WORK = "ȡ������";

	public static final String MESSAGE_CANNOT_DELETE_SITE_HAS_SUBSITE = "����վ�������վ�㣬����ɾ��";

	public static final String MESSAGE_CANNOT_DELETE_SITE_HAS_USER = "����վ������û�������ɾ��";

	public static final String MESSAGE_PASSWORD_NOT_ENOUGH_LENGTH = "�������볤��������Ҫ8λ������������";

	public static final String MESSAGE_PASSWORD_NOT_MATCHED = "��������������벻ͬ������������";

	public static final String MESSAGE_USERNAME_DUPLICATED = "��������û����Ѿ���ע����";

	public static final String MESSAGE_USERNAME_INVALID_CANNOT_CONTAIN_AT = "�û������ɰ���@�ַ�";

	public static final String MESSAGE_USERNAME_INVALID_CANNOT_ALL_NUMBER = "�û�������ȫ��Ϊ����";

	public static final String MESSAGE_CANNOT_PROJECT_TEAM_HAS_PROJECT = "��������ص���Ŀ������ɾ��";

	public static final String MESSAGE_QUESTION_DELETE = "\nɾ���󲻿ɻָ�����ȷ��Ҫɾ����";

	public static final String MESSAGE_QUESTION_NO_PROJECT_TEMPLATE_CONTINUE = "��û��Ϊ�����Ŀָ����Ŀģ�壬����ϵͳ���޷�Ϊ���Զ�������Ŀ�����ֽ�ṹ�͹�����������ڱ��棬����ֻ��ͨ���ֹ����幤���ֽ�ṹ�͹��������\n����������";

	public static final String MESSAGE_QUESTION_REMOVE_TASK = "\n����ɾ���󲻿ɻָ�����ȷ��Ҫɾ�����������";

	public static final String MESSAGE_QUESTION_REMOVE_DOC = "\n�ĵ�ɾ���󲻿ɻָ�����ȷ��Ҫɾ������ĵ���";

	public static final String MESSAGE_QUESTION_REMOVE_PROJECT = "\n��Ŀɾ���󲻿ɻָ�����ȷ��Ҫɾ�������Ŀ��";

	public static final String ACTION_MOVE_RIHGT_TOOLTIPS = "����½�һ��";

	public static final String ACTION_MOVE_LEFT_TOOLTIPS = "�������һ��";

	public static final String ACTION_MOVE_UP_TOOLTIPS = "�����ƶ�";

	public static final String ACTION_MOVE_DOWN_TOOLTIPS = "�����ƶ�";

	public static final String ACTION_CONTROL_TOOLTIPS = "������̿���";

	public static final String ACTION_MOVE_RIHGT = "com.sg.cpm.actions.wbs.moveRight";

	public static final String ACTION_MOVE_LEFT = "com.sg.cpm.actions.wbs.moveLeft";

	public static final String ACTION_MOVE_UP = "com.sg.cpm.actions.wbs.moveUp";

	public static final String ACTION_MOVE_DOWN = "com.sg.cpm.actions.wbs.moveDown";

	public static final String ACTION_CREATE_WORK = "com.sg.cpm.actions.wbs.createWork";

	public static final String ACTION_REMOVE = "com.sg.cpm.actions.wbs.remove";

	public static final String ACTION_CREATE_DOC = "com.sg.cpm.actions.wbs.createDocument";

	public static final String ACTION_PROCESS_START = "com.sg.cpm.actions.project.start";

	public static final String ACTION_PROCESS_STOP = "com.sg.cpm.actions.project.stop";

	public static final String ACTION_PROCESS_CLOSE = "com.sg.cpm.actions.project.close";

	public static final String ACTION_PROCESS_CANCEL = "com.sg.cpm.actions.project.cancel";

	public static final String ACTION_WBS_PROCESS_CONTROL = "com.sg.cpm.actions.wbs.process";

	public static final String EDITOR_PROJECT_CREATE = "com.sg.cpm.editor.project.create";

	public static final String EDITOR_PROJECT_EDIT = "com.sg.cpm.editor.project.edit";

	public static final String EDITOR_WORK_CREATE = "com.sg.cpm.editor.work.create";

	public static final String EDITOR_WORK_EDIT = "com.sg.cpm.editor.work.edit";

	public static final String EDITOR_WORK_READ = "com.sg.cpm.editor.work.read";

	public static final String EDITOR_DELIVERDOCUMENT_CREATE = "com.sg.cpm.editor.deliveryDocument";

	public static final String EDITOR_SELECT_TEMPLATE = "com.sg.cpm.editor.project.selectTemplate";

	public static final String EDITOR_PROJECT_TEMPLATE = "com.sg.cpm.editor.projectTemplate";

	public static final String EDITOR_WORK_TEMPLATE = "com.sg.cpm.editor.workTemplate";

	public static final String EDITOR_DELIVERY_TEMPLATE = "com.sg.cpm.editor.deliveryTemplate";

	public static final String EDITOR_ROLE_TEMPLATE = "com.sg.cpm.editor.roleTemplate";

	public static final String EDITOR_TEAM_TEMPLATE = "com.sg.cpm.editor.teamTemplate";

	public static final String EDITOR_USER_CREATE = "com.sg.cpm.editor.user.create";

	public static final String EDITOR_USER_SELF_EDIT = "com.sg.cpm.editor.user.self.edit";

	public static final String EDITOR_USER_EDIT = "com.sg.cpm.editor.user.edit";

	public static final String EDITOR_TEAM = "com.sg.cpm.editor.team";

	public static final String EDITOR_ROLE = "com.sg.cpm.editor.role";

	public static final String EDITOR_SITE = "com.sg.cpm.editor.site";

	public static final String EDITOR_FOLDER = "com.sg.cpm.editor.folder";

	public static final String EDITOR_STANDLONE_WORK_CREATE = "com.sg.cpm.editor.standlonework.create";

	public static final String VIEWER_TABLE_PROJECT_TEMPLATE = "com.sg.cpm.queryProjectTemplate";

	public static final String VIEWER_TREE_WBS_TEMPLATE = "com.sg.cpm.projectTemplate.wbs";

	public static final String VIEWER_TREE_OBS_TEMPLATE = "com.sg.cpm.projectTemplate.obs";

	public static final String VIEWER_TREE_ORG = "com.sg.admin.org.tree";

	public static final String VIEWER_TREE_SITE = "com.sg.admin.site.tree";

	public static final String VIEWER_TABLE_SITEUSER = "com.sg.admin.site.user.table";
	
	public static final String VIEWER_TABLE_SITEUSER2 = "com.sg.admin.site.user.table.nostyle";

	public static final String VIEW_PROJECT_NAVIGATOR = "com.sg.cpm.view.projectNavigator";

	public static final String VIEWER_TREE_ORG2 = "com.sg.common.org.tree";

	public static final String ACTION_ASSIGNMENT_TOOLTIPS = "ָ�ɹ���������";

	public static final String TEXT_AUTO_ASSIGNMENT = "�Զ�ָ�ɹ���������";

	public static final String TEXT_OPEN_ASSIGNMENT = "����ָ��";

	public static final String TEXT_WORKRESOURCE = "����������";

	public static final String MESSAGE_INVALID_EMAIL_ADDRESS = "����Ҫ����һ����ȷ�������ַ";

	public static final String VIEW_PROJECT_FOLDER_NAVIGATOR = "com.sg.cpm.view.projectdocument";

	public static final String VIEW_ORG_FOLDER_NAVIGATOR = "com.sg.cpm.view.orgdocument";

	public static final String MESSAGE_CANNOT_WORK_DEL = "������ɾ��һ�������Ľ����ĵ�";

	public static final String TEXT_REMOVE_FOLDER = "ɾ��Ŀ¼";

	public static final String MESSAGE_QUESTION_DELETE_FOLDER_MOVE_DOC = "ɾ��Ŀ¼��Ϊ�˱���Ŀ¼�µ��ĵ��Ķ�ʧ�����ǽ��ƶ�������Ŀ¼�¡�\n��ȷ��Ҫɾ��ѡ�е�Ŀ¼��";

	public static final String TEXT_MOVE = "�ƶ�";

	public static final String MESSAGE_CANNOT_MOVE_TO_ANOTHER_ROOT = "�����ܽ����Ŀ¼���ĵ��ƶ�����һ����Ŀ¼�¡�";

	public static final String MESSAGE_CANNOT_MOVE_TO_FILE = "�����ܰ�Ŀ¼�����ĵ��ƶ���һ���ļ��¡�";

	public static final String MESSAGE_CANNOT_MOVE_TO_CHILD = "�����ܰ�һ��Ŀ¼�����ļ��ƶ��������¼�Ŀ¼��";

	public static final String MESSAGE_CHANGE_FBS = "�ı��ļ��еĽṹ";

	public static final String MESSAGE_NOT_AUTH = "����û�еõ���Ҫ��Ȩ�޽������������";

	public static final String TEXT_IMPORT_PROJECT_TEMPLATE = "ѡ����Ŀģ�嵼��...";

	public static final String TEXT_READY_BOX = "׼��/����";

	public static final String TEXT_PROCESS_BOX = "����";

	public static final String TEXT_PAUSE_BOX = "��ͣ";

	public static final String TEXT_CLOSENCANCEL_BOX = "���/ȡ��";

	public static final String TEXT_SETPARTICIPAIED = "������Դ";

	public static final String TEXT_WARNING_PARTICIPAIEDEXSIT = "���û��Ѿ��ǵ�ǰ�������Դ��\n������Ҫ�ظ����á�";

	public static final String TEXT_WARNING_PARTICIPAIED_ISPM = "���û��Ѿ��ǵ�ǰ����ĸ����ˡ�\n�����Ծ��и��������Ĳ�����������Ҫ������Ϊ���������Դ��";

	public static final String TEXT_FINISHWORK = "��ɹ���";

	public static final String TEXT_STARTWORK = "��ʼ����";

	public static final String TEXT_STARWORK = "��ǹ���";

	public static final String TEXT_READWORK = "����Ѷ�";

	public static final String TEXT_CANCEL_SORTING = "ȡ�����е�����";

	public static final String VIEW_MYWORKS_WORKSINBOX = "com.sg.cpm.view.workinbox";

	public static final String VIEW_MYWORKS_RECYCLEBIN = "com.sg.cpm.view.work.recyclebin";

	public static final String MESSAGE_CANNOT_REMOVE_DOCUMENT_WHEN_TASK_FINISHED = "�����ڲ���ɾ������ĵ�������ĵ��������Ѿ���ɡ�";

	public static final String MESSAGE_CANNOT_REMOVE_DOCUMENT_WHEN_NOT_CHARGER = "��������������ĸ����ˣ����ȷʵ��Ҫɾ������ĵ���������֪���������˽���ɾ����";

	public static final String MESSAGE_CANNOT_REMOVE_DOCUMENT_UNKNOWN = "��Ǹ��ϵͳ�޷���ȡ��׼��ɾ�����ĵ��Ĺ�����Ϣ���޷�ִ��ɾ��������";

	public static final String MESSAGE_IMPORT_PROJECT_TEMPLATE_FINISHED = "ϵͳ�Ѿ�������ѡ�����Ŀģ�彨������Ŀ�����ֽ�ṹ��";

	public static final String MESSAGE_AUTO_ASSIGNMENT_FINISHED = "ϵͳ�Ѿ�������������ָ�ɹ������Ŀ�Ŷӽ����������Զ����䡣";

	public static final String MESSAGE_CANNOT_SETUSER_UNDER_AUSER = "�������԰�һ���û���������һ���û��¡�";

	public static final String TEXT_ORGEDIT = "�༭��֯�ṹ";

	public static final String MESSAGE_HAVE_A_SAME_USER_UNDER_A_ORG = "��ѡ����û��Ѿ�����֯���ˡ�";

	public static final String MESSAGE_AUTO_SET_ADMIN_USER = "��ǰ��վ����û�в��ҵ�admin�����û���ϵͳ��Ϊ������һ����Ϊadmin�ĳ����û�������Ϊ1����ע�Ᵽ��";

	public static final String ONLY_MOVE_USERFROMUSERLIST = "��Ǹ����ֻ�ܴ��û��б�������֯����û���\n�����ϣ���ı��û���������֯�����ȴ�ԭ��֯���Ƴ�����û���Ȼ������ӡ�";

	public static final String TEXT_SITE_ORG = "վ�����֯";

	public static final String TEXT_USERANDROLE = "�û��ͽ�ɫ";

	public static final String TEXT_AUTHLIST = "���õ�Ȩ��";

	public static final String TEXT_SITE_ELEMENT = "վ��";

	public static final String TEXT_ORG_ELEMENT = "��֯";

	public static final String TEXT_ROLE_ELEMENT = "��ɫ";

	public static final String TEXT_USER_ELEMENT = "�û�";

	public static final String TEXT_AUTH_SETTING = "Ȩ������";

	public static final String MESSAGE_AUTH_HAVE_SETTED = "���Ѿ�Ϊ��ǰ�Ľ�ɫ���û����������Ȩ�ޡ�Ȩ�����ò����ظ���";

	public static final String MESSAGE_CANNOT_DELETE_PROJECT_NOT_INIT_OR_READY = "��Ŀһ���������޷�ɾ���ˡ����޷�ִ��������ɾ��������";

	public static final String MESSAGE_CANNOT_DELETE_PROJECT_NOT_AUTH = "ֻ����Ŀ������֯����Ŀ����Ա�������Ŀ�Ĵ����߲ž���ɾ�������Ŀ��Ȩ�ޡ�\n�����ϣ����������������ϵȨ�޹���Ա��������Ҫ��Ȩ�޻���ֱ������֯����Ŀ����Ա���͡�";

	public static final String MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS = "ֻ����Ŀ������֯����Ŀ����Ա����Ŀ������ܶ���Ŀ���н��̿��ơ�\n�����ϣ����������������ϵȨ�޹���Ա��������Ҫ��Ȩ�ޡ�";

	public static final String TEXT_PROJECT_CONTROL = "��Ŀ���̿���";

	public static final String MESSAGE_USERID_INVALID_ALL_NUMBER = "�û�ID����ȫ��������";

	public static final String MESSAGE_EMAIL_DUPLICATED = "�����ѱ�ʹ��";

	public static final String TEXT_CREATE_WORK = "��������";

	public static final String MESSAGE_START_WORK_IMMEDIETELY = "���Ƿ���Ҫ�Ƿ������������������";

	public static final String TEXT_WORKTEMPLATE = "����ģ�弰������";

	public static final String TEXT_DOCUMENT = "�ĵ�";

	public static final String TEXT_WORKTEMPLATE_DELIVERY = "�����ĵ�";

	public static final String TEXT_WORKTEMPLATE_WORKFLOW = "���̲�����";

	public static final String VIEWER_TREE_ORG_TEAM = "com.sg.admin.org2.tree";

	public static final String VIEWER_TABLE_WORKTEAMPLATE = "com.sg.admin.table.queryWorkTemplate";

	public static final String TEXT_EDIT_WORK_TEMPLATE = "�༭��������";

	public static final String TEXT_REMOVE_DOC_TEMPLATE = "ɾ�������ĵ�����";

	public static final String TEXT_EDIT_DOC_TEMPLATE = "�༭�����ĵ�����";

	public static final String TEXT_PARTICIPAIED = "����������";

	public static final String TEXT_ADD_PATICIPATE = "��Ӳ�����";

	public static final String TEXT_REMOVE_PATICIPATE = "ɾ��������";

	public static final String TEXT_WORKFLOWSELETE = "ѡ������";

	public static final String MESSAGE_OVERIDE_WORKFLOWDEFINITION = "���Ѿ�Ϊ��ǰ������ģ��Ԥ�����˹��������������ѡ�����Ը�����Ԥ����Ĺ������Լ���������Ϣ������¡�\n��ϣ�����������������̶�����";

	public static final String MESSAGE_OVERIDE_WORKFLOWDEFINITION_WITHNULL = "��û��ѡ���κ����̣���Ҫ���ԭ��Ԥ����Ĺ�������";

	public static final String TEXT_UNAVILABLE_PROCESS_DEFINITION = "���̶��岻����";

	public static final String TEXT_NOTNEEDASSIGN = "";

	public static final String TEXT_NEEDASSIGN = "";

	public static final String TEXT_ACTIVATE_TEMPLATE = "����/ͣ��ģ��";

	public static final String EXCEPTION_UNCOMPLETE_ASSIGNMENT = "��ѡ�����������Ѿ��趨�˹������������������������Щ���Ҫָ��ִ���ߣ�û��ȷ��ִ���ߵ����̽��޷��������С�\n�������̲�����ҳ�������ִ���ߵĶ�������������ù���ģ�塣";

	public static final String TEXT_RULE_ASSIGNMENT = "[�е��߹�����]";

	public static final String MESSAGE_NEED_SELECT_TEMPLATE = "��������ѡ��һ��������ģ��";

	public static final String MESSAGE_TEMPLATE_HAS_WFDEF = "��ѡ��Ĺ���ģ���Ѿ�Ԥ���˹������������Ĳ�����ȡ���������ϵ����á�\n���޷�������Щ���á�";

	public static final String TEXT_STARTWFWORK = "��ʼ��������";

	public static final String TEXT_FINISHWFWORK = "�����������";

	public static final String WELCOME = "��ӭ ";

	public static final String TEXT_ONLYMYPROCESS = "ֻ��ʾ�ҵ���������";

	public static final String TEXT_ALLPROCESS = "��ʾ�ҵĹ�����������������";

	public static final String TEXT_DYNAMIC_ASSIGNMENT = "[�����ж�ָ̬��]";

	public static final String TEXT_VALIDATION_TASK = "����������";

	public static final String MESSAGE_CANNOT_CONTROL_PROJECT_TEAM = "ֻ�б���Ŀ������֯����Ŀ����Ա���Ա༭��Ŀ�Ŷ�";

}
