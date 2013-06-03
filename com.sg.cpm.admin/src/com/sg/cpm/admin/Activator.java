package com.sg.cpm.admin;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sg.common.db.IDBConstants;
import com.sg.db.DBActivator;
import com.sg.user.UserService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.sg.cpm.admin"; //$NON-NLS-1$

	
	// The shared instance
	private static Activator plugin;

	private String guvnorHost;

	private String workflowConsoleHost;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		loadConfiguration();
		initAdministratorUser();
		plugin = this;
	}

	private void loadConfiguration() {

		InputStream is = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "/conf/workflow.properties");
			is = new BufferedInputStream(fis);
			Properties dbProps = new Properties();
			dbProps.load(is);
			guvnorHost = dbProps.getProperty("host");
			workflowConsoleHost = dbProps.getProperty("console");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initAdministratorUser() {
		//检查系统有无admin用户
		DBCollection cUser = DBActivator.getDefaultDBCollection(IDBConstants.COLLECTION_USER);
		DBObject dbo = cUser.findOne(new BasicDBObject().append(IDBConstants.FIELD_DESC, IDBConstants.VALUE_USER_ADMIN));
		if(dbo==null){
			DBObject admin = new BasicDBObject()
				.append(IDBConstants.FIELD_DESC, UserService.SUPERUSER_DESC)
				.append(IDBConstants.FIELD_NAME, UserService.SUPERUSER_NAME)
				.append(IDBConstants.FIELD_EMAIL, UserService.SUPERUSER_EMAIL)
				.append(IDBConstants.FIELD_UID, UserService.SUPERUSER_ID)
				.append(IDBConstants.FIELD_ACTIVATE, true)
				.append(IDBConstants.FIELD_PASSWORD, UserService.SUPERUSER_PASSWORD);
			cUser.save(admin);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static Activator getDefault() {
		return plugin;
	}
	
	public static String getGuvnorHost(){
		if(plugin!=null){
			return plugin.guvnorHost;
		}
		return null;
	}

	
	public static String getWorkflowConsoleHost(){
		if(plugin!=null){
			return plugin.workflowConsoleHost;
		}
		return null;
	}

}
