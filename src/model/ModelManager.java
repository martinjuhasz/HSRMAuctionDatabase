package model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ModelManager {
	
    private Connection connection;
    private List<ModelManagerListener> modelManagerListeners;
    
    private static final String DATABASE_PATH = "db.intern.mi.hs-rm.de:5432/mjuha001_auction";
    private static final String DATABASE_USER = "mjuha001";
    private static final String DATABASE_PASSWORD = "XhED6Nj8yneGgcYwu:xnH8&d7h";
    
    private String loginUserName;
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
            connection = DriverManager.getConnection(url, DATABASE_USER, DATABASE_PASSWORD);
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
	
	public AuctionList getAuctionList(String category) {
		AuctionList auctionList = new AuctionList(connection);
		auctionList.setCategory(category);
		return auctionList;
	}
	
	public CategoryList getCategoriesList() {
		return new CategoryList(connection);
	}
	
	public ActiveAuctionsList getActiveAuctionsList(String category) {
		ActiveAuctionsList acticeAuctionsList = new ActiveAuctionsList(connection);
		acticeAuctionsList.setCategory(category);
		return acticeAuctionsList;
	}
	
	public ClosedAuctionsList getClosedAuctionsList() {
		return new ClosedAuctionsList(connection);
	}

	public void updateUser(String userName, String password, String firstName, String surName, String email, 
			String street, String streetNumber, String postalCode, String city, boolean newUser) throws Exception {
		
		// insert user
		PreparedStatement insertUserStmt;
		if (newUser) {
			insertUserStmt = connection.prepareStatement("INSERT INTO \"user_view\" VALUES(?,?,?,?,?,?,?,?,?)");
		} else {
			insertUserStmt = connection.prepareStatement("UPDATE \"user_view\" SET username=?,"
					+ "first_name=?, last_name=?, email=?, street=?, street_number=?, postal_code=?, city=?, password=?");
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
		if(userWasInserted <= 0) {
			throw new ModelManagerException("unable to insert or update user. bad arguments?");
		}
		
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateUser(this);
		}
		
	}
	
	public void updateCategory(String newCategory, String oldCategory, boolean isNew) throws Exception {
		
		PreparedStatement insertCategoryStmt;
		if (isNew) {
			insertCategoryStmt = connection.prepareStatement("INSERT INTO \"category\" VALUES(?)");
		} else {
			insertCategoryStmt = connection.prepareStatement("UPDATE \"category\" SET name=? WHERE name=?");
			insertCategoryStmt.setString(2, oldCategory);
		}
		
		insertCategoryStmt.setString(1, newCategory);
		
		int categoryWasInserted = insertCategoryStmt.executeUpdate();
		if(categoryWasInserted <= 0) {
			throw new ModelManagerException("unable to insert or update category. bad arguments?");
		}
		
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateCategory(this);
		}
		
	}
	
	public boolean login(String username, String password) throws SQLException {
		PreparedStatement loginStmt = connection.prepareStatement("SELECT u.username, "
				+ "coalesce((SELECT TRUE FROM \"admin\" a WHERE a.username = u.username ), FALSE) AS admin "
				+ "FROM \"user\" u WHERE u.username=? AND u.password=?");
		loginStmt.setString(1, username);
		loginStmt.setString(2, md5(password));
		ResultSet res = loginStmt.executeQuery();
		if (res.next()) {
			loginUserName = res.getString(1);
			admin = res.getBoolean(2);
			
			for (ModelManagerListener listener : modelManagerListeners) {
				listener.userDidLogin(this);
			}
			
			return true;
		}
		return false;
	}
	
	public void logout() {
		loginUserName = null;
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
			StringBuilder sb = new StringBuilder(2*hash.length); 
			for(byte b : hash){ 
				sb.append(String.format("%02x", b&0xff)); 
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return "";
	}
	
}
