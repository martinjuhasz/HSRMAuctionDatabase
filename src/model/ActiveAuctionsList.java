package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActiveAuctionsList extends DatabaseTableModel {
	
	public ActiveAuctionsList(Connection db) {
		super(db);
	}

	public void setCategory(String category) {
		try {
			selectStmt = db.prepareStatement("SELECT title FROM \"auction\" WHERE category=?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			selectStmt.setString(1, category);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == 0) return "Auktionen";
		return "";
	}

}
