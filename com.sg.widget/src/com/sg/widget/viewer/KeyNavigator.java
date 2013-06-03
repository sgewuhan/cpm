package com.sg.widget.viewer;

import java.util.Iterator;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.mongodb.DBObject;
import com.sg.db.model.ISingleObject;
import com.sg.widget.IWidgetImage;
import com.sg.widget.Widget;
import com.sg.widget.util.Util;

public class KeyNavigator implements KeyListener {

	private ColumnViewer viewer;

	private Shell shell;

	private Text text;

	private Button setting;

	private Menu settingMenu;

	private boolean useFuzzy;

	private boolean ignoreCase;

	private boolean useAlpha;

	private boolean useSearch;

	public KeyNavigator(ColumnViewer viewer) {

		this.viewer = viewer;

		viewer.getControl().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent event) {

				KeyNavigator.this.viewer.getControl().addKeyListener(
						KeyNavigator.this);
			}

			@Override
			public void focusLost(FocusEvent event) {

				KeyNavigator.this.viewer.getControl().removeKeyListener(
						KeyNavigator.this);
			}
		});

		viewer.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent event) {

				KeyNavigator.this.disactive();
			}
		});
	}

	protected void disactive() {
		
		if (shell != null && !shell.isDisposed()) {
			shell.dispose();
		}
	}

	public void activate(char initChar) {

		shell = new Shell(SWT.ON_TOP);
		shell.setData(RWT.CUSTOM_VARIANT, "keynavi");
		shell.setLayout(new FormLayout());
		shell.addShellListener(new ShellListener() {
			
			@Override
			public void shellDeactivated(ShellEvent e) {
				disactive();
			}
			
			@Override
			public void shellClosed(ShellEvent e) {
				
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
				
			}
		});

		text = new Text(shell, SWT.NONE);
		text.setData(RWT.CUSTOM_VARIANT, "keynavi");
		FormData fd = new FormData();
		text.setLayoutData(fd);
		if (Util.isDisplayedChar(initChar)) {
			text.insert("" + initChar);
		}

		text.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				if (e.character == SWT.ESC) {
					disactive();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {

				run();

			}
		});

		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(100, 0);
		fd.width = 240;
		fd.height = 24;

		setting = new Button(shell, SWT.PUSH);
		setting.setData(RWT.CUSTOM_VARIANT, "navimenu");
		setting.setImage(Widget.getImage(IWidgetImage.IMG_SETTING16));
		setting.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showSettingMenu();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		fd = new FormData();
		setting.setLayoutData(fd);
		fd.left = new FormAttachment(text, 4);
		fd.top = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(100, 0);
		fd.height = 24;
		fd.width = 24;

		shell.pack();
		Point loc = viewer.getControl().toDisplay(0, 0);
		shell.setLocation(loc);
		shell.open();

		createMenu();

	}

	private void createMenu() {
		settingMenu = new Menu(setting);
		final MenuItem useAlphaItem = new MenuItem(settingMenu, SWT.CHECK);
		useAlphaItem.setText("使用拼音首字母定位");
		useAlphaItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useAlpha = !useAlpha;
				run();
			}
		});

		final MenuItem useFuzzyItem = new MenuItem(settingMenu, SWT.CHECK);
		useFuzzyItem.setText("使用模糊匹配");
		useFuzzyItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useFuzzy = !useFuzzy;
				run();
			}
		});

		final MenuItem ignoreCaseItem = new MenuItem(settingMenu, SWT.CHECK);
		ignoreCaseItem.setText("忽略英文中的大小写");
		ignoreCaseItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ignoreCase = !ignoreCase;
				run();
			}
		});

		new MenuItem(settingMenu, SWT.SEPARATOR);

		final MenuItem useSearchItem = new MenuItem(settingMenu, SWT.RADIO);
		useSearchItem.setText("查询定位");
		final MenuItem useFilterItem = new MenuItem(settingMenu, SWT.RADIO);
		useFilterItem.setText("过滤数据");

		useSearchItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useSearch = true;
				run();
			}
		});
		useFilterItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useSearch = false;
				run();
			}
		});

		
		MenuItem removeFilterItem = new MenuItem(settingMenu, SWT.RADIO);
		removeFilterItem.setText("取消数据过滤");

		removeFilterItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.resetFilters();
			}
		});

		
		useFuzzy = true;
		ignoreCase = true;
		useAlpha = true;

		useSearch = true;

		useAlphaItem.setSelection(useAlpha);
		ignoreCaseItem.setSelection(ignoreCase);
		useFuzzyItem.setSelection(useFuzzy);
		useSearchItem.setSelection(useSearch);

		Point location = shell.toDisplay(setting.getLocation());
		location.y = location.y + 26;
		settingMenu.setLocation(location);
	}

	protected void showSettingMenu() {
		settingMenu.setVisible(true);
	}

	protected void run() {
		viewer.resetFilters();
		
		String key = text.getText();
		if (key.length() < 2) {
			return;
		}

		if (useSearch) {
			doLocate(key);
		} else {
			doFilter(key);
		}

	}

	private void doFilter(final String key) {
		viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if(element instanceof DBObject){
					return match((DBObject)element,key);
				}else if(element instanceof ISingleObject){
					return match(((ISingleObject)element).getData(),key);
				}
				
				return false;
			}

		} });
	}

	protected boolean match(DBObject data, String key) {
		Iterator<String> iter = data.keySet().iterator();
		while(iter.hasNext()){
			String fieldName = iter.next();
			Object value = data.get(fieldName);
			value = value==null?"":value;
			if(match(key, value.toString())){
				return true;
			}
		}
		
		return false;
	}

	private void doLocate(String key) {
		if (viewer instanceof TableViewer) {
			Table table = ((TableViewer) viewer).getTable();
			int columncount = table.getColumnCount();
			TableItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++) {
				if (columncount == 0) {
					String cellText = items[i].getText();
					if (match(key, cellText)) {
						table.setSelection(items[i]);
						return;
					}
				} else {
					for (int j = 0; j < columncount; j++) {
						String cellText = items[i].getText(j);
						if (match(key, cellText)) {
							table.setSelection(items[i]);
							table.showSelection();
							return;
						}

					}
				}
			}
		} else if (viewer instanceof TreeViewer) {
			Tree tree = ((TreeViewer) viewer).getTree();
			int columncount = tree.getColumnCount();
			TreeItem[] items = tree.getItems();
			TreeItem item = locateTreeItems(key, items, columncount);
			if (item != null){
				tree.select(item);
				tree.showItem(item);
			}

		}
	}

	private TreeItem locateTreeItems(String key, TreeItem[] items,
			int columncount) {

		for (int i = 0; i < items.length; i++) {
			if (columncount == 0) {
				String cellText = items[i].getText();
				if (match(key, cellText)) {
					return items[i];
				}
			} else {
				for (int j = 0; j < columncount; j++) {
					String cellText = items[i].getText(j);
					if (match(key, cellText)) {
						return items[i];
					}
				}
			}

			// 定位子节点
			TreeItem item = locateTreeItems(key, items[i].getItems(),
					columncount);
			if (item != null)
				return item;
		}
		return null;
	}

	private boolean match(String key, String cellText) {

		if (useFuzzy) {// 使用模糊匹配

			if (ignoreCase) {
				if (cellText.toLowerCase().contains(key.toLowerCase())) {
					return true;
				}
			} else {
				if (cellText.contains(key)) {
					return true;
				}
			}

			if (useAlpha) {
				String alpha = Util.String2Alpha(cellText);
				if (ignoreCase) {
					if (alpha.toLowerCase().contains(key.toLowerCase())) {
						return true;
					}
				} else {
					if (alpha.contains(key)) {
						return true;
					}
				}
			}
		} else {
			if (ignoreCase) {
				if (cellText.toLowerCase().equals(key.toLowerCase())) {
					return true;
				}
			} else {
				if (cellText.equals(key)) {
					return true;
				}
			}

			if (useAlpha) {
				String alpha = Util.String2Alpha(cellText);
				if (ignoreCase) {
					if (alpha.toLowerCase().equals(key.toLowerCase())) {
						return true;
					}
				} else {
					if (alpha.equals(key)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

		if (isActivateKey(e.character))
			activate(e.character);
	}

	public static final char[] activechar = new char[] { 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private boolean isActivateKey(char character) {

		for (int i = 0; i < activechar.length; i++) {
			if (character == activechar[i]) {
				return true;
			}
		}
		return false;
	}
}
