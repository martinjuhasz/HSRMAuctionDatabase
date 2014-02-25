package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ModelManager {
	
    private Connection connection;
    private static final String DATABASE_PATH = "localhost:5432/auction";
    private static final String DATABASE_USER = "";
    
    private UserList userList;

    static { 
    	try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver did not load - aborting");
            System.exit(-1);
        }
    } 
  
     
    private void initDBConnection() { 
        try {
            String url = "jdbc:postgresql://" + DATABASE_PATH;
            connection = DriverManager.getConnection(url);
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

	public ModelManager() {
		initDBConnection();
		
		userList = new UserList(connection);
	}

	public UserList getUserList() {
		return userList;
	}

	
	
}
