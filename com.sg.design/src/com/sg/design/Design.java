package com.sg.design;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.sg.design.ext.IEditAreaSupport;
import com.sg.design.ext.IHeadAreaSupport;

public class Design  {
	private static IEditAreaSupport ieas;
	private static IHeadAreaSupport ihas;
	private static boolean ieasLoaded = false;
	private static boolean headpicAreaLoaded = false;

	public static IEditAreaSupport getEditAreaConfig() {
		if(ieasLoaded){
			return ieas;
		}
		IExtensionPoint ePnt = Platform.getExtensionRegistry().getExtensionPoint("com.sg.design", "editorAreaPart");
		if (ePnt == null)
			return null;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("editorAreaPart".equals(confs[j].getName())) {
					try {
						ieas = (IEditAreaSupport) confs[j].createExecutableExtension("getEditorAreaPart");
						ieasLoaded = true;
						return ieas;
					} catch (CoreException e) {
					}
				}
			}
		}
		return null;
	}
	
	public static IHeadAreaSupport getHeadAreaConfig() {
		if(headpicAreaLoaded){
			return ihas;
		}
		IExtensionPoint ePnt = Platform.getExtensionRegistry().getExtensionPoint("com.sg.design", "headAreaPart");
		if (ePnt == null)
			return null;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if ("headAreaPart".equals(confs[j].getName())) {
					try {
						ihas = (IHeadAreaSupport) confs[j].createExecutableExtension("getHeadAreaPart");
						headpicAreaLoaded = true;
						return ihas;
					} catch (CoreException e) {
					}
				}
			}
		}
		return null;
	}
	
	

}
