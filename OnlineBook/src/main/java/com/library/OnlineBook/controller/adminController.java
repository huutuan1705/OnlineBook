/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.controller;

import com.library.OnlineBook.model.Admin;
import com.library.OnlineBook.model.AdminLogin;
import com.library.OnlineBook.model.Book;
import com.library.OnlineBook.model.Category;
import com.library.OnlineBook.model.Customer;
import com.library.OnlineBook.model.Order;
import com.library.OnlineBook.model.OrderDetail;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.util.Pair;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author huutuan
 */
@Controller
public class adminController {
    private DAO d = new DAO();
    private UserDAO uD = new UserDAO();
    
    @ModelAttribute("adminCategories")
    public List<Category> getCategories(){
        return d.getAllCategory();
    }
    
    @GetMapping("/adminBooks")
    public String getAllBooks(Model model) throws Exception{
        return "redirect:/adminBooks/page/1";
    }
    
    @GetMapping("/adminBooks/page/{numberPage}")
    public String getBooksByPage(Model model, @PathVariable String numberPage){
        List<Pair<Book, Integer>> allBooks = d.getAllBooksWithQuantity();
        int page, numPerPage = 5;
        int size = allBooks.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(numberPage);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        List<Pair<Book, Integer>> books = d.getListbyPage(allBooks, start, end);
        
        model.addAttribute("adminBooks",books);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        return "adminBooks";
    }
    
    @GetMapping("/adminBookDetail/{id}")
    public String getBook(Model model, @PathVariable String id, HttpSession session){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        model.addAttribute("id",id);
        Book book = d.getBook(Integer.valueOf(id));
        model.addAttribute("adminBook",book);
                
        return "adminBookDetail";
    }
    
    @GetMapping("/adminLogin")
    public String login(Model model){
        model.addAttribute("admin", new AdminLogin());
        return "adminLogin";
    }
    
    @PostMapping("/adminLogin")
    public String adminLogin(@ModelAttribute("adminLogin") AdminLogin adminLogin, Model model, HttpSession session){
        Admin admin = d.adminLogin(adminLogin.getUsername(), adminLogin.getPassword());
        if(admin==null){
            String error = "Username or password is incorrect!!";
            model.addAttribute("error",error);
            return "adminLogin";
        }
        session.setAttribute("admin",admin);
        return "redirect:/adminBooks";
    }
    
    @GetMapping("/adminLogout")
    public String adminLogout(HttpSession session){
        session.removeAttribute("admin");
        return "redirect:/adminBooks";
    }
    
    @PostMapping("/adminBook/save/{id}")
    public String addBook(@PathVariable String id, @RequestParam("image_input") MultipartFile multipartFile, 
            @ModelAttribute(name = "adminBook") Book book, Model model)throws IOException{
        if(d.checkExist(book.getTitle(), book.getAuthor())){
            String error = "The book existed!!!";
            model.addAttribute("error", error);
            model.addAttribute("id", -1);
            return "adminBookDetail";
        }
        
        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        book.setImage(filename);
        d.insertBook(book);
        
        Book savedBook = d.getLastBook();
        String uploadDir = "book-image/" + savedBook.getId();
        
        Path uploadPath = Paths.get(uploadDir);
        
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        try(InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(filename);
            System.out.println(filePath.toString());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            System.out.println(e);
        }
        
        return "redirect:/adminBooks/page/1";
    }
    
    @PutMapping("/adminBook/save/{id}")
    public String updateBook(@PathVariable String id, @ModelAttribute(name = "adminBook") Book book, 
            Model model, @RequestParam("image_input") MultipartFile multipartFile)throws IOException{
        Book b = d.getBook(Integer.valueOf(id));

        if(book.getTitle().equals(b.getTitle()) && book.getAuthor().equals(b.getTitle())){
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            System.out.println(filename);
            System.out.println(b.getImagePath());
            if(filename.equals("")){
                d.updateBook(book, b.getImage());
                return "redirect:/adminBooks";
            }
            book.setImage(filename);

            Book savedBook = d.updateBook(book, filename);
            String uploadDir = "book-image/" + savedBook.getId();

            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = multipartFile.getInputStream()){
                Path filePath = uploadPath.resolve(filename);
                //System.out.println(filePath.toString());
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException e){
                System.out.println(e);
            }
            return "redirect:/adminBooks";
        }
        if(d.checkExist(book.getTitle(), book.getTitle())==false){
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            System.out.println(filename);
            if(filename.equals("")){
                d.updateBook(book, b.getImage());
                return "redirect:/adminBooks";
            }
            book.setImage(filename);

            Book savedBook = d.updateBook(book, filename);
            String uploadDir = "book-image/" + savedBook.getId();

            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = multipartFile.getInputStream()){
                Path filePath = uploadPath.resolve(filename);
                //System.out.println(filePath.toString());
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException e){
                System.out.println(e);
            }
            return "redirect:/adminBooks";
        }
        String error = "Book does exist!!!";
        model.addAttribute("error", error);
        model.addAttribute("id", id);
        return "adminBookDetail";
    }
    
    @PostMapping("/adminBook/delete/{id}")
    public String deleteBook(@PathVariable String id, Model model){
        int id_raw = Integer.parseInt(id);
        d.deleteBook(id_raw);
//        System.out.println((Integer.parseInt(id)));
        return "redirect:/adminBooks";
    }
    
    @GetMapping("/adminBooks/search/page/{pageNumber}")
    public String adminSearchBook(@RequestParam("tmp") String tmp, Model model, @PathVariable String pageNumber){
        if(!tmp.equals("")){
            System.out.println(tmp);
            List<Pair<Book, Integer>> allBooks = d.searchBooks(tmp);
            int page, numPerPage = 5;
            int size = allBooks.size();

            int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));

            page = Integer.parseInt(pageNumber);
            int start, end;
            start = (page-1)*numPerPage;
            end = Math.min(page*numPerPage, size);
            List<Pair<Book, Integer>> books = d.getListbyPage(allBooks, start, end);

            model.addAttribute("adminBooks",books);
            model.addAttribute("page", page);
            model.addAttribute("num", num==0?1:num);
            model.addAttribute("string", tmp);
            return "adminSearchBooks";
        }
        return "redirect:/adminBooks";
    }
    
    @GetMapping("/adminBooks/category/page/{pageNumber}")
    public String getBookByCategory(Model model, @RequestParam("categoryID") String categoryID, @PathVariable String pageNumber){
        List<Pair<Book, Integer>> allBooks = d.getBookByCategory(Integer.valueOf(categoryID));
        int page, numPerPage = 5;
        int size = allBooks.size();

        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));

        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        List<Pair<Book, Integer>> books = d.getListbyPage(allBooks, start, end);
        
        model.addAttribute("adminBooks",books);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        model.addAttribute("categoryID",categoryID);
        return "adminCategoryBooks";
    }
    
    @GetMapping("/adminBooks/customers")
    public String getCustomer(){
        return "redirect:/adminBooks/customers/page/1";
    }
    
    @GetMapping("/adminBooks/customers/page/{pageNumber}")
    public String getCustomerByPage(Model model, @PathVariable String pageNumber, HttpSession session){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        List<Pair<Customer, Long>> allCustomer = d.getAllCustomer();
        int page, numPerPage = 5;
        int size = allCustomer.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        List<Pair<Customer, Long>> customers = d.getListCustomerByPage(allCustomer, start, end);
        
        model.addAttribute("customers",customers);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        return "adminCustomer";
    }
    
    @GetMapping("/adminBooks/searchCustomer/page/{pageNumber}")
    public String searchCustomer(Model model, @PathVariable String pageNumber, 
            HttpSession session, @RequestParam("tmp") String tmp){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        List<Pair<Customer, Long>> allCustomer = d.searchCustomer(tmp);
        int page, numPerPage = 5;
        int size = allCustomer.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        List<Pair<Customer, Long>> customers = d.getListCustomerByPage(allCustomer, start, end);
        
        model.addAttribute("string", tmp);
        model.addAttribute("customers",customers);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        return "adminSearchCustomer";
    }
    
    @GetMapping("/adminBooks/dashboard/{year}")
    public String getDashBoard(Model model, HttpSession session, @PathVariable String year,
            @RequestParam(value="years", required = false) String years){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        List<Pair<Book, Integer>> allBooks = d.getAllBooksWithQuantity();
        allBooks.sort(new Comparator<Pair<Book, Integer>>(){
            @Override
            public int compare(Pair<Book, Integer> o1, Pair<Book, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        List<Book> books = new ArrayList<>();
        List<String> title = new ArrayList<>();
        List<Integer> quantity = new ArrayList<>();
        for(int i=0;i<5;i++){
            books.add(allBooks.get(i).getKey());
            quantity.add(allBooks.get(i).getValue());
            title.add(allBooks.get(i).getKey().getTitle());
        }
        model.addAttribute("books", books);
        model.addAttribute("title", title);
        model.addAttribute("quantity", quantity);
        
        List<Pair<Customer, Long>> allCustomers = d.getAllCustomer();
        allCustomers.sort(new Comparator<Pair<Customer, Long>>(){
            @Override
            public int compare(Pair<Customer, Long> o1, Pair<Customer, Long> o2) {
                if(o1.getValue()>o2.getValue()){
                    return -1;
                }
                return 1;
            }
        });
        
        List<String> username = new ArrayList<>();
        List<Long> totalMoney = new ArrayList<>();
        for(int i=0;i<5;i++){
            username.add(allCustomers.get(i).getKey().getUsername());
            totalMoney.add(allCustomers.get(i).getValue());
        }
        
        List<Long> revenuePerYear = new ArrayList<>();
        
        if(year.equals(years)|| years==null){
            revenuePerYear = d.getRevenueByYear(year);
            model.addAttribute("years", year);
        }else{
            revenuePerYear = d.getRevenueByYear(years);
            model.addAttribute("years", years);
        }
        
        model.addAttribute("revenuePerYear", revenuePerYear);
        model.addAttribute("fullname", username);
        model.addAttribute("totalMoney", totalMoney);
        return "adminDashboard";
    }
    
    @GetMapping("/adminBooks/listOrders/{cid}/page/{pageNumber}")
    public String getListOrders(Model model, @PathVariable String cid, HttpSession session,
            @PathVariable String pageNumber){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        int cid_raw = Integer.parseInt(cid);
        List<Order> allOrders = new ArrayList<>();
        if(cid_raw==0){
            allOrders = d.getAllOrder();
        }else{
            allOrders = uD.getAllOrder(cid_raw);
        }
        allOrders.sort(new Comparator<Order>(){
            @Override
            public int compare(Order o1, Order o2) {
                if(o1.getStatus()==o2.getStatus()){
                    return o2.getId() - o1.getId();
                }
                return o1.getStatus()-o2.getStatus();
            }
        });
        
        int page, numPerPage = 5;
        int size = allOrders.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        
        
        List<Order> orders = d.getOrdersByPage(allOrders, start, end);
        
        model.addAttribute("cid", cid);
        model.addAttribute("orders",orders);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        return "adminOrders";
    }
    
    @GetMapping("/adminBooks/filter/{cid}&{alpha}/page/{pageNumber}")
    public String getOrdersByStauts(Model model, @PathVariable String alpha, 
            @PathVariable String cid, @PathVariable String pageNumber, HttpSession session){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        
        List<Order> allOrders = new ArrayList<>();
        int alpha_raw = Integer.parseInt(alpha);
        int cid_raw = Integer.parseInt(cid);
        
        if(alpha_raw==-1){
            if(cid_raw==0){
                allOrders = d.getAllOrder();
            }else{
                allOrders = uD.getAllOrder(cid_raw);
            }
        }else{
            allOrders = d.getOrdersByStatus(alpha_raw, cid_raw);
        }
        
        allOrders.sort(new Comparator<Order>(){
            @Override
            public int compare(Order o1, Order o2) {
                if(o1.getStatus()==o2.getStatus()){
                    return o2.getId() - o1.getId();
                }
                return o1.getStatus()-o2.getStatus();
            }
        });
        
        int page, numPerPage = 5;
        int size = allOrders.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        
        
        List<Order> orders = d.getOrdersByPage(allOrders, start, end);
        
        model.addAttribute("cid", cid);
        model.addAttribute("alpha", alpha);
        model.addAttribute("orders",orders);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        return "adminStatusOrders";
    }
    
    @GetMapping("/adminBooks/searchDate/{cid}/page/{pageNumber}")
    public String getByDate(Model model, @PathVariable String cid, @PathVariable String pageNumber, 
            HttpSession session, @RequestParam("startDate") String startDate, 
            @RequestParam("toDate") String toDate){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        
        int cid_raw = Integer.parseInt(cid);
        List<Order> allOrders = d.getOrdersByDate(startDate, toDate, cid_raw);
        
        allOrders.sort(new Comparator<Order>(){
            @Override
            public int compare(Order o1, Order o2) {
                if(o1.getStatus()==o2.getStatus()){
                    return o2.getId() - o1.getId();
                }
                return o1.getStatus()-o2.getStatus();
            }
        });
        
        int page, numPerPage = 5;
        int size = allOrders.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        
        
        List<Order> orders = d.getOrdersByPage(allOrders, start, end);
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("cid", cid);
        model.addAttribute("orders",orders);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        
        return "adminSearchDateOrder";
    }
    
    @GetMapping("/adminBooks/order/{oid}&{cid}")
    public String getOrderDetail(Model model, @PathVariable String oid, @PathVariable String cid,
            HttpSession session){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        int oid_raw = Integer.parseInt(oid);
        List<Pair<OrderDetail, Book>> listBooks = uD.getOrdetDetailByIDOrder(oid_raw);
        
        model.addAttribute("cid", cid);
        model.addAttribute("oid",oid);
        model.addAttribute("listBooks", listBooks);
        return "adminListOrderDetail";
    }
    
    @GetMapping("/adminBooks/confirmOrder/{oid}&{cid}")
    public String sendEmailForm(Model model, @PathVariable String oid, @PathVariable String cid,
            HttpSession session){
        Object o = session.getAttribute("admin");
        if(o==null){
            return "redirect:/adminBooks";
        }
        int oid_raw = Integer.parseInt(oid);
        Order od = d.getOrder(oid_raw);
        Customer cus = uD.getCustomerByID(od.getCusID());
        List<Pair<OrderDetail, Book>> listBooks = uD.getOrdetDetailByIDOrder(oid_raw);
        
        model.addAttribute("cid", cid);
        model.addAttribute("oid", oid);
        model.addAttribute("customer", cus);
        model.addAttribute("order", od);
        model.addAttribute("listBooks", listBooks);
        model.addAttribute("amount", listBooks.size());
        return "adminSendEmailForm";
    }
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @PostMapping("/adminBooks/sendEmail")
    public String sendEmail(@RequestParam("cid") String cid, @RequestParam("to") String to,
            @RequestParam("subject") String subject, @RequestParam("content") String content,
            @RequestParam("oid") String oid){
        int oid_raw = Integer.parseInt(oid);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        javaMailSender.send(msg);
        
        Order order = d.getOrder(oid_raw);
        if(order.getStatus()==0 && subject.contains("Xác nhận")){
            d.updateStatusOrder(1, oid_raw);
        }
        
        return "redirect:/adminBooks/listOrders/" + cid + "/page/1";
    }
    
    @PostMapping("/adminBooks/delivered")
    public String confirmDelivered(Model model, @RequestParam("cid") String cid,
            @RequestParam("oid") String oid){
        int oid_raw = Integer.parseInt(oid);
        d.updateStatusOrder(2, oid_raw);
        return "redirect:/adminBooks/listOrders/" + cid + "/page/1";
    }
    
    @PostMapping("adminBooks/deleteOrder")
    public String deleteOrder(Model model, @RequestParam("cid") String cid,
            @RequestParam("oid") String oid){
        int oid_raw = Integer.parseInt(oid);
        uD.deleteOrder(oid_raw);
        return "redirect:/adminBooks/listOrders/" + cid + "/page/1";
    }
}
