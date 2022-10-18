package TrainInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.util.Arrays;
import java.util.ArrayList;

public class UniversityDiscount {
    static final String[] weekday = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public static void makeTable() {
	try{
	    File ff = new File("trainDatabase/UniversityDiscount.db");
	    if (ff.exists())
		return;
	    Connection c = DriverManager.getConnection("jdbc:sqlite:trainDatabase/UniversityDiscount.db");
	    Statement stat = c.createStatement();
	    stat.executeUpdate("create table if not exists UniversityDiscount ( "
			       + "TrainNo integer not null, "
			       + "Monday double not null, "
			       + "Tuesday double not null, "
			       + "Wednesday double not null, "
			       + "Thursday double not null, "
			       + "Friday double not null, "
			       + "Saturday double not null, "
			       + "Sunday double not null)");
	    FileInputStream f = new FileInputStream("data/universityDiscount.json");
            JSONObject jo = new JSONObject(new JSONTokener(f));
	    JSONArray ja = (JSONArray) jo.get("DiscountTrains");
	    double[] discount = new double[7];
	    int TrainNo = 0;
	    for (Object o : ja) {
		jo = (JSONObject) o;
		TrainNo = jo.getInt("TrainNo");
		jo = (JSONObject) jo.get("ServiceDayDiscount");
		for (int i = 0; i < 7; ++i)
		    discount[i] = (double) jo.getDouble(weekday[i]);
		String cmd = String.format("insert into UniversityDiscount(TrainNo");
		for (int i = 0; i < 7; ++i)
		    cmd += String.format(", %s", weekday[i]);
		cmd += String.format(") select %d", TrainNo);
		for (int i = 0; i < 7; ++i)
		    cmd += String.format(", %.2f", discount[i]);
		cmd += String.format(" where not exists(select 1 from UniversityDiscount where TrainNo = %d)", TrainNo);
		stat.executeUpdate(cmd);
	    }
	    stat.close();
	    c.close();
	}
	catch (Exception e) {
	    System.out.println("makeTable : " + e.getMessage());
	}
    }
    public static void main(String[] argv) {
	makeTable();
    }
}
