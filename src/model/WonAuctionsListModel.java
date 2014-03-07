package model;

import java.sql.Connection;
import java.sql.SQLException;

public class WonAuctionsListModel extends DatabaseModel {

	public static final int COLUMN_TITLE = 0;
	public static final int COLUMN_BID = 1;
	public static final int COLUMN_BIDDER = 2;
	public static final int COLUMN_ID = 3;
	
	public WonAuctionsListModel(Connection db) {
		super(db);
	}
	
	public void setUser(int uid) {
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auctions_won_view\" WHERE max_bidder=?");
			selectStmt.setInt(1, uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Titel","Kaufpreis"});
	}
	
	public AuctionDetailModel getDetailModelForRow(int row) {
		int id = (int)getRow(row)[COLUMN_ID];
		return new AuctionDetailModel(db, id);
	}
	
}