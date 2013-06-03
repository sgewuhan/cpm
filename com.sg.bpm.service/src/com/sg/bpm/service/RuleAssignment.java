package com.sg.bpm.service;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.sg.bpm.service.actor.IActorIdProvider;


public class RuleAssignment {

	private String name;
	private IActorIdProvider actorIdProvider;

	protected RuleAssignment(IConfigurationElement element) {
		name = element.getAttribute("name");
		try {
			actorIdProvider = (IActorIdProvider) element.createExecutableExtension("actorIdProvider");
		} catch (CoreException e) {
		}
	}

	public String getName() {

		return name;
	}
	
	public String getActorId(Object[] input){
		if(actorIdProvider!=null){
			return actorIdProvider.getActorId(input);
		}
		return null;
	}

}
