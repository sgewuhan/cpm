package com.sg.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.NavigatableViewConfiguration;
import com.sg.widget.configuration.QueryTableConfiguration;
import com.sg.widget.configuration.QueryTreeConfiguration;
import com.sg.widget.resource.Enumerate;

/**
 * The activator class controls the plug-in life cycle
 */
public class Widget extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.sg.widget"; //$NON-NLS-1$
	// The shared instance
	private static Widget plugin;

	private Map<String, Enumerate> enumerateMap = new HashMap<String, Enumerate>();

	private Map<String, QueryTableConfiguration> table_Id2Conf = new HashMap<String, QueryTableConfiguration>();
	private Map<String, String> table_bindingPartId2Id = new HashMap<String, String>();

	private Map<String, QueryTreeConfiguration> tree_Id2Conf = new HashMap<String, QueryTreeConfiguration>();
	private Map<String, String> tree_bindingPartId2Id = new HashMap<String, String>();

	private Map<String, EditorConfiguration> editor_Id2Conf = new HashMap<String, EditorConfiguration>();
	private Map<String, Set<EditorConfiguration>> editor_CollectionName2Conf = new HashMap<String, Set<EditorConfiguration>>();

	private Map<String, String> part2editor = new HashMap<String, String>();

	private Map<String, NavigatableViewConfiguration> naviPart_id2Conf = new HashMap<String, NavigatableViewConfiguration>();

	/**
	 * The constructor
	 */
	public Widget() {
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
		loadResource();
		loadEditorConfiguration();
		loadQueryTableViewerConfiguration();
		loadQueryTreeViewerConfiguration();
		loadPartEditable();
		loadNavigatablelTreeView();
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
	public static Widget getDefault() {
		return plugin;
	}

	// --------------------------------------加载配置----获取配置-------------------------------------------

	private void loadResource() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "resource");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("enumerate".equals(confs[j].getName())) {
					Enumerate element = new Enumerate(confs[j]);
					enumerateMap.put(element.getId(), element);
				}
			}
		}
	}

	public Enumerate getEnumerate(String id) {
		return enumerateMap.get(id);
	}

	private void loadEditorConfiguration() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "singleobjecteditor");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("editor".equals(confs[j].getName())) {
					EditorConfiguration element = new EditorConfiguration(confs[j]);
					editor_Id2Conf.put(element.getId(), element);

					String collection = element.getCollection();
					Set<EditorConfiguration> conflist = editor_CollectionName2Conf.get(collection);
					if (conflist == null) {
						conflist = new HashSet<EditorConfiguration>();
					}
					conflist.add(element);
					editor_CollectionName2Conf.put(collection, conflist);
				}
			}
		}
	}

	private void loadPartEditable() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "documentCreator");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("documentCreator".equals(confs[j].getName())) {
					part2editor.put(confs[j].getAttribute("partId"), confs[j].getAttribute("singleObjectEditorId"));
				}
			}
		}
	}

	private void loadNavigatablelTreeView() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "navigatableView");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("navigatableView".equals(confs[j].getName())) {
					naviPart_id2Conf.put(confs[j].getAttribute("partId"), new NavigatableViewConfiguration(confs[j]));
				}
			}
		}

	}

	public static EditorConfiguration getSingleObjectEditorConfiguration(String id) {
		return getDefault().editor_Id2Conf.get(id);
	}

	public static EditorConfiguration getSingleObjectEditorConfigurationByCollection(String collectionName) {
		Set<EditorConfiguration> set = getDefault().editor_CollectionName2Conf.get(collectionName);
		if (set != null)
			return set.iterator().next();
		return null;
	}

	public static Set<EditorConfiguration> listSingleObjectEditorConfigurationByCollection(String collectionName) {
		return getDefault().editor_CollectionName2Conf.get(collectionName);
	}

	private void loadQueryTableViewerConfiguration() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "querytableviewer");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("table".equals(confs[j].getName())) {
					QueryTableConfiguration qtc = new QueryTableConfiguration(confs[j]);
					String confId = qtc.getId();
					table_Id2Conf.put(confId, qtc);
					String bpid = qtc.getBindingPartId();
					if (bpid != null) {
						table_bindingPartId2Id.put(bpid, confId);
					}
				}
			}
		}
	}

	public static QueryTableConfiguration getQueryTableViewerConfiguration(String id) {
		return getDefault().table_Id2Conf.get(id);
	}

	@Deprecated
	public static String getQueryTableViewerConfigurationIdByBindingPartId(String partId) {
		return getDefault().table_bindingPartId2Id.get(partId);
	}

	public static NavigatableViewConfiguration getNavigatablePartConfigurationByPartId(String partId) {
		return getDefault().naviPart_id2Conf.get(partId);
	}

	private void loadQueryTreeViewerConfiguration() {
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, "querytreeviewer");
		if (ePnt == null)
			return;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("tree".equals(confs[j].getName())) {
					QueryTreeConfiguration qtc = new QueryTreeConfiguration(confs[j]);
					String confId = qtc.getId();
					tree_Id2Conf.put(confId, qtc);
					String bpid = qtc.getBindingPartId();
					if (bpid != null) {
						tree_bindingPartId2Id.put(bpid, confId);
					}
				}
			}
		}
	}

	public static String getQueryTreeViewerConfigurationIdByBindingPartId(String partId) {
		return getDefault().tree_bindingPartId2Id.get(partId);
	}

	public static QueryTreeConfiguration getQueryTreeViewerConfiguration(String confId) {
		return getDefault().tree_Id2Conf.get(confId);
	}

	/**
	 * use part conf directly!
	 * 
	 * @param partId
	 * @return
	 */
	@Deprecated
	public static String getPartCreator(String partId) {
		return getDefault().part2editor.get(partId);
	}

	// --------------------------------------初始化图象----获取图像-------------------------------------------

	protected void initializeImageRegistry(ImageRegistry reg) {
		
		regImage(reg, IWidgetImage.IMG_SETTING16);
		regImage(reg, IWidgetImage.IMG_ALPHA16);
		regImage(reg, IWidgetImage.IMG_FILTER16);
		regImage(reg, IWidgetImage.IMG_SEARCH16);
		regImage(reg, IWidgetImage.IMG_FILTERCLEAR16);
		regImage(reg, IWidgetImage.IMG_ALPHA16);

		regImage(reg, IWidgetImage.IMG_W_CANCEL32);
		regImage(reg, IWidgetImage.IMG_W_CLOSE32);
		regImage(reg, IWidgetImage.IMG_W_OK32);
		regImage(reg, IWidgetImage.IMG_DATETIME16);
		regImage(reg, IWidgetImage.IMG_DATETIME16);
		regImage(reg, IWidgetImage.IMG_CLEAR16);
		regImage(reg, IWidgetImage.IMG_SELECTOR16);
		regImage(reg, IWidgetImage.IMG_EXCEL32);
		regImage(reg, IWidgetImage.IMG_EXCEL32_D);
		regImage(reg, IWidgetImage.IMG_CHECKED);
		regImage(reg, IWidgetImage.IMG_UNCHECKED);
		regImage(reg, IWidgetImage.IMG_OPEN32);
		regImage(reg, IWidgetImage.IMG_OPEN32_D);
		regImage(reg, IWidgetImage.IMG_UPDATE32);
		regImage(reg, IWidgetImage.IMG_UPDATE32_D);
		regImage(reg, IWidgetImage.IMG_ADD16);
		regImage(reg, IWidgetImage.IMG_OPEN16);
		regImage(reg, IWidgetImage.IMG_DELETE16);

		super.initializeImageRegistry(reg);
	}

	private void regImage(ImageRegistry reg, String key) {
		ImageDescriptor imgd = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "image/" + key);
		reg.put(key, imgd);
	}

	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return getDefault().getImageRegistry().getDescriptor(key);
	}

}
