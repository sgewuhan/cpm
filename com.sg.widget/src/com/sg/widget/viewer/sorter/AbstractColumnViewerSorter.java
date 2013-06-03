package com.sg.widget.viewer.sorter;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * ���н�������ĳ�����
 * 
 */
public abstract class AbstractColumnViewerSorter extends ViewerComparator {
	public static final int ASC = 1; // ����ķ�ʽ,˳��

	public static final int NONE = 0;

	public static final int DESC = -1; // ����ķ�ʽ,����

	private int direction = 0; // ����ķ�ʽ

	protected Item column;

	private ColumnViewer viewer;

	/**
	 * ����һ���µĶ���
	 * 
	 * @param viewer
	 * @param column
	 */
	/**
	 * ���췽��
	 * 
	 * @param viewer
	 * @param column
	 *            �������
	 */
	public AbstractColumnViewerSorter(ColumnViewer viewer, Item column) {
		this.column = column;
		this.viewer = viewer;
		if(column instanceof TreeColumn){
			((TreeColumn)column).addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (AbstractColumnViewerSorter.this.viewer.getComparator() != null) {
						if (AbstractColumnViewerSorter.this.viewer.getComparator() == AbstractColumnViewerSorter.this) {
							int tdirection = AbstractColumnViewerSorter.this.direction;

							if (tdirection == ASC) {
								setSorter(AbstractColumnViewerSorter.this, DESC);
							} else if (tdirection == DESC) {
								setSorter(AbstractColumnViewerSorter.this, NONE);
							}
						} else {
							setSorter(AbstractColumnViewerSorter.this, ASC);
						}
					} else {
						setSorter(AbstractColumnViewerSorter.this, ASC);
					}
				}
			});

		}else if(column instanceof TableColumn){
			((TableColumn)column).addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (AbstractColumnViewerSorter.this.viewer.getComparator() != null) {
						if (AbstractColumnViewerSorter.this.viewer.getComparator() == AbstractColumnViewerSorter.this) {
							int tdirection = AbstractColumnViewerSorter.this.direction;

							if (tdirection == ASC) {
								setSorter(AbstractColumnViewerSorter.this, DESC);
							} else if (tdirection == DESC) {
								setSorter(AbstractColumnViewerSorter.this, NONE);
							}
						} else {
							setSorter(AbstractColumnViewerSorter.this, ASC);
						}
					} else {
						setSorter(AbstractColumnViewerSorter.this, ASC);
					}
				}
			});

		}
	}

	/**
	 * ��������ķ���
	 * 
	 * @param sorter
	 * @param direction
	 */
	/**
	 * ��������ʽ
	 * 
	 * @param sorter
	 * @param direction
	 */
	public void setSorter(AbstractColumnViewerSorter sorter, int direction) {
		if (column instanceof TreeColumn) {
			if (direction == NONE) {
				((TreeColumn)column).getParent().setSortColumn(null);
				((TreeColumn)column).getParent().setSortDirection(SWT.NONE);
				viewer.setComparator(null);
			} else {
				((TreeColumn)column).getParent().setSortColumn(((TreeColumn)column));
				sorter.direction = direction;

				if (direction == ASC) {
					((TreeColumn)column).getParent().setSortDirection(SWT.UP);
				} else {
					((TreeColumn)column).getParent().setSortDirection(SWT.DOWN);
				}
				if (viewer.getComparator() == sorter) {
					viewer.refresh();
				} else {
					viewer.setComparator(sorter);
				}

				
			}
		} else if (column instanceof TableColumn) {
			if (direction == NONE) {
				((TableColumn)column).getParent().setSortColumn(null);
				((TableColumn)column).getParent().setSortDirection(SWT.NONE);
				viewer.setComparator(null);
			} else {
				((TableColumn)column).getParent().setSortColumn(((TableColumn)column));
				sorter.direction = direction;

				if (direction == ASC) {
					((TableColumn)column).getParent().setSortDirection(SWT.UP);
				} else {
					((TableColumn)column).getParent().setSortDirection(SWT.DOWN);
				}

				if (viewer.getComparator() == sorter) {
					viewer.refresh();
				} else {
					viewer.setComparator(sorter);
				}
			}
		}
	}

	/**
	 * ���Ǹ�������򷽷�
	 */
	/**
	 * ��������ķ�ʽ�Ƚϴ������������
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		return direction * doCompare(viewer, e1, e2);
	}

	/**
	 * ���򷽷���������ʵ��
	 * 
	 * @param viewer
	 * @param e1
	 * @param e2
	 * @return
	 */
	/**
	 * �Դ��������������бȽϵľ���ʵ��
	 * 
	 * @param viewer
	 * @param e1
	 * @param e2
	 * @return
	 */
	protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
}
