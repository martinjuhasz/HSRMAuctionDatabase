package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {

	protected Connection db;
	protected List<Object[]> resultSet;
	
	protected PreparedStatement selectStmt;
	
	public DatabaseTableModel(Connection db) {
		this.db = db;
		loadData();
	}
	
	protected void loadData() {
		resultSet = queryResults();

		fireTableDataChanged();
	}
	
	private List<Object[]> queryResults() {
		if(selectStmt == null) return null;
		
		List<Object[]> result = new ArrayList<>();
		try {
			ResultSet resultSet = selectStmt.executeQuery();
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
	
	@Override
	public int getRowCount() {
		return resultSet.size();
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return resultSet.get(rowIndex)[columnIndex];
	}
	
	public Object[] getRow(int rowIndex) {
		return resultSet.get(rowIndex);
	}
	
	public String getColumnName(int column) {
		return "";
	}

}
