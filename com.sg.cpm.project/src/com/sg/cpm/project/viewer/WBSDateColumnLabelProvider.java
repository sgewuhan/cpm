package com.sg.cpm.project.viewer;

import com.sg.common.db.DataUtil;
import com.sg.db.model.CascadeObject;


public class WBSDateColumnLabelProvider extends com.sg.widget.viewer.labelprovider.DateColumnLabelProvider {

	@Override
	public String getText(Object element) {
		CascadeObject so = (CascadeObject) element;
		if(DataUtil.isDocumentObject(so)){
			return "";
		}
		return super.getText(element);
	}


}
