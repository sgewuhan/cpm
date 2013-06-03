package com.sg.widget.test;

import org.eclipse.core.expressions.PropertyTester;

import com.sg.widget.part.IFileExportable;
import com.sg.widget.part.IUpdateablePart;

public class ActivePartTester extends PropertyTester {

	public static final String NEED_UPDATE = "needUpdate";
	
	public static final String CAN_EXPORT = "canExport";
	
	public ActivePartTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if(NEED_UPDATE.equals(property)){
			if(receiver instanceof IUpdateablePart){
				return ((IUpdateablePart)receiver).needUpdate();
			}
		}else if(CAN_EXPORT.equals(property)){
			if(receiver instanceof IFileExportable){
				return ((IFileExportable)receiver).canExport();
			}
		}
		return false;
	}

}
