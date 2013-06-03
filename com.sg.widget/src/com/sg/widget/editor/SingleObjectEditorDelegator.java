package com.sg.widget.editor;

import org.eclipse.jface.resource.ImageDescriptor;

import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.configuration.Configuration;

public class SingleObjectEditorDelegator implements IEditorDelegator{

	@Override
	public String getName(Configuration conf, ISingleObjectEditorInput input) {
		String basicText = getBasicText(conf,input);
		return input.isEditable()?basicText:basicText+"(Ö»¶Á)";
	}

	private String getBasicText(Configuration conf, ISingleObjectEditorInput input) {
		String typeName = ((EditorConfiguration)conf).getName();
		if(input.isNewObject()){
			return "ÐÂ"+typeName;
		}else{
			String labelFieldName = ((EditorConfiguration)conf).getLabelFieldName();
			if(labelFieldName!=null){
				return typeName+":"+input.getInputData().getValue(labelFieldName);
			}else{
				return typeName;
			}
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor(Configuration conf,
			ISingleObjectEditorInput input) {
		return ((EditorConfiguration)conf).getImageDescription();
	}

	@Override
	public String getTitleToolTips(Configuration conf,
			ISingleObjectEditorInput input) {
		return ((EditorConfiguration)conf).getTitleToolTips();
	}

}
