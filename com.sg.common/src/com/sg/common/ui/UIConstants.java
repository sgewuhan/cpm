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

	public static final String TEXT_SAVE = "保存";

	public static final String TEXT_REMOVE = "删除";

	public static final String TEXT_EDIT = "编辑";

	public static final String TEXT_SAVEALL = "保存全部";

	public static final String TEXT_CLOSE = "关闭编辑器";

	public static final String TEXT_CLOSEALL = "关闭编辑";

	public static final String TEXT_WBS_WITH_DOC = "工作分解和交付文档";

	public static final String TEXT_OBS_WITH_ROLE = "项目组结构和角色";

	public static final String TEXT_CREATE_PROJECT_TEMPLATE = "创建项目模板";

	public static final String TEXT_CREATE_DOC_TEMPLATE = "添加交付文档定义";

	public static final String TEXT_CREATE_WORK_TEMPLATE = "添加工作定义";

	public static final String TEXT_CREATE_TEAM_TEMPLATE = "添加团队定义";

	public static final String TEXT_CREATE_ROLE_TEMPLATE = "添加角色定义";

	public static final String TEXT_REMOVE_WORK = "删除工作";

	public static final String TEXT_REMOVE_PROJECT = "删除项目";

	public static final String TEXT_REMOVE_TEAM = "删除团队";

	public static final String TEXT_REMOVE_ROLE = "删除角色";

	public static final String TEXT_REMOVE_DOC = "删除文档";

	public static final String TEXT_CREATE_TEAM = "添加组织/团队";

	public static final String TEXT_PROJECT_TEMPLATE = "项目模板";

	public static final String TEXT_CREATE_ROLE = "添加角色";

	public static final String TEXT_CREATE_USER = "添加用户";

	public static final String TEXT_UNAVILABLE_TOOLTIPS = "(在同时显示交付文档时不可用)";

	public static final String TEXT_UNAVILABLE_TOOLTIPS_NO_AUTH = "(没有执行操作的权限)";

	public static final String TEXT_UNAVILABLE_TOOLTIPS_NO_SELECTION = "(没有选中项目和工作)";

	public static final String TEXT_UNAVILABLE_TOOLTIPS_PROJECT_NOT_WIP = "(项目进行状态下才可执行任务进程操作)";

	public static final String TEXT_SITE = "站点结构";

	public static final String TEXT_USER = "站点用户";

	public static final String TEXT_ACTIVATE_USER = "激活用户";

	public static final String TEXT_DISACTIVATE_USER = "使用户失效";

	public static final String TEXT_CREATE_SITE = "创建子站点";

	public static final String TEXT_REMOVE_USER = "永久删除用户";

	public static final String TEXT_REMOVE_SITE = "永久删除站点";

	public static final String TEXT_OBS = "组织结构";

	public static final String TEXT_START_WORK = "启动工作";

	public static final String TEXT_STOP_WORK = "暂停工作";

	public static final String TEXT_CLOSE_WORK = "完成工作";

	public static final String TEXT_CANCEL_WORK = "取消工作";

	public static final String MESSAGE_CANNOT_DELETE_SITE_HAS_SUBSITE = "，该站点包含子站点，不可删除";

	public static final String MESSAGE_CANNOT_DELETE_SITE_HAS_USER = "，该站点包含用户，不可删除";

	public static final String MESSAGE_PASSWORD_NOT_ENOUGH_LENGTH = "您的密码长度至少需要8位，请重新输入";

	public static final String MESSAGE_PASSWORD_NOT_MATCHED = "您两次输入的密码不同，请重新输入";

	public static final String MESSAGE_USERNAME_DUPLICATED = "您输入的用户名已经被注册了";

	public static final String MESSAGE_USERNAME_INVALID_CANNOT_CONTAIN_AT = "用户名不可包含@字符";

	public static final String MESSAGE_USERNAME_INVALID_CANNOT_ALL_NUMBER = "用户名不可全部为数字";

	public static final String MESSAGE_CANNOT_PROJECT_TEAM_HAS_PROJECT = "，存在相关的项目，不可删除";

	public static final String MESSAGE_QUESTION_DELETE = "\n删除后不可恢复，您确认要删除吗？";

	public static final String MESSAGE_QUESTION_NO_PROJECT_TEMPLATE_CONTINUE = "您没有为这个项目指定项目模板，这样系统就无法为您自动生成项目工作分解结构和工作交付物。现在保存，您将只能通过手工定义工作分解结构和工作交付物。\n继续保存吗？";

	public static final String MESSAGE_QUESTION_REMOVE_TASK = "\n任务删除后不可恢复，您确认要删除这个任务吗？";

	public static final String MESSAGE_QUESTION_REMOVE_DOC = "\n文档删除后不可恢复，您确认要删除这个文档吗？";

	public static final String MESSAGE_QUESTION_REMOVE_PROJECT = "\n项目删除后不可恢复，您确认要删除这个项目吗？";

	public static final String ACTION_MOVE_RIHGT_TOOLTIPS = "大纲下降一级";

	public static final String ACTION_MOVE_LEFT_TOOLTIPS = "大纲上升一级";

	public static final String ACTION_MOVE_UP_TOOLTIPS = "向上移动";

	public static final String ACTION_MOVE_DOWN_TOOLTIPS = "向下移动";

	public static final String ACTION_CONTROL_TOOLTIPS = "任务过程控制";

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

	public static final String ACTION_ASSIGNMENT_TOOLTIPS = "指派工作参与者";

	public static final String TEXT_AUTO_ASSIGNMENT = "自动指派工作参与者";

	public static final String TEXT_OPEN_ASSIGNMENT = "工作指派";

	public static final String TEXT_WORKRESOURCE = "工作参与者";

	public static final String MESSAGE_INVALID_EMAIL_ADDRESS = "您需要输入一个正确的邮箱地址";

	public static final String VIEW_PROJECT_FOLDER_NAVIGATOR = "com.sg.cpm.view.projectdocument";

	public static final String VIEW_ORG_FOLDER_NAVIGATOR = "com.sg.cpm.view.orgdocument";

	public static final String MESSAGE_CANNOT_WORK_DEL = "您不能删除一个工作的交付文档";

	public static final String TEXT_REMOVE_FOLDER = "删除目录";

	public static final String MESSAGE_QUESTION_DELETE_FOLDER_MOVE_DOC = "删除目录后，为了避免目录下的文档的丢失，他们将移动到顶级目录下。\n您确认要删除选中的目录吗？";

	public static final String TEXT_MOVE = "移动";

	public static final String MESSAGE_CANNOT_MOVE_TO_ANOTHER_ROOT = "您不能将这个目录或文档移动到另一个根目录下。";

	public static final String MESSAGE_CANNOT_MOVE_TO_FILE = "您不能把目录或者文档移动到一个文件下。";

	public static final String MESSAGE_CANNOT_MOVE_TO_CHILD = "您不能把一个目录或者文件移动到它的下级目录上";

	public static final String MESSAGE_CHANGE_FBS = "改变文件夹的结构";

	public static final String MESSAGE_NOT_AUTH = "您还没有得到必要的权限进行这个操作：";

	public static final String TEXT_IMPORT_PROJECT_TEMPLATE = "选择项目模板导入...";

	public static final String TEXT_READY_BOX = "准备/进行";

	public static final String TEXT_PROCESS_BOX = "进行";

	public static final String TEXT_PAUSE_BOX = "暂停";

	public static final String TEXT_CLOSENCANCEL_BOX = "完成/取消";

	public static final String TEXT_SETPARTICIPAIED = "设置资源";

	public static final String TEXT_WARNING_PARTICIPAIEDEXSIT = "该用户已经是当前任务的资源。\n您不需要重复设置。";

	public static final String TEXT_WARNING_PARTICIPAIED_ISPM = "该用户已经是当前任务的负责人。\n他可以具有该任务更多的操作，您不需要设置他为本任务的资源。";

	public static final String TEXT_FINISHWORK = "完成工作";

	public static final String TEXT_STARTWORK = "开始工作";

	public static final String TEXT_STARWORK = "标记工作";

	public static final String TEXT_READWORK = "标记已读";

	public static final String TEXT_CANCEL_SORTING = "取消所有的排序";

	public static final String VIEW_MYWORKS_WORKSINBOX = "com.sg.cpm.view.workinbox";

	public static final String VIEW_MYWORKS_RECYCLEBIN = "com.sg.cpm.view.work.recyclebin";

	public static final String MESSAGE_CANNOT_REMOVE_DOCUMENT_WHEN_TASK_FINISHED = "您现在不能删除这个文档，这个文档的任务已经完成。";

	public static final String MESSAGE_CANNOT_REMOVE_DOCUMENT_WHEN_NOT_CHARGER = "您不是这个工作的负责人，如果确实需要删除这个文档，请您告知工作负责人进行删除。";

	public static final String MESSAGE_CANNOT_REMOVE_DOCUMENT_UNKNOWN = "抱歉，系统无法获取您准备删除的文档的工作信息，无法执行删除操作。";

	public static final String MESSAGE_IMPORT_PROJECT_TEMPLATE_FINISHED = "系统已经按照您选择的项目模板建立了项目工作分解结构。";

	public static final String MESSAGE_AUTO_ASSIGNMENT_FINISHED = "系统已经按照您工作的指派规则和项目团队进行了任务自动分配。";

	public static final String MESSAGE_CANNOT_SETUSER_UNDER_AUSER = "您不可以把一个用户放置在另一个用户下。";

	public static final String TEXT_ORGEDIT = "编辑组织结构";

	public static final String MESSAGE_HAVE_A_SAME_USER_UNDER_A_ORG = "您选择的用户已经在组织下了。";

	public static final String MESSAGE_AUTO_SET_ADMIN_USER = "当前的站点下没有查找到admin超级用户，系统将为您设置一个名为admin的超级用户，密码为1，请注意保存";

	public static final String ONLY_MOVE_USERFROMUSERLIST = "抱歉，您只能从用户列表中向组织添加用户。\n如果您希望改变用户所属的组织，请先从原组织中移除这个用户，然后再添加。";

	public static final String TEXT_SITE_ORG = "站点和组织";

	public static final String TEXT_USERANDROLE = "用户和角色";

	public static final String TEXT_AUTHLIST = "设置的权限";

	public static final String TEXT_SITE_ELEMENT = "站点";

	public static final String TEXT_ORG_ELEMENT = "组织";

	public static final String TEXT_ROLE_ELEMENT = "角色";

	public static final String TEXT_USER_ELEMENT = "用户";

	public static final String TEXT_AUTH_SETTING = "权限设置";

	public static final String MESSAGE_AUTH_HAVE_SETTED = "您已经为当前的角色或用户设置了这个权限。权限设置不能重复。";

	public static final String MESSAGE_CANNOT_DELETE_PROJECT_NOT_INIT_OR_READY = "项目一旦启动就无法删除了。您无法执行这样的删除操作。";

	public static final String MESSAGE_CANNOT_DELETE_PROJECT_NOT_AUTH = "只有项目所在组织的项目管理员和这个项目的创建者才具有删除这个项目的权限。\n如果您希望继续操作，请联系权限管理员授予您必要的权限或者直接请组织的项目管理员代劳。";

	public static final String MESSAGE_CANNOT_CONTROL_PROJECT_PROCESS = "只有项目所在组织的项目管理员和项目经理才能对项目进行进程控制。\n如果您希望继续操作，请联系权限管理员授予您必要的权限。";

	public static final String TEXT_PROJECT_CONTROL = "项目进程控制";

	public static final String MESSAGE_USERID_INVALID_ALL_NUMBER = "用户ID必须全部是数字";

	public static final String MESSAGE_EMAIL_DUPLICATED = "邮箱已被使用";

	public static final String TEXT_CREATE_WORK = "创建工作";

	public static final String MESSAGE_START_WORK_IMMEDIETELY = "您是否需要是否立即启动这个工作？";

	public static final String TEXT_WORKTEMPLATE = "工作模板及参与者";

	public static final String TEXT_DOCUMENT = "文档";

	public static final String TEXT_WORKTEMPLATE_DELIVERY = "交付文档";

	public static final String TEXT_WORKTEMPLATE_WORKFLOW = "流程参与者";

	public static final String VIEWER_TREE_ORG_TEAM = "com.sg.admin.org2.tree";

	public static final String VIEWER_TABLE_WORKTEAMPLATE = "com.sg.admin.table.queryWorkTemplate";

	public static final String TEXT_EDIT_WORK_TEMPLATE = "编辑工作定义";

	public static final String TEXT_REMOVE_DOC_TEMPLATE = "删除交付文档定义";

	public static final String TEXT_EDIT_DOC_TEMPLATE = "编辑交付文档定义";

	public static final String TEXT_PARTICIPAIED = "工作参与者";

	public static final String TEXT_ADD_PATICIPATE = "添加参与者";

	public static final String TEXT_REMOVE_PATICIPATE = "删除参与者";

	public static final String TEXT_WORKFLOWSELETE = "选择流程";

	public static final String MESSAGE_OVERIDE_WORKFLOWDEFINITION = "您已经为当前的任务模板预定义了工作流，如果重新选择，您对该任务预定义的工作流以及活动参与的信息都会更新。\n您希望更改这个任务的流程定义吗？";

	public static final String MESSAGE_OVERIDE_WORKFLOWDEFINITION_WITHNULL = "您没有选择任何流程，需要清空原有预定义的工作流吗？";

	public static final String TEXT_UNAVILABLE_PROCESS_DEFINITION = "流程定义不可用";

	public static final String TEXT_NOTNEEDASSIGN = "";

	public static final String TEXT_NEEDASSIGN = "";

	public static final String TEXT_ACTIVATE_TEMPLATE = "启用/停用模板";

	public static final String EXCEPTION_UNCOMPLETE_ASSIGNMENT = "您选择的这个任务已经设定了工作流，可是这个工作流的有些活动需要指定执行者，没有确定执行者的流程将无法正常运行。\n请在流程参与者页面中完成执行者的定义后再重新启用工作模板。";

	public static final String TEXT_RULE_ASSIGNMENT = "[承担者规则定义]";

	public static final String MESSAGE_NEED_SELECT_TEMPLATE = "您必须先选择一个工作的模板";

	public static final String MESSAGE_TEMPLATE_HAS_WFDEF = "您选择的工作模板已经预设了工作流，工作的参与者取决与流程上的设置。\n您无法更改这些设置。";

	public static final String TEXT_STARTWFWORK = "开始流程任务";

	public static final String TEXT_FINISHWFWORK = "完成流程任务";

	public static final String WELCOME = "欢迎 ";

	public static final String TEXT_ONLYMYPROCESS = "只显示我的流程任务";

	public static final String TEXT_ALLPROCESS = "显示我的工作的所有流程任务";

	public static final String TEXT_DYNAMIC_ASSIGNMENT = "[流程中动态指定]";

	public static final String TEXT_VALIDATION_TASK = "流程任务检查";

	public static final String MESSAGE_CANNOT_CONTROL_PROJECT_TEAM = "只有本项目所在组织的项目管理员可以编辑项目团队";

}
