package com.sg.widget.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;


public class FieldGroupConfiguration extends Configuration {

	private List<FieldConfiguration> fields = new ArrayList<FieldConfiguration>();
	private int columnCount;
	
	public FieldGroupConfiguration(IConfigurationElement ce, EditorConfiguration ec) {
		super(ce);
		String columnCountString = ce.getAttribute("columnCount");
		try{
			columnCount = Integer.parseInt(columnCountString);
		}catch(Exception e){
		}
		IConfigurationElement[] children = ce.getChildren("field");
		for(int i=0;i<children.length;i++){
			FieldConfiguration fieldConfiguration = new FieldConfiguration(children[i]);
			fields.add(fieldConfiguration);
			ec.addField(fieldConfiguration);
		}
	}
	
	public int getColumnCount(){
		return columnCount;
	}

	public List<FieldConfiguration> getFields() {
		return fields;
	}

}
