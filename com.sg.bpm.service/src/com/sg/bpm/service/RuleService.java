package com.sg.bpm.service;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;



public class RuleService {
	
	private static final String EXP_ID = "actorRule";
	private static final String ACTOR_RULE_ID = "actorRule";
	private static final String PLUGIN_ID = "com.sg.bpm.service";


	public RuleAssignment getRuleAssignment(String id){
		IExtensionRegistry eReg = Platform.getExtensionRegistry();
		IExtensionPoint ePnt = eReg.getExtensionPoint(PLUGIN_ID, EXP_ID);
		if (ePnt == null)
			return null;
		IExtension[] exts = ePnt.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			IConfigurationElement[] confs = exts[i].getConfigurationElements();
			for (int j = 0; j < confs.length; j++) {
				if (ACTOR_RULE_ID.equals(confs[j].getName())) {
					String confId = confs[j].getAttribute("id");
					if(confId.equals(id)){
						return new RuleAssignment(confs[j]);
					}
				}
			}
		}
		
		return null;
	}


	public String getActorRuleName(String actorParameter) {
		RuleAssignment rass = getRuleAssignment(actorParameter);
		if(rass==null){
			return null;
		}
		return rass.getName();
	}

}
