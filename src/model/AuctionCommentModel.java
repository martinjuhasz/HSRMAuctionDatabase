package model;

import java.sql.Connection;
import java.sql.SQLException;

public class AuctionCommentModel extends DatabaseModel {

	public AuctionCommentModel(Connection db, int id) {
		super(db);

		try {
			selectStmt = db.prepareStatement("SELECT * FROM \"auction_comment_view\" WHERE auction=?");
			selectStmt.setInt(1, id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		loadData();
	}
	
	public DatabaseTableModel getTableModel() {
		return super.getTableModel(new String[]{"Benutzer", "Text", "Datum"});
	}

}
