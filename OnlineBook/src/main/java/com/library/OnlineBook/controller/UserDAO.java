/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.controller;

import com.library.OnlineBook.model.Admin;
import com.library.OnlineBook.model.Book;
import com.library.OnlineBook.model.Cart;
import com.library.OnlineBook.model.Customer;
import com.library.OnlineBook.model.Item;
import com.library.OnlineBook.model.Order;
import com.library.OnlineBook.model.OrderDetail;
import com.library.OnlineBook.model.Review;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author huutuan
 */
public class UserDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/online_library";
    private String jdbcUserName = "root";
    private String jdbcPassWord = "123456789";

    private static final String CUSTOMER_LOGIN = "SELECT * FROM customer where username=? and password=?";
    private static final String CUSTOMER_EXIST = "SELECT * FROM customer where username=?";
    private static final String GET_CUSTOMER_BY_ID = "SELECT * FROM customer where id=?";
    private static final String FORGOT_PASSWORD = "SELECT * FROM customer where username=? and email=?";
    private static final String ADD_CUSTOMER = "INSERT INTO customer (`fullname`,`email`,`address`,`username`,"
            + "`password`) VALUES(?,?,?,?,?)";
    private static final String UPDATE_ACCOUNT = "UPDATE customer SET fullname=?, email=?, address=?,"
            + "username=?, password=? where id=?";
    
    private static final String ALL_ORDER = "SELECT * FROM online_library.order where cid=?;";
    private static final String ORDER_DETAIL = "SELECT * FROM online_library.orderdetail where oid = ?;";
    
    private static final String DELETE_ORDER = "DELETE FROM online_library.order where id=?";
    private static final String DELETE_ORDER_DETAIL = "DELETE FROM online_library.orderdetail where oid=?";
    
    private static final String ADD_REVIEW = "INSERT INTO review (`bid`, `cid`, `rating`, `comment`, `time`) VALUES(?,?,?,?,?)";
    private static final String GET_REVIEWS = "SELECT * FROM online_library.review WHERE bid=?";
    private static final String UPDATE_REVIEW = "UPDATE review SET rating=?, comment=?, time=? where id=?";
    private static final String DELETE_REVIEW = "DELETE FROM online_library.review WHERE id=?";
     
    private static final String SELECT_ALL_BOOKS = "SELECT * FROM online_library.book"; 
    
    public UserDAO() {
    }

    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassWord);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public List<Book> getFeaturedBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT * FROM online_library.book \n"
                + "ORDER BY id DESC LIMIT 9) var1 ORDER BY id ASC;";
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String description = rs.getString("description");
                String releaseDate = rs.getString("releaseDate");
                int pages = rs.getInt("pages");
                int category = (rs.getInt("categoryID"));
                String image = rs.getString("image");
                double price = rs.getDouble("price");
                books.add(new Book(id, title, author, description, releaseDate, pages, category, image, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Book> searchBooks(String tmp){
        List<Book> listBook = new ArrayList<>();
        String sql = SELECT_ALL_BOOKS + " where title like '%" + tmp + "%' or "
                + "author like '%" + tmp + "%'";
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String description = rs.getString("description");
                String releaseDate = rs.getString("releaseDate");
                int pages = rs.getInt("pages");
                int category = (rs.getInt("categoryID"));
                String image = rs.getString("image");
                double price = rs.getDouble("price");
                listBook.add(new Book(id, title, author, description, releaseDate, pages, category, image, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listBook;
    }
    
    public List<Book> getBookByCategory(int id){
        List<Book> listBook = new ArrayList<>();
        String sql = SELECT_ALL_BOOKS + " where categoryID = ?";
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idBook = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String description = rs.getString("description");
                String releaseDate = rs.getString("releaseDate");
                int pages = rs.getInt("pages");
                String image = rs.getString("image");
                double price = rs.getDouble("price");
                listBook.add(new Book(idBook, title, author, description, releaseDate, pages, id, image, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listBook;
    }
    
    public List<Book> getListbyPage(List<Book> list, int start, int end){
        List<Book> arr = new ArrayList<>();
        for(int i=start; i<end;i++){
            arr.add(list.get(i));
        }
        return arr;
    }
    
    public Customer customerLogin(String username, String password){
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(CUSTOMER_LOGIN);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Customer customer = new Customer(id, fullname, email, address, username, password);
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Customer customerForgotPassword(String username, String email){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(FORGOT_PASSWORD);
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String address = rs.getString("address");
                String password = rs.getString("password");
                Customer customer = new Customer(id, fullname, email, address, username, password);
                return customer;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public Customer getCustomerByID(int id){
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(GET_CUSTOMER_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Customer customer = new Customer(id, fullname, email, address, username, password);
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean checkExist(String username){
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(CUSTOMER_EXIST);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void customerRegister(Customer customer){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_CUSTOMER);
            
            ps.setString(1, customer.getFullname());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getUsername());
            ps.setString(5, customer.getPassword());
            
            ps.executeUpdate();           
            ps.close();
            connection.close();
        }catch(SQLException e){
            System.out.println(e);
        }
    }
    
    public void updateCustomer(Customer customer){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_ACCOUNT);
            
            ps.setString(1, customer.getFullname());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getUsername());
            ps.setString(5, customer.getPassword());
            ps.setInt(6, customer.getId());
            
            ps.executeUpdate();           
            ps.close();
            connection.close();
        }catch(SQLException e){
            
        }
    }
    
    public void addOrder(Customer c,Cart cart){
        LocalDate curDate=LocalDate.now();
        String date=curDate.toString();
        try{
            Connection connection = getConnection();
            //add order
            String sql="INSERT INTO `online_library`.`order` (`date`, `cid`,`totalmoney`, `status`) VALUES(?,?,?,?)";
            PreparedStatement st=connection.prepareStatement(sql);
            st.setString(1, date);
            st.setInt(2, c.getId());
            st.setDouble(3, cart.getTotalMoney());
            st.setInt(4, 0);
            st.executeUpdate();
            //lay id cua order vua add
            String sql1="SELECT * FROM online_library.order where id = (SELECT MAX(id) FROM online_library.order)";
            PreparedStatement st1=connection.prepareStatement(sql1);
            ResultSet rs=st1.executeQuery();
            //add bang OrderDetail
            if(rs.next()){
                int oid=rs.getInt("id");
                for(Item i:cart.getItems()){
                    String sql2="insert into online_library.orderdetail values(?,?,?,?)";
                    PreparedStatement st2=connection.prepareStatement(sql2);
                    st2.setInt(1, oid);
                    st2.setInt(2, i.getBook().getId());
                    st2.setInt(3, i.getQuantity());
                    st2.setDouble(4, i.getPrice());
                    st2.executeUpdate();
                }
            }
        }catch(SQLException e){
            
        }
    }
    
    public List<Order> getAllOrder(int cid){
        List<Order> orders = new ArrayList<>();
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(ALL_ORDER);
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String date = rs.getString("date");
                double totalMoney = rs.getDouble("totalmoney");
                int status = rs.getInt("status");
                orders.add(new Order(id, date, cid, totalMoney, status));
            }
        }catch(SQLException e){
            
        }
        return orders;
    }
    
    public List<Pair<OrderDetail, Book>> getOrdetDetailByIDOrder(int oid){
        List<Pair<OrderDetail, Book>> orderDetails = new ArrayList<>();
        DAO d = new DAO();
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(ORDER_DETAIL);
            ps.setInt(1, oid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int bid = rs.getInt("bid");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                OrderDetail orderDetail = new OrderDetail(oid, bid, quantity, price);
                Book book = d.getBook(bid);
                orderDetails.add(new Pair<OrderDetail, Book>(orderDetail,book));
            }
        }catch(SQLException e){
            
        }
        return orderDetails;
    }
    
    public void deleteOrder(int id){
        try{
            Connection connection = getConnection();
            
            PreparedStatement ps1 = connection.prepareStatement(DELETE_ORDER_DETAIL);
            ps1.setInt(1, id);
            ps1.executeUpdate();
            
            PreparedStatement ps = connection.prepareStatement(DELETE_ORDER);
            ps.setInt(1, id);
            ps.executeUpdate();
            
            ps.close();
            ps1.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public void addReview(int bid, int cid, int rating, String comment){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_REVIEW);
            ps.setInt(1, bid);
            ps.setInt(2, cid);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.setString(5, time);
            
            ps.executeUpdate();           
            ps.close();
            connection.close();
        }catch(SQLException e){
            
        }
    }
    
    public List<Pair<Review, Customer>> getListReviewsOfBook(int bid){
        List<Pair<Review, Customer>> listReview = new ArrayList<>();
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(GET_REVIEWS);
            ps.setInt(1, bid);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int cid = rs.getInt("cid");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                String time = rs.getString("time");
                Review review = new Review(id, bid, cid, rating, comment, time);
                Customer customer = getCustomerByID(cid);
                listReview.add(new Pair<Review, Customer>(review, customer));
            }
        }catch(SQLException e){
            
        }
        return listReview;
    }
    
    public void updateReview(int id, int rating, String comment){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_REVIEW);
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setString(3, time);
            ps.setInt(4, id);
            
            ps.executeUpdate();           
            ps.close();
            connection.close();
        }catch(SQLException e){
            
        }
    }
    
    public void deleteReview(int id){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(DELETE_REVIEW);
            ps.setInt(1, id);
            ps.executeUpdate();
            
            ps.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        UserDAO uD = new UserDAO();
        List<Pair<OrderDetail, Book>> list = uD.getOrdetDetailByIDOrder(2);
        System.out.println(list.get(0).getValue().getTitle());
    }
}
