package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class CategoryList extends DatabaseTableModel {
	
	public CategoryList(Connection db) {
		super(db);
		
		try {
			countStmt = db.prepareStatement("SELECT COUNT(*) FROM \"category\"");
			selectStmt = db.prepareStatement("SELECT * FROM \"category\"",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		loadData();
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		if(column == 0) return "Kategorie";
		return "";
	}

}
