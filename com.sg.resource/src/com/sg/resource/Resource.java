package com.sg.resource;

import java.io.InputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Resource extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.sg.resource"; //$NON-NLS-1$

	// The shared instance
	private static Resource plugin;

	public static final String TASK16 = "m_task16.gif";

	public static final String PROJECT16 = "m_prj16.gif";

	public static final String ORG16 = "m_org16.png";

	public static final String TASK_DOC_FILTER32 = "v_docs32.png";

	public static final String TASK_DOC_FILTER_DISABLED32 = "v_docs_filterd32.png";

	public static final String USER16 = "m_user16.gif";

	public static final String USER_D16 = "m_user_d16.gif";

	public static final String TEMPLATE16 = "m_template16.gif";

	public static final String WORKDELIVERY16 = "m_workdelivery16.gif";

	public static final String WORKFLOW16 = "m_workflow16.gif";

	public static final String DOC16 = "m_doc16.gif";

	public static final String CREATE_PROJECTTEMPLATE32 = "v_proj_temp_create32.png";

	public static final String EDIT_PROJECTTEMPLATE32 = "v_proj_temp_edit32.png";

	public static final String REMOVE_PROJECTTEMPLATE32 = "v_proj_temp_remove32.png";

	public static final String CREATE_DELIVERY32 = "v_delivery_create32.png";

	public static final String EDIT_PROP32 = "v_edit32.png";

	public static final String CREATE_WORK32 = "v_task_create32.png";

	public static final String REMOVE32 = "v_delete32.png";

	public static final String CREATE_TEAM32 = "v_create_org32.png";

	public static final String CREATE_ROLE32 = "v_create_role32.png";

	public static final String ROLE16 = "m_role16.gif";

	public static final String TEAM16 = "m_team16.gif";

	public static final String SITE16 = "m_site16.gif";

	public static final String SITE_SHARED16 = "m_site_shared16.gif";

	public static final String PROJECT_TEAM16 = "m_prjteam16.png";

	public static final String CREATE_USER32 = "v_create_user32.png";

	public static final String ACTIVATE_USER32 = "v_activateuser32.png";

	public static final String DISACTIVATE_USER32 = "v_dis_act_user32.png";

	public static final String CREATE_SITE32 = "v_create_site32.png";

	public static final String ROOT_SITE16 = "m_rootsite16.gif";

	public static final String IMAGE_DEFAULT_USER64 = "p_default_user64.png";

	public static final String IMAGE_DEFAULT_USER_D64 = "p_default_user_d64.png";

	public static final String IMAGE_ACTIVE16 = "p_active16.png";

	public static final String IMAGE_DISACTIVE16 = "p_disactive16.png";

	public static final String IMAGE_BALL_GREEN16 = "ball_green.png";

	public static final String IMAGE_BALL_RED16 = "ball_red.png";

	public static final String IMAGE_BALL_YELLOW16 = "ball_yellow.png";

	public static final String IMAGE_CANCEL16 = "p_cancel16.png";

	public static final String IMAGE_CLOSE16 = "p_close16.png";

	public static final String IMAGE_PROCESS16 = "p_process16.png";

	public static final String IMAGE_PAUSE16 = "p_pause16.png";

	public static final String IMAGE_READY16 = "p_ready16.png";

	public static final String IMAGE_PROJECTFOLDER72 = "p_projectitem_doc72.png";

	public static final String START32 = "v_start32.png";

	public static final String STOP32 = "v_stop32.png";

	public static final String CLOSE32 = "v_close32.png";

	public static final String CANCEL32 = "v_cancel32.png";

	public static final String WORK_READY16 = "m_workready16.gif";

	public static final String WORK_PROCESS16 = "m_workprocess16.png";

	public static final String WORK_STOP16 = "m_workstop16.gif";

	public static final String WORK_CLOSE16 = "m_workclose16.png";

	public static final String WORK_CANCEL16 = "m_workcancel16.gif";

	public static final String ASSIGNMENT32 = "v_assinment32.png";

	public static final String AUTO_ASSIGNMENT32 = "v_auto_assinment32.png";

	public static final String CLEAN_ASSIGNMENT32 = "v_clean_assinment32.png";

	public static final String TB_SAVE32 = "tb_commit32.png";

	public static final String TB_SAVEALL32 = "tb_commitall32.png";

	public static final String TB_CLOSEALL32 = "tb_closeall32.png";

	public static final String V_UP32 = "v_arrow_up32.png";

	public static final String V_DOWN32 = "v_arrow_down32.png";

	public static final String V_LEFT32 = "v_arrow_left32.png";

	public static final String V_RIGHT32 = "v_arrow_right32.png";

	public static final String V_NAVICLOSE32 = "v_naviclose32.png";

	public static final String V_IMPORT_PROJECT32 = "v_proj_import32.png";
	public static final String IMAGE_ALPHA = "alpha.gif";

	// public static final String IMAGE_READY32 = "image_task_ready32.png";
	// public static final String IMAGE_PROCESS32 = "image_task_process32.png";
	// public static final String IMAGE_PAUSE32 = "image_task_pause32.png";
	// public static final String IMAGE_CLOSE32 = "image_task_finish32.png";
	// public static final String IMAGE_CANCEL32 = "image_task_cancel32.png";
	//
	// public static final String IMAGE_T_READY32 =
	// "image_team_task_ready32.png";
	// public static final String IMAGE_T_PROCESS32 =
	// "image_team_task_process32.png";
	// public static final String IMAGE_T_PAUSE32 =
	// "image_team_task_pause32.png";
	// public static final String IMAGE_T_CLOSE32 =
	// "image_team_task_finish32.png";
	// public static final String IMAGE_T_CANCEL32 =
	// "image_team_task_cancel32.png";

	public static final String IMAGE_DOCUMENT32 = "image_document32.png";

	public static final String V_FINISH32 = "v_finish32.png";

	public static final String V_MARKREAD32 = "v_markread32.png";

	public static final String V_MARKSTAR32 = "v_star32.png";

	public static final String V_UNMARKREAD32 = "v_unmarkread32.png";

	public static final String V_UNMARKSTAR32 = "v_unstar32.png";

	public static final String IMAGE_STAR16 = "v_star16.png";

	public static final String IMAGE_DEL16 = "v_del16.png";

	public static final String M_ARROW_UP24 = "m_arrow_up24.png";

	public static final String M_ARROW_DOWN24 = "m_arrow_down24.png";

	public static final String M_BLANK24 = "m_blank24.png";

	public static final String FILTER32 = "v_filter32.png";

	public static final String FILTER_DISABLE32 = "v_filter_d32.png";

	public static final String REMOVE_WORK32 = "v_task_remove32.png";

	public static final String REMOVE_DELIVERY32 = "v_remove_delivery32.png";

	public static final String REMOVE_USER32 = "v_remove_user32.png";

	public static final String WORKFLOW_REPO16 = "v_repository16.png";

	public static final String WORKFLOW32 = "v_workflow32.png";

	public static final String ACTVITI_1_16 = "m_activity16.gif";

	public static final String ACTVITI_2_16 = "m_activiti2_16.gif";

	public static final String ACTVITI_3_16 = "m_activiti3_16.gif";

	public static final String CONNECT32 = "v_connect32.png";

	public static final String DISCONNECT32 = "v_disconnect32.png";

	public static final String DISACTIVE16 = "p_disactive16.png";

	public static final String ACTIVE16 = "p_active16.png";

	public static final String IMAGE_WORK_READY16 = "m_workready16.png";

	public static final String IMAGE_WORK_PROCESS16 = "m_workprocess16.png";

	public static final String IMAGE_WORK_PAUSE16 = "m_workstop16.png";

	public static final String IMAGE_WORK_CLOSE16 = "m_workclose16.png";

	public static final String IMAGE_WORK_CANCEL16 = "m_workcancel16.png";

	public static final String IMAGE_WF_WORK16 = "image_wf_work16.png";

	public static final String IMAGE_TASK60 = "image_task60.png";

	public static final String IMAGE_PMTASK60 = "image_pm_task60.png";

	public static final String M_START32 = "m_start_wf32.png";

	public static final String M_COMPLETE32 = "m_complete_wf32.png";

	public static final String HEADER_LOGO = "logo_sm.png";

	public static final String ENT_LOGO = "tmtlogo.png";

	public static final String V_USERS32 = "v_role32.png";

	public static final String V_USER32 = "v_user32.png";
	
	public static final String BAR = "bar.png";
	
	public static final String PRG_EARLY = "m_early16.png";
	public static final String PRG_LATE = "m_late16.png";
	public static final String PRG_NORMAL = "m_normal16.png";
	public static final String PRG_VLATE = "m_vlate16.png";
	public static final String IMAGE_REFRESH96 = "refresh96.png";
	public static final String IMAGE_SCHEDUAL96 = "schedual96.png";


	protected void initializeImageRegistry(ImageRegistry reg) {

		regImage(reg, IMAGE_SCHEDUAL96);
		regImage(reg, IMAGE_REFRESH96);
		regImage(reg, IMAGE_ALPHA);
		regImage(reg, V_USERS32);
		regImage(reg, V_USER32);
		regImage(reg, ENT_LOGO);
		regImage(reg, HEADER_LOGO);
		regImage(reg, IMAGE_DEFAULT_USER64);
		regImage(reg, M_START32);
		regImage(reg, M_COMPLETE32);

		regImage(reg, CONNECT32);
		regImage(reg, DISCONNECT32);
		regImage(reg, DISACTIVE16);
		regImage(reg, ACTIVE16);

		regImage(reg, ACTVITI_1_16);
		regImage(reg, ACTVITI_2_16);
		regImage(reg, ACTVITI_3_16);
		regImage(reg, WORKFLOW32);
		regImage(reg, WORKFLOW_REPO16);
		regImage(reg, REMOVE_USER32);

		regImage(reg, REMOVE_DELIVERY32);

		regImage(reg, REMOVE_WORK32);

		regImage(reg, FILTER_DISABLE32);
		regImage(reg, FILTER32);

		regImage(reg, M_BLANK24);

		regImage(reg, M_ARROW_UP24);
		regImage(reg, M_ARROW_DOWN24);

		regImage(reg, V_MARKREAD32);
		regImage(reg, V_MARKSTAR32);
		regImage(reg, V_UNMARKREAD32);
		regImage(reg, V_UNMARKSTAR32);

		regImage(reg, V_FINISH32);

		regImage(reg, V_IMPORT_PROJECT32);

		regImage(reg, V_UP32);
		regImage(reg, V_DOWN32);
		regImage(reg, V_LEFT32);
		regImage(reg, V_RIGHT32);
		regImage(reg, V_NAVICLOSE32);

		regImage(reg, TB_SAVE32);
		regImage(reg, TB_SAVEALL32);
		regImage(reg, TB_CLOSEALL32);

		regImage(reg, TASK16);
		regImage(reg, PROJECT16);
		regImage(reg, TASK_DOC_FILTER32);
		regImage(reg, TASK_DOC_FILTER_DISABLED32);
		regImage(reg, ORG16);
		regImage(reg, USER16);
		regImage(reg, USER_D16);
		regImage(reg, TEMPLATE16);
		regImage(reg, WORKDELIVERY16);
		regImage(reg, WORKFLOW16);
		regImage(reg, DOC16);
		regImage(reg, CREATE_PROJECTTEMPLATE32);
		regImage(reg, EDIT_PROJECTTEMPLATE32);
		regImage(reg, REMOVE_PROJECTTEMPLATE32);
		regImage(reg, REMOVE_PROJECTTEMPLATE32);
		regImage(reg, CREATE_DELIVERY32);
		regImage(reg, CREATE_WORK32);
		regImage(reg, EDIT_PROP32);
		regImage(reg, REMOVE32);
		regImage(reg, CREATE_TEAM32);
		regImage(reg, CREATE_ROLE32);
		regImage(reg, CREATE_USER32);
		regImage(reg, ROLE16);
		regImage(reg, TEAM16);
		regImage(reg, SITE16);
		regImage(reg, SITE_SHARED16);
		regImage(reg, CREATE_SITE32);
		regImage(reg, ROOT_SITE16);
		regImage(reg, ACTIVATE_USER32);
		regImage(reg, DISACTIVATE_USER32);
		regImage(reg, PROJECT_TEAM16);

		regImage(reg, START32);
		regImage(reg, STOP32);
		regImage(reg, CANCEL32);
		regImage(reg, CLOSE32);

		regImage(reg, WORK_READY16);
		regImage(reg, WORK_PROCESS16);
		regImage(reg, WORK_STOP16);
		regImage(reg, WORK_CLOSE16);
		regImage(reg, WORK_CANCEL16);

		regImage(reg, ASSIGNMENT32);
		regImage(reg, AUTO_ASSIGNMENT32);
		regImage(reg, CLEAN_ASSIGNMENT32);

		super.initializeImageRegistry(reg);
	}

	/**
	 * The constructor
	 */
	public Resource() {

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
	}

	public InputStream getImageInputStream(String key) {

		InputStream is = Resource.class.getResourceAsStream(key);
		return is;
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
	public static Resource getDefault() {

		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {

		return getDefault().getImageRegistry().getDescriptor(key);
	}

	private void regImage(ImageRegistry reg, String key) {

		ImageDescriptor imgd = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "image/" + key);
		reg.put(key, imgd);
	}

	public static Image getImage(String key) {

		return getDefault().getImageRegistry().get(key);
	}
}
