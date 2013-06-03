/*******************************************************************************
 * Copyright (c) 2011 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.sg.widget.component.ckeditor;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class CKEditor extends Composite {

	private static final String RESOURCES_PATH = "resources/";
	private static final String REGISTER_PATH = "ckeditor/";
	private static final String READY_FUNCTION = "rap_ready";

	private static final String[] RESOURCE_FILES = { "ckeditor.html",
			"ckeditor.js", "config.js", "contents.css",
			"skins/kama/editor.css", "skins/kama/icons.png",
			"skins/kama/images/sprites.png" };

	private String text = "";
	Browser browser;
	boolean clientReady = false;
	private StringBuilder scriptBuffer = null;

	public CKEditor(Composite parent, int style) {
		super(parent, style);
		super.setLayout(new FillLayout());
		this.setBackgroundMode(SWT.INHERIT_FORCE);
		registerResources();
		browser = new Browser(this, SWT.BORDER);
		browser.setUrl(getEditorHtmlLocation());
		addBrowserHandler();
	}

	private void registerResources() {
		ResourceManager resourceManager = RWT.getResourceManager();
		boolean isRegistered = resourceManager.isRegistered(REGISTER_PATH
				+ RESOURCE_FILES[0]);
		if (!isRegistered) {
			try {
				for (String fileName : RESOURCE_FILES) {
					register(resourceManager, fileName);
				}
			} catch (IOException ioe) {
				throw new IllegalArgumentException("Failed to load resources",
						ioe);
			}
		}
	}

	private String getEditorHtmlLocation() {
		ResourceManager resourceManager = RWT.getResourceManager();
		return resourceManager.getLocation(REGISTER_PATH + RESOURCE_FILES[0]);
	}

	private void register(ResourceManager resourceManager, String fileName)
			throws IOException {
		ClassLoader classLoader = CKEditor.class.getClassLoader();
		InputStream inputStream = classLoader
				.getResourceAsStream(RESOURCES_PATH + fileName);
		try {
			resourceManager.register(REGISTER_PATH + fileName, inputStream);
		} finally {
			inputStream.close();
		}
	}

	// //////////////////
	// overwrite methods

	@Override
	public void setLayout(Layout layout) {
		throw new UnsupportedOperationException(
				"Cannot change internal layout of CkEditor");
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		writeFont();
	}

	// ////
	// API

	public void setText(String text) {
		checkWidget();
		if (text == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.text = text;
		writeText();
		clientReady = false; // order is important
	}

	public String getText() {
		checkWidget();
		readText();
		return text;
	}

	// ////////////
	// browser I/O

	void onReady() {
		writeFont(); // CKEditor re-creates the document with every setData,
						// losing inline styles
		evalScriptBuffer();
		clientReady = true;
	}

	private void writeText() {
		evalOnReady("rap.editor.setData( \"" + escapeText(text) + "\" );");
	}

	private void writeFont() {
		evalOnReady("rap.editor.document.getBody().setStyle( \"font\", \""
				+ getCssFont() + "\" );");
	}

	private void readText() {
		if (clientReady) {
			text = (String) browser.evaluate("return rap.editor.getData();");
		}
	}

	// ///////
	// helper

	private void addBrowserHandler() {
		new BrowserFunction(browser, READY_FUNCTION) {
			public Object function(Object[] arguments) {
				onReady();
				return null;
			}
		};
	}

	private void evalOnReady(String script) {
		if (clientReady) {
			browser.evaluate(script);
		} else {
			if (scriptBuffer == null) {
				scriptBuffer = new StringBuilder();
			}
			scriptBuffer.append(script);
		}
	}

	private void evalScriptBuffer() {
		if (scriptBuffer != null) {
			browser.evaluate(scriptBuffer.toString());
			scriptBuffer = null;
		}
	}

	private String getCssFont() {
		StringBuilder result = new StringBuilder();
		if (getFont() != null) {
			FontData data = getFont().getFontData()[0];
			result.append(data.getHeight());
			result.append("px ");
			result.append(escapeText(data.getName()));
		}
		return result.toString();
	}

	private static String escapeText(String text) {
		// escaping backslashes, double-quotes, newlines, and carriage-return
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '\n') {
				result.append("\\n");
			} else if (ch == '\r') {
				result.append("\\r");
			} else if (ch == '\\') {
				result.append("\\\\");
			} else if (ch == '"') {
				result.append("\\\"");
			} else {
				result.append(ch);
			}
		}
		return result.toString();
	}

}
