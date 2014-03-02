package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class AuctionList extends AbstractTableModel {

	private Connection db;
	private int rowCount;
	private ResultSet auctionSet;
	
	public AuctionList(Connection db){
		this.db = db;
		
		loadData();
	}
	
	private void loadData() {
		rowCount = queryRowCount();
		auctionSet = queryAuctions();
	}
	
	private int queryRowCount() {
		try {
			PreparedStatement countStmt = db.prepareStatement("SELECT COUNT(*) FROM \"auction\"");
			ResultSet res = countStmt.executeQuery();
			if (res.next()) {
				return res.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private ResultSet queryAuctions() {
		try {
			PreparedStatement selectStmt = db.prepareStatement("SELECT a.title, a.category, a.end_time, price FROM \"auction\" a, (SELECT MAX(b.price) FROM \"bid\" b, \"auction\" a  WHERE a.id = b.auction) AS price",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return selectStmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if(auctionSet.absolute(rowIndex+1)) {
				return auctionSet.getObject(columnIndex + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Titel";
		case 1:
			return "Kategorie";
		case 2:
			return "Enddatum";
		case 3:
			return "Höchstgebot";
		default:
			return "";
		}
	}

}
