package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class UserList extends DatabaseTableModel {
	
	public static final int COLUMN_USER_NAME = 0;
	public static final int COLUMN_FIRST_NAME = 1;
	public static final int COLUMN_SUR_NAME = 2;
	public static final int COLUMN_EMAIL = 3;
	public static final int COLUMN_STREET = 4;
	public static final int COLUMN_STREET_NUMBER = 5;
	public static final int COLUMN_POSTAL_CODE = 6;
	public static final int COLUMN_CITY = 7;

	public UserList(Connection db) {
		super(db);
		
		try {
			countStmt = db.prepareStatement("SELECT COUNT(*) FROM \"user_view\"");
			selectStmt = db.prepareStatement("SELECT * FROM \"user_view\"",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	@Override
	public int getColumnCount() {
		return 8;
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
