package TrainInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.util.Arrays;
import java.util.ArrayList;

public class DBWriter {
    /**
     * 把日期存成一個四位整數，十位跟個位代表日，百位千位代表月。
     * 時間也是， -1 代表不會到那站。
     * 對應到的 train 的資料在 train_[日期 * 10000 + 車次].db
     * 方向 1 代表北上，0 代表南下。
     */
    public static void createTrainsTable() {
	try {
	    File dir = new File("trainDatabase");
	    if (!dir.exists())
		dir.mkdir();
	    EarlyBirdDiscount.makeTable();
	    UniversityDiscount.makeTable();
	    Connection con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
	    Statement stat = con.createStatement();
	    //stat.setQueryTimeout(30);
	    stat.executeUpdate("create table if not exists trains ( "
			       + "車次 integer not null, "
			       + "日期 integer not null, "
			       + "方向 integer not null, "
			       + "南港 integer not null, "
			       + "台北 integer not null, "
			       + "板橋 integer not null, "
			       + "桃園 integer not null, "
			       + "新竹 integer not null, "
			       + "苗栗 integer not null, "
			       + "台中 integer not null, "
			       + "彰化 integer not null, "
			       + "雲林 integer not null, "
			       + "嘉義 integer not null, "
			       + "台南 integer not null, "
			       + "左營 integer not null, "
			       + "EB65 integer, "
			       + "EB8 integer, EB9 integer, "
			       + "university double)");
	    con.close();
	    stat.close();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
    private static final Integer[] stationID = {990, 1000, 1010, 1020, 1030, 1035, 1040, 1043, 1047, 1050, 1060, 1070};
    public static void addTrainsFromJson() {
	try {
	    FileInputStream f = new FileInputStream("data/timeTable.json");
	    JSONArray ja = new JSONArray(new JSONTokener(f));
	    int[] info = new int[15];
	    int[] service = new int[7];
	    for (Object o : ja) {
		JSONObject t = (JSONObject) o;
		t = (JSONObject) t.get("GeneralTimetable");
		info[0] = ((JSONObject)t.get("GeneralTrainInfo")).getInt("TrainNo");
		info[2] = ((JSONObject)t.get("GeneralTrainInfo")).getInt("Direction");
		JSONArray stop = (JSONArray) t.get("StopTimes");
		for (int i = 3; i < 15; ++i)
		    info[i] = -1;
		for (Object o2 : stop) {
		    JSONObject s = (JSONObject) o2;
		    String departure = s.getString("DepartureTime");
		    String[] departures = departure.split(":");
		    info[3 + Arrays.asList(stationID).indexOf(s.getInt("StationID"))] =
			Integer.parseInt(departures[0]) * 100 + Integer.parseInt(departures[1]);
		}
		t = (JSONObject) t.get("ServiceDay");
		service[0] = t.getInt("Sunday");
		service[1] = t.getInt("Monday");
		service[2] = t.getInt("Tuesday");
		service[3] = t.getInt("Wednesday");
		service[4] = t.getInt("Thursday");
		service[5] = t.getInt("Friday");
		service[6] = t.getInt("Saturday");

		LocalDate cur = LocalDate.now();
		LocalDate end = cur.plusDays(29);
		Connection con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
		Statement stat = con.createStatement();
		Connection uniCon = DriverManager.getConnection("jdbc:sqlite:trainDatabase/UniversityDiscount.db");
		Statement uniStat = uniCon.createStatement();
		Connection EBCon = DriverManager.getConnection("jdbc:sqlite:trainDatabase/EarlyBirdDiscount.db");
		Statement EBStat = EBCon.createStatement();

		while (!cur.equals(end)) {
		    String[] date = cur.toString().split("-");
		    info[1] = Integer.parseInt(date[1] + date[2]);
		    String sqlcmd = "insert into trains(車次, 日期, 方向, 南港, 台北, 板橋, 桃園, 新竹, 苗栗, "
			+ "台中, 彰化, 雲林, 嘉義, 台南, 左營, EB65, EB8, EB9, university) select ";
		    for (int i = 0; i < 14; ++i)
			sqlcmd += String.format("%d, ", info[i]);
		    sqlcmd += String.format("%d, ", info[14]);

		    // Early Bird
		    String weekday = cur.getDayOfWeek().toString();
		    String tmps = weekday.substring(1).toLowerCase();
		    weekday = weekday.charAt(0) + tmps;
		    tmps = String.format("select dis65, dis8, dis9 from EarlyBirdDiscount where ");
		    tmps += String.format("Weekday = '%s' and TrainNo = %d", weekday, info[0]);
		    ResultSet EBRs = EBStat.executeQuery(tmps);
		    if (EBRs.next()) {
			int[] EB = new int[3];
			EB[0] = EBRs.getInt("dis65"); EB[1] = EBRs.getInt("dis8"); EB[2] = EBRs.getInt("dis9");
			sqlcmd += String.format("%d, %d, %d, ", EB[0], EB[1], EB[2]);
		    }
		    else
			sqlcmd += String.format("0, 0, 0, ");			
		    EBRs.close();
		    
		    // University Discount
		    tmps = String.format("select %s from UniversityDiscount where TrainNo = %d", weekday, info[0]);
		    ResultSet uniRs = uniStat.executeQuery(tmps);
		    if (uniRs.next()) {
			double uniDiscount = uniRs.getDouble(weekday);
			sqlcmd += String.format("%.2f ", uniDiscount);
		    }
		    else
			sqlcmd += String.format("1.0 ");
		    uniRs.close();
		    
		    sqlcmd += String.format("where not exists(select 1 from trains where 車次 = %d and 日期 = %d)",
					    info[0], info[1]);
		    stat.executeUpdate(sqlcmd);
		    cur = cur.plusDays(1);
		}
		
		con.close();
		f.close();
		stat.close();
	    }
	}
	catch (Exception e) {
	    System.out.println("addTrainFromJson : " + e.getMessage());
	}
    }

    /**
     * id 從 0 到 99 分別代表 1A 到 20E。
     * 南港 0 代表沒人訂，1 代表有人訂。
     */
    static void trainSeatInit(String filename) {
	try {
	    Connection c = DriverManager.getConnection(filename);
	    Statement stat = c.createStatement();
	    stat.executeUpdate("create table if not exists seats ( "
			       + "car integer not null, "
			       + "id integer not null, "
			       + "type string not null, "
			       + "南港 integer not null, "
			       + "台北 integer not null, "
			       + "板橋 integer not null, "
			       + "桃園 integer not null, "
			       + "新竹 integer not null, "
			       + "苗栗 integer not null, "
			       + "台中 integer not null, "
			       + "彰化 integer not null, "
			       + "雲林 integer not null, "
			       + "嘉義 integer not null, "
			       + "台南 integer not null, "
			       + "左營 integer not null)");
	    for (int car = 1; car <= 12; ++car) {
		for (int i = 0; i < 100; ++i) {
		    String cmd = "insert into seats values(";
		    cmd += String.format("%d, %d, '%s', ", car, i, car == 6? "business" : "standard");
		    for (int j = 0; j < 11; ++j)
			cmd += "0, ";
		    cmd += "0)";
		    stat.executeUpdate(cmd);
		}
	    }
	    for (int car = 1; car <= 12; ++car) {
		switch (car) {
		case 1:
		    updateRow(c, car, 1, "DE", 1);
		    for (int i = 14; i <= 20; ++i)
			updateRow(c, car, i, "ABCDE", 1);
		    break;
		case 2: case 4: case 8: case 10:
		    updateRow(c, car, 1, "DE", 1);
		    updateRow(c, car, 20, "DE", 1);
		    break;
		case 3: case 9: case 11:
		    updateRow(c, car, 1, "DE", 1);
		    for (int i = 19; i <= 20; ++i)
			updateRow(c, car, i, "ABCDE", 1);
		    break;
		case 5:
		    updateRow(c, car, 1, "DE", 1);
		    for (int i = 18; i <= 20; ++i)
			updateRow(c, car, i, "ABCDE", 1);
		    break;
		case 7:
		    updateRow(c, car, 12, "ABC", 1);
		    for (int i = 13; i <= 20; ++i)
			updateRow(c, car, i, "ABCDE", 1);
		    break;
		case 12:
		    updateRow(c, car, 14, "DE", 1);
		    for (int i = 15; i <= 20; ++i)
			updateRow(c, car, i, "ABCDE", 1);
		    break;
		case 6:
		    updateRow(c, car, 1, "ABC", 1);
		    for (int i = 2; i <= 17; ++i)
			updateRow(c, car, i, "B", 1);
		    for (int i = 18; i <= 20; ++i)
			updateRow(c, car, i, "ABCDE", 1);
		}
	    }
	    c.close();
	    stat.close();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
    private static final String[] stations = {"南港", "台北", "板橋", "桃園",
	"新竹", "苗栗", "台中", "彰化", "雲林", "嘉義", "台南", "左營"};
    /**
     * 把指定的座位所有站都改成同個狀態（有訂沒訂），用於更新哪些位置可訂。
     * @param c 要改的檔，一定要是一個 train 的 database。
     * @param row 第幾個 row, 可以是 1 ~ 20。
     * @param col 第幾個 column, 需要是字串，也就是 "ABCD" 這樣的形式。
     * @param update 要改成有訂還是沒訂，改成有訂放 1，沒訂放 0。
     */
    private static void updateRow(Connection c, int car, int row, String col, int update) {
	try {
	    Statement stat = c.createStatement();
	    int baseid = (row - 1) * 5;
	    int len = col.length();
	    for (int j = 0; j < len; ++j) {
		String cmd = "update seats set ";
		for (int i = 0; i < stations.length - 1; ++i)
		    cmd += String.format("%s = 1, ", stations[i]);
		cmd += String.format("%s = 1 ", stations[stations.length - 1]);
		cmd += String.format("where car = %d and id = %d", car, baseid + (int) col.charAt(j) - 'A');
		stat.executeUpdate(cmd);
	    }
	    stat.close();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
    /**
     * @param start 起點，請打中文
     * @param end 終點，請打中文
     * @param date 整數，請遵循 [月 * 100 + 日] 的格式
     * @param count 要幾張票
     * @param require 請打中文「靠窗」、「走道」，或是 null
     * @param seatType 0 代表不指定，1 代表標準艙，2 代表商務艙，3 代表大學生票（會是標準艙）
     */
    public static ArrayList<Integer> findTrain(String start, String end, int date, int count, String require, int seatType) {
	ArrayList<Integer> ret = new ArrayList<Integer>();
	Connection c = null, cc = null;
	try {
	    c = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
	    Statement stat = c.createStatement();
	    int direction = Arrays.asList(stations).indexOf(start) > Arrays.asList(stations).indexOf(end)? 1 : 0;
	    String cmd = String.format("select 車次 from trains where 日期 = %d and %s != -1 and %s != -1 and 方向 = %d",
				       date, start, end, direction);
	    
	    LocalDateTime ldt = LocalDateTime.now();
	    int today = ldt.getMonthValue() * 100 + ldt.getDayOfMonth();
	    if (date == today) {
		ldt = ldt.plusHours(1);
		if (ldt.getHour() != 0)
		    cmd += String.format(" and %s > %d", start, ldt.getHour() * 100 + ldt.getMinute());
	    }
	    
	    if (seatType == 3)
		cmd += " and university < 0.99";
	    cmd += String.format(" order by %s", start);
	    ResultSet rs = stat.executeQuery(cmd);
	    while (rs.next()) {
		String filename = String.format("train_%04d%04d.db", date, rs.getInt("車次"));
		String path = "trainDatabase/" + filename;
		File f = new File(path);
		if (!f.exists())         // 表示還沒有人訂，一定夠，但是 count 不能太大。
		    ret.add(rs.getInt("車次"));
		else {
		    cc = DriverManager.getConnection("jdbc:sqlite:" + path);
		    stat = cc.createStatement();
		    int startIndex = Arrays.asList(stations).indexOf(start), endIndex = Arrays.asList(stations).indexOf(end);
		    int indexDirection = direction == 1? -1 : 1;
		    cmd = String.format("select id from seats where %s = 0", stations[startIndex]);
		    for (int i = startIndex + indexDirection; i != endIndex; i += indexDirection)
			cmd += String.format(" and %s = 0", stations[i]);
		    if (require != null && require.equals("靠窗"))
			cmd += " and (id % 5 = 0 or id % 5 = 4)";
		    else if (require != null && require.equals("走道"))
			cmd += " and (id % 5 = 2 or id % 5 = 3)";
		    if (seatType == 2)
			cmd += " and car = 6";
		    else if (seatType == 1 || seatType == 3)
			cmd += " and car != 6";
		    ResultSet rs2 = stat.executeQuery(cmd);
		    int size = 0;
		    while (rs2.next()) ++size;
		    if (size >= count)
			ret.add(rs.getInt("車次"));
		    rs2.close();
		    cc.close();
		}
	    }
	    c.close();
	    stat.close();
	    rs.close();
	}
	catch (Exception e) {
	    System.out.println("findTrain : " + e.getMessage());
	}
	return ret;
    }
    public static ArrayList<Integer> findTrain(String start, String end, int date, int count, String require) {
	return findTrain(start, end, date, count, require, 0);
    }
    public static ArrayList<Integer> findTrain(String start, String end, int date, int count) {
	return findTrain(start, end, date, count, null, 0);
    }
    /**
     * 呼叫前請先用 findTrain 確認真的有停在指定的起點跟終點!!!
     * return 的 ArrayList 裡的整數，是 [車廂][id]，也就是 [車像 * 100 + id]。
     */
    public static ArrayList<Integer> orderSeat(String start, String end, int date, int trainID, int count, String require, int business) {
	int startIndex = Arrays.asList(stations).indexOf(start), endIndex = Arrays.asList(stations).indexOf(end);
	int indexDirection = startIndex > endIndex? -1 : 1;
	Connection c = null;
	ArrayList<Integer> ret = new ArrayList<Integer>();
	try {
	    String path = String.format("trainDatabase/train_%04d%04d.db", date, trainID);
	    File f = new File(path);
	    String connectionPath = "jdbc:sqlite:" + path;
	    if (!f.exists())
		trainSeatInit(connectionPath);
      	    c = DriverManager.getConnection(connectionPath);
	    Statement stat = c.createStatement();
	    String cmd = String.format("select car, id from seats where %s = 0", stations[startIndex]);
	    for (int i = startIndex + indexDirection; i != endIndex; i += indexDirection)
		cmd += String.format(" and %s = 0", stations[i]);
	    if (require != null && require.equals("靠窗"))
		cmd += " and (id % 5 = 0 or id % 5 = 4)";
	    else if (require != null && require.equals("走道"))
		cmd += " and (id % 5 = 2 or id % 5 = 3)";
	    if (business == 1)
		cmd += " and car = 6";
	    else
		cmd += " and car != 6";
	    
	    ResultSet rs = stat.executeQuery(cmd);
	    
	    int size = 0;
	    while (rs.next()) ++size;
	    rs.close();
	    rs = stat.executeQuery(cmd);
	    if (size < count)
		throw new Exception("orderSeat : 座位不夠");
	    ArrayList<String> updateCmdSet = new ArrayList<String>();
	    for (int i = 0; i < count; ++i) {
		rs.next();
		int id = rs.getInt("id"), car = rs.getInt("car");
		String updateCmd = String.format("update seats set %s = 1", stations[startIndex]);
		for (int j = startIndex + indexDirection; j != endIndex; j += indexDirection)
		    updateCmd += String.format(", %s = 1", stations[j]);
		updateCmd += String.format(" where car = %d and id = %d", car, id);
		updateCmdSet.add(updateCmd);
		ret.add(car * 100 + id);
	    }

	    for (String updateCmd : updateCmdSet)
		stat.executeUpdate(updateCmd);
	    stat.close();
	    c.close();
	    rs.close();
	}
	catch (Exception e) {
	    System.out.println("orderSeat : " + e.getMessage());
	}
	return ret;
    }
    public static ArrayList<Integer> orderSeat(String start, String end, int date, int trainID, int count) {
	return orderSeat(start, end, date, trainID, count, null, 0);
    }
    public static void releaseSeat(String start, String end, int date, int trainID, int seatID) {
	Connection c = null;
	try {
	    String conPath = String.format("jdbc:sqlite:trainDatabase/train_%04d%04d.db", date, trainID);
	    int startIndex = Arrays.asList(stations).indexOf(start), endIndex = Arrays.asList(stations).indexOf(end);
	    c = DriverManager.getConnection(conPath);
	    Statement stat = c.createStatement();
	    int indexDirection = startIndex > endIndex? -1 : 1;
	    String cmd = String.format("update seats set %s = 0", stations[startIndex]);
	    for (int i = startIndex; i != endIndex; i += indexDirection)
		cmd += String.format(", %s = 0", stations[i]);
	    cmd += String.format(" where car = %d and id = %d", seatID / 100, seatID % 100);
	    stat.executeUpdate(cmd);
	}
	catch (Exception e) {
	    System.out.println("releaseSeat : " + e.getMessage());
	}
    }
    public static void main(String[] argv) {
	createTrainsTable();
	addTrainsFromJson();
	releaseSeat("台北", "新竹", 626, 1655, 100);
	/*
	ArrayList<Integer> l = findTrain("新竹", "左營", 626, 3, null, 1);
	
	for (Integer i : l)
	    System.out.println(i);
	*/
	System.out.println("-----------------END------------------");
	//	l = orderSeat("台北", "新竹", 626, 1655, 3, null, 0);
	//	for (Integer i : l)
	//	    System.out.println(i);
    }
}
