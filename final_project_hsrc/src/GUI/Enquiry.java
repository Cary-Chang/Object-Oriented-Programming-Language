package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Enquiry {
	protected Connection con;
	protected PreparedStatement ps;
	protected ResultSet rs;
	
	public Enquiry() {}
	
	/**
	 * Connect to the database
	 * @param file name
	 */
	void connectDB(String file) {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + file); // connecting to our database
			//			System.out.println("Connected!");
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e + "");
		}
	}
	
	
	/**
	 * Convert list to 2-dim array
	 * @param list
	 * @return Object[][]
	 */
	Object[][] listToArray(ArrayList<Object> list) {
		Object[][] arr = new Object[1][list.size()];
		arr[0] = list.toArray(arr[0]);
		return arr;
	}

	/**
	 * Convert 2-dim list to 2-dim array
	 * @param list
	 * @return Object[][]
	 */
	// Overload
	Object[][] listToArray(ArrayList<Object[]> list, int size){
		Object[][] arr = new Object[list.size()][size];
		arr = list.toArray(arr);
		return arr;
	}
	
	/**
	 * Get data from the database
	 */
	protected abstract void getINFO();	
	
	/**
	 * Use GUI JTable show the corresponding data
	 */
	public abstract void show();
}
