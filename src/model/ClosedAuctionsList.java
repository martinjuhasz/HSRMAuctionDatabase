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

public class ClosedAuctionsList extends DatabaseModel {

	/**
	 * Instantiates a new closed auctions list.
	 *
	 * @param db the database connection
	 */
	public ClosedAuctionsList(Connection db){
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT * FROM\"closed_auctions_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Kategorie","Anzahl der Auktionen","Summe"});
	}
}
