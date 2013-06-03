package com.sg.common.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sg.common.db.IDBConstants;
import com.sg.common.ui.UIConstants;
import com.sg.db.model.ISingleObject;
import com.sg.widget.Widget;
import com.sg.widget.configuration.EditorConfiguration;
import com.sg.widget.editor.ISingleObjectEditorInput;
import com.sg.widget.editor.field.option.IOptionProvider;
import com.sg.widget.resource.Enumerate;

public class DocumentFormEditorOption implements IOptionProvider {


	@Override
	public Enumerate getOption(ISingleObjectEditorInput input,ISingleObject data, String key, Object value) {
		Set<EditorConfiguration> editorSet = Widget.listSingleObjectEditorConfigurationByCollection(IDBConstants.COLLECTION_DOCUMENT);
		
		List<Enumerate> children = new ArrayList<Enumerate>();
		
		Enumerate e = new Enumerate(key, data.toString(), data, children);
		
		Iterator<EditorConfiguration> iter = editorSet.iterator();
		while(iter.hasNext()){
			EditorConfiguration ec = iter.next();
			if(ec.getId().equals(UIConstants.EDITOR_DELIVERDOCUMENT_CREATE)){//ÅÅ³ý»ù±¾ÎÄµµ
				continue;
			}
			children.add(new Enumerate(ec.getId(),ec.getName(),ec.getId(),null));
		}
		return e;
	}

}
