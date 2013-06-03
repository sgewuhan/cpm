package com.sg.widget.editor;

import org.eclipse.jface.resource.ImageDescriptor;

import com.sg.widget.configuration.Configuration;

public interface IEditorPageHeadDelegator {

	String getTitle(Configuration conf,ISingleObjectEditorInput input);

	String getDescription(Configuration conf,ISingleObjectEditorInput input);

	ImageDescriptor getImageDescriptor(Configuration conf, ISingleObjectEditorInput input);

}
