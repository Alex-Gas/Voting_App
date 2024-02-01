package dbcontext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtility {


	private static String host = "";
	private static String database = "";
	private static String username = "";
	private static String password = "";
	
	private Connection connection;
	
	public DatabaseUtility()
	{
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			String conn_string="jdbc:mysql://"+host+"/"+database;
			Connection connect = DriverManager.getConnection(conn_string, username, password);
			connection = connect;
			Thread closeConnection = new Thread(() -> this.closeConnection());
			Runtime.getRuntime().addShutdownHook(closeConnection);
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Connection getConnection(){
		return connection;
	}
	
	public void closeConnection()
	{
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
