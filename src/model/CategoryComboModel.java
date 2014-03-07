/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package model;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class CategoryComboModel extends AbstractListModel<String> implements ComboBoxModel<String> {

	private CategoryList categoryList;
	private String currentSelectedItem;
	
	/**
	 * Instantiates a new category combo model.
	 *
	 * @param categoryList the category list
	 */
	public CategoryComboModel(CategoryList categoryList) {
		super();
		this.categoryList = categoryList;
	}
	
	/**
	 * Gets the database model.
	 *
	 * @return the database model
	 */
	public DatabaseModel getDatabaseModel() {
		return this.categoryList;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return this.categoryList.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public String getElementAt(int index) {
		return (String)this.categoryList.getRow(index)[CategoryList.COLUMN_CATEGORY_NAME];
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object anItem) {
		currentSelectedItem = (String)anItem;
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {
		return currentSelectedItem;
	}

}
