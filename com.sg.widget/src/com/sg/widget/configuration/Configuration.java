package com.sg.widget.configuration;

import org.eclipse.core.runtime.IConfigurationElement;

public class Configuration {

	protected IConfigurationElement configuration;

	public Configuration(IConfigurationElement ce) {
		setConfigurationElement(ce);
	}
	
	public Configuration(){
		
	}
	
	public void setConfigurationElement(IConfigurationElement ce){
		this.configuration = ce;
	}
	
	public IConfigurationElement getConfigurationElement(){
		return configuration;
	}
}
