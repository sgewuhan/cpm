package com.sg.widget.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.sg.widget.configuration.Configuration;
import com.sg.widget.util.Util;

public class Enumerate  extends Configuration{
	private List<Enumerate> children;
	private IEnumerateContribution ectr;
	
	private String id;
	private String label;
	private Object value;

	public Enumerate(IConfigurationElement ce) {
		setConfigurationElement(ce);
	}
	
	public Enumerate(String id,String label,Object value,List<Enumerate> children) {
		this.id = id;
		this.label = label;
		this.value = value;
		this.children = children;
	}
	
	public Enumerate(String id,String labelAndValue){
		this.id = id;
		this.label = labelAndValue;
		this.value = labelAndValue;
	}
	
	public Enumerate(String labelAndValue){
		this.id = Util.getRandomString(8);
		this.label = labelAndValue;
		this.value = labelAndValue;
	}
	
	public void setConfigurationElement(IConfigurationElement ce) {
		super.setConfigurationElement(ce);		
		id = ce.getAttribute("id");
		try {
			ectr = (IEnumerateContribution) ce
					.createExecutableExtension("enumerateContribution");
		} catch (CoreException e) {
		}

		if (ectr != null) {
			label = ectr.getlabel();
			value = ectr.getValue();
			children = ectr.getChildren();
		} else {
			label = ce.getAttribute("label");
			value = ce.getAttribute("value");
			IConfigurationElement[] ces = ce.getChildren("enumerate");
			children = new ArrayList<Enumerate>();
			if (ces != null) {
				for (int i = 0; i < ces.length; i++) {
					children.add(new Enumerate(ces[i]));
				}
			}
		}
	}

	public String getId() {
		return id;
	}
	
	public String getLabel(){
		return label;
	}
	
	public Object getValue(){
		return value;
	}

	public List<Enumerate> getChildren(){
		return children;
	}
	
	public boolean hasChildren(){
		return children!=null&&children.size()>0;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enumerate other = (Enumerate) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
