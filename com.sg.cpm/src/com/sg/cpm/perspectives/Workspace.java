package com.sg.cpm.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Workspace implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
	}

}
