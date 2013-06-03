package com.sg.widget.configuration;

import org.eclipse.core.runtime.IConfigurationElement;

public class QueryTreeConfiguration extends ViewerConfiguration {

	private static final String AUTO_EXPAND_LEVEL = "autoExpandLevel";
	private static final String ATT_FULL_SELECTION = "fullSelection";
	public boolean fullSelection;
	private int level;

	public QueryTreeConfiguration(IConfigurationElement ce) {
		super(ce);
		fullSelection = VALUE_TRUE.equals(ce.getAttribute(ATT_FULL_SELECTION));
		String _level = ce.getAttribute(AUTO_EXPAND_LEVEL);
		level = 2;
		try {
			level = Integer.parseInt(_level);
		} catch (Exception e) {
		}
	}

	public boolean isFullSelection() {
		return fullSelection;
	}

	public int getAutoExpandLevel() {
		return level;
	}

}
