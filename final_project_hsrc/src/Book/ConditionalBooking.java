package Book;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.io.FileReader;
import java.io.InputStream;
import java.io.FileInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Calendar;
import java.util.Date;
import TrainInfo.DBWriter;
import TrainInfo.EarlyBirdDiscount;
import TicketInfo.Ticket;
import TicketInfo.TicketInfoWriter;
import GUI.GUI;

public class ConditionalBooking {
    /**
     * 依照想要窗邊或走道訂位
     * @param UID
     * UID
     * @param date
     * 預定日期
     * @param start
     * 起始站
     * @param des
     * 終點站
     * @param type
     * 表示商務(1)或經濟艙(0)、敬老票(2)、孩童票(3)、愛心票(4)、大學生(5)
     * @param num
     * 張數
     * @param AorW
     * true：走道 false：窗邊
     */
    public static ArrayList<Integer> AisleOrWindow(String UID, int date, String start, String des, int type, int num, boolean AorW) {
        // 檢查訂票時間
        Date todate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);
        int todateInInteger = 100*month + newDate;
        // 今天以前的票不能訂
        if (date < todateInInteger) {
            // System.out.println("輸入日期已過");
            JOptionPane.showMessageDialog(null, "輸入日期已過");
            return null;
        }

        // 超過28天以後的票不能訂
        c.add(Calendar.DATE, 28);
        month = c.get(Calendar.MONTH) + 1;
        newDate = c.get(Calendar.DATE);
        todateInInteger = 100*month + newDate;
        if (date > todateInInteger) {
            // System.out.println("尚未開放訂票");
            JOptionPane.showMessageDialog(null, "尚未開放訂票");
            return null;
        }

        // 尋找可用車次
        /**
         * By 彭光禎 function 2
         */
        String require = null;
        ArrayList<Integer> validTrains = new ArrayList<>();
        if (AorW) {
            require = "走道";
            if (type == 1)
                validTrains = DBWriter.findTrain(start, des, date, num, require, 2);
            else if (type == 5)
                validTrains = DBWriter.findTrain(start, des, date, num, require, 3);
            else 
                validTrains = DBWriter.findTrain(start, des, date, num, require, 1);
        }
        else {
            require = "靠窗";
            if (type == 1)
                validTrains = DBWriter.findTrain(start, des, date, num, require, 2);
            else if (type == 5)
                validTrains = DBWriter.findTrain(start, des, date, num, require, 3);
            else 
                validTrains = DBWriter.findTrain(start, des, date, num, require, 1);
        }

        // 查無車次
        if (validTrains.size() == 0) {
            // System.out.println("查無可用車次");
            JOptionPane.showMessageDialog(null, "查無可用車次");
            return null;
        }

        GUI.show(date, validTrains, start, des);

        return validTrains;
    }

    public static void AisleOrWindowSelected(String UID, int date, String start, String des, int type, int num, boolean AorW, int decision) {
        // 算錢時一律乘上discount，反正沒打折就是原價
        double discount = 1;
        // 將第一階段給出的所有車次給出時間表供選擇，這邊假設是車號661, 1649, 242
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (type == 0)
            discount = EarlyBirdDiscount.getEarlyDiscount(date, decision, num, 1);
        else if (type == 5) {
            // 確認當天禮拜幾
            LocalDate d = LocalDate.of(2021, date/100, date%100);
            String day = d.getDayOfWeek().toString();
            day = day.substring(0, 1) + day.substring(1, day.length()).toLowerCase();
            // Get Discount
            try (FileReader reader = new FileReader("data/universityDiscount.json")) {
                JSONArray List = (JSONArray) (new JSONObject(new JSONTokener(reader))).get("DiscountTrains");
                for (Object o1 : List) {
                    JSONObject t1 = (JSONObject) o1;
                    int train = Integer.parseInt(t1.getString("TrainNo"));

                    if (train == decision) {
                        JSONObject serviceDayDiscount = t1.getJSONObject("ServiceDayDiscount");
                        discount = serviceDayDiscount.getDouble(day);
                    }  
                }
            } catch (Exception e) {
                System.out.println("Error in finding university discount");
            }
        }
        else if (type >= 2)
            discount = 0.5;

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("Connection error");
            System.out.println(e.getMessage());
            return;
        }

        String require = null;
        if (AorW) {
            require = "走道";
        }
        else {
            require = "靠窗";
        }

        ArrayList<Integer> seats = new ArrayList<>();
        seats = DBWriter.orderSeat(start, des, date, decision, num, require, type);

        // 如果上方順利，開始吐訂位代碼 & 更新
        // 假設選擇567車次，成功吐出位置
        int departureTime = 0;
        int arrivalTime = 0;

        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision);
            ps.setInt(2, date);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("Aisle or window-train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in Aisle or window - finally closing");
                System.out.println(e.toString());
            }
        }
        int payment = (int)(TicketInfoWriter.priceCalculator(start, des, type) * discount * num);

        String bookingID = TicketInfoWriter.bookingIDAssignment();
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(date);
        int bid = Integer.parseInt(bookingID);
        String insertBookingID = String.format("%05d", bid);
        // 敬老
        if (type == 2)
            TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, 0.45, seats);
        else if (type == 3)
            TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, 0.6, seats);
        else if (type == 4)
            TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, 0.3, seats);
        else
            TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, discount, seats);
        // System.out.println("訂票成功！以下是訂票資訊：");
        Ticket ticket = new Ticket(insertBookingID, UID);
        ticket.printTicket();
    }

    /**
     * 來回票訂購
     * @param outboundDate
     * 去程時間
     * @param inboundDate
     * 回程時間
     * @param start
     * 去程起始站
     * @param des
     * 回程起始站(去程終點站)
     * @param type
     * 
     * @param num
     * 張數，來回算一張來回票，num = 1
     */
    public static ArrayList<Integer> RoundTripOutbound(String UID, int outboundDate, int inboundDate, String start, String des, int type, int num) {
        // 檢查訂票時間
        Date todate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);
        int todateInInteger = 100*month + newDate;
        // 今天以前的票不能訂
        if (outboundDate < todateInInteger || inboundDate < todateInInteger) {
            JOptionPane.showMessageDialog(null, "輸入日期已過");
            return null;
        }

        // 超過28天以後的票不能訂
        c.add(Calendar.DATE, 28);
        month = c.get(Calendar.MONTH) + 1;
        newDate = c.get(Calendar.DATE);
        todateInInteger = 100*month + newDate;
        if (outboundDate > todateInInteger || inboundDate > todateInInteger) {
            // System.out.println("尚未開放訂票");
            JOptionPane.showMessageDialog(null, "尚未開放訂票");
            return null;
        }


        // 尋找去程可用車次
        /**
         * By 彭光禎 function 2
         */
        ArrayList<Integer> validTrains = new ArrayList<>();
        if (type == 1)
            validTrains = DBWriter.findTrain(start, des, outboundDate, num, null, 2);
        else if (type == 5)
            validTrains = DBWriter.findTrain(start, des, outboundDate, num, null, 3);
        else 
            validTrains = DBWriter.findTrain(start, des, outboundDate, num, null, 1);

        if (validTrains.size() == 0) {
            JOptionPane.showMessageDialog(null, "查無可用車次");
            return null;
        }
        GUI.show(outboundDate, validTrains, start, des);
        return validTrains;
    }   

    public static String RoundTripOutboundSelected(String UID, int outboundDate, String start, String des, int type, int num, int decision) {
        // 將第一階段給出的所有車次給出時間表供選擇，這邊假設是車號661, 1649, 242
        double discount = 1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (type == 0)
            discount = EarlyBirdDiscount.getEarlyDiscount(outboundDate, decision, num, 1);
        else if (type == 5) {
            // 確認當天禮拜幾
            LocalDate d = LocalDate.of(2021, outboundDate/100, outboundDate%100);
            String day = d.getDayOfWeek().toString();
            day = day.substring(0, 1) + day.substring(1, day.length()).toLowerCase();
            // Get Discount
            try (FileReader reader = new FileReader("data/universityDiscount.json")) {
                JSONArray List = (JSONArray) (new JSONObject(new JSONTokener(reader))).get("DiscountTrains");
                for (Object o1 : List) {
                    JSONObject t1 = (JSONObject) o1;
                    int train = Integer.parseInt(t1.getString("TrainNo"));

                    if (train == decision) {
                        JSONObject serviceDayDiscount = t1.getJSONObject("ServiceDayDiscount");
                        discount = serviceDayDiscount.getDouble(day);
                    }  
                }
            } catch (Exception e) {
                System.out.println("Error in finding university discount");
            }
        }
        else if (type >= 2)
            discount = 0.5;

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("Connection error");
            System.out.println(e.getMessage());
            return null;
        }

        ArrayList<Integer> seats = new ArrayList<>();
        seats = DBWriter.orderSeat(start, des, outboundDate, decision, num, null, type);


        // 如果上方順利，開始吐訂位代碼 & 更新
        // 假設選擇809車次，成功吐出位置
        int departureTime = 0;
        int arrivalTime = 0;
        // ps = null;
        // rs = null;
        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision);
            ps.setInt(2, outboundDate);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("RoundTrip-outbound train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in RoundTrip outbound train setting finally closing");
                System.out.println(e.toString());
            }
        }

        int totalPayment = (int)(TicketInfoWriter.priceCalculator(start, des, type) * discount * num);
        String bookingID = TicketInfoWriter.bookingIDAssignment();
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(outboundDate);
        int bid = Integer.parseInt(bookingID);
        String insertBookingID = String.format("%05d", bid);
        
        if (type == 2)
            TicketInfoWriter.ticketInsert(insertBookingID, UID, outboundDate, decision, start, des, departureTime, arrivalTime, paymentDeadline, totalPayment, num, 0.45, seats);
        else if (type == 3)
            TicketInfoWriter.ticketInsert(insertBookingID, UID, outboundDate, decision, start, des, departureTime, arrivalTime, paymentDeadline, totalPayment, num, 0.6, seats);
        else if (type == 4)
            TicketInfoWriter.ticketInsert(insertBookingID, UID, outboundDate, decision, start, des, departureTime, arrivalTime, paymentDeadline, totalPayment, num, 0.3, seats);
        else
            TicketInfoWriter.ticketInsert(insertBookingID, UID, outboundDate, decision, start, des, departureTime, arrivalTime, paymentDeadline, totalPayment, num, discount, seats);
        
        return insertBookingID;
    }

    /**
     * @param start
     * 回程起始站
     * @param des
     * 回程終點站
     */
    public static ArrayList<Integer> RoundTripInbound(String UID, int outboundDate, int inboundDate, String start, String des, int type, int num, String insertBookingID) {
        // 回程處理

        ArrayList<Integer> validTrains = new ArrayList<>();
        // 尋找回程可用車次
        /**
         * By 彭光禎 function 2
         */
        if (type == 1)
            validTrains = DBWriter.findTrain(start, des, inboundDate, num, null, 2);
        else if (type == 5)
            validTrains = DBWriter.findTrain(start, des, inboundDate, num, null, 3);
        else 
            validTrains = DBWriter.findTrain(start, des, inboundDate, num, null, 1);

        if (validTrains.size() == 0) {
            JOptionPane.showMessageDialog(null, "查無可用車次");
            // 刪除前方的insertion
            TicketInfoWriter.recordDeleting(insertBookingID);
            return null;
        }
        GUI.show(inboundDate, validTrains, start, des);
        return validTrains;
    }  

    public static void RoundTripInboundSelected(String UID, int outboundDate, int inboundDate, String start, String des, int type, int num, String insertBookingID, int decision2) {
        // 選擇某台車丟入：彭光禎function 1
        double discount2 = 1;

        if (type == 0)
            discount2 = EarlyBirdDiscount.getEarlyDiscount(inboundDate, decision2, num, 1);
        else if (type == 5) {
            // 確認當天禮拜幾
            LocalDate d = LocalDate.of(2021, inboundDate/100, inboundDate%100);
            String day = d.getDayOfWeek().toString();
            day = day.substring(0, 1) + day.substring(1, day.length()).toLowerCase();
            // Get Discount
            try (FileReader reader = new FileReader("data/universityDiscount.json")) {
                JSONArray List = (JSONArray) (new JSONObject(new JSONTokener(reader))).get("DiscountTrains");
                for (Object o1 : List) {
                    JSONObject t1 = (JSONObject) o1;
                    int train = Integer.parseInt(t1.getString("TrainNo"));

                    if (train == decision2) {
                        JSONObject serviceDayDiscount = t1.getJSONObject("ServiceDayDiscount");
                        discount2 = serviceDayDiscount.getDouble(day);
                    }  
                }
            } catch (Exception e) {
                System.out.println("Error in finding university discount");
            }
        }
        else if (type >= 2)
            discount2 = 0.5;

        // 選擇某台車丟入：彭光禎function 1
        ArrayList<Integer> seats2 = new ArrayList<>();
        seats2 = DBWriter.orderSeat(start, des, inboundDate, decision2, num, null, type);
        int departureTime2 = 0;
        int arrivalTime2 = 0;

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("Connection error");
            System.out.println(e.getMessage());
            TicketInfoWriter.recordDeleting(insertBookingID);
            return;
        }

        // 如果上方順利，開始吐訂位代碼 & 更新
        // 假設選擇838車次，成功吐出位置
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision2);
            ps.setInt(2, inboundDate);
            rs = ps.executeQuery();
            departureTime2 = rs.getInt(1);
            arrivalTime2 = rs.getInt(2);
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("RoundTrip-outbound train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in RoundTrip outbound train setting finally closing");
                System.out.println(e.toString());
            }
        }

        int totalPayment = 0;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:ticketInfo/TicketINFO.db");
            String sql = "Select 總金額 from tickets WHERE 訂單編號 = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, insertBookingID);
            rs = ps.executeQuery();
            totalPayment = rs.getInt(1);
            con.close();
        }
        catch (Exception e) {
            System.out.println("Connection error");
            System.out.println(e.getMessage());
            TicketInfoWriter.recordDeleting(insertBookingID);
            return;
        }

        totalPayment += (int)(TicketInfoWriter.priceCalculator(start, des, type) * discount2 * num);
        if (type == 2)
            TicketInfoWriter.inboundTicketUpdate(insertBookingID, inboundDate, decision2, start, des, departureTime2, arrivalTime2, totalPayment, num, 0.45, seats2);
        else if (type == 3)
            TicketInfoWriter.inboundTicketUpdate(insertBookingID, inboundDate, decision2, start, des, departureTime2, arrivalTime2, totalPayment, num, 0.6, seats2);
        else if (type == 4)
            TicketInfoWriter.inboundTicketUpdate(insertBookingID, inboundDate, decision2, start, des, departureTime2, arrivalTime2, totalPayment, num, 0.3, seats2);
        else 
            TicketInfoWriter.inboundTicketUpdate(insertBookingID, inboundDate, decision2, start, des, departureTime2, arrivalTime2, totalPayment, num, discount2, seats2);
        Ticket ticket = new Ticket(insertBookingID, UID);
        ticket.printTicket();
    }

    /**
     * 大學生優惠票，必定為經濟艙
     * @param UID
     * @param date
     * @param start
     * @param des
     * @param num
     */
    public static ArrayList<Integer> StudentTicket(String UID, int date, String start, String des, int num) {
        // 檢查訂票時間
        Date todate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);
        int todateInInteger = 100*month + newDate;
        // 今天以前的票不能訂
        if (date < todateInInteger) {
            JOptionPane.showMessageDialog(null, "輸入日期已過");
            return null;
        }

        // 超過28天以後的票不能訂
        c.add(Calendar.DATE, 28);
        month = c.get(Calendar.MONTH) + 1;
        newDate = c.get(Calendar.DATE);
        todateInInteger = 100*month + newDate;
        if (date > todateInInteger) {
            JOptionPane.showMessageDialog(null, "尚未開放訂票");
            return null;
        }

        // 尋找可用車次
        /**
         * By 彭光禎 function 2
         */
        ArrayList<Integer> validTrains = new ArrayList<>();
        validTrains = DBWriter.findTrain(start, des, date, num, null, 3);

        if (validTrains.size() == 0) {
            JOptionPane.showMessageDialog(null, "查無可用車次");        
            return null;
        }
        GUI.show(date, validTrains, start, des);
        return validTrains;
    }

    public static void StudentTicketSelected(String UID, int date, String start, String des, int num, int decision) {
        /**
         * Selecting
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 確認當天禮拜幾
        LocalDate d = LocalDate.of(2021, date/100, date%100);
        String day = d.getDayOfWeek().toString();
        day = day.substring(0, 1) + day.substring(1, day.length()).toLowerCase();
        double discount = 1;
        // Get Discount
        try (FileReader reader = new FileReader("data/universityDiscount.json")) {
            JSONArray List = (JSONArray) (new JSONObject(new JSONTokener(reader))).get("DiscountTrains");
            for (Object o1 : List) {
				JSONObject t1 = (JSONObject) o1;
                int train = Integer.parseInt(t1.getString("TrainNo"));

                if (train == decision) {
                    JSONObject serviceDayDiscount = t1.getJSONObject("ServiceDayDiscount");
                    discount = serviceDayDiscount.getDouble(day);
                }  
            }
        } catch (Exception e) {
            System.out.println("Error in finding university discount");
        }

        ArrayList<Integer> seats = new ArrayList<>();
        seats = DBWriter.orderSeat(start, des, date, decision, num, null, 0);

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("StudentTicket-connection error");
            System.out.println(e.getMessage());
        }

        // 抓出發時間與抵達時間
        int departureTime = 0;
        int arrivalTime = 0;
        // ps = null;
        // rs = null;
        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision);
            ps.setInt(2, date);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("StudentTicket-train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in StudentTicket - finally closing after set train");
                System.out.println(e.toString());
            }
        }

        String bookingID = TicketInfoWriter.bookingIDAssignment();
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(date);
        int payment = (int)(TicketInfoWriter.priceCalculator(start, des, 0) * discount * num);
        int bid = Integer.parseInt(bookingID);
        String insertBookingID = String.format("%05d", bid);
        TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, discount, seats);
        Ticket ticket = new Ticket(insertBookingID, UID);
        ticket.printTicket();
    }

    public static ArrayList<Integer> ElderTicket(String UID, int date, String start, String des, int num) {
        // 檢查訂票時間
        Date todate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);
        int todateInInteger = 100*month + newDate;
        // 今天以前的票不能訂
        if (date < todateInInteger) {
            JOptionPane.showMessageDialog(null, "輸入日期已過");
            return null;
        }

        // 超過28天以後的票不能訂
        c.add(Calendar.DATE, 28);
        month = c.get(Calendar.MONTH) + 1;
        newDate = c.get(Calendar.DATE);
        todateInInteger = 100*month + newDate;
        if (date > todateInInteger) {
            JOptionPane.showMessageDialog(null, "尚未開放訂票");
            return null;
        }

        // 尋找可用車次
        /**
         * By 彭光禎 function 2
         */
        ArrayList<Integer> validTrains = new ArrayList<>();
        validTrains = DBWriter.findTrain(start, des, date, num, null, 1);

        if (validTrains.size() == 0) {
            JOptionPane.showMessageDialog(null, "查無可用車次");        
            return null;
        }
        GUI.show(date, validTrains, start, des);
        return validTrains;
    }

    public static void ElderTicketSelected(String UID, int date, String start, String des, int num, int decision) {
        /**
         * Selecting
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 敬老票折數
        double discount = 0.5;

        // 找座位
        ArrayList<Integer> seats = new ArrayList<>();
        seats = DBWriter.orderSeat(start, des, date, decision, num, null, 0);

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("StudentTicket-connection error");
            System.out.println(e.getMessage());
        }

        // 抓出發時間與抵達時間
        int departureTime = 0;
        int arrivalTime = 0;
        // ps = null;
        // rs = null;
        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision);
            ps.setInt(2, date);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("StudentTicket-train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in StudentTicket - finally closing after set train");
                System.out.println(e.toString());
            }
        }

        String bookingID = TicketInfoWriter.bookingIDAssignment();
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(date);
        int payment = (int)(TicketInfoWriter.priceCalculator(start, des, 0) * discount * num);
        int bid = Integer.parseInt(bookingID);
        String insertBookingID = String.format("%05d", bid);
        TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, 0.45, seats);
        Ticket ticket = new Ticket(insertBookingID, UID);
        ticket.printTicket();
    }

    public static ArrayList<Integer> ChildrenTicket(String UID, int date, String start, String des, int num) {
        // 檢查訂票時間
        Date todate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);
        int todateInInteger = 100*month + newDate;
        // 今天以前的票不能訂
        if (date < todateInInteger) {
            JOptionPane.showMessageDialog(null, "輸入日期已過");
            return null;
        }

        // 超過28天以後的票不能訂
        c.add(Calendar.DATE, 28);
        month = c.get(Calendar.MONTH) + 1;
        newDate = c.get(Calendar.DATE);
        todateInInteger = 100*month + newDate;
        if (date > todateInInteger) {
            JOptionPane.showMessageDialog(null, "尚未開放訂票");
            return null;
        }

        // 尋找可用車次
        /**
         * By 彭光禎 function 2
         */
        ArrayList<Integer> validTrains = new ArrayList<>();
        validTrains = DBWriter.findTrain(start, des, date, num, null, 1);

        if (validTrains.size() == 0) {
            JOptionPane.showMessageDialog(null, "查無可用車次");        
            return null;
        }
        GUI.show(date, validTrains, start, des);
        return validTrains;
    }

    public static void ChildrenTicketSelected(String UID, int date, String start, String des, int num, int decision) {
        /**
         * Selecting
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 孩童票折數
        double discount = 0.5;

        // 找座位
        ArrayList<Integer> seats = new ArrayList<>();
        seats = DBWriter.orderSeat(start, des, date, decision, num, null, 0);

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("StudentTicket-connection error");
            System.out.println(e.getMessage());
        }

        // 抓出發時間與抵達時間
        int departureTime = 0;
        int arrivalTime = 0;
        // ps = null;
        // rs = null;
        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision);
            ps.setInt(2, date);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("StudentTicket-train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in StudentTicket - finally closing after set train");
                System.out.println(e.toString());
            }
        }

        String bookingID = TicketInfoWriter.bookingIDAssignment();
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(date);
        int payment = (int)(TicketInfoWriter.priceCalculator(start, des, 0) * discount * num);
        int bid = Integer.parseInt(bookingID);
        String insertBookingID = String.format("%05d", bid);
        TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, 0.6, seats);
        Ticket ticket = new Ticket(insertBookingID, UID);
        ticket.printTicket();
    }

    public static ArrayList<Integer> DisabledTicket(String UID, int date, String start, String des, int num) {
        // 檢查訂票時間
        Date todate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todate);
        int month = c.get(Calendar.MONTH) + 1;
        int newDate = c.get(Calendar.DATE);
        int todateInInteger = 100*month + newDate;
        // 今天以前的票不能訂
        if (date < todateInInteger) {
            JOptionPane.showMessageDialog(null, "輸入日期已過");
            return null;
        }

        // 超過28天以後的票不能訂
        c.add(Calendar.DATE, 28);
        month = c.get(Calendar.MONTH) + 1;
        newDate = c.get(Calendar.DATE);
        todateInInteger = 100*month + newDate;
        if (date > todateInInteger) {
            JOptionPane.showMessageDialog(null, "尚未開放訂票");
            return null;
        }

        // 尋找可用車次
        /**
         * By 彭光禎 function 2
         */
        ArrayList<Integer> validTrains = new ArrayList<>();
        validTrains = DBWriter.findTrain(start, des, date, num, null, 1);

        if (validTrains.size() == 0) {
            JOptionPane.showMessageDialog(null, "查無可用車次");        
            return null;
        }
        GUI.show(date, validTrains, start, des);
        return validTrains;
    }

    public static void DisabledTicketSelected(String UID, int date, String start, String des, int num, int decision) {
        /**
         * Selecting
         */
        PreparedStatement ps = null;
        ResultSet rs = null;

        // 愛心票折數
        double discount = 0.5;

        // 找座位
        ArrayList<Integer> seats = new ArrayList<>();
        seats = DBWriter.orderSeat(start, des, date, decision, num, null, 0);

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:trainDatabase/TrainsINFO.db");
        }
        catch (Exception e) {
            System.out.println("StudentTicket-connection error");
            System.out.println(e.getMessage());
        }

        // 抓出發時間與抵達時間
        int departureTime = 0;
        int arrivalTime = 0;
        // ps = null;
        // rs = null;
        try {
            String sql = String.format("SELECT %s,%s FROM trains where 車次 = ? and 日期 = ?", start, des);
            ps = con.prepareStatement(sql);
            ps.setInt(1, decision);
            ps.setInt(2, date);
            rs = ps.executeQuery();
            departureTime = rs.getInt(1);
            arrivalTime = rs.getInt(2);
            rs.close();
            ps.close();
            
        } catch (SQLException e) {
            //TODO: handle exception
            System.out.println("StudentTicket-train setting error");
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error in StudentTicket - finally closing after set train");
                System.out.println(e.toString());
            }
        }

        String bookingID = TicketInfoWriter.bookingIDAssignment();
        int paymentDeadline = TicketInfoWriter.PaymentDeadline(date);
        int payment = (int)(TicketInfoWriter.priceCalculator(start, des, 0) * discount * num);
        int bid = Integer.parseInt(bookingID);
        String insertBookingID = String.format("%05d", bid);
        TicketInfoWriter.ticketInsert(insertBookingID, UID, date, decision, start, des, departureTime, arrivalTime, paymentDeadline, payment, num, 0.3, seats);
        Ticket ticket = new Ticket(insertBookingID, UID);
        ticket.printTicket();
    }

    public static void main(String[] args) {
        // DBWriter.createTrainsTable();
        // DBWriter.addTrainsFromJson();
        // TicketInfoWriter.createTicketDB();
        // TicketInfoWriter.bookingIDTable();
        
    }
}
