package model;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class CategoryComboModel extends AbstractListModel<String> implements ComboBoxModel<String> {

	private CategoryList categoryList;
	private String currentSelectedItem;
	
	public CategoryComboModel(CategoryList categoryList) {
		super();
		this.categoryList = categoryList;
	}
	
	@Override
	public int getSize() {
		return this.categoryList.getRowCount();
	}

	@Override
	public String getElementAt(int index) {
		return (String)this.categoryList.getValueAt(index, CategoryList.COLUMN_CATEGORY_NAME);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		currentSelectedItem = (String)anItem;
	}

	@Override
	public Object getSelectedItem() {
		return currentSelectedItem;
	}

}
