package com.sg.db;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.sg.db.expression.DBExpression;
import com.sg.db.expression.IConfConstants;
import com.sg.db.expression.insert.InsertExpression;
import com.sg.db.expression.query.QueryExpression;
import com.sg.db.expression.remove.RemoveExpression;
import com.sg.db.expression.update.UpdateExpression;
import com.sg.db.model.CascadeObject;

public class DBActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private String host;
	private int port;
	private String dbname;
	private Mongo mongo;
	private DB db;

	private Map<String, IConfigurationElement> queryExpMap = new HashMap<String, IConfigurationElement>();
	private Map<String, IConfigurationElement> insertExpMap = new HashMap<String, IConfigurationElement>();
	private Map<String, IConfigurationElement> modifyExpMap = new HashMap<String, IConfigurationElement>();
	private Map<String, IConfigurationElement> removeExpMap = new HashMap<String, IConfigurationElement>();

	private Map<String, IConfigurationElement> cascadeMap = new HashMap<String, IConfigurationElement>();
//	private String dbvault;

	private static DBActivator plugin;

	public static final String PLUGIN_ID = "com.sg.db"; //$NON-NLS-1$
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		DBActivator.context = bundleContext;
		plugin = this;
		loadDatabase();
		loadConfigruation(IConfConstants.EXTPOINT_NAME_QUERY, IConfConstants.ELEMENT_NAME_QUERY, queryExpMap);
		loadConfigruation(IConfConstants.EXTPOINT_NAME_INSERT, IConfConstants.ELEMENT_NAME_INSERT, insertExpMap);
		loadConfigruation(IConfConstants.EXTPOINT_NAME_MODIFY, IConfConstants.ELEMENT_NAME_MODIFY, modifyExpMap);
		loadConfigruation(IConfConstants.EXTPOINT_NAME_REMOVE, IConfConstants.ELEMENT_NAME_REMOVE, removeExpMap);

		loadCascade();
	}

	private void loadConfigruation(String extPointName, String elementName, Map<String, IConfigurationElement> map) {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, extPointName);
		if (ePnt == null)
			return;

		String id;
		IConfigurationElement[] confs;

		IExtension[] exts = ePnt.getExtensions();

		for (IExtension ext : exts) {
			confs = ext.getConfigurationElements();
			for (IConfigurationElement conf : confs) {
				if (elementName.equals(conf.getName())) {
					try {
						id = conf.getAttribute(IConfConstants.ATT_ID);
						map.put(id, conf);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void loadCascade() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, IConfConstants.EXTPOINT_NAME_CASCADE);
		if (ePnt == null)
			return;

		IExtension[] exts = ePnt.getExtensions();

		IConfigurationElement[] confs;

		for (IExtension ext : exts) {
			confs = ext.getConfigurationElements();
			for (IConfigurationElement conf : confs) {
				if (IConfConstants.ELEMENT_NAME_SINGLEOBJECT.equals(conf.getName())) {
					cascadeMap.put(conf.getAttribute(IConfConstants.ATT_ID), conf);
				}
			}
		}
	}

	private void loadDatabase() throws IOException {
		InputStream is = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "/conf/db.properties");
			is = new BufferedInputStream(fis);
			Properties dbProps = new Properties();
			dbProps.load(is);
			host = dbProps.getProperty("host");
			port = Integer.parseInt(dbProps.getProperty("port"));
			dbname = dbProps.getProperty("name");

			MongoOptions options = new MongoOptions();
			options.autoConnectRetry = "true".equalsIgnoreCase(dbProps.getProperty("options.autoConnectRetry"));
			options.connectionsPerHost = Integer.parseInt(dbProps.getProperty("options.connectionsPerHost"));
			options.maxWaitTime = Integer.parseInt(dbProps.getProperty("options.maxWaitTime"));
			options.socketTimeout = Integer.parseInt(dbProps.getProperty("options.socketTimeout"));
			options.connectTimeout = Integer.parseInt(dbProps.getProperty("options.connectTimeout"));
			options.threadsAllowedToBlockForConnectionMultiplier = Integer.parseInt(dbProps
					.getProperty("options.threadsAllowedToBlockForConnectionMultiplier"));

			ServerAddress add = new ServerAddress(host, port);

//			dbvault = dbProps.getProperty("vault");

			mongo = new Mongo(add, options);
			db = mongo.getDB(dbname);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
			if(is!=null)
				is.close();
		}
		Assert.isNotNull(db, "db is null! please check your mongo server and try again!");
	}
//
//	public static String getVault() {
//		return plugin.dbvault;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DBActivator.context = null;
		mongo.close();
		plugin = null;
	}

	public DB getDB() {
		return db;
	}

	public static DB getDatabase() {
		return plugin.getDB();
	}

	public static DBCollection getDefaultDBCollection(String collectionName) {
		return getDatabase().getCollection(collectionName);
	}

	public static CascadeObject getCascadeObject(String id) {
		IConfigurationElement conf = plugin.cascadeMap.get(id);
		return new CascadeObject(conf);
	}

	public static QueryExpression getQueryExpression(String id) {
		IConfigurationElement conf = plugin.queryExpMap.get(id);
		return new QueryExpression(conf);
	}

	public static UpdateExpression getUpdateExpression(String id) {
		IConfigurationElement conf = plugin.modifyExpMap.get(id);
		return new UpdateExpression(conf);
	}

	public static InsertExpression getInsertExpression(String id) {
		IConfigurationElement conf = plugin.insertExpMap.get(id);
		return new InsertExpression(conf);
	}

	public static RemoveExpression getRemoveExpression(String id) {
		IConfigurationElement conf = plugin.removeExpMap.get(id);
		return new RemoveExpression(conf);
	}

	public static DBExpression getExpression(String expId) {
		if (plugin.queryExpMap.containsKey(expId)) {
			return getQueryExpression(expId);
		}

		if (plugin.modifyExpMap.containsKey(expId)) {
			return getUpdateExpression(expId);
		}

		if (plugin.insertExpMap.containsKey(expId)) {
			return getInsertExpression(expId);
		}

		if (plugin.removeExpMap.containsKey(expId)) {
			return getRemoveExpression(expId);
		}
		return null;
	}

}
