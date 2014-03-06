package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClosedAuctionsList extends DatabaseTableModel {

	public ClosedAuctionsList(Connection db){
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT * FROM\"closed_auctions_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
		
	}
	
	
	public int getColumnCount() {
		return 3;
	}
	
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Kategorie";
		case 1:
			return "Anzahl der Auktionen";
		case 2:
			return "Summe";
		default:
			return "";
		}
	}
}
