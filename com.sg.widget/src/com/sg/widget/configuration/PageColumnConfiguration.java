package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;


public class PageColumnConfiguration extends Configuration{

	private String id;
	private List<SectionConfiguration> sections = new ArrayList<SectionConfiguration>();

	public PageColumnConfiguration(IConfigurationElement ce, EditorConfiguration ec) {
		super(ce);
		this.id = ce.getAttribute("id");
		IConfigurationElement[] children = ce.getChildren("section");
		for (int i = 0; i < children.length; i++) {
			sections.add(new SectionConfiguration(children[i],ec));
		}
	}
	
	public String getId(){
		return id;
	}

	public List<SectionConfiguration> getSections() {
		return sections;
	}
}
