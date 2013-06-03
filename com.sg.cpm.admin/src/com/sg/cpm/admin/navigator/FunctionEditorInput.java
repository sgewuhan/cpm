package com.sg.cpm.admin.navigator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.sg.common.db.IDBConstants;
import com.sg.db.model.ISingleObject;
import com.sg.user.IAuthorityResponse;

public class FunctionEditorInput implements IEditorInput {

	private String name;
	private String toolTips;
	private String id;
	private IAuthorityResponse auth;

	public FunctionEditorInput(ISingleObject so, String editorId, IAuthorityResponse resp) {
		this.id = editorId;
		name = (String) so.getValue(IDBConstants.FIELD_DESC);
		toolTips = (String) so.getValue(IDBConstants.FIELD_FUNCTION_TOOLTIPS);
		this.auth = resp;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return toolTips;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		FunctionEditorInput other = (FunctionEditorInput) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public IAuthorityResponse getAuth() {
		return auth;
	}

}
