package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MedAssistantDB {
	private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=MedAssistantDB;encrypt=true;trustServerCertificate=true";
	private static final String USER = "LMS_Admin";
	private static final String PASSWORD = "jdelima24";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
