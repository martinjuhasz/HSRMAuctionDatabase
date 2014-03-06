package model;

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

    
    private UserList userList;
    private AuctionList auctionList;
    private CategoryList categoriesList;
    private ActiveAuctionsList acticeAuctionsList;

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
		if(userList == null) {
			userList = new UserList(connection);
		}
		return userList;
	}
	
	public AuctionList getAuctionList() {
		if(auctionList == null) {
			auctionList = new AuctionList(connection);
		}
		return auctionList;
	}
	public CategoryList getCategoriesList() {
		if(categoriesList == null) {
			categoriesList = new CategoryList(connection);
		}
		return categoriesList;
	}
	
	public ActiveAuctionsList getActiveAuctionsList(String category) {
		if(acticeAuctionsList == null) {
			acticeAuctionsList = new ActiveAuctionsList(connection);
		}
		acticeAuctionsList.setCategory(category);
		return acticeAuctionsList;
	}
	

	public void insertObject(Object object) throws Exception {
		if(object instanceof User) {
			insertUser((User)object);
		}
	}
	
	// private void insertUser(String username, String firstname, String lastname, String email, String street, String houseNumber, String postalCode, String city)
	private void insertUser(User user) throws Exception {
		
		// insert user
		PreparedStatement insertUserStmt = connection.prepareStatement("INSERT INTO \"user_view\" VALUES(?,?,?,?,?,?,?, ?)");
		insertUserStmt.setString(1, user.getUsername());
		insertUserStmt.setString(2, user.getFirstName());
		insertUserStmt.setString(3, user.getSurName());
		insertUserStmt.setString(4, user.getEmail());
		insertUserStmt.setString(5, user.getStreet());
		insertUserStmt.setString(6, user.getHouseNumber());
		insertUserStmt.setString(7, user.getPostalCode());
		insertUserStmt.setString(8, user.getCity());
		
		int userWasInserted = insertUserStmt.executeUpdate();
		if(userWasInserted <= 0) {
			throw new ModelManagerException("unable to insert user. bad arguments?");
		}
		
		userList = null;
		
		for (ModelManagerListener listener : modelManagerListeners) {
			listener.didUpdate(this);
			listener.didUpdateUser(this);
		}
		
	}
	
}
