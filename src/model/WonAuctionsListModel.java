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

public class WonAuctionsListModel extends DatabaseModel {

	public static final int COLUMN_TITLE = 0;
	public static final int COLUMN_BID = 1;
	public static final int COLUMN_BIDDER = 2;
	public static final int COLUMN_ID = 3;
	
	/**
	 * Instantiates a new won auctions list model.
	 *
	 * @param db the db
	 */
	public WonAuctionsListModel(Connection db) {
		super(db);
	}
	
	/**
	 * Sets the user.
	 *
	 * @param uid the new user
	 */
	public void setUser(int uid) {
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auctions_won_view\" WHERE max_bidder=?");
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
		return super.getTableModel(new String[]{"Titel","Kaufpreis"});
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
