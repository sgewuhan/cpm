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
package com.sg.design.business.builder;

import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.sg.design.Design;
import com.sg.design.ILayoutSetConstants;
import com.sg.design.builder.DummyBuilder;
import com.sg.design.ext.IHeadAreaSupport;


public class HeaderBuilder extends ElementBuilder {

//  private static final int LOGOSPACING = 20;
  private Image left;
  private Image leftBg;
  private Image logo;
  private Image right;
//  private Image rightBg;
//  private Image wave;
  private FormData fdLogo;
  private Control toolBar;
private Composite head;

  public HeaderBuilder( Composite parent, String layoutSetId ) {
    super( parent, layoutSetId );
    initLayoutData();
  }

  private void initLayoutData() {
    LayoutSet set = getLayoutSet();
    // images
    left = createImage( set.getImagePath( ILayoutSetConstants.HEADER_LEFT ) );
    leftBg = createImage( set.getImagePath( ILayoutSetConstants.HEADER_LEFT_BG ) );    
    right = createImage( set.getImagePath( ILayoutSetConstants.HEADER_RIGHT ) );
    // logo
    ElementBuilder builder 
      = new DummyBuilder( null, ILayoutSetConstants.SET_ID_LOGO );
    logo = builder.getImage( ILayoutSetConstants.LOGO );
    // positions
    LayoutSet layoutSet = ( LayoutSet ) builder.getAdapter( LayoutSet.class );
    fdLogo = layoutSet.getPosition( ILayoutSetConstants.LOGO_POSITION );
  }

  public void addControl( Control control, Object layoutData ) {
    toolBar = control;
    toolBar.setLayoutData( layoutData );
  }

  public void addControl( Control control, String positionId ) {
  }

  public void addImage( Image image, Object layoutData ) {
  }

  public void addImage( Image image, String positionId ) {
  }

  public void build() {
    Composite parent = getParent();
    
//    GridLayout layout = new GridLayout(4,false);
//    layout.horizontalSpacing = 0;
//    layout.verticalSpacing = 0;
//    layout.marginHeight = 0;
//    layout.marginWidth = 0;

    parent.setLayout(new FormLayout());
    
    
    parent.setBackgroundMode( SWT.INHERIT_FORCE );
    
    // left border
    Label leftLabel = new Label( parent, SWT.NONE );
    leftLabel.setImage( left );
    FormData fd = new FormData();
    leftLabel.setLayoutData(fd);
    fd.left = new FormAttachment(0);
    fd.top = new FormAttachment(0);
    fd.bottom = new FormAttachment(100);
    fd.width = left.getBounds().width;
    
//    leftLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
    
    //插入可配置的初始化区域
    IHeadAreaSupport hac = Design.getHeadAreaConfig();
    Composite headPartArea = null;
    if(hac!=null){
    	headPartArea = new Composite(parent,SWT.NONE);
    	headPartArea.setBackgroundImage( leftBg );
    	headPartArea.setLayout(new FormLayout());
    	hac.creatHeadAreaPart(headPartArea);
        
    	fd = new FormData();
        headPartArea.setLayoutData(fd);
        fd.left = new FormAttachment(leftLabel, 0);
        fd.top = new FormAttachment(0);
        fd.bottom = new FormAttachment(100);
        fd.width = 380;
        
        Image newLogo = hac.getCenterLogo();
        if(newLogo!=null){
        	logo = newLogo;
        }
    }
    
    Composite logoArea = new Composite( parent, SWT.NONE );
    logoArea.setLayout( new FormLayout() );
    logoArea.setBackgroundImage( leftBg );
//    logoArea.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
    
    Label logoLabel = new Label( logoArea, SWT.NONE );
    logoLabel.setImage( logo ); 
    logoLabel.setLayoutData( fdLogo );
    fdLogo.height = logo.getBounds().height;
    fdLogo.width = logo.getBounds().width;

    
    fd = new FormData();
    logoArea.setLayoutData(fd);
    fd.left = new FormAttachment((headPartArea==null?leftLabel:headPartArea), 0);
    fd.top = new FormAttachment(0);
    fd.bottom = new FormAttachment(100);
    fd.width = logo.getBounds().width;

    head = new Composite( parent, SWT.NONE );
    head.setBackgroundImage( right );
    head.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    head.setBackgroundMode( SWT.INHERIT_NONE );
    
    
    fd = new FormData();
    head.setLayoutData(fd);
    fd.left = new FormAttachment(logoArea, 0);
    fd.top = new FormAttachment(0);
    fd.bottom = new FormAttachment(100);
    fd.right = new FormAttachment(100);
    
  }


  public void dispose() {
  }

  public Control getControl() {
    return head;
  }

  public Point getSize() {
    return head.getSize();
  }
  
  @SuppressWarnings("rawtypes")
public Object getAdapter( Class adapter ) {
    Object result = null;
    if( adapter == Composite.class ) {
      result = head;
    }
    return result;
  }
}
