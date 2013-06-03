package com.sg.widget.editor;

import org.eclipse.jface.resource.ImageDescriptor;

import com.sg.widget.configuration.Configuration;

public interface IEditorDelegator {

	String getName(Configuration conf, ISingleObjectEditorInput input);

	ImageDescriptor getImageDescriptor(Configuration conf, ISingleObjectEditorInput input);

	String getTitleToolTips(Configuration conf, ISingleObjectEditorInput input);

}
