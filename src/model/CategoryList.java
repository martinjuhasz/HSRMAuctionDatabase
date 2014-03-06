package model;

import java.sql.Connection;
import java.sql.SQLException;

public class CategoryList extends DatabaseModel {
	
	public static final int COLUMN_CATEGORY_NAME = 0;
	public static final int COLUMN_CATEGORY_ID = 1;
	
	public CategoryList(Connection db) {
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT name,id FROM \"category\"");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return new DatabaseTableModel(this, new String[]{"Kategorie"});
	}
}
