package com.sg.widget.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.sg.widget.editor.field.AbstractFieldPart;
import com.sg.widget.editor.field.actions.IFieldActionHandler;

public class FieldActionConfiguration extends Configuration {

	private String name;
	private String imagePath;
	private String id;
	private IFieldActionHandler handler;

	public FieldActionConfiguration(IConfigurationElement ce) {
		super(ce);
		name = ce.getAttribute("name");
		imagePath = ce.getAttribute("image");
		id = ce.getAttribute("id");
		try {
			handler = (IFieldActionHandler) ce.createExecutableExtension("handler");
		} catch (CoreException e) {
		}
	}
	
	public FieldActionConfiguration(){
	}

	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				getConfigurationElement().getNamespaceIdentifier(), imagePath);
	}
	
	public Image getImage(){
		ImageDescriptor imgdesc = getImageDescriptor();
		if(imgdesc!=null){
			return imgdesc.createImage();
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public Object run(AbstractFieldPart abstractFieldPart, IEditorInput input) {
		return handler.run(abstractFieldPart,input);
	}
	
	public String getId(){
		return id;
	}

}
