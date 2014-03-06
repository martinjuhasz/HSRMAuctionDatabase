package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {

	protected Connection db;
	protected int rowCount;
	protected Object resultSet[][];
	
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
	
	private Object[][] queryResults() {
		if(selectStmt == null) return null;
		
		Object result[][] = null;
		try {
			ResultSet resultSet = selectStmt.executeQuery();
			result = new Object[getRowCount()][resultSet.getMetaData().getColumnCount()];
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[i].length; j++) {
					resultSet.absolute(i + 1);
					result[i][j] = resultSet.getObject(j + 1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
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
		return resultSet[rowIndex][columnIndex];
	}
	
	public Object[] getRow(int rowIndex) {
		return resultSet[rowIndex];
	}
	
	public String getColumnName(int column) {
		return "";
	}

}
