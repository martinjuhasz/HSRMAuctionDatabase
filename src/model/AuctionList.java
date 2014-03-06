package model;

import java.sql.Connection;
import java.sql.SQLException;

public class AuctionList extends DatabaseModel {
	
	public static final int COLUMN_TITLE = 0;
	public static final int COLUMN_END_TIME = 1;
	public static final int COLUMN_MAX_BID = 2;
	public static final int COLUMN_CATEGORY = 3;
	
	public AuctionList(Connection db){
		super(db);
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	public void setCategory(int category) {
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\" WHERE category=?");
			selectStmt.setInt(1, category);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Titel","Enddatum","Höchstgebot / Kaufpreis"});
	}

}
