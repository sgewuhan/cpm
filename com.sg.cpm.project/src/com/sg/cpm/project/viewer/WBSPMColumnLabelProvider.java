package com.sg.cpm.project.viewer;

import com.sg.common.db.DataUtil;
import com.sg.db.model.CascadeObject;

public class WBSPMColumnLabelProvider extends com.sg.common.ui.DBObjectUserPresentation {

	@Override
	public String getText(Object element) {
		CascadeObject so = (CascadeObject) element;
		if(DataUtil.isDocumentObject(so)){
			return "";
		}
		return super.getText(element);
	}
}
