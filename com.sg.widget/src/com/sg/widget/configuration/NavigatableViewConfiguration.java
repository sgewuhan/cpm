package com.sg.widget.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

public class NavigatableViewConfiguration {

	private IConfigurationElement conf;
	private String queryViewerId;
	private String singleObjectEditorId;
	
	private Map<String,Map<String,String>> selectionProviders = new HashMap<String,Map<String,String>>();
	private boolean initLoad;

	public NavigatableViewConfiguration(IConfigurationElement ice) {
		conf = ice;
		
		queryViewerId = ice.getAttribute("queryViewerId");
		
		singleObjectEditorId = ice.getAttribute("singleObjectEditorId");
		
		IConfigurationElement[] children = ice.getChildren("selectionProvider" );
		
		for(IConfigurationElement child: children){
			String partId = child.getAttribute("partId");
			IConfigurationElement[] parasCe = child.getChildren("parameterEvaluation" );
			HashMap<String, String> paramap = new HashMap<String,String>();
			for(IConfigurationElement para:parasCe){
				paramap.put(para.getAttribute("getValueFromWhichKey"), para.getAttribute("putValueFromWhichParameter"));
			}
			selectionProviders.put(partId, paramap);
		}
		initLoad = "true".equals(ice.getAttribute("initLoad"));
	}
	
	
	public Set<String> getSelectionProviderId(){
		return selectionProviders.keySet();
	}
	
	public Map<String,String> getParametersMap(String providerId){
		return selectionProviders.get(providerId);
	}
	
	public String getQueryViewerId(){
		return queryViewerId;
	}
	
	public String getSingleObjectEditorId(){
		return singleObjectEditorId;
	}
	
	public IConfigurationElement getConfiguration(){
		return conf;
	}


	public boolean isInitLoad() {
		return initLoad;
	}

}
