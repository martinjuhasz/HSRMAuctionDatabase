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

public class AuctionList extends DatabaseModel {
	
	public static final int COLUMN_TITLE = 0;
	public static final int COLUMN_END_TIME = 1;
	public static final int COLUMN_MAX_BID = 2;
	public static final int COLUMN_CATEGORY = 3;
	public static final int COLUMN_DESCRIPTION = 4;
	public static final int COLUMN_ID = 5;
	
	/**
	 * Instantiates a new auction list.
	 *
	 * @param db the database connection
	 */
	public AuctionList(Connection db){
		super(db);
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(int category) {
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\" WHERE category=?");
			selectStmt.setInt(1, category);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	/**
	 * Sets the search term.
	 *
	 * @param searchTerm the new search term
	 */
	public void setSearchTerm(String searchTerm) {
		searchTerm = "%"+searchTerm+"%";
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\" WHERE title ILIKE ? OR description ILIKE ?");
			selectStmt.setString(1, searchTerm);
			selectStmt.setString(2, searchTerm);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Titel","Enddatum","Höchstgebot / Kaufpreis"});
	}
	
	/**
	 * Gets the table model.
	 *
	 * @param onlyTitle the only title
	 * @return the table model
	 */
	public DatabaseTableModel getTableModel(boolean onlyTitle) {
		if(onlyTitle) {
			return super.getTableModel(new String[]{"Titel"});
		}
		return this.getTableModel();
	}
	
	/**
	 * Gets the detail model for row.
	 *
	 * @param row the row
	 * @return the detail model for row
	 */
	public AuctionDetailModel getDetailModelForRow(int row) {
		int id = (int)getRow(row)[COLUMN_ID];
		return new AuctionDetailModel(db, id);
	}

}
