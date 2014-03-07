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

public class AuctionCommentModel extends DatabaseModel {

	/**
	 * Instantiates a new auction comment model.
	 *
	 * @param db the database connection
	 * @param id the id
	 */
	public AuctionCommentModel(Connection db, int id) {
		super(db);

		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_comment_view\" WHERE auction=?");
			selectStmt.setInt(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Benutzer", "Text", "Datum"});
	}

}
