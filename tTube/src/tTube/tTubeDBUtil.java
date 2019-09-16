package tTube;

import java.sql.Connection;
import java.sql.DriverManager;

public class tTubeDBUtil {
	
	static String url = "jdbc:oracle:thin:@localhost:1521:XE";
	private static String user="mydev";
	private static String pwd="tiger";
	
	static {
		
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		System.out.println("Driver Loading Success!");
		}catch(ClassNotFoundException e) {
			System.out.println("Driver Loading Fail..: ");
			e.printStackTrace();
		}
		
	}
	
	public static Connection getCon() throws java.sql.SQLException{
		Connection con=DriverManager.getConnection(url,user,pwd);
		return con;
	}

}
