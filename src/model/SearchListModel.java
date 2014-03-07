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

public class SearchListModel extends DatabaseModel {
	
	public static final int COLUMN_CATEGORY_TERM = 0;
	
	/**
	 * Instantiates a new search list model.
	 *
	 * @param db the db
	 * @param uid the uid
	 */
	public SearchListModel(Connection db, int uid) {
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT term FROM \"search_term\" WHERE \"uid\" = ? ORDER BY term");
			selectStmt.setInt(1, uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Suchbegriff"});
	}
}