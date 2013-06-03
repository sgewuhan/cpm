package com.sg.widget.editor.field.actions;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.sg.widget.Widget;
import com.sg.widget.WidgetConstants;
import com.sg.widget.resource.Enumerate;
import com.sg.widget.util.Util;

public class FilteredOptionsSelector extends FilteredItemsSelectionDialog {

	private Collection<Enumerate> input;

	public FilteredOptionsSelector(Shell parentShell, Collection<Enumerate> root) {
		super(parentShell);
		input = root;
		setMessage(WidgetConstants.STR_FILTERDIALOG_MESSAGE);
		setListLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return getElementName(element);
			}
		});
	}

	protected int getShellStyle() {
		return SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.SYSTEM_MODAL;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		return super.createDialogArea(parent);
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Widget.getDefault().getDialogSettings()
				.getSection(WidgetConstants.SETTING_FILTERDIALOG);
		if (settings == null) {
			settings = Widget.getDefault().getDialogSettings()
					.addNewSection(WidgetConstants.SETTING_FILTERDIALOG);
		}
		return settings;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			public boolean matchItem(Object item) {
				// 按标签匹配
				String text = ((Enumerate) item).getLabel();
				if (text == null) {
					text = "";
				}

				if (matches(text)) {
					return true;
				}

				String alpha = Util.String2Alpha(text);
				if (matches(alpha)) {
					return true;
				}

				// 按值匹配
				Object value = ((Enumerate) item).getValue();
				if (value instanceof String) {
					text = (String) value;
					if (matches(text)) {
						return true;
					}
					
					alpha = Util.String2Alpha(text);
					if (matches(alpha)) {
						return true;
					}
				}

				// 按id匹配
				text = ((Enumerate) item).getId();
				if (matches(text)) {
					return true;
				}

				alpha = Util.String2Alpha(text);
				if (matches(alpha)) {
					return true;
				}

				return false;

			}

			public boolean isConsistentItem(Object item) {
				return true;
			}
		};
	}

	protected String getMatchText(Object item) {
		if (item == null)
			return "";
		return ((Enumerate) item).getLabel();
	}

	@Override
	protected Comparator<Enumerate> getItemsComparator() {
		return new Comparator<Enumerate>() {
			@Override
			public int compare(Enumerate arg0, Enumerate arg1) {
				return getElementName(arg0).compareTo(getElementName(arg1));
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		for (Iterator<Enumerate> iter = input.iterator(); iter.hasNext();) {
			contentProvider.add(iter.next(), itemsFilter);
		}

	}

	@Override
	public String getElementName(Object item) {
		if (item == null)
			return "";
		return ((Enumerate) item).getLabel() + " - "
				+ ((Enumerate) item).getId();
	}

	public static Enumerate openSelector(Shell shell, String title,
			Collection<Enumerate> root) {
		FilteredOptionsSelector dialog = new FilteredOptionsSelector(shell,
				root);
		dialog.setTitle(title);
		dialog.setInitialPattern("?");
		dialog.open();
		return (Enumerate) dialog.getFirstResult();
	}

}
