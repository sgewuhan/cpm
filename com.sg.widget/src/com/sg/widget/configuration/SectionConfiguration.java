package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.forms.widgets.Section;

import com.sg.widget.util.Util;

public class SectionConfiguration extends Configuration {

	private String label;
	private String description;
	private List<Object> fields = new ArrayList<Object>();
	private int style;

	public SectionConfiguration(IConfigurationElement ce, EditorConfiguration ec) {
		super(ce);
		this.label = ce.getAttribute("label");
		this.description = ce.getAttribute("description");
		
		style = Section.CLIENT_INDENT | Section.TWISTIE ;
		
		if(!"false".equals(ce.getAttribute("expand"))){
			style =  style |Section.EXPANDED;
		}

		if(!Util.isNullOrEmptyString(description)){
			style = style|Section.DESCRIPTION;
		}
		
		IConfigurationElement[] children = ce.getChildren();
		for (int i = 0; i < children.length; i++) {
			if("fieldgroup".equals(children[i].getName())){
				fields.add(new FieldGroupConfiguration(children[i],ec));
			}else if("field".equals(children[i].getName())){
				FieldConfiguration fieldConfiguration = new FieldConfiguration(children[i]);
				fields.add(fieldConfiguration);
				ec.addField(fieldConfiguration);
			}
		}
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public int getStyle(){
		return style;
	}

	public List<Object> getFields() {
		return fields;
	}

}
