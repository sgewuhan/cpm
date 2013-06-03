package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;

import com.sg.db.Util;
import com.sg.widget.viewer.IDeleteHandler;
import com.sg.widget.viewer.IViewerPopupCreator;

public class ViewerConfiguration {
	
	protected static final String ATT_CONTENT_PROVIDER = "contentProvider";

	protected static final String ATT_DELETE_HANDLER = "deleteHandler";

	protected static final String ATT_DOUBLE_CLICK_LISTENER = "doubleClickListener";

	protected static final String ATT_POPUP = "popup";

	protected static final String ELEMENT_COLUMN = "column";

	protected static final String ATT_BINDING_PART_ID = "bindingPartId";

	protected static final String ATT_USER_LAZY_CONTENT_PROVIDER = "userLazyContentProvider";

	protected static final String ATT_MULTI_SELECTION = "multiSelection";

	protected static final String ATT_USE_HASHLOOKUP = "useHashlookup";

	protected static final String ATT_LINE_VISIABLE = "lineVisiable";

	protected static final String ATT_HEADER_VISIABLE = "headerVisiable";

	protected static final String ATT_QUERY = "query";

	protected static final String VALUE_TRUE = "true";

	protected static final String VALUE_FALSE = "false";

	protected static final String ATT_ID = "id";

	protected static final String ATT_MARKUP_ENABLED = "markupEnabled";
	
	protected static final String ATT_CUSTOM_ITEM_HEIGHT = "customItemHeight";
	
	protected String id;

	protected boolean headerVisiable;

	protected boolean lineVisiable;

	protected boolean useHashlookup;

	protected boolean multiSelection;

	protected String bindingPartId;

	protected List<ColumnConfiguration> columnsConfigurations = new ArrayList<ColumnConfiguration>();

	protected IConfigurationElement ce;

	protected boolean userLazyContentProvider;

	protected String query;
	
	protected boolean markupEnabled;
	
	protected int itemHeight = 0;

	public ViewerConfiguration(IConfigurationElement ce) {
		this.ce = ce;
		id = ce.getAttribute(ATT_ID);
		headerVisiable = !VALUE_FALSE.equals(ce
				.getAttribute(ATT_HEADER_VISIABLE));
		lineVisiable = !VALUE_FALSE.equals(ce.getAttribute(ATT_LINE_VISIABLE));
		useHashlookup = VALUE_TRUE.equals(ce.getAttribute(ATT_USE_HASHLOOKUP));
		multiSelection = VALUE_TRUE
				.equals(ce.getAttribute(ATT_MULTI_SELECTION));
		userLazyContentProvider = VALUE_TRUE.equals(ce
				.getAttribute(ATT_USER_LAZY_CONTENT_PROVIDER));
		bindingPartId = ce.getAttribute(ATT_BINDING_PART_ID);
		query = ce.getAttribute(ATT_QUERY);

		IConfigurationElement[] colces = ce.getChildren(ELEMENT_COLUMN);
		for (int i = 0; i < colces.length; i++) {
			ColumnConfiguration cc = new ColumnConfiguration(colces[i]);
			columnsConfigurations.add(cc);
		}
		
		markupEnabled = VALUE_TRUE.equals(ce.getAttribute(ATT_MARKUP_ENABLED));
		
		String strItemHeight = ce.getAttribute(ATT_CUSTOM_ITEM_HEIGHT);
		
		if(!Util.isNullorEmpty(strItemHeight)){
			try{
				itemHeight = Integer.parseInt(strItemHeight);
			}catch(Exception e){
			}
		}
	}

	public String getQuery() {
		return query;
	}

	public String getId() {
		return id;
	}

	public boolean isHeaderVisiable() {
		return headerVisiable;
	}

	public boolean isLineVisiable() {
		return lineVisiable;
	}

	public boolean isUseHashlookup() {
		return useHashlookup;
	}
	
	public boolean isMarkupEnabled() {
		return markupEnabled;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public IViewerPopupCreator getPopup() {
		try {
			return (IViewerPopupCreator) ce
					.createExecutableExtension(ATT_POPUP);
		} catch (CoreException e) {
		}
		return null;
	}

	public List<ColumnConfiguration> getColumnsConfigurations() {
		return columnsConfigurations;
	}

	public IDoubleClickListener getDoubleClickListener() {
		try {
			return (IDoubleClickListener) ce
					.createExecutableExtension(ATT_DOUBLE_CLICK_LISTENER);
		} catch (CoreException e) {
		}
		return null;
	}

	public boolean isMultiSelection() {
		return multiSelection;
	}

	public IDeleteHandler getDeleteHandler() {
		try {
			return (IDeleteHandler) ce
					.createExecutableExtension(ATT_DELETE_HANDLER);
		} catch (CoreException e) {
		}
		return null;
	}

	public IContentProvider getContentProvider() {
		try {
			return (IContentProvider) ce
					.createExecutableExtension(ATT_CONTENT_PROVIDER);
		} catch (CoreException e) {
		}
		return null;
	}

	public boolean isUserLazyContentProvider() {
		return userLazyContentProvider;
	}

	public String getBindingPartId() {
		return bindingPartId;
	}
}
