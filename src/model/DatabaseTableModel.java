package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {

	protected Connection db;
	protected int rowCount;
	protected ResultSet resultSet;
	
	protected PreparedStatement countStmt;
	protected PreparedStatement selectStmt;
	
	public DatabaseTableModel(Connection db) {
		this.db = db;
		loadData();
	}
	
	protected void loadData() {
		rowCount = queryRowCount();
		resultSet = queryResults();

		fireTableDataChanged();
	}
	
	private int queryRowCount() {
		
		if(countStmt == null) return 0;
		
		try {
			ResultSet res = countStmt.executeQuery();
			if (res.next()) {
				return res.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private ResultSet queryResults() {
		
		if(selectStmt == null) return null;
		
		try {
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
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if(resultSet.absolute(rowIndex+1)) {
				return resultSet.getObject(columnIndex + 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getColumnName(int column) {
		return "";
	}

}
