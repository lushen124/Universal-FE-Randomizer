package ui.views.components;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

public class DualListSelectionView<T extends ListDisplayable> extends Composite {
	private Label leftListLabel;
	private Label rightListLabel;
	private List leftList;
	private List rightList;
	private Button moveAllToRight;
	private Button moveAllToLeft;
	
	private java.util.List<T> fullData;
	private java.util.List<T> leftData;
	private java.util.List<T> rightData;
	
	public DualListSelectionView(Composite parent, 
			String leftTitle, String rightTitle, 
			String allToLeftTitle, String allToRightTitle, 
			java.util.List<T> allItems, java.util.List<T> leftItems, java.util.List<T> rightItems) {
		super(parent, SWT.NONE);
		
		setLayout(new FormLayout());
		
		leftListLabel = new Label(this, SWT.NONE);
		leftListLabel.setText(leftTitle);
		
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(0, 0);
		leftListLabel.setLayoutData(labelData);
		
		rightListLabel = new Label(this, SWT.NONE);
		rightListLabel.setText(rightTitle);
		
		labelData = new FormData();
		labelData.top = new FormAttachment(0, 0);
		labelData.left = new FormAttachment(50, 0);
		rightListLabel.setLayoutData(labelData);
		
		fullData = new ArrayList<T>(allItems);
		leftData = new ArrayList<T>(leftItems);
		rightData = new ArrayList<T>(rightItems);
		
		leftData.sort((i1, i2) -> i1.displayString().compareTo(i2.displayString()));
		rightData.sort((i1, i2) -> i1.displayString().compareTo(i2.displayString()));
		
		leftList = new List(this, SWT.SINGLE);
		leftData.forEach(item -> leftList.add(item.displayString()));
		leftList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				moveLeftToRight(leftList.getSelectionIndex());
			}
		});
		
		FormData listData = new FormData();
		listData.top = new FormAttachment(leftListLabel, 5);
		listData.left = new FormAttachment(0, 0);
		listData.right = new FormAttachment(50, -5);
		listData.height = 150;
		leftList.setLayoutData(listData);
		
		rightList = new List(this, SWT.SINGLE);
		rightData.forEach(item -> rightList.add(item.displayString()));
		rightList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				moveRightToLeft(rightList.getSelectionIndex());
			}
		});
		
		listData = new FormData();
		listData.top = new FormAttachment(rightListLabel, 5);
		listData.left = new FormAttachment(50, 0);
		listData.right = new FormAttachment(100, -5);
		listData.height = 150;
		rightList.setLayoutData(listData);
		
		moveAllToRight = new Button(this, SWT.PUSH);
		moveAllToRight.setText(allToRightTitle);
		moveAllToRight.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				moveAllToRightList();
			}
		});
		
		FormData buttonData = new FormData();
		buttonData.top = new FormAttachment(leftList, 5);
		buttonData.left = new FormAttachment(0, 5);
		buttonData.right = new FormAttachment(100, -5);
		moveAllToRight.setLayoutData(buttonData);
		
		moveAllToLeft = new Button(this, SWT.PUSH);
		moveAllToLeft.setText(allToLeftTitle);
		moveAllToLeft.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				moveAllToLeftList();
			}
		});
		
		buttonData = new FormData();
		buttonData.top = new FormAttachment(moveAllToRight, 5);
		buttonData.left = new FormAttachment(0, 5);
		buttonData.right = new FormAttachment(100, -5);
		buttonData.bottom = new FormAttachment(100, 0);
		moveAllToLeft.setLayoutData(buttonData);
	}
	
	private void moveAllToRightList() {
		leftData.clear();
		rightData.clear();
		fullData.forEach(item -> rightData.add(item));
		reloadLeftList();
		reloadRightList();
	}
	
	private void moveAllToLeftList() {
		leftData.clear();
		rightData.clear();
		fullData.forEach(item -> leftData.add(item));
		reloadLeftList();
		reloadRightList();
	}
	
	private void moveLeftToRight(int index) {
		if (index < 0 || index >= leftData.size()) { return; }
		T item = leftData.get(index);
		leftData.remove(index);
		leftList.remove(index);
		rightData.add(item);
		reloadRightList();
	}
	
	private void moveRightToLeft(int index) {
		if (index < 0 || index >= rightData.size()) { return; }
		
		T item = rightData.get(index);
		rightData.remove(index);
		rightList.remove(index);
		leftData.add(item);
		reloadLeftList();
	}
	
	private void reloadLeftList() {
		leftList.removeAll();
		leftData.sort((i1, i2) -> i1.displayString().compareTo(i2.displayString()));
		leftData.forEach(item -> leftList.add(item.displayString()));
	}
	
	private void reloadRightList() {
		rightList.removeAll();
		rightData.sort((i1, i2) -> i1.displayString().compareTo(i2.displayString()));
		rightData.forEach(item -> rightList.add(item.displayString()));
	}
	
	public void setListHeight(int height) {
		FormData listData = new FormData();
		listData.top = new FormAttachment(leftListLabel, 5);
		listData.left = new FormAttachment(0, 0);
		listData.right = new FormAttachment(50, -5);
		listData.height = height;
		leftList.setLayoutData(listData);
		
		listData = new FormData();
		listData.top = new FormAttachment(rightListLabel, 5);
		listData.left = new FormAttachment(50, 0);
		listData.right = new FormAttachment(100, -5);
		listData.height = height;
		rightList.setLayoutData(listData);
	}
	
	public java.util.List<T> getAllLeftItems() {
		return new ArrayList<T>(leftData);
	}
	
	public java.util.List<T> getAllRightItems() {
		return new ArrayList<T>(rightData);
	}
	
	public void updateItems(java.util.List<T> leftItems, java.util.List<T> rightItems) {
		leftData.clear();
		rightData.clear();
		leftData.addAll(leftItems.stream().filter(item -> fullData.contains(item)).toList());
		rightData.addAll(rightItems.stream().filter(item -> fullData.contains(item)).toList());
		reloadLeftList();
		reloadRightList();
	}
	
	public void setEnabled(boolean isEnabled) {
		leftListLabel.setEnabled(isEnabled);
		rightListLabel.setEnabled(isEnabled);
		leftList.setEnabled(isEnabled);
		rightList.setEnabled(isEnabled);
		moveAllToLeft.setEnabled(isEnabled);
		moveAllToRight.setEnabled(isEnabled);
	}
}
