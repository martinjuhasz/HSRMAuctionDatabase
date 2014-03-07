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
	
	/**
	 * Instantiates a new database model.
	 *
	 * @param db the database connection
	 */
	public DatabaseModel(Connection db) {
		this.db = db;
		this.tableModels = new LinkedList<>();
	}
	
	/**
	 * Load data and notify the table models.
	 */
	protected void loadData() {
		resultSet = queryResults();
		for(DatabaseTableModel tableModel : tableModels) {
			tableModel.fireTableDataChanged();
		}
	}
	
	/**
	 * Refresh the data.
	 */
	public void refresh() {
		loadData();
	}
	
	/**
	 * Query results.
	 *
	 * @return the list of database rows
	 */
	private List<Object[]> queryResults() {
		if(selectStmt == null) return null;
		try {
			return resultSetToList(selectStmt.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Convert result set to list of object arrays.
	 *
	 * @param resultSet the result set
	 * @return the list
	 */
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

	/**
	 * Gets the row.
	 *
	 * @param rowIndex the row index
	 * @return the row
	 */
	public Object[] getRow(int rowIndex) {
		return resultSet.get(rowIndex);
	}
	
	/**
	 * Gets the first row.
	 *
	 * @return the first
	 */
	public Object[] getFirst() {
		return getRow(0);
	}
	
	/**
	 * Count of rows.
	 *
	 * @return the int
	 */
	public int size() {
		return resultSet.size();
	}
	
	/**
	 * Gets the table model.
	 *
	 * @param columns the columns
	 * @return the table model
	 */
	protected DatabaseTableModel getTableModel(String []columns) {
		DatabaseTableModel tableModel = new DatabaseTableModel(this, columns);
		tableModels.add(tableModel);
		return tableModel;
	}
	
	/**
	 * Gets the table model.
	 *
	 * @return the table model
	 */
	public DatabaseTableModel getTableModel() {
		return new DatabaseTableModel(this, new String[]{});
	}
}
