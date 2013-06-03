package com.sg.common.model;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class CompositionSelectionProvider implements ISelectionProvider, ISelectionChangedListener {
    ISelection selection;

    ListenerList listeners = new ListenerList();

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.add(listener);
    }

    public ISelection getSelection() {
        return selection;
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.remove(listener);
    }

	public void setSelection(ISelection selection) {
        this.selection = selection;
        final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
        Object[] listenersArray = listeners.getListeners();

        for (int i = 0; i < listenersArray.length; i++) {
            final ISelectionChangedListener l = (ISelectionChangedListener) listenersArray[i];
            SafeRunner.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(e);
                }
            });
        }
    }

    /*****************************************************************
    ************** ISelectionChangedListener implementation **********
    ******************************************************************/
    public void selectionChanged(SelectionChangedEvent event) {
        setSelection(event.getSelection());

    }

}