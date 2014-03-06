package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClosedAuctionsList extends DatabaseModel {

	public ClosedAuctionsList(Connection db){
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT * FROM\"closed_auctions_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return new DatabaseTableModel(this, new String[]{"Kategorie","Anzahl der Auktionen","Summe"});
	}
}
