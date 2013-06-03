package com.sg.widget.part;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

public class ColumnAutoResizer {

	public ColumnAutoResizer(final Composite panel, final TreeColumn column) {
		panel.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				column.setWidth(panel.getBounds().width - 10);
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	}
	
	public ColumnAutoResizer(final Composite panel, final TableColumn column) {
		panel.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				column.setWidth(panel.getBounds().width - 10);
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	}

}
