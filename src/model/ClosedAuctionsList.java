package model;

import java.sql.Connection;

public class ClosedAuctionsList extends DatabaseTableModel {

	public ClosedAuctionsList(Connection db){
		super(db);
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
