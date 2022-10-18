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

public class EarlyBirdDiscount {
    static final String[] weekday = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    public static void makeTable() {
	try {
	    File ff = new File("trainDatabase/EarlyBirdDiscount.db");
	    if (ff.exists())
		return;
	    Connection c = DriverManager.getConnection("jdbc:sqlite:trainDatabase/EarlyBirdDiscount.db");
	    Statement stat = c.createStatement();
	    String cmd = String.format("create table if not exists EarlyBirdDiscount ( "
				       + "TrainNo integer not null, "
				       + "Weekday string not null, "
				       + "dis65 integer not null, dis8 integer not null, dis9 integer not null)");
	    stat.executeUpdate(cmd);

	    FileInputStream f = new FileInputStream("data/earlyDiscount.json");
            JSONObject jo = new JSONObject(new JSONTokener(f));
            JSONArray ja = (JSONArray) jo.get("DiscountTrains");
	    int TrainNo = 0;
	    for (Object o : ja) {
		jo = (JSONObject) o;
		TrainNo = jo.getInt("TrainNo");
		jo = (JSONObject) jo.get("ServiceDayDiscount");
		for (int i = 0; i < 7; ++i) {
		    int[] discount = new int[3];
		    if (jo.get(weekday[i]).getClass().equals(new JSONArray().getClass())) {
			for (Object d : (JSONArray)jo.get(weekday[i])) {
			    JSONObject jo2 = (JSONObject) d;
			    if ((double) jo2.getDouble("discount") == 0.65)
				discount[0] = jo2.getInt("tickets");
			    else if ((double) jo2.getDouble("discount") == 0.8)
				discount[1] = jo2.getInt("tickets");
			    else if ((double) jo2.getDouble("discount") == 0.9)
				discount[2] = jo2.getInt("tickets");
			}
			cmd = String.format("insert into EarlyBirdDiscount(TrainNo, Weekday, dis65, dis8, dis9) select ");
			cmd += String.format("%d, '%s', %d, %d, %d ", TrainNo, weekday[i], discount[0], discount[1], discount[2]);
			cmd += String.format("where not exists(select 1 from EarlyBirdDiscount where TrainNo = %d and Weekday = '%s')", TrainNo, weekday[i]);
			stat.executeUpdate(cmd);
		    }
		    else {
			cmd = String.format("insert into EarlyBirdDiscount(TrainNo, Weekday, dis65, dis8, dis9) select ");
			cmd += String.format("%d, '%s', 0, 0, 0 ", TrainNo, weekday[i]);
			cmd += String.format("where not exists(select 1 from EarlyBirdDiscount where TrainNo = %d and Weekday = '%s')", TrainNo, weekday[i]);
			stat.executeUpdate(cmd);
		    }
		}
	    }
	    stat.close();
	    c.close();
	}
	catch (Exception e) {
	    System.out.println("makeTable : " + e.getMessage());
	}
    }
    /**
     * YOU SHOULD MAKE SURE THAT "EarlyBirdDiscount.db" and "TrainsINFO.db" EXIST ALREADY.
     */
    public static double getEarlyDiscount(int date, int TrainNo, int count, int updateDatabase) {
	double ret = 1.0;
	Connection c = null;
	LocalDate deadline = LocalDate.of(LocalDate.now().getYear(), date / 100, date % 100).minusDays(5);
	if (LocalDate.now().isAfter(deadline))
	    return ret;
	try {
	    c = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
	    Statement stat = c.createStatement();
	    String cmd = String.format("select EB65, EB8, EB9 from trains where 日期 = %d and 車次 = %d", date, TrainNo);
	    ResultSet rs = stat.executeQuery(cmd);
	    int remainSeat = 0;
	    if (rs.getInt("EB65") >= count) {
		ret = 0.65;
		remainSeat = rs.getInt("EB65");
	    }
	    else if (rs.getInt("EB8") >= count) {
		ret = 0.8;
		remainSeat = rs.getInt("EB8");
	    }
	    else if (rs.getInt("EB9") >= count) {
		ret = 0.9;
		remainSeat = rs.getInt("EB9");
	    }
	    if (updateDatabase == 1 && ret == 0.65) {
		cmd = String.format("update trains set EB65 = %d where 日期 = %d and 車次 = %d", remainSeat - count, date, TrainNo);
		stat.executeUpdate(cmd);
	    }
	    else if (updateDatabase == 1 && ret == 0.8) {
		cmd = String.format("update trains set EB8 = %d where 日期 = %d and 車次 = %d", remainSeat - count, date, TrainNo);
		stat.executeUpdate(cmd);
	    }
	    else if (updateDatabase == 1 && ret == 0.9) {
		cmd = String.format("update trains set EB9 = %d where 日期 = %d and 車次 = %d", remainSeat - count, date, TrainNo);
		stat.executeUpdate(cmd);
	    }
	    rs.close();
	    stat.close();
	    c.close();
	}
	catch (Exception e) {
	    System.out.println("getEarlyDiscount : " + e.getMessage());
	}
	return ret;
    }
    public static void releaseEarlyDiscount(int date, int TrainNo, int count, double discount) {
	Connection c = null;
	try {
	    c = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
	    Statement stat = c.createStatement();
	    int d = 0;
	    if (discount == 0.65)
		d = 65;
	    else if (discount == 0.8)
		d = 8;
	    else
		d = 9;
	    String cmd = String.format("update trains set EB%d = EB%d + %d where 日期 = %d and 車次 = %d",
				       d, d, count, date, TrainNo);
	    stat.executeUpdate(cmd);
	    stat.close();
	    c.close();
	}
	catch (Exception e) {
	    System.out.println("releaseEarlyDiscount : " + e.getMessage());
	}
    }
    public static void main(String[] argv) {
	makeTable();
	System.out.println(getEarlyDiscount(701, 565, 2, 1));
	releaseEarlyDiscount(701, 565, 1, 0.65);
    }
}
