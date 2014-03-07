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

public class ActiveAuctionsList extends DatabaseModel {
	
	/**
	 * Instantiates a new active auctions list.
	 *
	 * @param db the db
	 */
	public ActiveAuctionsList(Connection db) {
		super(db);
	}

	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) {
		try {
			selectStmt = db.prepareStatement("SELECT title FROM \"auction\" WHERE category=?");
			selectStmt.setString(1, category);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Auktionen"});
	}
}
