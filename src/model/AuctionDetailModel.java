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

public class AuctionDetailModel extends DatabaseModel {
	
	public static final int COLUMN_ID = 0;
	public static final int COLUMN_START_TIME = 1;
	public static final int COLUMN_END_TIME = 2;
	public static final int COLUMN_TITLE = 3;
	public static final int COLUMN_DESCRIPTION = 4;
	public static final int COLUMN_IMAGE = 5;
	public static final int COLUMN_CATEGORY = 6;
	public static final int COLUMN_OFFERER = 7;
	public static final int COLUMN_PRICE = 8;
	public static final int COLUMN_DIRECT_BUY = 9;
	public static final int COLUMN_MAX_BID = 10;
	public static final int COLUMN_MAX_BIDDER = 11;
	public static final int COLUMN_OPEN = 12;
	public static final int COLUMN_MAX_BIDDER_ID = 13;
	public static final int COLUMN_RATING = 14;

	/**
	 * Instantiates a new auction detail model.
	 *
	 * @param db the db
	 * @param id the id
	 */
	public AuctionDetailModel(Connection db, int id) {
		super(db);

		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_detail_view\" WHERE id=?");
			selectStmt.setInt(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}

	/**
	 * Gets the comment model.
	 *
	 * @return the comment model
	 */
	public AuctionCommentModel getCommentModel() {
		return new AuctionCommentModel(db, (int)getFirst()[COLUMN_ID]);
	}
}
