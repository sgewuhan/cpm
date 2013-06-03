package com.sg.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mongodb.DBObject;
import com.sg.bpm.service.BPM;
import com.sg.bpm.service.HTService;
import com.sg.common.service.DocumentService;
import com.sg.common.service.OrganizationService;
import com.sg.common.service.WorkReminder;
import com.sg.common.service.WorkService;
import com.sg.common.service.WorkflowService;
import com.sg.common.workflow.TaskFormConfig;

public class BusinessService extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.sg.common"; //$NON-NLS-1$

	// The shared instance
	private static BusinessService plugin;

	private OrganizationService orgService;

	private DocumentService docService;

	private WorkService workService;

	private WorkflowService workflowService;

	private Map<String, TaskFormConfig> taskStartFormMap = new HashMap<String, TaskFormConfig>();

	private Map<String, TaskFormConfig> taskCompleteFormMap = new HashMap<String, TaskFormConfig>();

	// setting
	public int interval_work_refresh = 10;

	private int monthReportDate;

	private String jobTrigerTime;

	private ListenerList jobSettingChangeListener = new ListenerList();

	private int workRetrieveIntervalMinute;

	private String noticeHtml;

	private WorkReminder workReminder;

//	private static Logger LOGGER;

	/**
	 * The constructor
	 */
	public BusinessService() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
//		initLogService();
		initService();
		loadConfig();
		loadResource();
		startNoticeService();

	}

//	private void initLogService() {
//		InputStream is = null;
//		FileInputStream fis = null;
//		try {
//			fis = new FileInputStream(System.getProperty("user.dir")
//					+ "/conf/log.properties");
//			is = new BufferedInputStream(fis);
//			Properties appProps = new Properties();
//			appProps.load(is);
//			PropertyConfigurator.configure(appProps);
//			LOGGER = Logger.getLogger(BusinessService.class);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (fis != null)
//				try {
//					fis.close();
//				} catch (IOException e) {
//				}
//			if (is != null)
//				try {
//					is.close();
//				} catch (IOException e) {
//				}
//		}
//
//	}

	private void startNoticeService() {
		workReminder = new WorkReminder();
		workReminder.start();
	}

	private void loadResource() {
		noticeHtml = loadHtml("notice.html");
	}

	private String loadHtml(String filename) {
		StringBuffer stringBuffer = new StringBuffer();
		InputStream is = BusinessService.class.getResourceAsStream(filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String string;
		try {
			while ((string = in.readLine()) != null) {
				stringBuffer.append(string + "\n");
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 加载plugins配置
	 */
	private void loadConfig() {

		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "taskForm");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("taskForm".equals(confs[j].getName())) {
					TaskFormConfig element = new TaskFormConfig(confs[j]);
					if (element.isStartForm()) {
						taskStartFormMap.put(element.getTaskFormId(), element);
					} else if (element.isCompleteForm()) {
						taskCompleteFormMap.put(element.getTaskFormId(),
								element);
					}
				}
			}
		}

	}

	public TaskFormConfig getTaskCompleteFormConfig(String processDefinitionId,
			String taskName) {

		return taskCompleteFormMap.get(processDefinitionId + "@" + taskName);
	}

	public TaskFormConfig getTaskStartFormConfig(String processDefinitionId,
			String taskName) {

		return taskStartFormMap.get(processDefinitionId + "@" + taskName);
	}

	private void initService() {

		orgService = new OrganizationService();
		docService = new DocumentService();
		workService = new WorkService();
		workflowService = new WorkflowService();

		synchronizUserToBPM();
	}

	private void synchronizUserToBPM() {

		HTService s = BPM.getHumanTaskService();
		s.addParticipateUser("Administrator");

		// 将用户信息同步到流程管理器
		List<DBObject> users = orgService.getUserIdList();
		for (int i = 0; i < users.size(); i++) {
			DBObject usersItem = users.get(i);
			String id = (String) usersItem.get("uid");
			s.addParticipateUser(id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BusinessService getDefault() {

		return plugin;
	}

	public static OrganizationService getOrganizationService() {

		return plugin.orgService;
	}

	public static DocumentService getDocumentService() {

		return plugin.docService;
	}

	public static WorkService getWorkService() {

		return plugin.workService;
	}

	public static WorkflowService getWorkflowService() {

		return plugin.workflowService;
	}

	public void setWorkRefreshInterval(int second) {
		this.interval_work_refresh = second;
	}

	public int getWorkRefreshInterval() {
		return this.interval_work_refresh;
	}

	public int getMonthlyReportDate() {
		return monthReportDate == 0 ? 20 : monthReportDate;
	}

	public void setMonthlyReportDate(int date) {
		monthReportDate = date;
	}

	public String getJobTrigerTime() {
		return jobTrigerTime == null ? "4:00:00" : jobTrigerTime;
	}

	public void setJobTrigerTime(String jobTrigerTime) {
		this.jobTrigerTime = jobTrigerTime;
		Object[] listeners = jobSettingChangeListener.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IJobSettingChangeListener) listeners[i])
					.jobSettingChanged(jobTrigerTime);
		}
	}

	public void addJobSettingChangeListener(IJobSettingChangeListener listener) {
		this.jobSettingChangeListener.add(listener);
	}

	public void removeJobSettingChangeListener(
			IJobSettingChangeListener listener) {
		this.jobSettingChangeListener.remove(listener);
	}

	public int getWorkRetrieveInterval() {
		return workRetrieveIntervalMinute == 0 ? 10
				: workRetrieveIntervalMinute;
	}

	public void setWorkRetrieveInterval(int value) {
		workRetrieveIntervalMinute = value;

	}

	public static String getNoticeHtml() {
		return plugin.noticeHtml;
	}

//	public static void loginfor(Object message) {
//		if (LOGGER != null)
//			LOGGER.info(message);
//	}
//	
//	public static void logerror(Object message) {
//		if (LOGGER != null)
//			LOGGER.error(message);
//	}

}
