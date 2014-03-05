package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class UserList extends DatabaseTableModel {

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
