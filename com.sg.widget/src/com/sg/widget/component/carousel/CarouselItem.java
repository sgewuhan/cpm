/******************************************************************************* 
* Copyright (c) 2010, 2011 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/ 
package com.sg.widget.component.carousel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;


public class CarouselItem {
  
  private String resourceName;
  private List<SelectionListener> listeners;
  private Carousel parent;

  public CarouselItem( Carousel parent, Image icon ) {
    this.parent = parent;
    resourceName = parent.registerItem(this,icon);
  }
  
  String getIconResourceName() {
    return resourceName;
  }
  
  public void addSelectionListener( SelectionListener listener ) {
    lazyInitListeners();
    listeners.add( listener );
  }

  private void lazyInitListeners() {
    if( listeners == null ) {
      listeners = new ArrayList<SelectionListener>();
    }
  }
  
  public void removeSelectionListener( SelectionListener listener ) {
    if( listeners != null ) {
      listeners.remove( listener );
    }
  }

  void fireClickEvent() {
    if( listeners != null ) {
      for( SelectionListener listener : listeners ) {
        Event event = new Event();
        event.widget = parent;
        event.display = parent.getDisplay();
        event.type = SWT.Selection;
        listener.widgetSelected( new SelectionEvent( event ) );
      }
    }
  }
}
