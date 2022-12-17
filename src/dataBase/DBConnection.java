package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	static Connection connect = null;

	public static Connection getConnection() {

		try {
			
			Class.forName("org.h2.Driver");
			connect = DriverManager.getConnection(
					"jdbc:h2:tcp://localhost/C:\\Users\\pe6o0\\eclipse-workspace\\Final project - DB & Java\\Database\\RentCarDB",
					"sa",
					"1234"
					);
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return connect;
	}
}