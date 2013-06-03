/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.sg.design.business.layoutsets;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;

import com.sg.design.ILayoutSetConstants;

public class HeaderInitializer implements ILayoutSetInitializer {

	public void initializeLayoutSet(final LayoutSet layoutSet) {
		// images
		String path = ILayoutSetConstants.IMAGE_PATH_BUSINESS;
		layoutSet.addImagePath(ILayoutSetConstants.HEADER_LEFT, path + "header_left.png"); //$NON-NLS-1$
		layoutSet.addImagePath(ILayoutSetConstants.HEADER_LEFT_BG, path + "header_left_bg.png"); //$NON-NLS-1$
		layoutSet.addImagePath(ILayoutSetConstants.HEADER_RIGHT, path + "header_right.png"); //$NON-NLS-1$
		layoutSet.addImagePath(ILayoutSetConstants.SHELL_BG, path + "bg.png"); //$NON-NLS-1$

	}
}
