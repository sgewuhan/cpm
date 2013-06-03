package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.sg.widget.editor.IEditorPageHeadDelegator;
import com.sg.widget.editor.IPageDelegator;
import com.sg.widget.editor.ISingleObjectEditorInput;

public class PageConfiguration extends Configuration {

	private String title;
	private String description;
	private String imagePath;
	private List<PageColumnConfiguration> columns = new ArrayList<PageColumnConfiguration>();
	private IEditorPageHeadDelegator pageHeadDelegator;
	private String id;

	public PageConfiguration(IConfigurationElement ce, EditorConfiguration ec) {
		super(ce);
		this.id = ce.getAttribute("id");
		this.title = ce.getAttribute("title");
		this.description = ce.getAttribute("description");
		this.imagePath = ce.getAttribute("image");

		IConfigurationElement[] children = ce.getChildren("pagecolumn");
		for (int i = 0; i < children.length; i++) {
			columns.add(new PageColumnConfiguration(children[i],ec));
		}
		try {
			pageHeadDelegator = (IEditorPageHeadDelegator) ce.createExecutableExtension("pageHeadDelegator");
		} catch (CoreException e) {
		}
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getDescription(ISingleObjectEditorInput input) {
		if (pageHeadDelegator != null)
			return pageHeadDelegator.getDescription(this, input);
		return getDescription();
	}

	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(getConfigurationElement().getNamespaceIdentifier(), imagePath);
	}

	public ImageDescriptor getImageDescriptor(ISingleObjectEditorInput input) {
		if (pageHeadDelegator != null)
			return pageHeadDelegator.getImageDescriptor(this, input);
		return getImageDescriptor();
	}

	public List<PageColumnConfiguration> getColumns() {
		return columns;
	}

	public String getId() {
		return id;
	}

	public IPageDelegator getPageDelegator() {
		try {
			return (IPageDelegator) configuration.createExecutableExtension("pageDelegator");
		} catch (CoreException e) {
		}
		return null;
	}

}
