/*
 * HSRMAuctionDatabase
 * 
 * @author Martin Juhasz
 * @author Simon Seyer
 * @author Julia Kraft
 * 
 */
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

	/**
	 * Instantiates a new model manager.
	 */
	public ModelManager() {
		modelManagerListeners = new LinkedList<>();
		loginUserID = -1;

		initDBConnection();
	}

	/**
	 * Adds the model manager listener.
	 *
	 * @param listener the listener
	 */
	public void addModelManagerListener(ModelManagerListener listener) {
		modelManagerListeners.add(listener);
	}

	/**
	 * Removes the model manager listener.
	 *
	 * @param listener the listener
	 */
	public void removeModelManagerListener(ModelManagerListener listener) {
		modelManagerListeners.remove(listener);
	}

	/**
	 * Inits the db connection.
	 */
	private void initDBConnection() {
		try {
			String url = "jdbc:postgresql://" + DATABASE_PATH;
			connection = DriverManager.getConnection(url, DATABASE_USER,
					DATABASE_PASSWORD);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Couldn't connect - aborting");
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

	/**
	 * Gets the user list.
	 *
	 * @return the user list
	 */
	public UserList getUserList() {
		return new UserList(connection);
	}

	/**
	 * Gets the auction list.
	 *
	 * @return the auction list
	 */
	public AuctionList getAuctionList() {
		return new AuctionList(connection);
	}

	/**
	 * Gets the auction list.
	 *
	 * @param category the category
	 * @return the auction list
	 */
	public AuctionList getAuctionList(int category) {
		AuctionList auctionList = new AuctionList(connection);
		auctionList.setCategory(category);
		return auctionList;
	}
	
	/**
	 * Gets the auction list with search term.
	 *
	 * @param term the term
	 * @return the auction list with search term
	 */
	public AuctionList getAuctionListWithSearchTerm(String term) {
		AuctionList auctionList = new AuctionList(connection);
		auctionList.setSearchTerm(term);
		return auctionList;
	}
	
	/**
	 * Gets the won auctions list model.
	 *
	 * @return the won auctions list model
	 */
	public WonAuctionsListModel getWonAuctionsListModel() {
		WonAuctionsListModel listModel = new WonAuctionsListModel(connection);
		listModel.setUser(loginUserID);
		return listModel;
	}

	/**
	 * Gets the categories list.
	 *
	 * @return the categories list
	 */
	public CategoryList getCategoriesList() {
		return new CategoryList(connection);
	}
	
	/**
	 * Gets the search list.
	 *
	 * @param uid the uid
	 * @return the search list
	 */
	public SearchListModel getSearchList(int uid) {
		return new SearchListModel(connection, uid);
	}

	/**
	 * Gets the active auctions list.
	 *
	 * @param category the category
	 * @return the active auctions list
	 */
	public ActiveAuctionsList getActiveAuctionsList(String category) {
		ActiveAuctionsList acticeAuctionsList = new ActiveAuctionsList(
				connection);
		acticeAuctionsList.setCategory(category);
		return acticeAuctionsList;
	}

	/**
	 * Gets the closed auctions list.
	 *
	 * @return the closed auctions list
	 */
	public ClosedAuctionsList getClosedAuctionsList() {
		return new ClosedAuctionsList(connection);
	}
	
	/**
	 * Gets the category combo model.
	 *
	 * @return the category combo model
	 */
	public CategoryComboModel getCategoryComboModel() {
		return new CategoryComboModel(getCategoriesList());
	}
	
	/**
	 * Gets the user model.
	 *
	 * @param id the id
	 * @return the user model
	 */
	public UserModel getUserModel(int id) {
		return new UserModel(connection, id);
	}

	/**
	 * Adds the search term.
	 *
	 * @param searchTerm the search term
	 * @throws SQLException the SQL exception
	 * @throws ModelManagerException the model manager exception
	 */
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
	
	/**
	 * Delete search term.
	 *
	 * @param searchTerm the search term
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Update user.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param firstName the first name
	 * @param surName the sur name
	 * @param email the email
	 * @param street the street
	 * @param streetNumber the street number
	 * @param postalCode the postal code
	 * @param city the city
	 * @param uid the uid
	 * @throws SQLException the SQL exception
	 * @throws ModelManagerException the model manager exception
	 */
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
		insertUserStmt.setString(9, password.length() == 0 ? "" : md5(password));

		int userWasInserted = insertUserStmt.executeUpdate();
		if (userWasInserted <= 0) {
			throw new ModelManagerException(
					"unable to insert or update user. bad arguments?");
		}

		notifyUserUpdate();
	}
	
	/**
	 * Delete user.
	 *
	 * @param uid the uid
	 * @throws SQLException the SQL exception
	 */
	public void deleteUser(int uid) throws SQLException{
		PreparedStatement deleteUserStmt = connection.prepareStatement("DELETE FROM \"user_view\" WHERE id=?");
		deleteUserStmt.setInt(1, uid);
		deleteUserStmt.executeUpdate();
		
		notifyUserUpdate();
	}
	
	/**
	 * Notify user update.
	 */
	private void notifyUserUpdate() {
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateUser(this);
		}
	}

	/**
	 * Update category.
	 *
	 * @param newCategory the new category
	 * @param cid the cid
	 * @throws ModelManagerException the model manager exception
	 * @throws SQLException the SQL exception
	 */
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
	
	/**
	 * Delete category.
	 *
	 * @param cid the cid
	 * @throws SQLException the SQL exception
	 */
	public void deleteCategory(int cid) throws SQLException{
		PreparedStatement deleteCategoryStmt = connection.prepareStatement("DELETE FROM \"category\" WHERE id=?");
		deleteCategoryStmt.setInt(1, cid);
		deleteCategoryStmt.executeUpdate();
		
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateCategory(this);
		}
	}
	
	/**
	 * Insert auction.
	 *
	 * @param title the title
	 * @param categoryID the category id
	 * @param description the description
	 * @param isDirectBuy the is direct buy
	 * @param price the price
	 * @param image the image
	 * @throws ModelManagerException the model manager exception
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Login.
	 *
	 * @param username the username
	 * @param password the password
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 */
	public boolean login(String username, String password) throws SQLException {
		PreparedStatement loginStmt = connection
				.prepareStatement("SELECT u.username, u.id,"
						+ "coalesce((SELECT TRUE FROM \"admin\" a WHERE a.uid = u.id ), FALSE) AS admin "
						+ "FROM \"user\" u WHERE u.username=? AND u.password=? AND u.deleted=FALSE");
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

	/**
	 * Logout.
	 */
	public void logout() {
		loginUserName = null;
		loginUserID = -1;
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.userDidLogout(this);
		}
	}

	/**
	 * Checks if is logged in.
	 *
	 * @return true, if is logged in
	 */
	public boolean isLoggedIn() {
		return loginUserName != null;
	}

	/**
	 * Gets the login user name.
	 *
	 * @return the login user name
	 */
	public String getLoginUserName() {
		return loginUserName;
	}
	
	/**
	 * Gets the login user id.
	 *
	 * @return the login user id
	 */
	public int getLoginUserID() {
		return loginUserID;
	}

	/**
	 * Checks if is admin.
	 *
	 * @return true, if is admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * Md5.
	 *
	 * @param toHash the to hash
	 * @return the string
	 */
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
	
	/**
	 * Bid.
	 *
	 * @param auction the auction
	 * @param price the price
	 * @throws SQLException the SQL exception
	 * @throws ModelManagerException the model manager exception
	 */
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

	/**
	 * Comment.
	 *
	 * @param auction the auction
	 * @param text the text
	 * @throws SQLException the SQL exception
	 * @throws ModelManagerException the model manager exception
	 */
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
	
	/**
	 * Rate.
	 *
	 * @param auction the auction
	 * @param rating the rating
	 * @throws SQLException the SQL exception
	 * @throws ModelManagerException the model manager exception
	 */
	public void rate(int auction, int rating) throws SQLException, ModelManagerException {
		PreparedStatement insertRatingStmt = connection
				.prepareStatement("INSERT INTO \"rating\"(rater, auction, score) VALUES(?,?,?)");
		insertRatingStmt.setInt(1, loginUserID);
		insertRatingStmt.setInt(2, auction);
		insertRatingStmt.setInt(3, rating);
		
		int ratingWasInserted = insertRatingStmt.executeUpdate();
		if (ratingWasInserted <= 0) {
			throw new ModelManagerException("unable to insert rating. bad arguments?");
		}

		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateAuction(this);
		}
	}
}
