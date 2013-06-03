package com.sg.widget.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.sg.widget.editor.field.IFieldTypeConstants;
import com.sg.widget.util.Util;
import com.sg.widget.viewer.sorter.AbstractColumnViewerSorter;
import com.sg.widget.viewer.sorter.IColumnSortor;

public class ColumnConfiguration {

	private String name;
	private String column;
	private int style;
	private ImageDescriptor imageDesc;
	private int width;
	private boolean moveable;
	private boolean resizable;
	private String toolTipText;
	private boolean sorter;
	private String type;
	private IConfigurationElement ce;
	

	public ColumnConfiguration(IConfigurationElement ce) {
		this.ce = ce;
		name = ce.getAttribute("name");
		column = ce.getAttribute("column");

		type = ce.getAttribute("type");
		
		String styleName = ce.getAttribute("style");
		if(Util.isNullOrEmptyString(styleName)){
			if(IFieldTypeConstants.FIELD_DOUBLE.equals(type)|IFieldTypeConstants.FIELD_INTEGER.equals(type)){
				style = SWT.RIGHT;
			}else if(IFieldTypeConstants.FIELD_BOOLEAN.equals(type)){
				style = SWT.CENTER;
			}else{
				style = SWT.LEFT;
			}
		}else{
			if("SWT.RIGHT".equals(styleName)){
				style = SWT.RIGHT;
			}else if("SWT.CENTER".equals(styleName)){
				style = SWT.CENTER;
			}else{
				style = SWT.LEFT;
			}
		}
		String imagePath = ce.getAttribute("image");
		if(imagePath!=null){
			imageDesc = AbstractUIPlugin.imageDescriptorFromPlugin(ce.getNamespaceIdentifier(), imagePath);
		}
		try{
			width = Integer.parseInt(ce.getAttribute("width"));
		}catch(Exception e){
			width = 80;
		}
		
		moveable = !"false".equals(ce.getAttribute("moveable"));
		resizable = !"false".equals(ce.getAttribute("resizable"));
		toolTipText = ce.getAttribute("toolTipText");
		sorter = !"false".equals(ce.getAttribute("sorter"));

	}

	public String getName() {
		return name;
	}

	public String getColumn() {
		return column;
	}

	public int getStyle() {
		return style;
	}

	public Image getImage() {
		return imageDesc!=null?imageDesc.createImage():null;
	}

	public int getWidth() {
		return width;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public boolean isResizable() {
		return resizable;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	public IEditingSupportor getEditingSupport() {
		try {
			return (IEditingSupportor)ce.createExecutableExtension("editingSupport");
		} catch (CoreException e) {
		}
		return null;
	}

	public boolean isSorter() {
		return sorter;
	}

	public ColumnLabelProvider getLabelProvider() {
		try {
			return (ColumnLabelProvider)ce.createExecutableExtension("labelProvider");
		} catch (CoreException e) {
		}
		return null;
	}

	public String getType() {
		return type;
	}

	public AbstractColumnViewerSorter getSortor(ColumnViewer viewer, Item item, String name) {
		try {
			IColumnSortor is = (IColumnSortor)ce.createExecutableExtension("columnSorter");
			return is.CreateSortor(viewer,item,name);
		} catch (CoreException e) {
		}
		return null;
	}

}
