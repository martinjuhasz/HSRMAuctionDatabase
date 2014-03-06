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
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\"",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	public void setCategory(int category) {
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_view\" WHERE category=?",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			selectStmt.setInt(1, category);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Titel";
		case 1:
			return "Enddatum";
		case 2:
			return "Höchstgebot / Kaufpreis";
		default:
			return "";
		}
	}

}
