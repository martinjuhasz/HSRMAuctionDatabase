/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
package model;

import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {

	private DatabaseModel databaseModel;
	private String[] columns;
	
	/**
	 * Instantiates a new database table model.
	 *
	 * @param databaseModel the database model
	 * @param columns the columns
	 */
	public DatabaseTableModel(DatabaseModel databaseModel, String[] columns) {
		this.databaseModel = databaseModel;
		this.columns = columns;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return databaseModel.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columns.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return databaseModel.getRow(rowIndex)[columnIndex];
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		return columns[column];
	}
	
	/**
	 * Gets the database model.
	 *
	 * @return the database model
	 */
	public DatabaseModel getDatabaseModel() {
		return databaseModel;
	}
}
