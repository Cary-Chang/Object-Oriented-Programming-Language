package TicketInfo;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.PreparedStatement;
import java.io.FileReader;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONObject;

public class TicketInfoWriter {
    /**
     * 計算兩站間的票價
     * @param start
     * @param des
     * @param type
     * 票種，這邊只有分標準(0)跟商務(1)
     * @return
     */
    public static int priceCalculator(String start, String des, int type) {
        // get stationID for searching price
        String startID = null;
        String desID = null;
        try (FileReader reader = new FileReader("data/station.json")) {
            JSONArray List = (JSONArray) (new JSONTokener(reader).nextValue());
            for (Object o1 : List) {
                JSONObject t1 = (JSONObject) o1;
                String stationName = t1.getJSONObject("StationName").getString("Zh_tw");
                if (stationName.equals(start)) 
                    startID = t1.getString("StationID");
                else if (stationName.equals(des))
                    desID = t1.getString("StationID");
                
                if (startID != null && desID != null)
                    break; 
            } 
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Finding stationID error");
            System.out.println(e.toString());
        }

        // searching for price
        try (FileReader reader2 = new FileReader("data/price.json")) {
            JSONArray pricelist = (JSONArray) (new JSONTokener(reader2).nextValue());
            for (Object o1 : pricelist) {
                JSONObject t1 = (JSONObject) o1;
                if (t1.getString("OriginStationID").equals(startID)) {
                    JSONArray des_list = t1.getJSONArray("DesrinationStations");
                    for (Object o2 : des_list) {
                        JSONObject t2 = (JSONObject) o2;
                        if (t2.getString("ID").equals(desID)) {
                            JSONArray Fares = t2.getJSONArray("Fares");
                            if (type == 1) {
                                for (Object o3 : Fares) {
                                    JSONObject t3 = (JSONObject) o3;
                                    if (t3.getString("TicketType").equals("business"))
                                        return t3.getInt("Price");
                                }
                            }
                            else {
                                for (Object o3 : Fares) {
                                    JSONObject t3 = (JSONObject) o3;
                                    if (t3.getString("TicketType").equals("standard"))
                                        return t3.getInt("Price");
                                }
                            }
                        }
                    } 
                }
            }
            
        } catch (Exception e) {
            //TODO: handle exception
        }

        return 0;
    }

    /**
     * 針對current time給payment deadline
     * @return
     * OXX, 一個整數，O(百位數)表示月份，XX表示日期
     */
    public static int PaymentDeadline(int departureDate) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 2);

        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);

        int deadline = 100*month + newDate;
        if (deadline < departureDate) {
            return deadline;
        }
        else {
            return departureDate;
        }
    }
    /**
     * 建造一個存放訂單資訊的Database
     */
    public static void createTicketDB() {
        try {
            File dir = new File("ticketInfo");
            if (!dir.exists()) {
                dir.mkdir();
            }

            Connection con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
            Statement stat = con.createStatement();
            stat.setQueryTimeout(30);
            stat.executeUpdate("create table if not exists tickets ( "
			       + "訂單編號 integer not null, "
			       + "UID text not null, "
                   + "付款期限 integer not null, "
                   + "總金額 integer not null, "
			       + "日期 integer not null, "
			       + "車次 integer not null, "
			       + "起始站 text not null, "
			       + "終點站 text not null, "
                   + "出發時間 integer not null, "
                   + "抵達時間 integer not null, "
			       + "座位數 integer not null, "
			       + "座位1 integer, "
			       + "座位2 integer, "
			       + "座位3 integer, "
                   + "座位4 integer, "
                   + "座位5 integer, "
                   + "座位6 integer, "
                   + "座位7 integer, "
                   + "座位8 integer, "
                   + "座位9 integer, "
                   + "座位10 integer, "
                   + "折扣形式 double not null, "
                   + "回程日期 integer, "
			       + "回程車次 integer, "
			       + "回程起始站 text, "
			       + "回程終點站 text, "
                   + "回程出發時間 integer, "
                   + "回程抵達時間 integer, "
			       + "回程座位數 integer, "
			       + "回程座位1 integer, "
			       + "回程座位2 integer, "
			       + "回程座位3 integer, "
                   + "回程座位4 integer, "
                   + "回程座位5 integer, "
                   + "回程座位6 integer, "
                   + "回程座位7 integer, "
                   + "回程座位8 integer, "
                   + "回程座位9 integer, "
                   + "回程座位10 integer, "
                   + "回程折扣形式 double) ");
            con.close();
        }
        catch (Exception e) {
            System.out.println("Create ticketDB failed");
            System.out.println(e.getMessage());
        }	    
    }

    /**
     * 建立負責吐出bookingID的table
     */
    public static void bookingIDTable() {
        Connection con = null;
        try {
            File dir = new File("ticketInfo");
            if (!dir.exists()) {
                dir.mkdir();
            }

            con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
            Statement stat = con.createStatement();
            stat.setQueryTimeout(30);
            stat.executeUpdate("create table if not exists BookingID ( "
			       + "下一個訂單編號 integer not null)");

            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                String sql = "Select 下一個訂單編號 from BookingID";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    try {
                        rs.close();
                        ps.close();
                        sql = "Insert into BookingID(下一個訂單編號) Values(?)";
                        ps = con.prepareStatement(sql);
                        ps.setInt(1, 0);
                        ps.execute();
                    }
                    catch (SQLException e) {
                        System.out.println(e.toString());
                    }
                }

                con.close();
            } catch (SQLException e) {
                //TODO: handle exception
                System.out.println(e.toString());
            } 
        }
        catch (Exception e) {
            System.out.println("bookingIDTable set up failed");
            System.out.println(e.getMessage());
        }	    
    }

    /**
     * 依據Database中現行的數字給出新的訂單編號，並把原本的訂單編號+1
     * @return
     * String of bookingID
     */
    public static String bookingIDAssignment() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e.getMessage());
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        int ID4return = 0;
        try {
            String sql = "SELECT * from BookingID";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            // 要吐出來的訂單編號
            ID4return = rs.getInt("下一個訂單編號");
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        // Update BookingID database
        int newBookingID = ID4return + 1;
        try {
            String sql = "UPDATE BookingID set 下一個訂單編號 = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, newBookingID);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        return Integer.toString(ID4return);
    }

    /**
     * 把訂票資料寫進去database
     * @param bookingID
     * @param UID
     * @param date
     * @param train
     * @param start
     * @param des
     * @param departureTime
     * @param arrivalTime
     * @param paymentDeadline
     * @param payment
     * @param num
     * @param discountType
     * 1為不打折票、0.9
     * 0.5 / 0.75 / 0.88 大學生
     * 0.65 / 0.8 / 0.9 早鳥
     * 0.6 兒童
     * 0.45 敬老
     * 0.3 愛心
     * @param seats
     */
    public static void ticketInsert(String bookingID, String UID, int date, int train, String start, String des, 
    int departureTime, int arrivalTime, int paymentDeadline, int payment, int num, double discountType, ArrayList<Integer> seats) {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
        }
        catch (Exception e) {
            System.out.println("ticketInsert-connection error");
            System.out.println(e.getMessage());
        }


        PreparedStatement ps = null;
        try {
            String sql = "Insert into tickets(訂單編號, UID, 日期, 車次, 起始站, 終點站, 出發時間, 抵達時間, 座位數, 付款期限, 總金額, 折扣形式) Values(?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.setString(2, UID);
            ps.setInt(3, date);
            ps.setInt(4, train);
            ps.setString(5, start);
            ps.setString(6, des);
            ps.setInt(7, departureTime);
            ps.setInt(8, arrivalTime);
            ps.setInt(9, num);
            ps.setInt(10, paymentDeadline);
            ps.setInt(11, payment);
            ps.setDouble(12, discountType);
            ps.execute();
        }
        catch (SQLException e) {
            System.out.println("ticketInsert-insertion error");
            System.out.println(e.toString());
        }

        for (int i = 0; i < num; i++) {
            String seat = "座位" + (i + 1);
            try {
                String sql = String.format("UPDATE tickets set %s = ? WHERE 訂單編號 = ?", seat);
                ps = con.prepareStatement(sql);
                ps.setInt(1, seats.get(i));
                ps.setString(2, bookingID);
                ps.execute();
            }
            catch (SQLException e) {
                System.out.println("ticketInsert-seats update error");
                System.out.println(e.toString());
            }

        }

        try {
	    ps.close();
            con.close();
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("ticketInsert-closing error");
            System.out.println(e.toString());
        }

    }

    /**
     * 回程票寫入database
     * @param bookingID
     * @param date
     * @param train
     * @param start
     * @param des
     * 終點站
     * @param departureTime
     * @param arrivalTime
     * @param totalPayment
     * 由於回程票還要等到回程算完再加上回程票的錢，所以這裡要傳入去程+回程總價
     * @param num
     * @param seats
     */
    public static void inboundTicketUpdate(String bookingID, int date, int train, String start, String des, 
    int departureTime, int arrivalTime, int totalPayment, int num, double discountType, ArrayList<Integer> seats) {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
        }
        catch (Exception e) {
            System.out.println("inboundTicketUpdate-connection error");
            System.out.println(e.getMessage());
        }

        PreparedStatement ps = null;
        try {
            String sql = "UPDATE tickets set 回程日期 = ?, 回程車次 = ?, 回程起始站 = ?, 回程終點站 = ?, 回程出發時間 = ?, 回程抵達時間 = ?, 回程座位數 = ?, 總金額 = ?, 回程折扣形式 = ? WHERE 訂單編號 = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, date);
            ps.setInt(2, train);
            ps.setString(3, start);
            ps.setString(4, des);
            ps.setInt(5, departureTime);
            ps.setInt(6, arrivalTime);
            ps.setInt(7, num);
            ps.setInt(8, totalPayment);
            ps.setDouble(9, discountType);
            ps.setString(10, bookingID);
            ps.execute();
        }
        catch (SQLException e) {
            System.out.println("inboundTicketUpdate-insertion error");
            System.out.println(e.toString());
        }      
        
        for (int i = 0; i < num; i++) {
            String seat = "回程座位" + (i + 1);
            try {
                String sql = String.format("UPDATE tickets set %s = ? WHERE 訂單編號 = ?", seat);
                ps = con.prepareStatement(sql);
                ps.setInt(1, seats.get(i));
                ps.setString(2, bookingID);
                ps.execute();
            }
            catch (SQLException e) {
                System.out.println("inboundTicketUpdate-seats insertion error");
                System.out.println(e.toString());
            }

        }

        try {
	    ps.close();
            con.close();
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("inboundTicketUpdate-closing error");
            System.out.println(e.toString());
        }
        
    }

    /**
     * Delete single row of data from database
     * 當流程出現錯誤，導致database變得很亂，可以用這個函式手動把不要的data砍掉
     * @param bookingID
     */
    public static void recordDeleting(String bookingID) {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
        }
        catch (Exception e) {
            System.out.println("Deleting-connection error");
            System.out.println(e.getMessage());
        }

        PreparedStatement ps = null;
        try {
            String sql = "delete from tickets WHERE 訂單編號 = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.execute();

        } catch (SQLException e) {
            System.out.println("Deleting-deletion error");
            System.out.println(e.toString());
        } finally {
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                System.out.println("Deleting-closing error");
                System.out.println(e.toString());
            }

        }

    }

}
