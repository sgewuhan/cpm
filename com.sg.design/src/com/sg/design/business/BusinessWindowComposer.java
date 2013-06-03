/*******************************************************************************
 * Copyright (c) 2008, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.sg.design.business;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.ui.interactiondesign.IWindowComposer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

import com.sg.design.ICSSConstants;
import com.sg.design.ILayoutSetConstants;
import com.sg.design.builder.FooterBuilder;
import com.sg.design.builder.PerspectiveSwitcherBuilder;
import com.sg.design.business.builder.HeaderBuilder;

public class BusinessWindowComposer implements IWindowComposer {

	private static final int MARGIN = 10;
	protected Shell shell;
	private IWorkbenchWindowConfigurer configurer;
	private ApplicationWindow window;
	private Composite headerArea;
	private Composite page;
	private Composite footer;
	private Composite coolBar;

	public Composite createWindowContents(final Shell shell, final IWorkbenchWindowConfigurer configurer) {
		// setup components
		setupComponents(shell, configurer);

		// create the header
		createHeader();

		// we will not create menubar-----------------------------zhonghua
		// 2012/3/20
		// create the menubar composite
		/*
		 * Composite menuBarComp = new Composite( shell, SWT.NONE );
		 * menuBarComp.setData( WidgetUtil.CUSTOM_VARIANT,
		 * ICSSConstants.COMP_GRAY ); //$NON-NLS-1$ FormData fdMenuBarComp = new
		 * FormData(); menuBarComp.setLayoutData( fdMenuBarComp );
		 * fdMenuBarComp.top = new FormAttachment( headerArea );
		 * fdMenuBarComp.left = new FormAttachment( 0, MARGIN );
		 * fdMenuBarComp.right = new FormAttachment( 100, -MARGIN ); if(
		 * configurer.getShowMenuBar() ) { createMenuBar( menuBarComp ); }
		 * 
		 * // create the separator between menubar and page Label menuBarBorder
		 * = new Label( shell, SWT.NONE ); menuBarBorder.setData(
		 * WidgetUtil.CUSTOM_VARIANT, ICSSConstants.MENUBAR ); //$NON-NLS-1$
		 * FormData fdMenuBarBorder = new FormData();
		 * menuBarBorder.setLayoutData( fdMenuBarBorder ); fdMenuBarBorder.left
		 * = new FormAttachment( 0, MARGIN ); fdMenuBarBorder.right = new
		 * FormAttachment( 100, -MARGIN ); fdMenuBarBorder.top = new
		 * FormAttachment( menuBarComp ); fdMenuBarBorder.height = 1;
		 */

		// create the CoolBar
		if (configurer.getShowCoolBar()) {
			createCoolbarArea(shell);
		}


		// pageBg
		Composite pageBg = new Composite(shell, SWT.NONE);
		pageBg.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_TRANS); //$NON-NLS-1$
		FormData fdPageBg = new FormData();
		pageBg.setLayoutData(fdPageBg);
		fdPageBg.left = new FormAttachment(0, MARGIN);
		fdPageBg.right = new FormAttachment(100, -MARGIN);
		if (coolBar == null)
			fdPageBg.top = new FormAttachment(headerArea, 0);
		else
			fdPageBg.top = new FormAttachment(coolBar, 0);
		fdPageBg.bottom = new FormAttachment(100, -MARGIN);
		pageBg.setLayout(new FormLayout());

		createFooter(pageBg);


		// create the page Parent Composite
		page = new Composite(pageBg, SWT.NONE);
		page.setLayout(new FillLayout());
		page.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_TRANS); //$NON-NLS-1$
		FormData fdPage = new FormData();
		page.setLayoutData(fdPage);
		fdPage.left = new FormAttachment(0, 0);
		fdPage.top = new FormAttachment(footer,0);
		fdPage.right = new FormAttachment(100, 0);
		fdPage.bottom = new FormAttachment(100, 0);

		
		shell.layout(true, true);
		return page;
	}

	private void createStatusLine(final Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		Control statusLineControl = configurer.createStatusLineControl(parent);
		final Composite statusLineComp = (Composite) statusLineControl;
		statusLineComp.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				styleButtons(statusLineComp.getChildren());
			};
		});
		parent.setBackgroundMode(SWT.INHERIT_FORCE);
		styleButtons(statusLineComp.getChildren());
		statusLineControl.moveAbove(parent);
	}

	private void setupComponents(final Shell shell, final IWorkbenchWindowConfigurer configurer) {
		this.shell = shell;
		shell.setBackgroundMode(SWT.INHERIT_FORCE);
		shell.setData(RWT.CUSTOM_VARIANT, ICSSConstants.SHELL_MAIN); //$NON-NLS-1$
		this.configurer = configurer;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWbWindow = workbench.getActiveWorkbenchWindow();
		window = (ApplicationWindow) activeWbWindow;
		shell.setLayout(new FormLayout());
	}

	// void createMenuBar(final Composite menuBarComp) {
	// MenuManager manager = window.getMenuBarManager();
	// RowLayout layout = new RowLayout();
	// layout.marginLeft = 0;
	// layout.marginRight = 0;
	// layout.marginTop = 3;
	// menuBarComp.setLayout(layout);
	// manager.fill(menuBarComp);
	// }

	private void createFooter(final Composite pageBg) {
		// create the statusline
		if (configurer.getShowStatusLine()) {
			footer = new Composite(pageBg, SWT.NONE);
			FormData fdFooter = new FormData();
			footer.setLayoutData(fdFooter);

			fdFooter.top = new FormAttachment(0, 0);
			fdFooter.left = new FormAttachment(0, 0);
			fdFooter.right = new FormAttachment(100, 0);

			ElementBuilder footerBuilder = new FooterBuilder(footer, ILayoutSetConstants.SET_ID_FOOTER);
			footerBuilder.build();
			Composite statusLineParent = (Composite) footerBuilder.getControl();

			createStatusLine(statusLineParent);
		}
	}

	private void createHeader() {
		headerArea = new Composite(shell, SWT.NONE);
		FormData fdHeaderArea = new FormData();
		headerArea.setLayoutData(fdHeaderArea);
		fdHeaderArea.left = new FormAttachment(0, 0);
		fdHeaderArea.right = new FormAttachment(100, 0);

		ElementBuilder headerBuilder = new HeaderBuilder(headerArea, ILayoutSetConstants.SET_ID_HEADER);
		headerBuilder.build();

		// create the Perspective Switcher
		if (configurer.getShowPerspectiveBar()) {
			createPerspectiveBar((Composite) headerBuilder.getControl());
		}
	}

	private void createPerspectiveBar(final Composite header) {
		ElementBuilder perspBuilder = new PerspectiveSwitcherBuilder(header, ILayoutSetConstants.SET_ID_PERSP);
		perspBuilder.build();
	}

	private void createCoolbarArea(final Composite header) {
		ICoolBarManager manager = window.getCoolBarManager2();
		// If no Coolbar is needed, change this method call
		createCoolBar(manager, header);

	}

	private void createCoolBar(final ICoolBarManager manager, final Composite header) {
		if (manager != null) {
			coolBar = new Composite(header, SWT.NONE);
			
			coolBar.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_TRANS); //$NON-NLS-1$
			FormData fdCoolBar = new FormData();
			coolBar.setLayoutData(fdCoolBar);
			fdCoolBar.top = new FormAttachment(headerArea,0);
			fdCoolBar.left = new FormAttachment(0, 8);
			fdCoolBar.height = 36;
			fdCoolBar.right = new FormAttachment(100);
			coolBar.setLayout(new FillLayout());

			// Create the actual coolbar
			if (manager instanceof ICoolBarManager2) {
				ICoolBarManager2 coolbarManager2 = (ICoolBarManager2) manager;
				coolbarManager2.createControl2(coolBar);
//				if (manager instanceof CoolBarManager) {
//					CoolBarManager coolbarManager = (CoolBarManager) manager;
//				}
			}
		}
	}

	public void postWindowOpen(final IWorkbenchWindowConfigurer configurer) {
		final Shell windowShell = configurer.getWindow().getShell();
		windowShell.setMaximized(true);
	}

	public void preWindowOpen(final IWorkbenchWindowConfigurer configurer) {
		configurer.setShellStyle(SWT.NO_TRIM);
		configurer.setShowPerspectiveBar(true);
	}

	private void styleButtons(final Control[] buttons) {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] instanceof Button) {
				buttons[i].setData(RWT.CUSTOM_VARIANT, ICSSConstants.CLEAR_BUTTON); //$NON-NLS-1$
			}
		}
	}

}
