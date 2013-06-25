package com.sg.bpm.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class BPM implements BundleActivator {

	private static BundleContext context;

//	private int humanTaskHandlerPort;

	private BPMService bpmService;

	private String guvnorRoot;

	private HTService humanTaskService;

	private RuleService ruleService;

	private String localBPMPath;

	public static BundleContext getContext() {

		return context;
	}

	private static BPM bundle;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		BPM.context = bundleContext;
		bundle = this;
		loadConfiguration();
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");

		startBPMService(emf);

		startTaskService(emf);
		
		startRuleService();
		
	}

	private void startRuleService() {

		ruleService = new RuleService();
	}

	private void startBPMService(EntityManagerFactory emf) {

		bpmService = new BPMService(guvnorRoot,localBPMPath);
		bpmService.init(emf);
	}

	private void startTaskService(EntityManagerFactory emf) {

		humanTaskService = new HTService();
		humanTaskService.init(emf);

	}

	private void stopBPMService() {

		bpmService.stop();
	}

	private void stopTaskService() {

		humanTaskService.stop();
	}


	private void loadConfiguration() {

		InputStream is = null;
		FileInputStream fis = null;
		try {
			
			
			
			fis = new FileInputStream(System.getProperty("user.dir") + "/conf/bpm.properties");
			is = new BufferedInputStream(fis);
			Properties dbProps = new Properties();
			dbProps.load(is);
			guvnorRoot = dbProps.getProperty("guvnor.address");
			localBPMPath = dbProps.getProperty("bpmn.path");

			String jbpmDs = dbProps.getProperty("jbpm5.ds");
			String jbpmClassName = dbProps.getProperty("jbpm5.classname");
			int jbpmPoolSize = Integer.parseInt(dbProps.getProperty("jbpm5.poolsize"));
			boolean jbpmAllowLocalTransactions = "true".equals(dbProps.getProperty("jbpm5.localtransactions"));
			String jbpmUserName = dbProps.getProperty("jbpm5.user");
			String jbpmPassword = dbProps.getProperty("jbpm5.password");
			String jbpmURL = dbProps.getProperty("jbpm5.url");

			
			PoolingDataSource jbpmDataSource = new PoolingDataSource();
			jbpmDataSource.setUniqueName(jbpmDs);
			jbpmDataSource.setClassName(jbpmClassName);
			jbpmDataSource.setMaxPoolSize(jbpmPoolSize);
			jbpmDataSource.setAllowLocalTransactions(jbpmAllowLocalTransactions);
			jbpmDataSource.getDriverProperties().put("user", jbpmUserName);
			jbpmDataSource.getDriverProperties().put("password", jbpmPassword);
			jbpmDataSource.getDriverProperties().put("URL", jbpmURL);
			jbpmDataSource.init();
			
			//配置服务器本地知识库资源
			
			
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		stopBPMService();
		stopTaskService();

		context = null;
		bundle = null;
	}

	public static BPMService getBPMService() {
		return bundle.bpmService;
	}

	public static HTService getHumanTaskService() {
		return bundle.humanTaskService;
	}
	

	public static RuleService getRuleService() {

		return bundle.ruleService;
	}
	
	

}
