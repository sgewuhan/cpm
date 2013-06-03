package com.sg.cpm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Project implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		layout.setEditorAreaVisible(false);
	}

}
