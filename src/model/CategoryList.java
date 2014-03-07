/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package model;

import java.sql.Connection;
import java.sql.SQLException;

public class CategoryList extends DatabaseModel {
	
	public static final int COLUMN_CATEGORY_NAME = 0;
	public static final int COLUMN_CATEGORY_ID = 1;
	
	/**
	 * Instantiates a new category list.
	 *
	 * @param db the database connection
	 */
	public CategoryList(Connection db) {
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT name,id FROM \"category\" ORDER BY name");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Kategorie"});
	}
}
