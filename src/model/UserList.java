package model;

import java.sql.Connection;
import java.sql.SQLException;

public class UserList extends DatabaseModel {
	
	public static final int COLUMN_USER_NAME = 0;
	public static final int COLUMN_FIRST_NAME = 1;
	public static final int COLUMN_SUR_NAME = 2;
	public static final int COLUMN_EMAIL = 3;
	public static final int COLUMN_STREET = 4;
	public static final int COLUMN_STREET_NUMBER = 5;
	public static final int COLUMN_POSTAL_CODE = 6;
	public static final int COLUMN_CITY = 7;
	public static final int COLUMN_PASSWORD = 8;
	public static final int COLUMN_UID = 9;

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
