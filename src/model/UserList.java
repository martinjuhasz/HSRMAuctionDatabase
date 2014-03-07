package model;

import java.sql.Connection;
import java.sql.SQLException;

public class UserList extends DatabaseModel {

	public UserList(Connection db) {
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"user_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Benutzername","Vorname",
				"Nachname","E-Mail","Strasse","Hausnummer", "PLZ", "Stadt"});
	}

}
