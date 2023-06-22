/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.controller;

import com.library.OnlineBook.model.Admin;
import com.library.OnlineBook.model.Book;
import com.library.OnlineBook.model.Category;
import com.library.OnlineBook.model.Customer;
import com.library.OnlineBook.model.Order;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author huutuan
 */
public class DAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/online_library";
    private String jdbcUserName = "root";
    private String jdbcPassWord = "123456789";
    
    private static final String SELECT_ALL_BOOKS = "SELECT * FROM online_library.book";    
    private static final String SELECT_BOOKS_BY_ID = "SELECT * FROM online_library.book where id = ?";    
    private static final String UPDATE_BOOK = "UPDATE book SET title=?,author=?,"
            + "description=?,releaseDate=?,pages=?,categoryID=?,image=?,price=? WHERE id=?";
    private static final String DELETE_BOOK = "DELETE FROM book WHERE id=?";
    private static final String INSERT_BOOK_SQL = "INSERT INTO book VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String QUANTITY_OF_BOOK = "SELECT * FROM online_library.orderdetail where bid=?";
     
    private static final String SELECT_ALL_CATEGORIES = "select * from category";    
    private static final String ADMIN_LOGIN = "select * from admin where username=? and password=?";
    
    private static final String SELECT_ALL_CUSTOMER = "SELECT * FROM online_library.customer";
    private static final String GET_ORDER_BY_CUSTOMERID = "SELECT * FROM online_library.order WHERE cid=?";
    
    private static final String ALL_ORDER = "SELECT * FROM online_library.order";
    private static final String UPDATE_STATUS_ORDER = "UPDATE  online_library.order SET status = ? where id = ?";
    
    public DAO() {
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
    
    public Category getCategoryByID(int id){
        String sql = SELECT_ALL_CATEGORIES + " where id=?";
        Category category = new Category();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                category = new Category(id, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
    
    public List<Category> getAllCategory(){
        List<Category> categories = new ArrayList<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ALL_CATEGORIES);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categories.add(new Category(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ALL_BOOKS);
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
    
    public Book getBook(int id) {
        Book book = new Book();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_BOOKS_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setDescription(rs.getString("description"));
                book.setReleaseDate(rs.getString("releaseDate"));
                book.setPages(rs.getInt("pages"));
                book.setCategoryID((rs.getInt("categoryID")));
                book.setImage(rs.getString("image"));
                book.setPrice(rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }
    
    public boolean checkExist(String title, String author){
        String sql = SELECT_ALL_BOOKS + " where title=? and author=?";        
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, author);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
    public Admin adminLogin(String username, String password){
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(ADMIN_LOGIN);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                Admin a = new Admin(id, fullname, email, username, password);
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Book insertBook(Book book){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(INSERT_BOOK_SQL);
            
            ps.setInt(1, book.getId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getDescription());
            ps.setString(5, book.getReleaseDate());
            ps.setInt(6, book.getPages());
            ps.setInt(7, book.getCategoryID());
            ps.setString(8, book.getImage());
            ps.setDouble(9, book.getPrice());
            
            ps.executeUpdate();           
            ps.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return book;
    }
    
    public Book updateBook(Book book, String imgage){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_BOOK);

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getDescription());
            ps.setString(4, book.getReleaseDate());
            ps.setInt(5, book.getPages());
            ps.setInt(6, book.getCategoryID());
            ps.setString(7, imgage);
            ps.setDouble(8, book.getPrice());
            ps.setInt(9, book.getId());
            
            ps.executeUpdate();           
            ps.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return book;
    }
    
    public boolean checkExistInOrder(int bid){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(QUANTITY_OF_BOOK);
            
            ps.setInt(1, bid);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){
            
        }
        return false;
    }
    
    public void deleteBook(int id){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(DELETE_BOOK);
            
            ps.setInt(1, id);
            ps.executeUpdate();
            
            ps.close();
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public List<Pair<Book, Integer>> searchBooks(String tmp){
        List<Pair<Book, Integer>> listBook = new ArrayList<>();
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
                Book book = new Book(id, title, author, description, releaseDate, pages, category, image, price);
                
                PreparedStatement ps1 = connection.prepareStatement(QUANTITY_OF_BOOK);
                ps1.setInt(1, id);
                ResultSet rs1 = ps1.executeQuery();
                int quantity = 0;
                while(rs1.next()){
                    quantity += rs1.getInt("quantity");
                }
                listBook.add(new Pair<Book, Integer>(book, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listBook;
    }
    
    public List<Pair<Book, Integer>> getBookByCategory(int id){
        List<Pair<Book, Integer>> listBook = new ArrayList<>();
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
                Book book = new Book(idBook, title, author, description, releaseDate, pages, id, image, price);
                
                PreparedStatement ps1 = connection.prepareStatement(QUANTITY_OF_BOOK);
                ps1.setInt(1, idBook);
                ResultSet rs1 = ps1.executeQuery();
                int quantity = 0;
                while(rs1.next()){
                    quantity += rs1.getInt("quantity");
                }
                listBook.add(new Pair<Book, Integer>(book, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listBook;
    }
    
    public Book getLastBook() {
        Book book = new Book();
        String sql = SELECT_ALL_BOOKS + " where id = (SELECT MAX(id) FROM online_library.book)";
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setDescription(rs.getString("description"));
                book.setReleaseDate(rs.getString("releaseDate"));
                book.setPages(rs.getInt("pages"));
                book.setCategoryID((rs.getInt("categoryID")));
                book.setImage(rs.getString("image"));
                book.setPrice(rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }
    
    public List<Pair<Book, Integer>> getListbyPage(List<Pair<Book, Integer>> list, int start, int end){
        List<Pair<Book, Integer>> arr = new ArrayList<>();
        for(int i=start; i<end;i++){
            arr.add(list.get(i));
        }
        return arr;
    }
    
    public List<Pair<Book, Integer>> getAllBooksWithQuantity() {
        List<Pair<Book, Integer>> books = new ArrayList<>();
        try {
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ALL_BOOKS);
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
                Book book = new Book(id, title, author, description, releaseDate, pages, category, image, price);
                
                PreparedStatement ps1 = connection.prepareStatement(QUANTITY_OF_BOOK);
                ps1.setInt(1, id);
                ResultSet rs1 = ps1.executeQuery();
                int quantity = 0;
                while(rs1.next()){
                    quantity += rs1.getInt("quantity");
                }
                books.add(new Pair<Book, Integer>(book, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Pair<Customer, Long>> getAllCustomer(){
        List<Pair<Customer, Long>> customers = new ArrayList<>();
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(SELECT_ALL_CUSTOMER);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Customer customer = new Customer(id, fullname, email, address, username, password);
                
                PreparedStatement ps1 = connection.prepareStatement(GET_ORDER_BY_CUSTOMERID);
                ps1.setInt(1, id);
                ResultSet rs1 = ps1.executeQuery();
                long total = 0;
                while(rs1.next()){
                    total += rs1.getLong("totalmoney");
                }
                
                customers.add(new Pair<Customer, Long>(customer, total));
            }
        }catch(SQLException e){
            
        }
        return customers;
    }
    
    public List<Pair<Customer, Long>> searchCustomer(String tmp){
        List<Pair<Customer, Long>> customers = new ArrayList<>();
        String sql = SELECT_ALL_CUSTOMER + " where fullname like '%"+tmp+"%' or id like '%"+tmp+"%' ";
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Customer customer = new Customer(id, fullname, email, address, username, password);
                
                PreparedStatement ps1 = connection.prepareStatement(GET_ORDER_BY_CUSTOMERID);
                ps1.setInt(1, id);
                ResultSet rs1 = ps1.executeQuery();
                long total = 0;
                while(rs1.next()){
                    total += rs1.getLong("totalmoney");
                }
                
                customers.add(new Pair<Customer, Long>(customer, total));
            }
        }catch(SQLException e){
            
        }
        return customers; 
    }
    
    public List<Pair<Customer, Long>> getListCustomerByPage(List<Pair<Customer, Long>> list, int start, int end){
        List<Pair<Customer, Long>> arr = new ArrayList<>();
        for(int i=start; i<end;i++){
            arr.add(list.get(i));
        }
        return arr;
    }
    
    public List<Order> getAllOrder(){
        List<Order> orders = new ArrayList<>();
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(ALL_ORDER);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String date = rs.getString("date");
                int cid = rs.getInt("cid");
                double totalMoney = rs.getDouble("totalmoney");
                int status = rs.getInt("status");
                orders.add(new Order(id, date, cid, totalMoney, status));
            }
        }catch(SQLException e){
            
        }
        return orders;
    }
    
    public List<Order> getOrdersByStatus(int status, int cid_raw){
        List<Order> orders = new ArrayList<>();
        String sql_noCid = ALL_ORDER + " where status=?";
        String sql_Cid = ALL_ORDER + " where status=? and cid=?";
        try{
            Connection connection = getConnection();
            if(cid_raw==0){
                PreparedStatement ps = connection.prepareStatement(sql_noCid);
                ps.setInt(1, status);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String date = rs.getString("date");
                    int cid = rs.getInt("cid");
                    double totalMoney = rs.getDouble("totalmoney");
                    orders.add(new Order(id, date, cid, totalMoney, status));
                }
            }else{
                PreparedStatement ps = connection.prepareStatement(sql_Cid);
                ps.setInt(1, status);
                ps.setInt(2, cid_raw);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String date = rs.getString("date");
                    double totalMoney = rs.getDouble("totalmoney");
                    orders.add(new Order(id, date, cid_raw, totalMoney, status));
                }
            }
            
        }catch(SQLException e){
            
        }
        return orders;
    }
    
    public List<Order> getOrdersByDate(String startDate, String toDate, int cid_raw){
        List<Order> orders = new ArrayList<>();
        String sql_noCid = ALL_ORDER + " where (order.date between '" + startDate +"' and '" + toDate + "') ";
        String sql_Cid = sql_noCid + "and cid=?";
        try{
            Connection connection = getConnection();
            if(cid_raw==0){
                PreparedStatement ps = connection.prepareStatement(sql_noCid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String date = rs.getString("date");
                    int cid = rs.getInt("cid");
                    double totalMoney = rs.getDouble("totalmoney");
                    int status = rs.getInt("status");
                    orders.add(new Order(id, date, cid, totalMoney, status));
                }
            }else{
                PreparedStatement ps = connection.prepareStatement(sql_Cid);
                ps.setInt(1, cid_raw);
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    String date = rs.getString("date");
                    double totalMoney = rs.getDouble("totalmoney");
                    int status = rs.getInt("status");
                    orders.add(new Order(id, date, cid_raw, totalMoney, status));
                }
            }
        }catch(SQLException e){
            
        }
        return orders;
    }
    
    public List<Order> getOrdersByPage(List<Order> list, int start, int end){
        List<Order> arr = new ArrayList<>();
        for(int i=start; i<end;i++){
            arr.add(list.get(i));
        }
        return arr;
    }
    
    public Order getOrder(int id){
        String sql = ALL_ORDER + " where id=?";
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                String date = rs.getString("date");
                int cid = rs.getInt("cid");
                double totalMoney = rs.getDouble("totalmoney");
                int status = rs.getInt("status");
                return new Order(id, date, cid, totalMoney, status);
            }
        }catch(SQLException e){
            
        }
        return null;
    }
    
    public void updateStatusOrder(int status,int id){
        try{
            Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS_ORDER);
            ps.setInt(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();           
            ps.close();
        }catch(SQLException e){
            
        }
    }
    
    public List<Long> getRevenueByYear(String year){
        List<Long> list = new ArrayList<>();
        try{
            Connection connection = getConnection();
            int month = 1;
            while(month<=12){
                String sql = ALL_ORDER + " where Year(date) = '" + year + 
                        "' and Month(date) = '" + month + "'";
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                long quantity = 0;
                while(rs.next()){
                    quantity += rs.getInt("totalmoney");
                }
                list.add(quantity);
                month++;
            }
        }catch(SQLException e){
            
        }
        return list;
    }
    
    public static void main(String[] args) {
        DAO d = new DAO();
        List<Long> list = d.getRevenueByYear("2023");
        System.out.println(list.get(0));
    }
}
