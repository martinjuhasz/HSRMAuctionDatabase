package controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import model.ActiveAuctionsList;
import model.AuctionList;
import model.CategoryComboModel;
import model.CategoryList;
import model.ClosedAuctionsList;
import model.SearchListModel;
import model.UserList;
import model.UserModel;
import model.WonAuctionsListModel;

public class ModelManager {

	private Connection connection;
	private List<ModelManagerListener> modelManagerListeners;

    private static final String DATABASE_PATH = "db.intern.mi.hs-rm.de:5432/mjuha001_auction";
    private static final String DATABASE_USER = "mjuha001";
    private static final String DATABASE_PASSWORD = "XhED6Nj8yneGgcYwu:xnH8&d7h";

	private String loginUserName;
	private int loginUserID;
	private boolean admin;

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver did not load - aborting");
			System.exit(-1);
		}
	}

	public ModelManager() {
		modelManagerListeners = new LinkedList<>();
		loginUserID = -1;

		initDBConnection();
	}

	public void addModelManagerListener(ModelManagerListener listener) {
		modelManagerListeners.add(listener);
	}

	public void removeModelManagerListener(ModelManagerListener listener) {
		modelManagerListeners.remove(listener);
	}

	private void initDBConnection() {
		try {
			String url = "jdbc:postgresql://" + DATABASE_PATH;
			connection = DriverManager.getConnection(url, DATABASE_USER,
					DATABASE_PASSWORD);
		} catch (SQLException e) {
			System.out.println("Couldn't connect - aborting");
			System.exit(-1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					if (!connection.isClosed() && connection != null) {
						connection.close();
						if (connection.isClosed())
							System.out.println("Connection to Database closed");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public UserList getUserList() {
		return new UserList(connection);
	}

	public AuctionList getAuctionList() {
		return new AuctionList(connection);
	}

	public AuctionList getAuctionList(int category) {
		AuctionList auctionList = new AuctionList(connection);
		auctionList.setCategory(category);
		return auctionList;
	}
	
	public AuctionList getAuctionListWithSearchTerm(String term) {
		AuctionList auctionList = new AuctionList(connection);
		auctionList.setSearchTerm(term);
		return auctionList;
	}
	
	public WonAuctionsListModel getWonAuctionsListModel() {
		WonAuctionsListModel listModel = new WonAuctionsListModel(connection);
		listModel.setUser(loginUserID);
		return listModel;
	}

	public CategoryList getCategoriesList() {
		return new CategoryList(connection);
	}
	
	public SearchListModel getSearchList(int uid) {
		return new SearchListModel(connection, uid);
	}

	public ActiveAuctionsList getActiveAuctionsList(String category) {
		ActiveAuctionsList acticeAuctionsList = new ActiveAuctionsList(
				connection);
		acticeAuctionsList.setCategory(category);
		return acticeAuctionsList;
	}

	public ClosedAuctionsList getClosedAuctionsList() {
		return new ClosedAuctionsList(connection);
	}
	
	public CategoryComboModel getCategoryComboModel() {
		return new CategoryComboModel(getCategoriesList());
	}
	
	public UserModel getUserModel(int id) {
		return new UserModel(connection, id);
	}

	public int getLoginUserID() {
		return loginUserID;
	}
	
	public void addSearchTerm(String searchTerm) throws SQLException, ModelManagerException {
		PreparedStatement insertTermStmt = connection.prepareStatement("INSERT INTO \"search_term\" VALUES(?,?)");
		insertTermStmt.setInt(1, loginUserID);
		insertTermStmt.setString(2, searchTerm);
		
		int termWasInserted = insertTermStmt.executeUpdate();
		if (termWasInserted <= 0) {
			throw new ModelManagerException(
					"unable to insert search term. bad arguments?");
		}
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateSearchTerms(this);
		}
	}
	
	public void deleteSearchTerm(String searchTerm) throws SQLException {
		PreparedStatement deleteTermStmt = connection.prepareStatement("DELETE FROM \"search_term\" WHERE uid=? AND term=?");
		deleteTermStmt.setInt(1, loginUserID);
		deleteTermStmt.setString(2, searchTerm);
		deleteTermStmt.executeUpdate();
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateSearchTerms(this);
		}
	}

	public void updateUser(String userName, String password, String firstName,
			String surName, String email, String street, String streetNumber,
			String postalCode, String city, int uid) throws SQLException, ModelManagerException {
		
		// TODO: if password is empty, keep old password

		// insert user
		PreparedStatement insertUserStmt;
		if (uid < 0) {
			insertUserStmt = connection
					.prepareStatement("INSERT INTO \"user_view\" VALUES(?,?,?,?,?,?,?,?,?)");
		} else {
			insertUserStmt = connection
					.prepareStatement("UPDATE \"user_view\" SET username=?,"
							+ "first_name=?, last_name=?, email=?, street=?, street_number=?, postal_code=?, city=?, password=? WHERE id=?");
			insertUserStmt.setInt(10, uid);
		}

		insertUserStmt.setString(1, userName);
		insertUserStmt.setString(2, firstName);
		insertUserStmt.setString(3, surName);
		insertUserStmt.setString(4, email);
		insertUserStmt.setString(5, street);
		insertUserStmt.setString(6, streetNumber);
		insertUserStmt.setString(7, postalCode);
		insertUserStmt.setString(8, city);
		insertUserStmt.setString(9, md5(password));

		int userWasInserted = insertUserStmt.executeUpdate();
		if (userWasInserted <= 0) {
			throw new ModelManagerException(
					"unable to insert or update user. bad arguments?");
		}

		notifyUserUpdate();
	}
	
	public void deleteUser(int uid) throws SQLException{
		PreparedStatement deleteUserStmt = connection.prepareStatement("DELETE FROM \"user_view\" WHERE id=?");
		deleteUserStmt.setInt(1, uid);
		deleteUserStmt.executeUpdate();
		
		notifyUserUpdate();
	}
	
	private void notifyUserUpdate() {
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateUser(this);
		}
	}

	public void updateCategory(String newCategory, int cid) throws ModelManagerException,SQLException {
		
		PreparedStatement insertCategoryStmt;
		if (cid < 0) {
			insertCategoryStmt = connection.prepareStatement("INSERT INTO \"category\"(name) VALUES(?)");
		} else {
			insertCategoryStmt = connection.prepareStatement("UPDATE \"category\" SET name=? WHERE id=?");
			insertCategoryStmt.setInt(2, cid);
		}

		insertCategoryStmt.setString(1, newCategory);

		int categoryWasInserted = insertCategoryStmt.executeUpdate();
		if (categoryWasInserted <= 0) {
			throw new ModelManagerException("unable to insert or update category. bad arguments?");
		}

		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateCategory(this);
		}

	}
	
	public void deleteCategory(int cid) throws SQLException{
		PreparedStatement deleteCategoryStmt = connection.prepareStatement("DELETE FROM \"category\" WHERE id=?");
		deleteCategoryStmt.setInt(1, cid);
		deleteCategoryStmt.executeUpdate();
		
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateCategory(this);
		}
	}
	
	public void insertAuction(String title, int categoryID, String description, boolean isDirectBuy, int price, BufferedImage image) throws ModelManagerException,SQLException {
		
		PreparedStatement insertAuctionStmt = connection.prepareStatement("INSERT INTO \"auction\"(start_time, end_time, title, description, image, category, offerer, price, is_directbuy) VALUES(?,?,?,?,?,?,?,?,?)");
		
		insertAuctionStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		insertAuctionStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
		insertAuctionStmt.setString(3, title);
		insertAuctionStmt.setString(4, description);
		insertAuctionStmt.setInt(6, categoryID);
		insertAuctionStmt.setInt(7, loginUserID);
		insertAuctionStmt.setInt(8, price);
		insertAuctionStmt.setBoolean(9, isDirectBuy);
		
		if(image != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "jpg", os);
			} catch (IOException e) {
				throw new ModelManagerException("image not readable");
			}
			byte imageBytes[] = os.toByteArray();
			InputStream is = new ByteArrayInputStream(imageBytes);
			insertAuctionStmt.setBinaryStream(5, is, imageBytes.length);
		} else {
			insertAuctionStmt.setNull(5, Types.NULL);
		}
		
		int auctionWasInserted = insertAuctionStmt.executeUpdate();
		if (auctionWasInserted <= 0) {
			throw new ModelManagerException("unable to insert auction. bad arguments?");
		}

		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateAuction(this);
		}
		
	}

	public boolean login(String username, String password) throws SQLException {
		PreparedStatement loginStmt = connection
				.prepareStatement("SELECT u.username, u.id,"
						+ "coalesce((SELECT TRUE FROM \"admin\" a WHERE a.uid = u.id ), FALSE) AS admin "
						+ "FROM \"user\" u WHERE u.username=? AND u.password=?");
		loginStmt.setString(1, username);
		loginStmt.setString(2, md5(password));
		ResultSet res = loginStmt.executeQuery();
		if (res.next()) {
			loginUserName = res.getString(1);
			loginUserID = res.getInt(2);
			admin = res.getBoolean(3);

			for (ModelManagerListener listener : modelManagerListeners) {
				listener.userDidLogin(this);
			}

			return true;
		}
		return false;
	}

	public void logout() {
		loginUserName = null;
		loginUserID = -1;
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.userDidLogout(this);
		}
	}

	public boolean isLoggedIn() {
		return loginUserName != null;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public boolean isAdmin() {
		return admin;
	}

	private String md5(String toHash) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(toHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder(2 * hash.length);
			for (byte b : hash) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return "";
	}
	
	public void bid(int auction, int price) throws SQLException, ModelManagerException {
		PreparedStatement insertBidStmt = connection
				.prepareStatement("INSERT INTO \"bid\"(uid, auction, price) VALUES(?,?,?)");
		insertBidStmt.setInt(1, loginUserID);
		insertBidStmt.setInt(2, auction);
		insertBidStmt.setInt(3, price);
		
		int bidnWasInserted = insertBidStmt.executeUpdate();
		if (bidnWasInserted <= 0) {
			throw new ModelManagerException("unable to insert bid. bad arguments?");
		}

		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateAuction(this);
		}
	}

	public void comment(int auction, String text) throws SQLException, ModelManagerException {
		PreparedStatement insertCommentStmt = connection
				.prepareStatement("INSERT INTO \"comment\"(uid, auction, content) VALUES(?,?,?)");
		insertCommentStmt.setInt(1, loginUserID);
		insertCommentStmt.setInt(2, auction);
		insertCommentStmt.setString(3, text);
		
		int commentWasInserted = insertCommentStmt.executeUpdate();
		if (commentWasInserted <= 0) {
			throw new ModelManagerException("unable to insert comment. bad arguments?");
		}

		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateAuction(this);
		}
	}
}
