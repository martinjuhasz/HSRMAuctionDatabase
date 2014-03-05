package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class AuctionList extends DatabaseTableModel {
	
	public AuctionList(Connection db){
		super(db);
		
		try {
			countStmt = db.prepareStatement("SELECT COUNT(*) FROM \"auction_view\"");
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\"",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 4;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Titel";
		case 1:
			return "Kategorie";
		case 2:
			return "Enddatum";
		case 3:
			return "Höchstgebot / Kaufpreis";
		default:
			return "";
		}
	}

}
