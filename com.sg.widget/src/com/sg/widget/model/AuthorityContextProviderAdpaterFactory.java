package com.sg.widget.model;

import org.eclipse.core.runtime.IAdapterFactory;

import com.sg.widget.part.IAuthorityContextProvider;

@Deprecated
public class AuthorityContextProviderAdpaterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof IAuthorityContextProvider) {
			IAuthorityContextProvider dc = (IAuthorityContextProvider) adaptableObject;
			if (adapterType == ActiveCollection.class) {
				if(!dc.activeCollectionAdaptable()){
					return null;
				}
				
				final String collectionName = dc.getAuthorityContextCollectionName();

				if (collectionName == null)
					return null;
				
				return new ActiveCollection(){

					@Override
					public String getCollectionName() {
						return collectionName;
					}
					
					@Override
					public int getObjectType() {
						return TYPE_COLLECTION;
					}

					@Override
					public String getDisplayText() {
						return collectionName;
					}
					
				};
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{ActiveCollection.class};
	}

}
