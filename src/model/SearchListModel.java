package model;

import java.sql.Connection;
import java.sql.SQLException;

public class SearchListModel extends DatabaseModel {
	
	public static final int COLUMN_CATEGORY_TERM = 0;
	
	public SearchListModel(Connection db, int uid) {
		super(db);
		
		try {
			selectStmt = db.prepareStatement("SELECT term FROM \"search_term\" WHERE \"uid\" = ? ORDER BY term");
			selectStmt.setInt(1, uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Suchbegriff"});
	}
}