package TicketInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;
import TrainInfo.DBWriter;
import TrainInfo.EarlyBirdDiscount;
import GUI.BookingHistoryEnquiry;
public class Ticket {
    // 去程資訊
    String bookingID;
    String UID;
    int date;
    int trainID;
    String start;
    String end;
    int departureTime;
    int arrivalTime;
    int paymentDeadline;
    int price;
    double discount;
    int num;
    ArrayList<Integer> seats;

    // 回程資訊
    int back_date;
    int back_trainID;
    String back_start;
    String back_end;
    int back_departureTime;
    int back_arrivalTime;
    int back_paymentDeadline;
    int total_price;
    double back_discount;
    int back_num;
    ArrayList<Integer> back_seats;

    public Ticket(String bookingID, String UID) {
        this.bookingID = new String(bookingID);
        this.UID = new String(UID);
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketinfo/TicketINFO.db");
        }
        catch (Exception e) {
            System.out.println("modifyTicket connection error");
            System.out.println(e.getMessage());
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT 座位數,折扣形式,日期,車次,回程座位數 FROM tickets WHERE 訂單編號 = ? and UID = ?");
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.setString(2, UID);
            rs = ps.executeQuery();
            if (!rs.next()) {
		JOptionPane.showMessageDialog(null, "無法找到此筆訂單，請確認輸入是否有誤");
		con.close();
		return;
            }
	    this.num = rs.getInt(1);
	    con.close();
	}
	catch (Exception e) {
	    JOptionPane.showMessageDialog(null, "無法找到資料庫");
	}
    }
    public int getNum() {
	return this.num;
    }
    public Ticket(String bookingID, String UID, int date, int trainID, String start, String end, 
    int departureTime, int arrivalTime, int paymentDeadline, int price, double discount, int num, ArrayList<Integer> seats) {
        this.bookingID = new String(bookingID);
        this.UID = new String(UID);
        this.date = date;
        this.trainID = trainID;
        this.start = new String(start);
        this.end = new String(end);
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.paymentDeadline = paymentDeadline;
        this.price = price;
        this.discount = discount;
        this.num = num;
        this.seats = new ArrayList<Integer>(seats);
    }

    // 使用 身份識別號碼 與 車次 印出車票資訊
    public void printTicket() {
        BookingHistoryEnquiry print = new BookingHistoryEnquiry(UID, bookingID);
        print.show();
        // System.out.println("--------------------------------------------------");
        // System.out.printf("訂單編號：%s\n", bookingID);
        // System.out.printf("UID：%s\n", UID);
        // System.out.printf("付款期限：%02d月%02d日\n", paymentDeadline/100, paymentDeadline%100);
        // System.out.printf("價格：%d元\n", price);
        // System.out.println("--------------------------------------------------");
        // System.out.printf("日期：%02d月%02d日\n", date/100, date%100);
        // System.out.printf("車次：%d\n", trainID);
        // System.out.printf("起站：%s\n", start);
        // System.out.printf("訖站：%s\n", end);
        // System.out.printf("出發時間：%02d:%02d\n", departureTime/100, departureTime%100);
        // System.out.printf("到達時間：%02d:%02d\n", arrivalTime/100, arrivalTime%100);
        // System.out.printf("座位數：%d\n", num);
        // System.out.printf("座位編號：");
        // for (int i = 0; i < num; i++) {
        //     System.out.printf("%d車%d%c", seats.get(i)/100, (seats.get(i)%100)/5+1, 'A'+seats.get(i)%5);
        //     if (i != num - 1) 
        //         System.out.printf("/");
        // }
        // System.out.println("\n--------------------------------------------------");
    }

    
    /** 
     * @param back_date 回程日期
     * @param back_trainID 回程車次
     * @param back_start 回程起始站
     * @param back_end 回程終點站
     * @param back_departureTime 回程發車時間
     * @param back_arrivalTime 回程到站時間
     * @param back_paymentDeadline 
     * @param total_price 總價
     * @param back_num 回車人數
     * @param back_seats 回程座位
     */
    public void updateBackInfo(int back_date, int back_trainID, String back_start, String back_end, 
    int back_departureTime, int back_arrivalTime, int back_paymentDeadline, int total_price, int back_num, ArrayList<Integer> back_seats) {
        this.back_date = back_date;
        this.back_trainID = back_trainID;
        this.back_start = new String(back_start);
        this.back_end = new String(back_end);
        this.back_departureTime = back_departureTime;
        this.back_arrivalTime = back_arrivalTime;
        this.back_paymentDeadline = back_paymentDeadline;
        this.total_price = total_price;
        this.back_num = back_num;
        this.back_seats = new ArrayList<Integer>(back_seats);
    }

    // public void printRoundTicket() {
    //     System.out.println("--------------------------------------------------");
    //     System.out.printf("訂單編號：%s\n", bookingID);
    //     System.out.printf("UID：%s\n", UID);
    //     System.out.printf("付款期限：%02d月%02d日\n", paymentDeadline/100, paymentDeadline%100);
    //     System.out.printf("價格：%d元\n", total_price);
    //     System.out.println("--------------------------------------------------");
    //     System.out.printf("去程日期：%02d月%02d日\n", date/100, date%100);
    //     System.out.printf("去程車次：%d\n", trainID);
    //     System.out.printf("去程起站：%s\n", start);
    //     System.out.printf("去程訖站：%s\n", end);
    //     System.out.printf("去程出發時間：%02d:%02d\n", departureTime/100, departureTime%100);
    //     System.out.printf("去程到達時間：%02d:%02d\n", arrivalTime/100, arrivalTime%100);
    //     System.out.printf("去程座位數：%d\n", num);
    //     System.out.printf("去程座位編號：");
    //     for (int i = 0; i < num; i++) {
    //         System.out.printf("%d車%d%c", seats.get(i)/100, (seats.get(i)%100)/5+1, 'A'+seats.get(i)%5);
    //         if (i != num - 1) 
    //             System.out.printf("/");
    //     }
    //     System.out.println("\n--------------------------------------------------");
    //     System.out.printf("回程日期：%02d月%02d日\n", back_date/100, back_date%100);
    //     System.out.printf("回程車次：%d\n", back_trainID);
    //     System.out.printf("回程起站：%s\n", back_start);
    //     System.out.printf("回程訖站：%s\n", back_end);
    //     System.out.printf("回程出發時間：%02d:%02d\n", back_departureTime/100, back_departureTime%100);
    //     System.out.printf("回程到達時間：%02d:%02d\n", back_arrivalTime/100, back_arrivalTime%100);
    //     System.out.printf("回程座位數：%d\n", back_num);
    //     System.out.printf("回程座位編號：");
    //     for (int i = 0; i < back_num; i++) {
    //         System.out.printf("%d車%d%c", back_seats.get(i)/100, (back_seats.get(i)%100)/5+1, 'A'+back_seats.get(i)%5);
    //         if (i != num - 1) 
    //             System.out.printf("/");
    //     }
    //     System.out.println("\n--------------------------------------------------");
    // }
    
    public void modifyTicket(int newnum) {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketinfo/TicketINFO.db");
        }
        catch (Exception e) {
            System.out.println("modifyTicket connection error");
            System.out.println(e.getMessage());
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT 座位數,折扣形式,日期,車次,回程座位數 FROM tickets WHERE 訂單編號 = ? and UID = ?");
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.setString(2, UID);
            rs = ps.executeQuery();
            if (!rs.next()) {
		JOptionPane.showMessageDialog(null, "查無資料");
                return;
            }

            int num = rs.getInt(1);
            double discount = rs.getDouble(2);
            int trainID = rs.getInt(3);
            int date = rs.getInt(4);
            int back_num = rs.getInt(5);

            rs.close();
            ps.close();  

            // System.out.println(num);
            // System.out.println(back_num);

            if (num < newnum) {
                System.out.println("無法增加座位數！");
                return;
            }
            if (num == newnum) {
                System.out.println("座位數與原先相等");
                return;
            }

            sql = String.format("UPDATE tickets set 座位數 = %d WHERE 訂單編號 = ? and UID = ?", newnum);
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.setString(2, UID);
            ps.execute();

            if (discount == 0.65 || discount == 0.8 || discount == 0.9)
                EarlyBirdDiscount.releaseEarlyDiscount(date, trainID, num - newnum, discount);

            for (int i = num; i > newnum; i--) {
                sql = "SELECT 車次,日期,起始站,終點站,座位" + i + " FROM tickets WHERE 訂單編號 = ? and UID = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, bookingID);
                ps.setString(2, UID);
                rs = ps.executeQuery();
                
                trainID = rs.getInt(1);
                date = rs.getInt(2);
                String start = rs.getString(3);
                String end = rs.getString(4);
                int seatID = rs.getInt(5);
                DBWriter.releaseSeat(start, end, date, trainID, seatID);

                rs.close();
                ps.close();   

                sql = "UPDATE tickets set 座位" + i + " = null WHERE 訂單編號 = ? and UID = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, bookingID);
                ps.setString(2, UID);
                ps.execute();
                rs.close();
                ps.close();   
            }

            if (back_num == 0) {
                if (newnum == 0) {
                    sql = String.format("UPDATE tickets set UID = '-1',訂單編號 = '-1' WHERE 訂單編號 = ? and UID = ?");
                    ps = con.prepareStatement(sql);
                    ps.setString(1, bookingID);
                    ps.setString(2, UID);
                    ps.execute();
                    rs.close();
                    ps.close();   
		    JOptionPane.showMessageDialog(null, "取消訂單成功！");
                    //System.out.println("取消訂單成功！");
                    return;
                }
		JOptionPane.showMessageDialog(null, "減少訂單人數成功！");
                //System.out.println("減少訂單人數成功！");
                return;
            }

            // 處理回程
            sql = String.format("SELECT 回程座位數,回程折扣形式,回程日期,回程車次 FROM tickets WHERE 訂單編號 = ? and UID = ?");
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.setString(2, UID);
            rs = ps.executeQuery();
            num = rs.getInt(1);
            discount = rs.getDouble(2);
            trainID = rs.getInt(3);
            date = rs.getInt(4);

            if (discount == 0.65 || discount == 0.8 || discount == 0.9)
                EarlyBirdDiscount.releaseEarlyDiscount(date, trainID, num - newnum, discount);

            sql = String.format("UPDATE tickets set 回程座位數 = %d WHERE 訂單編號 = ? and UID = ?", newnum);
            ps = con.prepareStatement(sql);
            ps.setString(1, bookingID);
            ps.setString(2, UID);
            ps.execute();

            for (int i = num; i > newnum; i--) {
                sql = "SELECT 回程車次,回程日期,回程起始站,回程終點站,回程座位" + i + " FROM tickets WHERE 訂單編號 = ? and UID = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, bookingID);
                ps.setString(2, UID);
                rs = ps.executeQuery();
                
                trainID = rs.getInt(1);
                date = rs.getInt(2);
                String start = rs.getString(3);
                String end = rs.getString(4);
                int seatID = rs.getInt(5);
                DBWriter.releaseSeat(start, end, date, trainID, seatID);
                rs.close();
                ps.close();   

                sql = "UPDATE tickets set 回程座位" + i + " = null WHERE 訂單編號 = ? and UID = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, bookingID);
                ps.setString(2, UID);
                ps.execute();
                rs.close();
                ps.close();   
            }
            if (newnum == 0) {
                sql = String.format("UPDATE tickets set UID = null,訂單編號 = null WHERE 訂單編號 = ? and UID = ?");
                ps = con.prepareStatement(sql);
                ps.setString(1, bookingID);
                ps.setString(2, UID);
                ps.execute();
                rs.close();
                ps.close();   
                //System.out.println("取消訂單成功！");
                return;
            }
	    con.close();
            //System.out.println("減少訂單人數成功！");
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("modify Ticket error");
            System.out.println(e.toString());
        }
    }
}

