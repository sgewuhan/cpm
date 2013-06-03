package com.sg.design.stacks;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.ConfigurationAction;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackDropResult;

import com.sg.design.ICSSConstants;
import com.sg.design.ILayoutSetConstants;
import com.sg.design.builder.StackPresentationBuider;

public class ViewStackPresentation extends ConfigurableStack {

	private static final String ID_PART_BUTTON = "partButton";
	private static final String ID_CLOSE = "close"; 
	private static final String TYPE = "type";
	private static final String BUTTON_ID = "buttonId"; //$NON-NLS-1$
	
	private static final int BUTTON_SPACING = 8;
	private static final int WIDTH_SPACING = 65;
	private static final int HEIGHT_SPACING = 15;

	private Control presentationControl;
	private IPresentablePart currentPart;
	private ElementBuilder stackBuilder;
	private Composite tabBg;
	private Composite confArea;
	private Label confCorner;
	private Map<IPresentablePart, Composite> partButtonMap;
	private List<IPresentablePart> partList;
	private List<Composite> buttonList;
	private Composite toolbarBg;
	private int activeState;
	protected boolean deactivated;
	private Button viewMenuButton;
	private Map<IPresentablePart, IPropertyListener> dirtyListenerMap;
	private Button overflowButton;
	private List<Control> overflowButtons;
	private Map<Composite, IPresentablePart> buttonPartMap;
	private IPresentablePart oldPart;
	private Label standaloneViewTitle;
	private String type;

	private class DirtyListener implements IPropertyListener {

		private IPresentablePart part;

		public DirtyListener(IPresentablePart part) {
			this.part = part;
		}

		public void propertyChanged(Object source, int propId) {
			if (propId == ISaveablePart.PROP_DIRTY) {
				handleDirtyStateChanged();
			} else if (propId == IWorkbenchPartConstants.PROP_PART_NAME) {
				handlePartNameChanged();
			}
		}

		private void handleDirtyStateChanged() {
			Button partButton = getPartButton(part);
			if (partButton != null) {
				String text = partButton.getText();
				char lastCharacter = getLastCharacter(text);
				if (part.isDirty()) {
					// mark the part as dirty
					if (lastCharacter != '*') {
						text = text + "*"; //$NON-NLS-1$
					}
				} else {
					// mark the part as clean
					if (lastCharacter == '*') {
						text = text.substring(0, text.length() - 1);
					}
				}
				partButton.setText(text);
			}
		}

		private void handlePartNameChanged() {
			Button partButton = getPartButton(part);
			if (partButton != null) {
				partButton.setText(part.getName());
				partButton.getParent().layout(true);
			}
			if (standaloneViewTitle != null) {
				standaloneViewTitle.setText(part.getName());
				standaloneViewTitle.getParent().layout(true);
			}
		}

		private Button getPartButton(IPresentablePart part) {
			Button result = null;
			Object object = partButtonMap.get(part);
			if (object instanceof Composite) {
				Control[] children = ((Composite) object).getChildren();
				for (Control b : children) {
					if (children[0] instanceof Button) {
						if (ID_PART_BUTTON.equals(b.getData(TYPE))) {
							result = (Button) b;
							break;
						}
					}
				}
			}
			return result;
		}

		private char getLastCharacter(String text) {
			char[] starArray = new char[1];
			text.getChars(text.length() - 1, text.length(), starArray, 0);
			return starArray[0];
		}
	}

	public ViewStackPresentation() {
		activeState = AS_INACTIVE;
		deactivated = false;
		partButtonMap = new HashMap<IPresentablePart, Composite>();
		partList = new ArrayList<IPresentablePart>();
		buttonList = new ArrayList<Composite>();
		dirtyListenerMap = new HashMap<IPresentablePart, IPropertyListener>();
		overflowButtons = new ArrayList<Control>();
		buttonPartMap = new HashMap<Composite, IPresentablePart>();
	}
	
	

	@Override
	public void init(IStackPresentationSite site, String stackId, Composite parent, ConfigurableStackProxy proxy) {
		type = proxy.getType();
		super.init(site, stackId, parent, proxy);
	}



	public void init() {
		presentationControl = createStyledControl();
		ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
		registry.addViewPartPresentation(this);
	}

	void catchToolbarChange() {
		layoutToolBar();
		setBounds(presentationControl.getBounds());
	}

	private void createToolBarBg() {
		Composite tabBar = getTabBar();
		toolbarBg = new Composite(tabBar.getParent(), SWT.NONE);
		toolbarBg.setLayout(new FormLayout());
		Image bg = stackBuilder.getImage(ILayoutSetConstants.STACK_VIEW_TOOLBAR_BG);
		toolbarBg.setBackgroundImage(bg);
		FormData fdToolBar = new FormData();
		toolbarBg.setLayoutData(fdToolBar);
		fdToolBar.left = new FormAttachment(0);
		fdToolBar.right = new FormAttachment(100);
		fdToolBar.top = new FormAttachment(tabBar);
		fdToolBar.height = bg.getBounds().height;
		toolbarBg.moveAbove(tabBar);
	}

	private Control createStyledControl() {
		getParent().setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_GRAY); //$NON-NLS-1$
		final Composite parent = new Composite(getParent(), SWT.NONE);
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setBounds(parent.getBounds());
			}
		});
		parent.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_GRAY); //$NON-NLS-1$
		String setID = ILayoutSetConstants.SET_ID_STACKPRESENTATION;
		stackBuilder = new StackPresentationBuider(parent, setID,type);
		stackBuilder.build();
		return parent;
	}

	private boolean isStandalone() {
		return getType().equals(PresentationFactory.KEY_STANDALONE_VIEW);
	}

	public void addPart(IPresentablePart newPart, Object cookie) {
		checkTabBg();
		if (!isStandalone()) {
			createPartButton(newPart);
			partList.add(newPart);
			Control partControl = newPart.getControl();
			if (partControl != null) {
				partControl.getParent().setBackgroundMode(SWT.INHERIT_NONE);
				partControl.setData(RWT.CUSTOM_VARIANT, ICSSConstants.PART_BORDER); //$NON-NLS-1$
			}
			tabBg.layout(true);
		} else {
			decorateStandaloneView(newPart);
		}
		// add the listener for the dirty activeState
		IPropertyListener listener = new DirtyListener(newPart);
		dirtyListenerMap.put(newPart, listener);
		newPart.addPropertyListener(listener);
	}

	private void decorateStandaloneView(IPresentablePart newPart) {
		checkTabBg();
		if (getShowTitle()) {
			getTabBar().setVisible(true);
			tabBg.setVisible(true);
			standaloneViewTitle = new Label(tabBg, SWT.NONE);
			standaloneViewTitle.setData(RWT.CUSTOM_VARIANT, ICSSConstants.STANDALONE_VIEW); //$NON-NLS-1$
			standaloneViewTitle.setText(newPart.getName());
			hideFrameLabel(StackPresentationBuider.TOP_BORDER);
		} else {
			getTabBar().setVisible(false);
			hideFrameLabel(StackPresentationBuider.LEFT);
			hideFrameLabel(StackPresentationBuider.RIGHT);
		}
	}

	@SuppressWarnings("rawtypes")
	private void hideFrameLabel(String id) {
		Object labelMap = stackBuilder.getAdapter(Map.class);
		if (labelMap != null && (labelMap instanceof Map)) {
			Map map = (Map) labelMap;
			Object object = map.get(id);
			if (object != null) {
				Label frameLabel = (Label) object;
				frameLabel.setVisible(false);
			}
		}
	}

	private void layoutToolBar() {
		if (toolbarBg == null && tabBg != null) {
			createToolBarBg();
		}
		if (currentPart != null && getPartPane(currentPart) != null) {
			Control toolBar = currentPart.getToolBar();
			final IPartMenu viewMenu = currentPart.getMenu();
			// viewmenu
			if (viewMenu != null) {
				if (viewMenuButton == null) {
					viewMenuButton = new Button(toolbarBg, SWT.PUSH);
					viewMenuButton.setData(RWT.CUSTOM_VARIANT, ICSSConstants.CLEAR_BUTTON); //$NON-NLS-1$
					Image icon = stackBuilder.getImage(ILayoutSetConstants.STACK_VIEW_MENU_ICON);
					viewMenuButton.setImage(icon);
					FormData fdViewMenuButton = new FormData();
					viewMenuButton.setLayoutData(fdViewMenuButton);
					fdViewMenuButton.right = new FormAttachment(100, -4);
					fdViewMenuButton.top = new FormAttachment(0, 8);
					viewMenuButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							Display display = viewMenuButton.getDisplay();
							int height = viewMenuButton.getSize().y;
							Point newLoc = display.map(viewMenuButton, null, 0, height);
							viewMenu.showMenu(newLoc);
						}
					});
				}
			} else if (viewMenuButton != null) {
				viewMenuButton.setVisible(false);
				viewMenuButton.dispose();
				viewMenuButton = null;
			}
			// toolbar
			Point size = toolbarBg.getSize();
			if (toolBar != null) {
				Point point = currentPart.getControl().getLocation();
				point.y -= (size.y + 2);
				point.x += (size.x - toolBar.getSize().x);
				if (viewMenu != null) {
					point.x -= 20;
				}
				toolBar.setLocation(point);
				toolbarBg.moveBelow(toolBar);
				presentationControl.moveBelow(toolBar);
				currentPart.getControl().moveBelow(toolBar);
			}
			// toolbarbg and layer
			if (toolBar != null || viewMenu != null) {
				toolbarBg.setVisible(true);
			} else {
				toolbarBg.setVisible(false);
			}
			toolbarBg.layout(true);
		}
	}

	private void createPartButton(final IPresentablePart part) {
		Composite buttonArea = new Composite(tabBg, SWT.NONE);
		buttonArea.setData(RWT.CUSTOM_VARIANT, ICSSConstants.INACTIVE_BUTTON); //$NON-NLS-1$
		buttonArea.setLayout(new FormLayout());

		final Button partButton = new Button(buttonArea, SWT.PUSH);
		partButton.setData(RWT.CUSTOM_VARIANT, ICSSConstants.PART_INACTIVE);
		partButton.setText(part.getName());
		partButton.setImage(part.getTitleImage());
		
		partButton.setData(TYPE, ID_PART_BUTTON);
		
		
		partButton.setToolTipText(part.getTitleToolTip());
		final IPropertyListener nameListener = new IPropertyListener() {
			public void propertyChanged(Object source, int propId) {
				if (propId == IPresentablePart.PROP_PART_NAME) {
					partButton.setText(part.getName());
					tabBg.layout();
				} else if (propId == IPresentablePart.PROP_TITLE) {
					partButton.setToolTipText(part.getTitleToolTip());
					partButton.setImage(part.getTitleImage());
				}
			}
		};
		part.addPropertyListener(nameListener);
		partButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				partButton.removeDisposeListener(this);
				part.removePropertyListener(nameListener);
			}
		});
		FormData fdPartButton = new FormData();
		partButton.setLayoutData(fdPartButton);
		fdPartButton.left = new FormAttachment(0);
		fdPartButton.top = new FormAttachment(0, 0);
		fdPartButton.bottom = new FormAttachment(100);
		partButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!currentPart.equals(part)) {
					selectPart(part);
				}
				activatePart(part);
				// move the toolbar on top
				currentPart.getControl().moveAbove(null);
				Control toolBar = currentPart.getToolBar();
				if (toolBar != null) {
					toolBar.moveAbove(null);
				}
			}
		});
		// partButton.addListener( SWT.MouseDoubleClick, new Listener() {
		// public void handleEvent( Event event ) {
		// handleToggleZoom( part );
		// }
		// } );
		Composite corner = new Composite(buttonArea, SWT.NONE);
		corner.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_TRANS); //$NON-NLS-1$
		corner.setLayout(new FormLayout());

		String separatorActive = ILayoutSetConstants.STACK_TAB_INACTIVE_SEPARATOR_ACTIVE;
		Image cornerImage = stackBuilder.getImage(separatorActive);
		FormData fdCorner = new FormData();
		corner.setLayoutData(fdCorner);
		corner.setBackgroundImage(cornerImage);
		fdCorner.width = cornerImage.getBounds().width;
		fdCorner.height = cornerImage.getBounds().height;
		fdCorner.right = new FormAttachment(100);
		fdCorner.bottom = new FormAttachment(100);

		fdPartButton.height = cornerImage.getBounds().height;
		fdPartButton.right = new FormAttachment(corner, -2);
		partButtonMap.put(part, buttonArea);
		buttonPartMap.put(buttonArea, part);
		buttonList.add(buttonArea);
		moveToTabBarEnd(buttonArea);
	}

	// private void handleToggleZoom( IPresentablePart part ) {
	// IWorkbench workbench = PlatformUI.getWorkbench();
	// IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	// IWorkbenchPage page = window.getActivePage();
	// page.toggleZoom( getReference( part ) );
	// handleToolbarsOnToggleZoom();
	// }
	//
	// private void handleToolbarsOnToggleZoom() {
	// ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
	// if( state == IStackPresentationSite.STATE_MAXIMIZED ) {
	// registry.moveAllToolbarsBellow( null );
	// } else if( state == IStackPresentationSite.STATE_RESTORED ) {
	// registry.fireToolBarChanged();
	// }
	// if( currentPart != null ) {
	// currentPart.getControl().moveAbove( null );
	// Control toolBar = currentPart.getToolBar();
	// if( toolBar != null ) {
	// toolBar.moveAbove( null );
	// }
	// }
	// }

	public void hideAllToolBars(Control control) {
		for (int i = 0; i < partList.size(); i++) {
			IPresentablePart part = partList.get(i);
			Control toolBar = part.getToolBar();
			if (toolBar != null) {
				toolBar.moveBelow(control);
			}
		}
	}

	protected void activatePart(IPresentablePart part) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		IWorkbenchPart workbenchPart = getReference(part).getPart(true);
		if (workbenchPart != null) {
			if (oldPart != null) {
				Control toolBar = oldPart.getToolBar();
				if (toolBar != null) {
					toolBar.setVisible(false);
				}
			}
			activePage.activate(workbenchPart);
		}
	}

	private IWorkbenchPartReference getReference(IPresentablePart part) {
		IWorkbenchPartReference result = null;
		if (part instanceof PresentablePart) {
			PresentablePart presentablePart = (PresentablePart) part;
			PartPane pane = presentablePart.getPane();
			result = pane.getPartReference();
		}
		return result;
	}

	private void makePartButtonActive(final IPresentablePart part) {
		Object object = partButtonMap.get(part);
		if (object instanceof Composite) {
			Composite buttonArea = (Composite) object;
			buttonArea.setData(RWT.CUSTOM_VARIANT, ICSSConstants.TAB_INACTIVE); //$NON-NLS-1$
			Control[] children = buttonArea.getChildren();
			buttonArea.setLayout(new FormLayout());
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child instanceof Button) {
					// Partbutton
					Button partButton = (Button) child;
					partButton.setData(RWT.CUSTOM_VARIANT, ICSSConstants.PART_ACTIVE);
					// FormData fdButton = ( FormData )
					// partButton.getLayoutData();
					// FormData pos = stackBuilder.getPosition(
					// ILayoutSetConstants.STACK_BUTTON_TOP );
					// fdButton.top = pos.top;
				} else if (child instanceof Composite) {
					// Corner
					Composite corner = (Composite) child;
					corner.setVisible(true);
					String cornerDesc = ILayoutSetConstants.STACK_TAB_INACTIVE_SEPARATOR_ACTIVE;
					Image cornerImage = stackBuilder.getImage(cornerDesc);
					corner.setBackgroundImage(cornerImage);
					FormData fdCorner = (FormData) corner.getLayoutData();
					fdCorner.top = new FormAttachment(0);
					fdCorner.width = cornerImage.getBounds().width;
					fdCorner.height = cornerImage.getBounds().height;
					if (part.isCloseable()) {
						Button close = new Button(buttonArea, SWT.PUSH);
						close.setData(BUTTON_ID, ID_CLOSE);
						// ����ĵط��������� partButton��������
						close.setData(TYPE, ID_CLOSE);
						// ����ĵط��������� partButton��������
						close.setData(RWT.CUSTOM_VARIANT, ICSSConstants.VIEW_CLOSE_INACTIVE); //$NON-NLS-1$
						close.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent e) {
								IStackPresentationSite site = getSite();
								if (site.isCloseable(part)) {
									site.close(new IPresentablePart[] { part });
									showPartButton(currentPart);
								}
							}
						});
						FormData fdClose = new FormData();
						close.setLayoutData(fdClose);
						fdClose.right = new FormAttachment(100, -2);
						fdClose.top = new FormAttachment(0, 4);
						fdClose.width = 12;
						fdClose.height = 12;
						close.setLayoutData(fdClose);
						close.moveAbove(null);
					}
				}
			}
			showPartButton(currentPart);
		}
	}

	// private void checkHideSeparator( Composite buttonArea ) {
	// int indexOf = buttonList.indexOf( buttonArea );
	// for( int i = 0; i < buttonList.size(); i++ ) {
	// Composite area = buttonList.get( i );
	// Control[] children = area.getChildren();
	// for( int j = 0; j < children.length; j++ ) {
	// if( children[ j ] instanceof Composite ) {
	// if( i == indexOf || ( i == indexOf - 1 ) ) {
	// ( ( Composite ) children[ j ] ).setVisible( false );
	// } else {
	// ( ( Composite ) children[ j ] ).setVisible( true );
	// }
	//
	// }
	// }
	// }
	// }

	private void makePartButtonInactive(IPresentablePart part) {
		Object object = partButtonMap.get(part);
		if (object instanceof Composite) {
			Composite buttonArea = (Composite) object;
			buttonArea.setData(RWT.CUSTOM_VARIANT, ICSSConstants.INACTIVE_BUTTON); //$NON-NLS-1$
			buttonArea.setBackground(null);
			Control[] children = buttonArea.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child instanceof Button) {
					Button button = (Button) child;
					// Partbutton
					if (button.getData(BUTTON_ID) != null) {
						// close button
						button.setVisible(false);
						button.dispose();
					} else {
						// Part button
						button.setData(RWT.CUSTOM_VARIANT, ICSSConstants.PART_INACTIVE);
						FormData fdButton = (FormData) button.getLayoutData();
						fdButton.top = new FormAttachment(0, 0);
					}
					// } else if( child instanceof Composite ) {
					// // Corner
					// Composite corner = ( Composite ) child;
					// corner.setVisible( true );
					// String sepConst =
					// ILayoutSetConstants.STACK_TAB_INACTIVE_SEPARATOR_ACTIVE;
					// Image cornerImage = stackBuilder.getImage( sepConst );
					// corner.setBackgroundImage( cornerImage );
					// FormData fdCorner = ( FormData ) corner.getLayoutData();
					// fdCorner.width = cornerImage.getBounds().width;
					// fdCorner.height = cornerImage.getBounds().height;
					// fdCorner.top = new FormAttachment( 0, 6 );
				}
			}
			buttonArea.getParent().layout();
		}

	}

	/*
	 * check if the tabBg exists. If not it will create it.
	 */
	private void checkTabBg() {
		Composite tabBar = getTabBar();
		if (tabBg == null && tabBar != null) {
			tabBg = new Composite(tabBar, SWT.NONE);
			tabBg.setData(RWT.CUSTOM_VARIANT, ICSSConstants.COMP_TRANS); //$NON-NLS-1$
			FormData fdTabBg = new FormData();
			tabBg.setLayoutData(fdTabBg);
			fdTabBg.left = new FormAttachment(0);
			fdTabBg.top = new FormAttachment(0);
			fdTabBg.bottom = new FormAttachment(100);
			createConfArea(fdTabBg);
			FormData fdLayout = stackBuilder.getPosition(ILayoutSetConstants.STACK_TABBG_POS);
			RowLayout layout = new RowLayout(SWT.HORIZONTAL);
			layout.spacing = 0;
			layout.marginBottom = 0;
			if (!isStandalone()) {
				layout.marginHeight = 0;
				layout.marginLeft = fdLayout.width;
			} else {
				layout.marginHeight = 4;
				layout.marginLeft = BUTTON_SPACING;
			}
			layout.marginRight = 16;
			layout.marginTop = fdLayout.height;
			layout.marginWidth = 0;
			layout.wrap = false;
			tabBg.setLayout(layout);
			// calculate overflow
			presentationControl.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					manageOverflow();
				}
			});
			addPartActivationListners();
		}
	}

	@SuppressWarnings("rawtypes")
	private void addPartActivationListners() {
		addPartActivationListnerToControl(tabBg);
		Map cornerMap = (Map) stackBuilder.getAdapter(Map.class);
		Object corner = cornerMap.get(StackPresentationBuider.LEFT);
		addPartActivationListenerToCorner(corner);
		corner = cornerMap.get(StackPresentationBuider.RIGHT);
		addPartActivationListenerToCorner(corner);
	}

	private void addPartActivationListenerToCorner(Object corner) {
		if (corner != null && corner instanceof Label) {
			Label cornerLabel = (Label) corner;
			addPartActivationListnerToControl(cornerLabel);
		}
	}

	private void addPartActivationListnerToControl(Control control) {
		control.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				activatePartWithTabbar();
			}

			// public void mouseDoubleClick( MouseEvent e ) {
			// handleToggleZoom( currentPart );
			// }
		});
	}

	private void activatePartWithTabbar() {
		activatePart(currentPart);
		// move the toolbar on top
		currentPart.getControl().moveAbove(null);
		Control toolBar = currentPart.getToolBar();
		if (toolBar != null) {
			toolBar.moveAbove(null);
		}
	}

	private void manageOverflow() {
		if (isOverflowNecessary()) {
			hideLastVisibleButton();
			manageOverflow();
		} else {
			showLastChildIfNecessary(0);
		}
		handleOverflowButton();
	}

	private boolean isOverflowNecessary() {
		int tabChildrenSize = getTabChildrenSize();
		boolean childrenBiggerThanParent = tabChildrenSize > tabBg.getBounds().width;
		return childrenBiggerThanParent && moreThanOneChildVisible();
	}

	private boolean moreThanOneChildVisible() {
		boolean result = false;
		Control[] children = tabBg.getChildren();
		int visibleChilds = 0;
		for (int i = 0; i < children.length && !result; i++) {
			if (children[i].isVisible()) {
				visibleChilds++;
				if (visibleChilds > 1) {
					result = true;
				}
			}
		}
		return result;
	}

	private void handleOverflowButton() {
		if (overflowButton == null) {
			overflowButton = new Button(tabBg.getParent(), SWT.PUSH);
			String stackOverflowPosition = ILayoutSetConstants.STACK_OVERFLOW_POSITION;
			FormData fdOverflowButton = stackBuilder.getPosition(stackOverflowPosition);
			overflowButton.setLayoutData(fdOverflowButton);
			String stackTabOverflowActive = ILayoutSetConstants.STACK_TAB_OVERFLOW_ACTIVE;
			Image icon = stackBuilder.getImage(stackTabOverflowActive);
			fdOverflowButton.height = icon.getBounds().height;
			fdOverflowButton.width = icon.getBounds().width;
			String variant = ICSSConstants.TAB_OVERFLOW_INACTIVE; //$NON-NLS-1$
			if (activeState == AS_ACTIVE_FOCUS) {
				variant = "tabOverflowActive"; //$NON-NLS-1$
			}
			overflowButton.setData(RWT.CUSTOM_VARIANT, variant);
			overflowButton.moveAbove(tabBg);
			overflowButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					performOverflow();
				}
			});
		}
		if (tabBgHasInvisibleButtons()) {
			overflowButton.setVisible(true);
		} else {
			overflowButton.setVisible(false);
		}
	}

	private void performOverflow() {
		activatePart(currentPart);
		Menu overflowMenu = new Menu(overflowButton);
		for (int i = 0; i < overflowButtons.size(); i++) {
			Object obj = buttonPartMap.get(overflowButtons.get(i));
			final IPresentablePart part = (IPresentablePart) obj;
			MenuItem item = new MenuItem(overflowMenu, SWT.PUSH);
			if (part != null) {
				item.setText(part.getName());
				item.setImage(part.getTitleImage());
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						activatePart(part);
						showPartButton(part);
					}
				});
			}
		}
		// show popup
		overflowButton.setMenu(overflowMenu);
		overflowMenu.setVisible(true);
		Display display = overflowButton.getDisplay();
		Point newLocation = display.map(overflowButton, null, 0, 10);
		overflowMenu.setLocation(newLocation);
	}

	private void showPartButton(IPresentablePart part) {
		Control button = partButtonMap.get(part);
		if (button != null && !button.isDisposed() && !button.isVisible()) {
			overflowButtons.remove(button);
			moveToTabBarEnd(button);
			button.setVisible(true);
		}
		if (tabBg != null && !tabBg.isDisposed()) {
			tabBg.layout(true, true);
			manageOverflow();
		}
	}

	private void moveToTabBarEnd(Control partButton) {
		Control lastInvisibleButton = getLastInvisibleButton();
		if (lastInvisibleButton != null) {
			if (lastInvisibleButton.isVisible()) {
				partButton.moveBelow(lastInvisibleButton);
			} else {
				partButton.moveAbove(lastInvisibleButton);
			}
		} else {
			partButton.moveBelow(null);
		}
	}

	private void showLastChildIfNecessary(int recursionCount) {
		Control childToShow = getLastInvisibleButton();
		if (childToShow != null && futureTabChildrenSize(childToShow) < tabBg.getBounds().width && tabBgHasInvisibleButtons()) {
			childToShow.setVisible(true);
			IPresentablePart part = buttonPartMap.get(childToShow);
			makePartButtonInactive(part);
			overflowButtons.remove(childToShow);
			tabBg.layout(true, true);
			if (recursionCount <= tabBg.getChildren().length) {
				int newCount = recursionCount + 1;
				showLastChildIfNecessary(newCount);
			}
		}
	}

	private boolean tabBgHasInvisibleButtons() {
		boolean result = false;
		Control[] children = tabBg.getChildren();
		for (int i = 0; i < children.length && !result; i++) {
			if (!children[i].isVisible()) {
				result = true;
			}
		}
		return result;
	}

	private int futureTabChildrenSize(Control childToShow) {
		int result = 0;
		result = getTabChildrenSize();
		result += childToShow.getBounds().width;
		result += BUTTON_SPACING;
		return result;
	}

	private Control getLastInvisibleButton() {
		Control result = null;
		Control[] children = tabBg.getChildren();
		boolean childShowedUp = false;
		for (int i = children.length - 1; i >= 0 && !childShowedUp; i--) {
			if (children[i].isVisible()) {
				if (children.length >= (i + 2)) {
					result = children[i + 1];
				} else {
					result = children[i];
				}
				childShowedUp = true;
			}
		}
		return result;
	}

	/*
	 * Returns the control which was hide.
	 */
	private Control hideLastVisibleButton() {
		Control result = null;
		if (tabBg != null && !tabBg.isDisposed()) {
			Control[] children = tabBg.getChildren();
			boolean lastChildHidden = false;
			for (int i = children.length - 1; i >= 0 && !lastChildHidden; i--) {
				if (children[i].isVisible()) {
					if (isButtonActive(children[i])) {
						if (i > 0) {
							children[i - 1].setVisible(false);
							result = children[i - 1];
							if (!overflowButtons.contains(children[i - 1])) {
								overflowButtons.add(children[i - 1]);
							}
							children[i].moveAbove(children[i - 1]);
						}
					} else {
						children[i].setVisible(false);
						result = children[i];
						overflowButtons.add(children[i]);
					}
					lastChildHidden = true;
					tabBg.layout(true, true);
				}
			}
		}
		return result;
	}

	private boolean isButtonActive(Control control) {
		boolean result = false;
		// check against the button variant
		if (control instanceof Composite) {
			Composite buttonArea = (Composite) control;
			Control[] children = buttonArea.getChildren();
			for (int i = 0; i < children.length && !result; i++) {
				if (children[i] instanceof Button) {
					Object data = children[i].getData(RWT.CUSTOM_VARIANT);
					if (data.equals(ICSSConstants.PART_INACTIVE_ACTIVE) || data.equals(ICSSConstants.PART_ACTIVE)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	private int getTabChildrenSize() {
		int result = 0;
		Control[] children = tabBg.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isVisible() && !children[i].isDisposed()) {
				result += children[i].getBounds().width;
				result += BUTTON_SPACING;
			}
		}
		return result;
	}

	private void createConfArea(FormData fdTabBg) {
		final ConfigurationAction configAction = getConfigAction();

		if (configAction != null) {
			confArea = new Composite(getTabBar(), SWT.NONE);
			Image confBg = stackBuilder.getImage(ILayoutSetConstants.STACK_CONF_BG_INACTIVE);
			confArea.setBackgroundImage(confBg);
			confArea.setLayout(new FormLayout());
			confArea.setBackgroundMode(SWT.INHERIT_FORCE);
			FormData fdConfArea = new FormData();
			confArea.setLayoutData(fdConfArea);
			fdConfArea.top = new FormAttachment(0);
			fdConfArea.bottom = new FormAttachment(100);
			fdConfArea.right = new FormAttachment(100);
			fdConfArea.width = 28;
			fdTabBg.right = new FormAttachment(confArea);
			confCorner = new Label(confArea, SWT.NONE);
			addPartActivationListnerToControl(confCorner);
			Image cornerImage = stackBuilder.getImage(ILayoutSetConstants.STACK_INACTIVE_CORNER);
			confCorner.setImage(cornerImage);
			FormData fdCorner = new FormData();
			confCorner.setLayoutData(fdCorner);
			fdCorner.left = new FormAttachment(0);
			fdCorner.top = new FormAttachment(0);
			fdCorner.bottom = new FormAttachment(100);
			// confButton = new Button( confArea, SWT.PUSH );
			// Image confImage = stackBuilder.getImage(
			// ILayoutSetConstants.STACK_CONF_INACTIVE );
			// confButton.setImage( confImage );
			//      confButton.setData( RWT.CUSTOM_VARIANT, "clearButton" ); //$NON-NLS-1$
			// FormData fdConfButton = stackBuilder.getPosition(
			// ILayoutSetConstants.STACK_CONF_POSITION );
			// confButton.setLayoutData( fdConfButton );
			// FormData fdConfPos = stackBuilder.getPosition(
			// ILayoutSetConstants.STACK_CONF_POS );
			// fdConfButton.right = fdConfPos.right;
			// addPartActivationListnerToControl( confButton );
			// confButton.addSelectionListener( new SelectionAdapter(){
			// public void widgetSelected( SelectionEvent e ) {
			// configAction.run();
			// }
			// } );
		} else {
			// make tabarea full width if no confarea exist.
			fdTabBg.right = new FormAttachment(100);
		}
	}

	public void dispose() {
		ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
		registry.removeViewPartPresentation(this);
		presentationControl.dispose();
	}

	public Control getControl() {
		return presentationControl;
	}

	public Control[] getTabList(IPresentablePart part) {
		ArrayList<Control> list = new ArrayList<Control>();
		if (getControl() != null) {
			list.add(getControl());
		}
		if (part.getToolBar() != null) {
			list.add(part.getToolBar());
		}
		if (part.getControl() != null) {
			list.add(part.getControl());
		}
		return list.toArray(new Control[list.size()]);
	}

	public void removePart(IPresentablePart oldPart) {
		Object object = partButtonMap.get(oldPart);
		buttonPartMap.remove(object);
		// remove the dirtyListener
		Object listener = dirtyListenerMap.get(oldPart);
		if (listener != null && listener instanceof IPropertyListener) {
			oldPart.removePropertyListener((IPropertyListener) listener);
		}
		partButtonMap.remove(oldPart);
		buttonList.remove(object);
		handleButtonDispose(object);
		partList.remove(oldPart);
		oldPart.setVisible(false);
		tabBg.layout();
	}

	private void handleButtonDispose(Object buttonArea) {
		if (buttonArea != null && !isStandalone()) {
			((Composite) buttonArea).dispose();
		}
	}

	public void selectPart(IPresentablePart toSelect) {
		if (toSelect != null) {
			toSelect.setVisible(true);
		}
		if (currentPart != null) {
			oldPart = currentPart;
			if (currentPart instanceof PresentablePart && ((PresentablePart) currentPart).getPane() != null) {
				currentPart.setVisible(false);
			}
		}
		makePartButtonInactive(currentPart);
		currentPart = toSelect;
		currentPart.getControl().moveAbove(null);
		makePartButtonActive(currentPart);
		setBounds(presentationControl.getBounds());
	}

	public StackDropResult dragOver(Control currentControl, Point location) {
		return null;
	}

	public void setActive(int newState) {
		activeState = newState;
		Image confBg = null;
		Image cornerImage = null;
		// Image confImage = null;
		Image tabBgImage = null;
		String tabOverflow = ICSSConstants.TAB_OVERFLOW_INACTIVE; //$NON-NLS-1$
		// create the necessary images
		if (newState == AS_ACTIVE_FOCUS) {
			if (!isStandalone()) {
				changeSelectedActiveButton(true);
			}
			confBg = stackBuilder.getImage(ILayoutSetConstants.STACK_CONF_BG_ACTIVE);
			String rightActive = ILayoutSetConstants.STACK_TAB_INACTIVE_RIGHT_ACTIVE;
			cornerImage = stackBuilder.getImage(rightActive);
			// confImage = stackBuilder.getImage(
			// ILayoutSetConstants.STACK_CONF_ACTIVE );
			tabOverflow = "tabOverflowActive"; //$NON-NLS-1$
			tabBgImage = stackBuilder.getImage(ILayoutSetConstants.STACK_TAB_BG_ACTIVE);
			changeStack(true);
		} else {
			if (!isStandalone()) {
				changeSelectedActiveButton(false);
			}
			confBg = stackBuilder.getImage(ILayoutSetConstants.STACK_CONF_BG_INACTIVE);
			cornerImage = stackBuilder.getImage(ILayoutSetConstants.STACK_INACTIVE_CORNER);
			// confImage = stackBuilder.getImage(
			// ILayoutSetConstants.STACK_CONF_INACTIVE );
			String stackTabInactiveBgActive = ILayoutSetConstants.STACK_TAB_INACTIVE_BG_ACTIVE;
			tabBgImage = stackBuilder.getImage(stackTabInactiveBgActive);
			changeStack(false);
		}

		// set the images
		if (tabBg != null) {
			tabBg.getParent().setBackgroundImage(tabBgImage);
		}
		if (confArea != null) {
			confArea.setBackgroundImage(confBg);
			if (confCorner != null) {
				confCorner.setImage(cornerImage);
			}
			// if( confButton != null ) {
			// confButton.setImage( confImage );
			// }
			confArea.getParent().layout(true);
			if (currentPart != null && getPartPane(currentPart) != null) {
				currentPart.setVisible(true);
			}
			confArea.layout(true);
		}
		if (overflowButton != null) {
			overflowButton.setData(RWT.CUSTOM_VARIANT, tabOverflow);
		}
		setBounds(presentationControl.getBounds());
	}

	@SuppressWarnings("rawtypes")
	private void changeStack(boolean active) {
		Object adapter = stackBuilder.getAdapter(Map.class);
		if (adapter != null && adapter instanceof Map) {
			Map labelMap = (Map) adapter;
			Label leftLabel = (Label) labelMap.get(StackPresentationBuider.LEFT);
			Label rightLabel = (Label) labelMap.get(StackPresentationBuider.RIGHT);
			Label leftBorder = (Label) labelMap.get(StackPresentationBuider.LEFT_BORDER);
			Label rightBorder = (Label) labelMap.get(StackPresentationBuider.RIGHT_BORDER);
			Label bottomBorder = (Label) labelMap.get(StackPresentationBuider.BOTTOM_BORDER);
			Label topBorder = (Label) labelMap.get(StackPresentationBuider.TOP_BORDER);
			Image left;
			Image right;
			Image leftBorderImg;
			Image rightBorderImg;
			Image bottomBorderImg;
			Image topBorderImg;
			if (active) {
				String leftActive = ILayoutSetConstants.STACK_TABBAR_LEFT_ACTIVE;
				left = stackBuilder.getImage(leftActive);
				String rightActive = ILayoutSetConstants.STACK_TABBAR_RIGHT_ACTIVE;
				right = stackBuilder.getImage(rightActive);
				String bottomActive = ILayoutSetConstants.STACK_BORDER_BOTTOM_ACTIVE;
				bottomBorderImg = stackBuilder.getImage(bottomActive);
				String leftBorderActive = ILayoutSetConstants.STACK_BORDER_LEFT_ACTIVE;
				leftBorderImg = stackBuilder.getImage(leftBorderActive);
				String rightBorderActive = ILayoutSetConstants.STACK_BORDER_RIGHT_AVTIVE;
				rightBorderImg = stackBuilder.getImage(rightBorderActive);
				String stackTopStandaloneActive = ILayoutSetConstants.STACK_TOP_STANDALONE_ACTIVE;
				topBorderImg = stackBuilder.getImage(stackTopStandaloneActive);
			} else {
				String leftInactive = ILayoutSetConstants.STACK_TABBAR_LEFT_INACTIVE;
				left = stackBuilder.getImage(leftInactive);
				String rightInactive = ILayoutSetConstants.STACK_TABBAR_RIGHT_INACTIVE;
				right = stackBuilder.getImage(rightInactive);
				bottomBorderImg = stackBuilder.getImage(ILayoutSetConstants.STACK_BORDER_BOTTOM);
				leftBorderImg = stackBuilder.getImage(ILayoutSetConstants.STACK_BORDER_LEFT);
				rightBorderImg = stackBuilder.getImage(ILayoutSetConstants.STACK_BORDER_RIGHT);
				String stackTopStandaloneInactive = ILayoutSetConstants.STACK_TOP_STANDALONE_INACTIVE;
				topBorderImg = stackBuilder.getImage(stackTopStandaloneInactive);
			}
			leftLabel.setImage(left);
			rightLabel.setImage(right);
			leftBorder.setBackgroundImage(leftBorderImg);
			rightBorder.setBackgroundImage(rightBorderImg);
			bottomBorder.setBackgroundImage(bottomBorderImg);
			// top image for standalone view
			if (isStandalone() && topBorderImg != null) {
				topBorder.setBackgroundImage(topBorderImg);
				int height = topBorderImg.getBounds().height;
				FormData fdTopBorder = (FormData) topBorder.getLayoutData();
				fdTopBorder.height = height;
				fdTopBorder.top = new FormAttachment(0, 7);
				FormData fdLeftBorder = (FormData) leftBorder.getLayoutData();
				FormData fdRightBorder = (FormData) rightBorder.getLayoutData();
				fdLeftBorder.top = new FormAttachment(0, height + 6);
				fdRightBorder.top = new FormAttachment(0, height + 6);
				topBorder.getParent().layout(true);
				topBorder.moveAbove(null);
			}
		}
	}

	private PartPane getPartPane(IPresentablePart part) {
		PartPane result = null;
		if (part instanceof PresentablePart) {
			result = ((PresentablePart) part).getPane();
		}
		return result;
	}

	private void changeSelectedActiveButton(boolean selected) {
		String close = ""; //$NON-NLS-1$
		Color buttonAreaBg;
		String font = ""; //$NON-NLS-1$
		String tab = ""; //$NON-NLS-1$
		if (selected) {
			buttonAreaBg = stackBuilder.getColor(ILayoutSetConstants.STACK_BUTTON_ACTIVE);
			close = ICSSConstants.VIEW_CLOSE; //$NON-NLS-1$
			font = ICSSConstants.PART_INACTIVE_ACTIVE; //$NON-NLS-1$
			tab = ICSSConstants.TAB_ACTIVE; //$NON-NLS-1$
		} else {
			buttonAreaBg = stackBuilder.getColor(ILayoutSetConstants.STACK_BUTTON_INACTIVE);
			close = ICSSConstants.VIEW_CLOSE_INACTIVE; //$NON-NLS-1$
			font = ICSSConstants.PART_ACTIVE; //$NON-NLS-1$
			tab = ICSSConstants.TAB_INACTIVE; //$NON-NLS-1$
		}
		Object object = partButtonMap.get(currentPart);
		if (object != null && object instanceof Composite) {
			Composite buttonArea = (Composite) object;
			buttonArea.setData(RWT.CUSTOM_VARIANT, tab);
			buttonArea.setBackground(buttonAreaBg);
			Control[] children = buttonArea.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child instanceof Button) {
					Button button = (Button) child;
					if (button.getData(BUTTON_ID) != null) {
						button.setData(RWT.CUSTOM_VARIANT, close);
					} else {
						button.setData(RWT.CUSTOM_VARIANT, font);
					}
				}
			}
		}
	}

	public void setBounds(Rectangle bounds) {
		presentationControl.setBounds(bounds);
		Composite tabBar = getTabBar();
		if (currentPart != null && tabBar != null && getPartPane(currentPart) != null) {
			int newHeight = bounds.height - 16;
			int partBoundsY = bounds.y + 8;
			if (getTabBar().isVisible()) {
				newHeight -= (tabBar.getBounds().height);
				partBoundsY += tabBar.getBounds().height;
			}

			Control toolBar = currentPart.getToolBar();
			if (toolbarBg != null && (toolbarBg.isVisible() || toolBar != null)) {
				int toolbarHeight = toolbarBg.getBounds().height;
				newHeight -= toolbarHeight;
				partBoundsY += toolbarHeight;
			}
			String stackTopStandaloneActive = ILayoutSetConstants.STACK_TOP_STANDALONE_ACTIVE;
			Image stackTop = stackBuilder.getImage(stackTopStandaloneActive);
			if (stackTop != null) {
				partBoundsY += 1;
				newHeight -= 1;
			}
			Rectangle partBounds = new Rectangle(bounds.x + 8, partBoundsY, bounds.width - 16, newHeight);
			currentPart.setBounds(partBounds);
		}
		layoutToolBar();
	}

	private Composite getTabBar() {
		Composite result = null;
		Object adapter = stackBuilder.getAdapter(this.getClass());
		if (adapter != null && adapter instanceof Composite) {
			result = (Composite) adapter;
		}
		return result;
	}

	public void setState(int state) {
	}

	public void setVisible(boolean isVisible) {
		if (currentPart != null && getPartPane(currentPart) != null) {
			currentPart.setVisible(isVisible);
			// Toolbar Layer
			deactivated = !isVisible;
			layoutToolBar();
			setBounds(presentationControl.getBounds());
		}
	}

	public void showPaneMenu() {
	}

	public void showSystemMenu() {
	}

	public int computePreferredSize(final boolean width, final int availableParallel, final int availablePerpendicular,
			final int preferredResult) {
		int result = preferredResult;
		if (width) {
			// preferred width
			int minWidth = calculateMinimumWidth();
			if (getSite().getState() == IStackPresentationSite.STATE_MINIMIZED || preferredResult < minWidth) {
				result = minWidth;
			}
		} else {
			// preferred height
			result = calculateMinimumHeight();
		}
		return result;
	}

	/*
	 * Returns the height of the tabbar plus a spacing
	 */
	private int calculateMinimumHeight() {
		int result = 0;
		if (tabBg != null) {
			tabBg.pack();
			result = tabBg.getSize().y;
		}
		return result + HEIGHT_SPACING;
	}

	/*
	 * Calculates the width of the biggest child
	 */
	private int calculateMinimumWidth() {
		int result = 0;
		if (tabBg != null) {
			Control[] children = tabBg.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].getSize().x >= result) {
					result = children[i].getSize().x;
				}
			}
		}
		return result + WIDTH_SPACING;
	}

}
