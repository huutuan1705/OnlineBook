/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.controller;

import com.library.OnlineBook.model.AdminLogin;
import com.library.OnlineBook.model.Book;
import com.library.OnlineBook.model.Cart;
import com.library.OnlineBook.model.Category;
import com.library.OnlineBook.model.Customer;
import com.library.OnlineBook.model.CustomerLogin;
import com.library.OnlineBook.model.Item;
import com.library.OnlineBook.model.Order;
import com.library.OnlineBook.model.OrderDetail;
import com.library.OnlineBook.model.Review;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author huutuan
 */
@Controller
public class homeController {
    private DAO d = new DAO();
    private UserDAO uD = new UserDAO();
    
    @ModelAttribute("categories")
    public List<Category> getCategories(){
        return d.getAllCategory();
    }
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @GetMapping("/")
    public String getHomePage(Model model){
        List<Book> featuredBooks = uD.getFeaturedBooks();
        model.addAttribute("featuredBooks", featuredBooks);
        return "homePage";
    }
    
    @GetMapping("/allBooks")
    public String getAllBook(Model model){
        return "redirect:/allBooks/page/1";
    }
    
    @GetMapping("/allBooks/page/{numberPage}")
    public String getBookByPage(Model model, @PathVariable String numberPage){
        List<Book> allBooks = d.getAllBooks();
        int page, numPerPage = 8;
        int size = allBooks.size();
        
        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));
        
        page = Integer.parseInt(numberPage);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        List<Book> books = uD.getListbyPage(allBooks, start, end);
        
        model.addAttribute("allBooks", books);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        
        return "allBooks";
    }
    
    @GetMapping("/allBooks/category/page/{pageNumber}")
    public String getBookByCategory(Model model, @RequestParam("categoryID") String categoryID, 
            @PathVariable String pageNumber){
        List<Book> allBooks = uD.getBookByCategory(Integer.valueOf(categoryID));
        int page, numPerPage = 8;
        int size = allBooks.size();

        int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));

        page = Integer.parseInt(pageNumber);
        int start, end;
        start = (page-1)*numPerPage;
        end = Math.min(page*numPerPage, size);
        List<Book> books = uD.getListbyPage(allBooks, start, end);
        
        model.addAttribute("allBooks",books);
        model.addAttribute("page", page);
        model.addAttribute("num", num==0?1:num);
        model.addAttribute("categoryID",categoryID);
        return "categoryBooks";
    }
    
    @GetMapping("/bookDetail/{id}")
    public String getBookDetail(@PathVariable String id, Model model){
        model.addAttribute("id",id);
        Book book = d.getBook(Integer.valueOf(id));
        
        List<Pair<Review, Customer>> listReview = uD.getListReviewsOfBook(Integer.valueOf(id));
        int size = 0;
        for(Pair<Review, Customer> review : listReview){
            if(review.getKey().getRating()!=0){
                size++;
            }
        }
        listReview.sort(new Comparator<Pair<Review, Customer>>(){
            @Override
            public int compare(Pair<Review, Customer> o1, Pair<Review, Customer> o2) {
                return o2.getKey().getId() - o1.getKey().getId();
            }
            
        });
        model.addAttribute("sizeReviews", size);
        model.addAttribute("reviews", listReview);
        model.addAttribute("bookDetail", book);
        return "bookDetail";
    }
    
    @GetMapping("/allBooks/search/page/{pageNumber}")
    public String searchBook(@RequestParam("tmp") String tmp, Model model, @PathVariable String pageNumber){
        if(!tmp.equals("")){
            System.out.println(tmp);
            List<Book> allBooks = uD.searchBooks(tmp);
            int page, numPerPage = 8;
            int size = allBooks.size();

            int num =(size%numPerPage==0?(size/numPerPage):(size/numPerPage+1));

            page = Integer.parseInt(pageNumber);
            int start, end;
            start = (page-1)*numPerPage;
            end = Math.min(page*numPerPage, size);
            List<Book> books = uD.getListbyPage(allBooks, start, end);

            model.addAttribute("allBooks",books);
            model.addAttribute("page", page);
            model.addAttribute("num", num==0?1:num);
            model.addAttribute("string", tmp);
            return "searchBooks";
        }
        return "redirect:/allBooks";
    }
    
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("customer",new CustomerLogin());
        return "customerLogin";
    }
    
    @PostMapping("/login")
    public String customerLogin(@ModelAttribute("customerLogin") CustomerLogin customerLogin, 
            Model model, HttpSession session){
        Customer customer = uD.customerLogin(customerLogin.getUsername(), customerLogin.getPassword());
        if(customer==null){
            String error = "Username or password is incorrect!!";
            model.addAttribute("error",error);
            return "customerLogin";
        }
        session.setAttribute("customer",customer);
        return "redirect:";
    }
    
    @GetMapping("/logout")
    public String customerLogout(HttpSession session){
        session.removeAttribute("customer");
        return "redirect:";
    }
    
    @GetMapping("/signup")
    public String signup(Model model){
        return "userRegister";
    }
    
    @PostMapping("/register/{id}")
    public String getRegister(Model model, @PathVariable String id, Customer customer,
            @RequestParam("confirm") String confirm){
        System.out.println(customer.getId() + " " + customer.getPassword() + " " + confirm);
        if(!customer.getPassword().equals(confirm)){
            String error1 = "Password and confirm password do not match!!!";
            model.addAttribute("error1", error1);
            model.addAttribute("id", -1);
            return "userRegister";
        }
        if(uD.checkExist(customer.getUsername())){
            String error2 = "Username does exist!!!";
            model.addAttribute("error2", error2);
            model.addAttribute("id", -1);
            return "userRegister";
        }
        uD.customerRegister(customer);
        String success = "Registration successful!!!";
        model.addAttribute("success",success);
        if(customer!=null){
            model.addAttribute("customer", customer);
        }
        
        return "userRegister";
    }
    
    @PostMapping("/forgotPassword")
    public String forgotPassword(Model model, @RequestParam("username") String username,
            @RequestParam("email") String email){
        Customer customer = uD.customerForgotPassword(username, email);
        if(customer==null){
            String tmp = "Username or E-mail doesn't exist!!!";
            model.addAttribute("notExist", tmp);
            return "customerLogin";
        }
        
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
         + "0123456789"
         + "abcdefghijklmnopqrstuvxyz";
        
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < 9; i++){
            int index = (int)(AlphaNumericString.length()*Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        String newPassword = sb.toString();
        
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        String subject = "Your New Password";
        msg.setSubject(subject);
        String content = "Your new password is: " + newPassword + "\n";
        content += "Please change the password when receiving it \n";
        content += "and do not share it with anyone else.\n";
        msg.setText(content);
        javaMailSender.send(msg);
        
        customer.setPassword(newPassword);
        uD.updateCustomer(customer);
        
        return "customerConfirm";
    }
    
    
    @GetMapping("/shoppingCart")
    public String getShoppingCart(Model model, HttpSession session){
        Cart cart = null;
        Object o = session.getAttribute("cart");
        if(o!=null){
            cart = (Cart)o;
        }else{
            cart = new Cart();
        }
        List<Item> list = cart.getItems();
        session.setAttribute("cart", cart);
        session.setAttribute("size", list.size());
        return "shoppingCart";
    }
    
    @GetMapping("/buy/{id}")
    public String buyItem(Model model, @RequestParam("num") String num,
            @PathVariable String id, HttpSession session){
        Cart cart = null;
        Object o = session.getAttribute("cart");
        if(o!=null){
            cart = (Cart)o;
        }else{
            cart = new Cart();
        }
        
        int num_raw, id_raw;
        id_raw = Integer.parseInt(id);
        try{
            num_raw = Integer.parseInt(num);
            
            Book b = d.getBook(id_raw);
            double price = b.getPrice();
            Item t = new Item(b, num_raw, price);
            cart.addItem(t);
        }catch(NumberFormatException e){
            num_raw=1;
        }
        
        List<Item> list = cart.getItems();
        session.setAttribute("cart", cart);
        session.setAttribute("size", list.size());
        
        return "redirect:/bookDetail/" + id_raw;
    }
    
    @DeleteMapping("/deleteItem/{id}")
    public String deleteItem(@PathVariable String id, HttpSession session){
        Cart cart = (Cart)session.getAttribute("cart");
        cart.removeItem(Integer.parseInt(id));
        session.setAttribute("cart", cart);
        session.setAttribute("size", cart.getItems().size());
        return "redirect:/shoppingCart";
    }
    
    @GetMapping("/process")
    public String getProcess(HttpSession session, @RequestParam("num") String num_raw,
            @RequestParam("id") String id_raw){
        Cart cart = null;
        Object o = session.getAttribute("cart");
        if(o!=null){
            cart = (Cart)o;
        }else{
            cart = new Cart();
        }
        int num, id;
        try{
            num = Integer.parseInt(num_raw);
            id = Integer.parseInt(id_raw);
            
            if((num==-1) && (cart.getQuantityById(id)<=1)){
                cart.removeItem(id);
            }else{
                Book b = d.getBook(id);
                double price = b.getPrice();
                Item t = new Item(b, num, price);
                cart.addItem(t);
            }
        }catch(NumberFormatException e){
            
        }
        session.setAttribute("cart", cart);
        session.setAttribute("size", cart.getItems().size());
        return "redirect:/shoppingCart";
    }
    
    @GetMapping("/checkoutForm")
    public String getCheckoutForm(Model model, HttpSession session){
        Cart cart = null;
        Object o = session.getAttribute("cart");
        if(o!=null){
            cart = (Cart)o;
        }else{
            cart = new Cart();
        }
        List<Item> list = cart.getItems();
        session.setAttribute("cart", cart);
        session.setAttribute("size", list.size());
        return "checkOut";
    }
    
    @PostMapping("/checkout")
    public String checkout(Model model, HttpSession session){
        Cart cart = null;
        Object o = session.getAttribute("cart");
        if(o!=null){
            cart = (Cart)o;
        }else{
            cart = new Cart();
        }
        Customer customer = null;
        Object cus = session.getAttribute("customer");
        if(cus!=null){
            customer = (Customer)cus;
            uD.addOrder(customer, cart);
            session.removeAttribute("cart");
            session.setAttribute("size", 0);
            return "redirect:/myOrder";
        }else{
            return "customerLogin";
        }
    }
    
    @GetMapping("/myOrder")
    public String getAllOrder(Model model, HttpSession session){
        Object o = session.getAttribute("customer");
        if(o==null){
            return "redirect:/";
        }
        Customer customer = (Customer)session.getAttribute("customer");
        List<Order> orders = uD.getAllOrder(customer.getId());
        orders.sort(new Comparator<Order>(){
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getId() - o1.getId();
            }
            
        });
        model.addAttribute("orders", orders);
        return "customerListOrder";
    }
    
    @GetMapping("/myAccount")
    public String getAccount(Model model, HttpSession session){
        Object o = session.getAttribute("customer");
        if(o==null){
            return "redirect:/";
        }
        return "customerAccount";
    }
    
    @PutMapping("/changePassword/{id}")
    public String updateAccount(Model model, HttpSession session, @PathVariable String id, 
            @RequestParam(value = "newPassword", required = false) String newPassword, 
            @RequestParam(value = "confirm", required = false) String confirm,
            @RequestParam("currentPassword") String currentPassword, Customer customer){
        Object o = session.getAttribute("customer");
        if(o==null){
            return "redirect:/";
        }
        Customer cus = (Customer)session.getAttribute("customer");
        
        if(!cus.getUsername().equals(customer.getUsername()) && uD.checkExist(customer.getUsername())){
            String tmp = "Username dose exist!!!";
            model.addAttribute("error",tmp);
            return "customerAccount";
        }
        if(!customer.getPassword().equals(currentPassword)){
            String tmp = "Your current password is not true!!!";
            model.addAttribute("error",tmp);
            return "customerAccount";
        }
        if(!newPassword.equals(confirm)){
            String tmp = "Confirm password is not true!!!";
            model.addAttribute("error",tmp);
            return "customerAccount";
        }
        
        customer.setId(cus.getId());
        customer.setPassword(newPassword);
        
        uD.updateCustomer(customer);
        System.out.println(cus.getId() + " " + cus.getPassword());
        String tmp = "Update account successfull!!!";
        model.addAttribute("success",tmp);
        session.setAttribute("customer", customer);
        return "customerAccount";
    }
    
    @GetMapping("/orderDetail/{oid}")
    public String getOrderDetail(Model model, @PathVariable String oid){
        List<Pair<OrderDetail, Book>> list = uD.getOrdetDetailByIDOrder(Integer.parseInt(oid));
        System.out.println(list.get(0).getValue().getTitle());
        model.addAttribute("oid",oid);
        model.addAttribute("listDetail", list);
        return "orderDetail";
    }
    
    @PostMapping("/deleteOrder/{id}")
    public String deleteOrder(Model model, @PathVariable String id){
        uD.deleteOrder(Integer.parseInt(id));
        return "redirect:/myOrder";
    }
    
    @GetMapping("/postComment/{bid}")
    public String postComment(Model model, @PathVariable String bid, @RequestParam("comment") String comment,
            HttpSession session, @RequestParam("rating-1") String rating_raw){
        int bid_raw = Integer.parseInt(bid);
        if(rating_raw==null){
            rating_raw = "1";
        }
        int rating = Integer.parseInt(rating_raw);
        Customer customer = null;
        Object cus = session.getAttribute("customer");
        if(cus==null){
            return "customerLogin";
        }
        customer = (Customer)cus;
        uD.addReview(bid_raw, customer.getId(), rating, comment);
        return "redirect:/bookDetail/" + bid;
    }
    
    @GetMapping("/saveComment/{bid}")
    public String updateComment(Model model, @PathVariable String bid, @RequestParam("id") String id_raw,
            @RequestParam("rating") String rating_raw, @RequestParam("comment") String comment){
        int bid_raw = Integer.parseInt(bid);
        int id = Integer.parseInt(id_raw);
        int rating = Integer.parseInt(rating_raw);
        
        uD.updateReview(id, rating, comment);
        return "redirect:/bookDetail/" + bid;
    }
    
    @GetMapping("/deleteComment")
    public String delelteComment(Model model, @RequestParam("bid") String bid,
            @RequestParam("rid") String rid){
        int bid_raw = Integer.parseInt(bid);
        //System.out.println(rid);
        uD.deleteReview(Integer.parseInt(rid));
        return "redirect:/bookDetail/"+bid_raw;
    }
    
    @GetMapping("/contact")
    public String contactUs(Model model){
        return "contactPage";
    }
    
    @GetMapping("/sendEmailContact")
    public String sendEmailContact(Model model, @RequestParam("name") String name,
            @RequestParam("email") String email, @RequestParam("subject") String subject,
            @RequestParam(value = "message", required = false) String mesage){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(email);
        msg.setTo("onlinebookstore2002@gmail.com");
        String title = name + " has sent a message";
        String content = "Sender Name: " + name + "\n";
        content += "Sender E-mail: " + email + "\n";
        content += "Subject: " + subject + "\n";
        content += "Content:\n" + mesage + "\n";
        msg.setSubject(title);
        msg.setText(content);
        
        javaMailSender.send(msg);
        
        String success = "Thank you for contacting us. We'll get back to you shortly!!!";
        model.addAttribute("success",success);
        return "contactPage";
    }
}
