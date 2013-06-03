package com.sg.cpm.project.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.sg.common.db.DataUtil;
import com.sg.db.model.CascadeObject;

public class WBSCodeColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		CascadeObject so = (CascadeObject) element;
		if(DataUtil.isWorkObject(so)){
			return DataUtil.getWorkWBSCode(so);
		}else{
			return "";
		}
	}

}
