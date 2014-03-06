package model;

import java.sql.Connection;
import java.sql.SQLException;

public class ActiveAuctionsList extends DatabaseModel {
	
	public ActiveAuctionsList(Connection db) {
		super(db);
	}

	public void setCategory(String category) {
		try {
			selectStmt = db.prepareStatement("SELECT title FROM \"auction\" WHERE category=?");
			selectStmt.setString(1, category);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return new DatabaseTableModel(this, new String[]{"Auktionen"});
	}
}
