package com.sg.user;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.sg.db.DBActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class UserService extends AbstractUIPlugin {

	public static final String CPMSYSTEM_FILE_NAMESPACE = "cpmsystem";

	public static final String BIGLOGO_FILENAME = "biglogo.png";

	public static final String APPNAME_FILENAME = "app.png";
	// The plug-in ID
	public static final String PLUGIN_ID = "com.sg.user"; //$NON-NLS-1$

	public static final Object SUPERUSER_DESC = "admin";

	public static final Object SUPERUSER_NAME = "”Ú≥¨º∂”√ªß";

	public static final String SUPERUSER_EMAIL = "admin@yaozheng.com.cn";

	public static final Object SUPERUSER_ID = "000000";

	public static final Object SUPERUSER_PASSWORD = "1";

	public static final String HEAD_FILENAME = "headerlogo.png";


	// The shared instance
	private static UserService plugin;

	public static final String IMAGE_LOGO_XXL = "logo_xxl.png";

	public static final String IMAGE_SPLIT = "splittrim.png";

	public static final String IMAGE_LOGIN_OK = "login_ok.png";

	public static final String IMAGE_HEADLOGO = "logo_sm.png";

	public Image appLogo;

	public Image productLogo;

	public String productInformation;

	private ImageDescriptor productLogoDesc;

	private ImageDescriptor appLogoDesc;

	private static boolean loaded = false;

	private void loadConfig() {
		if (loaded) {
			return;
		}

		IExtensionPoint ePnt = Platform.getExtensionRegistry()
				.getExtensionPoint("com.sg.user", "appLoginPage");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("appLoginPage".equals(confs[j].getName())) {
					try {
						productLogoDesc = AbstractUIPlugin
								.imageDescriptorFromPlugin(
										confs[0].getNamespaceIdentifier(),
										confs[0].getAttribute("productLogo"));
					} catch (Exception e) {
					}

					try {
						appLogoDesc = AbstractUIPlugin
								.imageDescriptorFromPlugin(
										confs[0].getNamespaceIdentifier(),
										confs[0].getAttribute("appLogo"));
					} catch (Exception e) {
					}
					productInformation = confs[0].getAttribute("information");
					return;
				}
			}
		}
		return;
	}

	/**
	 * The constructor
	 */
	public UserService() {
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
		loadConfig();
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
	public static UserService getDefault() {
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

	protected void initializeImageRegistry(ImageRegistry reg) {
		regImage(reg, IMAGE_LOGO_XXL);
		regImage(reg, IMAGE_SPLIT);
		regImage(reg, IMAGE_LOGIN_OK);
		regImage(reg, IMAGE_HEADLOGO);
		
		super.initializeImageRegistry(reg);
	}

	private void regImage(ImageRegistry reg, String key) {
		ImageDescriptor imgd = AbstractUIPlugin.imageDescriptorFromPlugin(
				PLUGIN_ID, "image/" + key);
		reg.put(key, imgd);
	}

	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}

	public Image getProductLogo() {
		InputStream is = getInputSteamFromGridFSByFileName(
				CPMSYSTEM_FILE_NAMESPACE, BIGLOGO_FILENAME);
		if (is != null) {
			try{
				productLogo = new Image(null, is);
			}catch(Exception e){
				productLogo = null;
			}
		} 
		if (productLogo == null) {
			if (productLogoDesc != null) {
				productLogo = productLogoDesc.createImage();
			}
			if (productLogo == null) {
				productLogo = getImage(IMAGE_LOGO_XXL);
			}
		}
		return productLogo;
	}

	public Image getAppLogo() {
		InputStream is = getInputSteamFromGridFSByFileName(
				CPMSYSTEM_FILE_NAMESPACE, APPNAME_FILENAME);
		if (is != null) {
			
			try{
				appLogo = new Image(null, is);
			}catch(Exception e){
				appLogo = null;
			}
		}
		if (appLogo == null && appLogoDesc != null) {
			appLogo = appLogoDesc.createImage();
		}
		return appLogo;
	}

	public Image getHeaderLogo() {
		InputStream is = getInputSteamFromGridFSByFileName(
				UserService.CPMSYSTEM_FILE_NAMESPACE, UserService.HEAD_FILENAME);
		Image headLogo = null;
		if (is != null) {
			try{
				headLogo = new Image(null, is);
			}catch(Exception e){
			}
		}
		if(headLogo ==null){
			headLogo = getImage(IMAGE_HEADLOGO);
		}
		return headLogo;
	}
	
	public String getProductInformation() {
		return productInformation;
	}

	public static InputStream getInputSteamFromGridFSByFileName(
			String namespace, String fileName) {

		GridFS gridfs = new GridFS(DBActivator.getDatabase(), namespace);
		GridFSDBFile result = gridfs.findOne(fileName);
		if (result == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			result.writeTo(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

}
