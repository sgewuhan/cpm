package com.sg.email;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.sg.email"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private String senderEmailAddress;

	private String senderPassword;

	private String smtpHost;

	private String smtpAuth;

	private String smtpPort;
	
	private boolean useSSL;

	private String senderAccount;

	/**
	 * The constructor
	 */
	public Activator() {
//		MailJob m = new MailJob("admin@yaozheng.com.cn","lt@yaozheng.com.cn","sfds","sdfds");
//		m.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		plugin = this;
		loadEmailConfiguration();
//		MailJob.SENDTEST();
	}

	private void loadEmailConfiguration() throws IOException {
		InputStream is = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "/conf/email.properties");
			is = new BufferedInputStream(fis);
			Properties emailProp = new Properties();
			emailProp.load(is);
			senderEmailAddress = emailProp.getProperty("sender.address");
			senderAccount = emailProp.getProperty("sender.account");
			senderPassword = emailProp.getProperty("sender.password");
			smtpHost = emailProp.getProperty("mail.smtp.host");
			smtpPort = emailProp.getProperty("mail.smtp.port");
			smtpAuth = emailProp.getProperty("mail.smtp.auth");
			useSSL = "true".equalsIgnoreCase(emailProp.getProperty("mail.smtp.useSSL"));
			if(senderAccount==null||senderAccount.isEmpty()){
				senderAccount = senderEmailAddress;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
			if(is!=null)
				is.close();
		}
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	

	public static String getSenderEmailAddress() {
		return plugin.senderEmailAddress;
	}

	public static String getSenderPassword() {
		return plugin.senderPassword;
	}

	public static String getSmtpHost() {
		return plugin.smtpHost;
	}

	public static String getSmtpAuth() {
		return plugin.smtpAuth;
	}
	
	public static String getPort(){
		return plugin.smtpPort;
	}
	
	public static boolean useSSL(){
		return plugin.useSSL;
	}

	public static String getSenderAccount(){
		return plugin.senderAccount;
	}
}
