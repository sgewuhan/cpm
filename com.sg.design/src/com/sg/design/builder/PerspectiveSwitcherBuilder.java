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
package com.sg.design.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.internal.preferences.SessionScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.sg.design.ICSSConstants;
import com.sg.design.ILayoutSetConstants;

public class PerspectiveSwitcherBuilder extends ElementBuilder {

	private static final String RAP_PERSPECTIVES = "RAP_PERSPECTIVES"; //$NON-NLS-1$

	private Composite background;
	private Map<IPerspectiveDescriptor,Button> perspectiveButtonMap;
	private Map<Button,IPerspectiveDescriptor> buttonPerspectiveMap;
	private List<Button> buttonList;
	private List<String> perspectiveList;

	private PerspectiveAdapter perspectiveAdapter = new PerspectiveAdapter() {

		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			addIdToStore(perspective.getId());

			Button button = createPerspectiveButton(perspective);
			cleanButtons(button);
		}
	};

	private Image bgActive;

	public PerspectiveSwitcherBuilder(final Composite parent, final String subSetId) {
		super(parent, subSetId);
		GridLayout glayout = new GridLayout();
		glayout.marginBottom = 0;
		glayout.marginHeight = 0;

		parent.setLayout(glayout);
		background = new Composite(parent, SWT.NONE);
		background.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_TRANS); //$NON-NLS-1$
		background.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 1, 1));
		RowLayout layout = new RowLayout();
		background.setLayout(layout);
		layout.spacing = 4;
		layout.marginBottom = 0;
		layout.marginRight = 10;
		layout.marginTop = 0;
		layout.wrap = false;
		layout.pack = false;
		perspectiveButtonMap = new HashMap<IPerspectiveDescriptor,Button>();
		buttonPerspectiveMap = new HashMap<Button,IPerspectiveDescriptor>();
		buttonList = new ArrayList<Button>();
		perspectiveList = new ArrayList<String>();
		// images
		bgActive = getImage(ILayoutSetConstants.PERSP_BG_ACTIVE);
	}

	public void addControl(final Control control, final Object layoutData) {
	}

	public void addControl(final Control control, final String positionId) {
	}

	private void addIdToStore(final String id) {
		if (!perspectiveList.contains(id)) {
			perspectiveList.add(id);
		}
		save();
	}

	public void addImage(final Image image, final Object layoutData) {
	}

	public void addImage(final Image image, final String positionId) {
	}

	public void build() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		workbenchWindow.addPerspectiveListener(perspectiveAdapter);

		IPerspectiveDescriptor[] descriptors = load();
		for (int i = 0; i < descriptors.length; i++) {
			createPerspectiveButton(descriptors[i]);
		}

	}

	// /*
	// * redesign the buttons
	// */
	// private void cleanButtons(final Button current) {
	// for (int i = 0; i < buttonList.size(); i++) {
	// Button button = (Button) buttonList.get(i);
	// if (!button.equals(current)) {
	//				button.setData(WidgetUtil.CUSTOM_VARIANT, ICSSConstants.PERSP_BOTTON); //$NON-NLS-1$
	// } else {
	//				button.setData(WidgetUtil.CUSTOM_VARIANT, ICSSConstants.PERSP_BOTTON_ACTIVE); //$NON-NLS-1$
	// }
	// }
	// }

	/*
	 * redesign the buttons
	 */
	private void cleanButtons(final Button current) {
		for (int i = 0; i < buttonList.size(); i++) {
			Button button = buttonList.get(i);
			Composite parent = button.getParent();
			Control[] children = parent.getChildren();
			if (!button.equals(current)) {
				for (int j = 0; j < children.length; j++) {
					if (children[j] instanceof Label) {
						children[j].setVisible(false);
					}
				}
//				button.setData(WidgetUtil.CUSTOM_VARIANT, "perspective"); //$NON-NLS-1$
			} else {
				for (int j = 0; j < children.length; j++) {
					children[j].setVisible(true);
				}
//				button.setData(WidgetUtil.CUSTOM_VARIANT, "perspectiveActive"); //$NON-NLS-1$
			}
			parent.layout(true);
		}
	}

	// private void cleanUpButton(
	// final IPerspectiveDescriptor perspective, final Button button )
	// {
	// buttonList.remove( button );
	// perspectiveButtonMap.remove( perspective );
	// buttonPerspectiveMap.remove( button );
	// button.getParent().dispose();
	// background.layout( true );
	// Control[] children = { background };
	// Composite parent = getParent();
	// parent.changed( children );
	// parent.layout( true );
	// parent.getParent().layout( true );
	// }
	//
	// private void closePerspective( final IPerspectiveDescriptor desc ) {
	// IWorkbench workbench = PlatformUI.getWorkbench();
	// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	// IWorkbenchPage activePage = workbenchWindow.getActivePage();
	// activePage.closePerspective( desc, true, false );
	// }

	private Button createPerspectiveButton(final IPerspectiveDescriptor desc) {

		Button result =  perspectiveButtonMap.get(desc);
		if (result == null && desc != null && desc.getLabel() != null) {
			Composite buttonBg = new Composite(background, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginBottom = 0;
			layout.marginTop = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginWidth= 0;
			layout.marginHeight= 0;
			
			buttonBg.setLayout(layout);

			final Button perspButton = new Button(buttonBg, SWT.PUSH | SWT.FLAT);
			perspButton.setData(RWT.CUSTOM_VARIANT, ICSSConstants.PERSP_BOTTON);
			perspButton.setText(desc.getLabel());
			perspButton.setImage(desc.getImageDescriptor().createImage());
			perspButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));

			Label indicator = new Label(buttonBg, SWT.NONE);
			indicator.setImage(bgActive);
			indicator.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false));

			
			RowData rd = new RowData();
			rd.height = 60;
			buttonBg.setLayoutData(rd);

			perspectiveButtonMap.put(desc, perspButton);
			buttonPerspectiveMap.put(perspButton, desc);
			buttonList.add(perspButton);

			perspButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					cleanButtons(perspButton);
					switchPerspective(desc.getId());
				}
			});

			result = perspButton;
		}

		return result;
	}

	private IEclipsePreferences createSessionScope() {
		return new SessionScope().getNode(RAP_PERSPECTIVES);
	}

	public void dispose() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		workbenchWindow.removePerspectiveListener(perspectiveAdapter);
		Composite parent = getParent();
		if (parent != null && !parent.isDisposed()) {
			parent.dispose();
		}

	}

	public Control getControl() {
		return background;
	}

	public Point getSize() {
		return null;
	}

	private IPerspectiveDescriptor[] load() {
//		IPerspectiveDescriptor[] result = null;

		IWorkbench workbench = PlatformUI.getWorkbench();
		IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();

		IPerspectiveDescriptor[] perspectives = registry.getPerspectives();
		for (IPerspectiveDescriptor persp : perspectives) {
			perspectiveList.add(persp.getId());
		}

		return perspectives;
	}

//	private void removeIdFromStore(final String id) {
//		perspectiveList.remove(id);
//		Preferences store = createSessionScope();
//		store.remove(id);
//		save();
//	}

	private void save() {
		Preferences store = createSessionScope();
		try {
			store.clear();
			for (int i = 0; i < perspectiveList.size(); i++) {
				String id = perspectiveList.get(i);
				store.putInt(id, i);
			}
			store.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void switchPerspective(final String perspectiveId) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		try {
			workbench.showPerspective(perspectiveId, window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}
}
