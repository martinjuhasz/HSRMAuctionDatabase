package model;

import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {

	private DatabaseModel databaseModel;
	private String[] columns;
	
	public DatabaseTableModel(DatabaseModel databaseModel, String[] columns) {
		this.databaseModel = databaseModel;
		this.columns = columns;
	}
	
	@Override
	public int getRowCount() {
		return databaseModel.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return databaseModel.getRow(rowIndex)[columnIndex];
	}
	
	public String getColumnName(int column) {
		return columns[column];
	}
	
	public DatabaseModel getDatabaseModel() {
		return databaseModel;
	}
}
