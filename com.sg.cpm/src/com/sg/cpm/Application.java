package com.sg.cpm;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		new App().createUI();
		return new Integer( 0 );
	}

	@Override
	public void stop() {

	}

}
