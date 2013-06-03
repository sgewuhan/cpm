package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.sg.widget.editor.IEditorDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.export.IExportParameterProvider;
import com.sg.widget.editor.saveHandler.IEditorSaveHandler;

public class EditorConfiguration extends Configuration {

	private String id;

	private String name;

	private IEditorSaveHandler saveHandler;

	private List<PageConfiguration> pages = new ArrayList<PageConfiguration>();

	private String imagePath;

	private IEditorDelegator editorDelegator;

	private String titleToolTips;

	private String labelFieldName;

	private String collectionName;

	private boolean recreateable;

	private Set<FieldConfiguration> fields = new HashSet<FieldConfiguration>();

	private String exportType;

	private IExportParameterProvider exportParameter;

	public EditorConfiguration(IConfigurationElement ce) {

		super(ce);
		id = ce.getAttribute("id");
		name = ce.getAttribute("name");
		labelFieldName = ce.getAttribute("labelFieldName");
		collectionName = ce.getAttribute("collectionName");
		imagePath = ce.getAttribute("image");
		titleToolTips = ce.getAttribute("titleToolTips");
		if (titleToolTips == null)
			titleToolTips = name;
		IConfigurationElement[] pageces = ce.getChildren("basicpage");
		for (int i = 0; i < pageces.length; i++) {
			pages.add(new PageConfiguration(pageces[i], this));
		}
		try {
			saveHandler = (IEditorSaveHandler) ce.createExecutableExtension("saveHandler");
		} catch (CoreException e) {
		}
		try {
			editorDelegator = (IEditorDelegator) ce.createExecutableExtension("editorDelegator");
		} catch (CoreException e) {
		}
		recreateable = "true".equals(ce.getAttribute("recreateable"));
		exportType = ce.getAttribute("exportType");
		
		try {
			exportParameter = (IExportParameterProvider) ce.createExecutableExtension("exportParameter");
		} catch (CoreException e) {
		}
	}

	public void addField(FieldConfiguration fieldConfiguration) {

		fields.add(fieldConfiguration);
	}

	public String getId() {

		return id;
	}

	public String getName() {

		return name;
	}

	public String getName(ISingleObjectEditorInput input) {

		if (editorDelegator != null) {
			return editorDelegator.getName(this, input);
		} else {
			return getName();
		}
	}

	public List<PageConfiguration> getPages() {

		return pages;
	}

	public ImageDescriptor getImageDescription() {

		return AbstractUIPlugin.imageDescriptorFromPlugin(getConfigurationElement().getNamespaceIdentifier(), imagePath);
	}

	public ImageDescriptor getImageDescription(ISingleObjectEditorInput input) {

		if (editorDelegator != null) {
			return editorDelegator.getImageDescriptor(this, input);
		} else {
			return getImageDescription();
		}
	}

	public IEditorSaveHandler getSaveHandler() {

		return saveHandler;
	}

	public String getTitleToolTips(ISingleObjectEditorInput input) {

		if (editorDelegator != null) {
			return editorDelegator.getTitleToolTips(this, input);
		} else {
			return getTitleToolTips();
		}
	}

	public String getTitleToolTips() {

		return titleToolTips;
	}

	public String getLabelFieldName() {

		return labelFieldName;
	}

	public String getCollection() {

		return collectionName;
	}

	public boolean isRecreateable() {

		return recreateable;
	}

	public Set<FieldConfiguration> getSaveHistoryFields() {

		HashSet<FieldConfiguration> result = new HashSet<FieldConfiguration>();
		Iterator<FieldConfiguration> iter = fields.iterator();
		while (iter.hasNext()) {
			FieldConfiguration fe = iter.next();
			if (fe.isSaveHistory())
				result.add(fe);
		}
		return result;
	}

	public String getExportType() {

		return exportType;
	}

	
	public IExportParameterProvider getExportParameterProvider(){
		return exportParameter;
	}
	
}
