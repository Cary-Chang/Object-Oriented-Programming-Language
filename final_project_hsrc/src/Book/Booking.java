package Book;

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
import TrainInfo.DBWriter;
import TrainInfo.EarlyBirdDiscount;
import TicketInfo.Ticket;
import TicketInfo.TicketInfoWriter;
import GUI.GUI;
import javax.swing.JOptionPane;
public class Booking {
    
    /** 
     * @param UID 身份識別號碼
     * @param date 日期
     * @param start 起始站
     * @param end 終點站
     * @param type 票種 (0: 一般, 1: 商務艙, 2: 敬老, 3:孩童, 4: 愛心)
     * @param count 人數
     */
    public static ArrayList<Integer> normalBookingA(String UID, int date, String start, String end, int type, int count) {
        LocalDate cur = LocalDate.now();
	LocalDate future = cur.plusDays(29);
        // 判斷訂票時間
        if (date < cur.getMonthValue()*100 + cur.getDayOfMonth()) {
            System.out.println("無法預定過去之列車！");
            return null;
        }
        if (date >= future.getMonthValue()*100 + future.getDayOfMonth()) {
            System.out.println("無法預定超過28天後之列車！");
            return null;
        }

        if (type == 2)
            ConditionalBooking.ElderTicket(UID, date, start, end, count);
        else if (type == 3)
            ConditionalBooking.ChildrenTicket(UID, date, start, end, count);
        else if (type == 4)
            ConditionalBooking.DisabledTicket(UID, date, start, end, count);

        ArrayList<Integer> availableTrains = DBWriter.findTrain(start, end, date, count, null, type);

        if (availableTrains.size() == 0) {
	    JOptionPane.showMessageDialog(null, "無可訂位車次");
	    //            System.out.println("無可定位車次 :(");
            return availableTrains;
        }
        GUI.show(date, availableTrains, start, end);

        return availableTrains;
    }

    public static void normalBookingB(int trainID, String UID, int date, String start, String end, int type, int count) {
        
        /*
        try {
            for (int i = 0; i < availableTrains.size(); i++) {
                String sql = String.format("SELECT 車次,日期,%s,%s FROM trains where 車次 = ? and 日期 = ?", start, end);
                ps = con.prepareStatement(sql);
                ps.setInt(1, availableTrains.get(i));
                ps.setInt(2, date);
                rs = ps.executeQuery();
                int result1 = rs.getInt(1);
                //int result2 = rs.getInt(2);
                int result3 = rs.getInt(3);
                int result4 = rs.getInt(4);

                System.out.printf("車次：%4d 出發時間：%02d:%02d 到達時間：%02d:%02d\n", result1, result3/100, result3%100, result4/100, result4%100);

                rs.close();
                ps.close();    
            }

        } catch (SQLException e) {
            System.out.println("Booking error");
            System.out.println(e.toString());
        }
        */
        // System.out.printf("請輸入欲訂位之車次：");
        // // for (Integer i : availableTrains)
        // //     System.out.println(i);
        // Scanner scanner = new Scanner(System.in);
        // int trainID = scanner.nextInt();
        // while (!availableTrains.contains(trainID)) {
        //     System.out.printf("請輸入可供訂位之車次！");
        //     trainID = scanner.nextInt();
        // }
        // scanner.close();
        ArrayList<Integer> seats = DBWriter.orderSeat(start, end, date, trainID, count, null, type);
        String bookingID = TicketInfoWriter.bookingIDAssignment();
        //GUI.disshow();
        
        double discount = 1;
        if (type == 0)
            EarlyBirdDiscount.getEarlyDiscount(date, trainID, count, 1);

        int departureTime = 0;
        int arrivalTime = 0;

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("Booking error");
            System.out.println(e.getMessage());
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, end);
            ps = con.prepareStatement(sql);
            ps.setInt(1, trainID);
            ps.setInt(2, date);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
        } catch (SQLException e) {
            System.out.println("Booking setting error");
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                System.out.println("Booking closing error");
                System.out.println(e.toString());
            }
        }

        int price = (int)(TicketInfoWriter.priceCalculator(start, end, type)*discount*count);
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(date);

        TicketInfoWriter.ticketInsert(bookingID, UID, date, trainID, start, end, departureTime, arrivalTime, paymentDeadline, price, count, discount, seats);
        //System.out.println("訂票成功！以下是訂票資訊：");
        // BookingHistoryEnquiry test = new BookingHistoryEnquiry(UID, bookingID);
        // test.show();
        Ticket ticket = new Ticket(bookingID, UID);
        ticket.printTicket();
    }

    
    /** 
     * @param argv
     */
    public static void main(String[] argv) {
        DBWriter.createTrainsTable();
        DBWriter.addTrainsFromJson();
        TicketInfoWriter.createTicketDB();
        TicketInfoWriter.bookingIDTable();
        
	//        normalBooking("A123456789", 708, "新竹", "左營", 0, 5, 0);
        // BookingHistoryEnquiry test = new BookingHistoryEnquiry("A123456789", "00011");
        // test.connectDB("ticketINFO.db");
        // test.getINFO();
        // test.show();

        // Ticket.modifyTicket("jjk846505", "00003", 0);
        //TicketInfoWriter.recordDeleting("00003");
    }
}
