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

public class UserModel extends DatabaseModel {
	
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
	
	/**
	 * Instantiates a new user model.
	 *
	 * @param db the db
	 * @param id the id
	 */
	public UserModel(Connection db, int id) {
		super(db);

		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"user_view\" WHERE id=?");
			selectStmt.setInt(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
}
