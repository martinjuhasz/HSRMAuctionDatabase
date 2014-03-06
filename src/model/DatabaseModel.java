package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseModel {

	protected Connection db;
	protected List<Object[]> resultSet;
	private List<DatabaseTableModel> tableModels;
	
	protected PreparedStatement selectStmt;
	
	public DatabaseModel(Connection db) {
		this.db = db;
		this.tableModels = new LinkedList<>();
	}
	
	protected void loadData() {
		resultSet = queryResults();
		for(DatabaseTableModel tableModel : tableModels) {
			tableModel.fireTableDataChanged();
		}
	}
	
	private List<Object[]> queryResults() {
		if(selectStmt == null) return null;
		try {
			return resultSetToList(selectStmt.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected List<Object[]> resultSetToList(ResultSet resultSet) {
		List<Object[]> result = new ArrayList<>();
		try {
			while(resultSet.next()) {
				Object[] row = new Object[resultSet.getMetaData().getColumnCount()];
				for (int i = 0; i < row.length; i++) {
					row[i] = resultSet.getObject(i + 1);
				}
				result.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Object[] getRow(int rowIndex) {
		return resultSet.get(rowIndex);
	}
	
	public int size() {
		return resultSet.size();
	}
	
	protected DatabaseTableModel getTableModel(String []columns) {
		DatabaseTableModel tableModel = new DatabaseTableModel(this, columns);
		tableModels.add(tableModel);
		return tableModel;
	}
	
	public DatabaseTableModel getTableModel() {
		return new DatabaseTableModel(this, new String[]{});
	}
}
