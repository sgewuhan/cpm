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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.sg.design.Design;
import com.sg.design.ILayoutSetConstants;
import com.sg.design.ext.IEditAreaSupport;
import com.sg.design.stacks.ViewStackPresentation;

public class StackPresentationBuider extends ElementBuilder {

	public static final String BOTTOM_BORDER = "bottomBorder"; //$NON-NLS-1$
	public static final String RIGHT_BORDER = "rightBorder"; //$NON-NLS-1$
	public static final String LEFT_BORDER = "leftBorder"; //$NON-NLS-1$
	public static final String TOP_BORDER = "topBorder"; //$NON-NLS-1$

	public static final String TOPLEFT_BORDER = "topLeftBorder"; //$NON-NLS-1$
	public static final String TOPRIGHT_BORDER = "topRightBorder"; //$NON-NLS-1$
	public static final String BOTTOMLEFT_BORDER = "bottomLeftBorder"; //$NON-NLS-1$
	public static final String BOTTOMRIGHT_BORDER = "bottomRightBorder"; //$NON-NLS-1$

	public static final String RIGHT = "right"; //$NON-NLS-1$
	public static final String LEFT = "left"; //$NON-NLS-1$
	private Image tabInactiveBgActive;
	private Composite content;
	private Image borderBottom;
	private Image borderTop;
	private Image borderLeft;
	private Image borderRight;

	private Image borderTopLeft;
	private Image borderTopRight;
	private Image borderBottomLeft;
	private Image borderBottomRight;

	private Composite tabBar;
	private Image leftCorner;
	private Image rightCorner;
	private Label leftCornerLabel;
	private Label rightCornerLabel;
	private Map<String, Label> labelMap;
	private String type;
	private Composite backgroundArea;

	public StackPresentationBuider(Composite parent, String layoutSetId,String type) {
		super(parent, layoutSetId);
		labelMap = new HashMap<String, Label>();
		this.type = type;
		init();
	}

	private void init() {
		tabInactiveBgActive = createImageById(ILayoutSetConstants.STACK_TAB_INACTIVE_BG_ACTIVE);

		borderBottom = createImageById(ILayoutSetConstants.STACK_BORDER_BOTTOM);
		borderTop = createImageById(ILayoutSetConstants.STACK_BORDER_TOP);
		borderLeft = createImageById(ILayoutSetConstants.STACK_BORDER_LEFT);
		borderRight = createImageById(ILayoutSetConstants.STACK_BORDER_RIGHT);

		borderBottomRight = createImageById(ILayoutSetConstants.STACK_BORDER_BOTTOM_RIGHT);
		borderTopRight = createImageById(ILayoutSetConstants.STACK_BORDER_TOP_RIGHT);
		borderBottomLeft = createImageById(ILayoutSetConstants.STACK_BORDER_BOTTOM_LEFT);
		borderTopLeft = createImageById(ILayoutSetConstants.STACK_BORDER_TOP_LEFT);

		leftCorner = createImageById(ILayoutSetConstants.STACK_TABBAR_LEFT_ACTIVE);
		rightCorner = createImageById(ILayoutSetConstants.STACK_TABBAR_RIGHT_ACTIVE);
	}

	private Image createImageById(final String id) {
		LayoutSet set = getLayoutSet();
		return createImage(set.getImagePath(id));
	}

	public void addControl(Control control, Object layoutData) {
	}

	public void addControl(Control control, String positionId) {
	}

	public void addImage(Image image, Object layoutData) {
	}

	public void addImage(Image image, String positionId) {
	}

	public void build() {
		getParent().setLayout(new FillLayout());
		Composite stack = createFrame();
		stack.setLayout(new FormLayout());

		tabBar = new Composite(stack, SWT.NONE);
		tabBar.setLayout(new FormLayout());
		tabBar.setBackgroundImage(tabInactiveBgActive);
		FormData fdTabBar = new FormData();
		tabBar.setLayoutData(fdTabBar);
		fdTabBar.top = new FormAttachment(0);
		fdTabBar.left = new FormAttachment(0);
		fdTabBar.right = new FormAttachment(100);
		fdTabBar.height = tabInactiveBgActive.getBounds().height;

		if (rightCorner != null && leftCorner != null) {
			leftCornerLabel = new Label(stack.getParent(), SWT.NONE);
			leftCornerLabel.setImage(leftCorner);
			FormData fdLeftCorner = new FormData();
			leftCornerLabel.setLayoutData(fdLeftCorner);
			fdLeftCorner.left = new FormAttachment(0, 3);
			fdLeftCorner.top = new FormAttachment(0, 7);

			rightCornerLabel = new Label(stack.getParent(), SWT.NONE);
			rightCornerLabel.setImage(rightCorner);
			FormData fdRightCorner = new FormData();
			rightCornerLabel.setLayoutData(fdRightCorner);
			fdRightCorner.right = new FormAttachment(100, -3);
			fdRightCorner.top = new FormAttachment(0, 7);
			rightCornerLabel.moveAbove(null);
			leftCornerLabel.moveAbove(null);
			labelMap.put(LEFT, leftCornerLabel);
			labelMap.put(RIGHT, rightCornerLabel);
		}

		// 内容显示区域如果需要定制编辑器的空白区域可从此处着手
		content = new Composite(stack, SWT.NONE);
		content.setBackground(Graphics.getColor(255, 255, 255));
		try {
			IEditAreaSupport ieas = Design.getEditAreaConfig();
			if (ieas != null) {
				if ("editor".equals(type)) {
					content.setLayout(new FillLayout());
					backgroundArea = ieas.creatEditorAreaPart(content);
				}
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FormData fdContent = new FormData();
		content.setLayoutData(fdContent);
		fdContent.top = new FormAttachment(tabBar,1);
		fdContent.left = new FormAttachment(0,1);
		fdContent.right = new FormAttachment(100,-1);
		fdContent.bottom = new FormAttachment(100,-1);
	}

	private Composite createFrame() {
		Composite frameComp = new Composite(getParent(), SWT.NONE);
		frameComp.setData(RWT.CUSTOM_VARIANT, "compGray"); //$NON-NLS-1$
		frameComp.setLayout(new FormLayout());
//		frameComp.setBackgroundMode(SWT.INHERIT_FORCE);

		// 创建左上角
		Label lTopLeft = createBorder(frameComp, borderTopLeft, TOPLEFT_BORDER);
		FormData fdTopLeft = new FormData();
		lTopLeft.setLayoutData(fdTopLeft);
		fdTopLeft.top = new FormAttachment(0);
		fdTopLeft.left = new FormAttachment(0);
		fdTopLeft.height = borderTopLeft.getBounds().height;
		fdTopLeft.width = borderLeft.getBounds().width;
		
		
		// 创建右上角
		Label lTopRight = createBorder(frameComp, borderTopRight,TOPRIGHT_BORDER);
		FormData fdTopRight = new FormData();
		lTopRight .setLayoutData(fdTopRight);
		fdTopRight.top = new FormAttachment(0);
		fdTopRight.right = new FormAttachment(100);
		fdTopRight.height = borderTopLeft.getBounds().height;
		fdTopRight.width = borderLeft.getBounds().width;
		
		
		// 创建左下角
		Label lBottomLeft = createBorder(frameComp, borderBottomLeft,BOTTOMLEFT_BORDER);
		FormData fdBottomLeft = new FormData();
		lBottomLeft.setLayoutData(fdBottomLeft);
		fdBottomLeft.bottom = new FormAttachment(100);
		fdBottomLeft.left = new FormAttachment(0);
		fdBottomLeft.height = borderTopLeft.getBounds().height;
		fdBottomLeft.width = borderLeft.getBounds().width;

		
		// 创建右下角
		Label lBottomRight = createBorder(frameComp, borderBottomRight,BOTTOMRIGHT_BORDER);
		FormData fdBottomRight = new FormData();
		lBottomRight.setLayoutData(fdBottomRight);
		fdBottomRight.bottom = new FormAttachment(100);
		fdBottomRight.right = new FormAttachment(100);
		fdBottomRight.width = borderLeft.getBounds().width;
		fdBottomRight.height = borderTopLeft.getBounds().height;

		// 创建上边线
		Label lTop = createBorder(frameComp, borderTop,TOP_BORDER);
		FormData fdTop = new FormData();
		lTop.setLayoutData(fdTop);
		fdTop.top = new FormAttachment(0);
		fdTop.height = borderTop.getBounds().height;
		fdTop.left = new FormAttachment(0,borderTopLeft.getBounds().width);
		fdTop.right = new FormAttachment(100,-borderTopRight.getBounds().width);
		
		// 创建下边线
		Label lBottom = createBorder(frameComp, borderBottom,BOTTOM_BORDER);
		FormData fdBottom  = new FormData();
		lBottom.setLayoutData(fdBottom);
		fdBottom.bottom = new FormAttachment(100);
		fdBottom.height = borderBottom.getBounds().height;
		fdBottom.left = new FormAttachment(0,borderBottomLeft.getBounds().width);
		fdBottom.right = new FormAttachment(100,-borderBottomRight.getBounds().width);
		
		// 创建左边线
		Label lLeft = createBorder(frameComp, borderLeft,LEFT_BORDER);
		FormData fdLeft = new FormData();
		lLeft.setLayoutData(fdLeft);
		fdLeft.left = new FormAttachment(0);
		fdLeft.width = borderLeft.getBounds().width;
		fdLeft.top = new FormAttachment(0,borderTopLeft.getBounds().height);
		fdLeft.bottom = new FormAttachment(100,-borderBottomLeft.getBounds().height);
		
		// 创建右边线
		Label lRight = createBorder(frameComp, borderRight,RIGHT_BORDER);
		FormData fdRight  = new FormData();
		lRight.setLayoutData( fdRight);
		fdRight.right = new FormAttachment(100);
		fdRight.width = borderRight.getBounds().width;
		fdRight.top = new FormAttachment(0,borderTopRight.getBounds().height);
		fdRight.bottom = new FormAttachment(100,-borderBottomRight.getBounds().height);
		
		Composite result = new Composite(frameComp, SWT.NONE);
		result.setData(RWT.CUSTOM_VARIANT, "compGray"); //$NON-NLS-1$
		FormData fdResult = new FormData();
		result.setLayoutData(fdResult);
		
		int  centerLine = (borderTop.getBounds().height-1)/2;
		int offset = 0;
		fdResult.top = new FormAttachment(0,offset+centerLine);
		fdResult.left = new FormAttachment(0,offset+centerLine);
		fdResult.right = new FormAttachment(100,-centerLine-offset);
		fdResult.bottom = new FormAttachment(100,-centerLine-offset);

		return result;
	}

	private Label createBorder(Composite frameComp, Image img, String key) {
		Label l = new Label(frameComp, SWT.NONE);
		l.setData(RWT.CUSTOM_VARIANT, "stackBorder"); //$NON-NLS-1$
		l.setBackgroundImage(img);
		labelMap.put(key, l);
		return l;
	}

	public void dispose() {
	}

	public Control getControl() {
		return content;
	}

	public Point getSize() {
		Point result = null;
		if (content != null) {
			result = content.getSize();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter) {
		Object result = null;
		if (adapter == ViewStackPresentation.class) {
			result = tabBar;
		} else if (adapter == Map.class) {
			result = labelMap;
		} 
		else if (adapter == Composite.class){
			result = backgroundArea;
		}
		return result;
	}
}
