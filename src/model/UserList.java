package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class UserList extends AbstractTableModel {

	private Connection db;
	
	private int rowCount;
	private ResultSet userSet;
	

	public UserList(Connection db) {
		this.db = db;
		
		loadData();
	}
	
	private void loadData() {
		rowCount = queryRowCount();
		userSet = queryUsers();
	}
	
	private int queryRowCount() {
		try {
			PreparedStatement countStmt = db.prepareStatement("SELECT COUNT(*) FROM \"user\"");
			ResultSet res = countStmt.executeQuery();
			if (res.next()) {
				return res.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private ResultSet queryUsers() {
		try {
			PreparedStatement selectStmt = db.prepareStatement("SELECT u.username, u.first_name, u.last_name, u.email, u.street, u.street_number, u.postal_code, c.city FROM \"user\" u LEFT JOIN city c ON u.postal_code=c.postal_code",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return selectStmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if(userSet.absolute(rowIndex+1)) {
				return userSet.getObject(columnIndex + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Benutzername";
		case 1:
			return "Vorname";
		case 2:
			return "Nachname";
		case 3:
			return "E-Mail";
		case 4:
			return "Strasse";
		case 5:
			return "Hausnummer";
		case 6:
			return "PLZ";
		case 7:
			return "Stadt";
		default:
			return "";
		}
	}
	

}
