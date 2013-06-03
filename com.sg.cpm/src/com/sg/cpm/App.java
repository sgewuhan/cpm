package com.sg.cpm;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

import com.sg.user.ui.LoginPage;

/**
 * This class controls all aspects of the application's execution
 * and is contributed through the plugin.xml.
 */
public class App implements EntryPoint {

//	private void registerService() {
//		ServiceManager manager = RWT.getServiceManager();
//		String url = manager.getServiceHandlerUrl("downloadServiceHandler");
//		if(url==null){
//			manager.registerServiceHandler( "downloadServiceHandler", new DownloadServiceHandler() );
//		}
//		String url1 = manager.getServiceHandlerUrl("gridFSDownloadServiceHandler");
//		if(url1==null){
//			manager.registerServiceHandler( "gridFSDownloadServiceHandler", new GridFSDownloadServiceHandler() );
//		}
//	}

	public void stop() {
	}

	@Override
	public int createUI() {
		Display display = PlatformUI.createDisplay();
//		String para = RWT.getRequest().getParameter("test");
		WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
//		registerService();
		LoginPage.OpenPage();
		int result = PlatformUI.createAndRunWorkbench(display, advisor);
		return result;
	}
}
