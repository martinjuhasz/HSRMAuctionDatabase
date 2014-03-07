/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package model;

import java.sql.Connection;
import java.sql.SQLException;

public class UserList extends DatabaseModel {

	/**
	 * Instantiates a new user list.
	 *
	 * @param db the db
	 */
	public UserList(Connection db) {
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"user_view\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	/* (non-Javadoc)
	 * @see model.DatabaseModel#getTableModel()
	 */
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Benutzername","Vorname",
				"Nachname","E-Mail","Strasse","Hausnummer", "PLZ", "Stadt"});
	}

}
