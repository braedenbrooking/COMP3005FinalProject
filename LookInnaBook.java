package COMP3005FinalProject;

//Author: Braeden Brooking
//Student #: 101107870
//COMP 3005 - F21 - Final Project

//Reminder to myself for how to run this
//java -cp .;"C:\Users\braed\Downloads\postgresql-42.2.24.jar" COMP3005FinalProject/LookInnaBook


import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class LookInnaBook{

    public static final String DB_USER = "postgres";
    public static final String DB_PASS = "brooking";
    public static final String DB_PATH = "/project";
    public static final String DB_HOST = "jdbc:postgresql://localhost:5432";
    public static final DecimalFormat df = new DecimalFormat("0.00");

    public static void ownerLoop(Scanner scan){
        while(true){
            System.out.println();
            System.out.println();
            System.out.println("Welcome Back Owner!");
            System.out.println("=== Owner Menu ===");
            System.out.println("1 - Add New Books");
            System.out.println("2 - Remove Books");
            System.out.println("3 - View Publisher Info");
            System.out.println("4 - View Sales Reports");
            System.out.println("5 - View Orders to Publishers");
            System.out.println("q - Return to Main Menu");

            String selection = scan.nextLine();
            if(selection.equals("1")){
                //Function
            }else if(selection.equals("2")){
                //Function
            }else if(selection.equals("3")){
                //Function
            }else if(selection.equals("4")){
                //Function
            }else if(selection.equals("5")){
                //Function
            }else if(selection.equals("q")){
                break;
            }else{
                System.out.println("Invalid Option");
                System.out.println("Please select only 1-4 or q");
            }
        }
    }


    //Helper Function
    public static boolean isNumeric(String str){
        try{
            Double.parseDouble(str);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }


    // Customer Functions
    public static void searchForBooks(Scanner scan, String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");
            
            while(true){
                System.out.println("Book Search: ");
                System.out.println("Enter the following: (or just enter if not applicable)");


                String[] prompts = {"Title", "Author", "Publisher", "Genre", "More than __ Pages", "Less than __ Pages", "More than $__", "Less than $__"};
                String[] selections = new String[8];
                String[] columns = {"title", "name", "publisher", "genre", "pages", "pages", "price", "price"};
                for(int i=0; i<selections.length; i++){
                    System.out.print(prompts[i] + ": ");
                    String currentSelection = scan.nextLine();
                    if(currentSelection.equals("")){
                        selections[i] = null;
                    }else if(i>=4 && !isNumeric(currentSelection)){
                        System.out.println("Must be a number");
                        i--;
                    }else{
                        selections[i] = currentSelection;
                    }
                }

                String query = "select title, name, price, pages, genre, stock, ISBN from book natural join wrote natural join author ";
                for(int i=0; i<selections.length; i++){
                    if (selections[i] != null){
                        query += "where ";
                        break;
                    }
                }

                
                String and = "";
                for(int i=0; i<selections.length; i++){
                    if(selections[i] == null){
                        continue;
                    }else if(i<4){
                        query += and + columns[i] + "='" + selections[i] + "' "; 
                    }else if(i%2==0){
                        query += and + "(" + columns[i] + ">" + selections[i] + ") ";
                    }else{
                        query += and + "(" + columns[i] + "<" + selections[i] + ") ";
                    }
                    and = "and ";

                }
                query += " order by name, title;";
                ResultSet rset = stmt.executeQuery(query);
                ArrayList<ArrayList<String>> bookList = new ArrayList<ArrayList<String>>();
                while(rset.next()){
                    ArrayList<String> bookinfo = new ArrayList<String>();
                    String title = rset.getString("title");
                    while(title.length()<45){
                        title += " ";
                    }
                    String name = rset.getString("name");
                    while(name.length()<35){
                        name += " ";
                    }
                    String genre = rset.getString("genre");
                    while(genre.length()<16){
                        genre += " ";
                    }
                    String stock = rset.getString("stock");
                    String isbn = rset.getString("ISBN");

                    bookinfo.add(title);
                    bookinfo.add(name);
                    bookinfo.add(rset.getString("price"));
                    bookinfo.add(rset.getString("pages"));
                    bookinfo.add(genre);
                    bookinfo.add(stock);
                    bookinfo.add(isbn);


                    boolean bookAlreadyFound = false;
                    for(int i=0; i<bookList.size(); i++){
                        if(bookList.get(i).contains(isbn)){
                            bookAlreadyFound = true;
                            String replacement = bookList.get(i).get(1).stripTrailing() + " & " + name.stripTrailing();
                            while(replacement.length()<35){
                                replacement += " ";
                            }
                            bookList.get(i).set(1, replacement);
                        }
                    }
                    if(!bookAlreadyFound){
                        bookList.add(bookinfo);
                    }
                }
                System.out.println("=== Results ===");
                System.out.println("x: Title                                        \tAuthor(s)                          \tPrice\tPages\tGenre                   InStock\tISBN");
                for(int i=0; i<bookList.size(); i++){
                    System.out.print(i + ": ");
                    for(int j=0; j<bookList.get(i).size(); j++){
                        System.out.print(bookList.get(i).get(j) + "\t");
                    }
                    System.out.println();
                }
                System.out.println("Enter the numbers you'd like to add to your cart: ('end' when done)");
                ArrayList<String> addToCartItems = new ArrayList<String>();
                while(true){
                    System.out.print("Item: ");
                    String item = scan.nextLine();
                    if(item.equals("end")) break;

                    if(!isNumeric(item) || Integer.parseInt(item) >= bookList.size()){
                        System.out.println("Just the index number");
                        continue;
                    }
                    String quantity = "";
                    while(true){
                        System.out.print("Quantity: ");
                        quantity = scan.nextLine();
                        if(isNumeric(quantity)) break;
                        System.out.println("Must be a number");
                    }
                    int quantityAsInt = Integer.parseInt(quantity);
                    int itemAsInt = Integer.parseInt(item);
                    int maximumQuantity = Math.min(quantityAsInt, Integer.parseInt(bookList.get(itemAsInt).get(5)));
                        
                    for(int i=0; i<maximumQuantity; i++)
                        addToCartItems.add(bookList.get(itemAsInt).get(bookList.get(itemAsInt).size()-1));

                    query = "update book set stock=" + (Integer.parseInt(bookList.get(itemAsInt).get(5))-maximumQuantity) + " where ISBN='" +  bookList.get(itemAsInt).get(bookList.get(itemAsInt).size()-1) + "';";
                    stmt.executeUpdate(query);
                    
                }
                String cartId = getCurrentCartId(username);
                addToCart(cartId, addToCartItems);


                System.out.println("Would you like to search again? (y/n)");
                boolean flag = false;
                while(true){
                    String choice = scan.nextLine();
                    if(choice.equals("n")){
                        flag = true;
                        break;
                    }else if(choice.equals("y")){
                        break;
                    }else{
                        System.out.println("Only answer with y or n");
                    }
                }
                if(flag) break;

            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static void addToCart(String cartId, ArrayList<String> isbns){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");

            if(cartId == null){
                System.out.println("Error No Shopping Cart");
                return;
            }

            String query = "select isbn, quantity from in_cart where shopping_cart_id='" + cartId + "';";
            ResultSet rset = stmt.executeQuery(query);

            while(rset.next()){
                String curISBN = rset.getString("ISBN");
                
                for(int i=0; i<Integer.parseInt(rset.getString("quantity")); i++){
                    isbns.add(curISBN);
                }
            }
            query = "delete from in_cart where shopping_cart_id=" + cartId + ";";
            stmt.executeUpdate(query);
            Collections.sort(isbns);
            String prev = "";
            query = "";
            for(int i=0; i<isbns.size(); i++){
                if(isbns.get(i).equals(prev)){
                    continue;
                }
                int quant = 0;
                for(int j=i; j<isbns.size(); j++){
                    if(isbns.get(i).equals(isbns.get(j))) quant++;
                }
                
                query += "insert into in_cart values(" + cartId + ", " + isbns.get(i) + ", " + quant + ");";
                prev = isbns.get(i);
            }

            stmt.executeUpdate(query);

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static String getCurrentCartId(String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){        
            String query = "select shopping_cart_id from shopping_cart where customer_username='" + username + "' order by shopping_cart_id;";
            ResultSet rset = stmt.executeQuery(query);
            String cartId = null;
            while(rset.next()){
                cartId = rset.getString("shopping_cart_id");
            }
            return cartId;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return null;
        }
    }

    public static void viewCart(Scanner scan, String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");
            String cartId = getCurrentCartId(username);
            double total = 0.0;
            ArrayList<String> isbnsInCart = new ArrayList<String>();
            ArrayList<ArrayList<String>> booksInCart = new ArrayList<ArrayList<String>>();
            while(true){
                String query = "select title, price, quantity, ISBN from shopping_cart natural join in_cart natural join book where shopping_cart_id=" + cartId + " order by title;";

                ResultSet rset = stmt.executeQuery(query);
                double subtotal = 0.0;
                while(rset.next()){
                    ArrayList<String> currentBookInfo = new ArrayList<String>();
                    String title = rset.getString("title");
                    String price = rset.getString("price");
                    String quantity = rset.getString("quantity");

                    subtotal += (Double.parseDouble(price) * Integer.parseInt(quantity));

                    while(title.length()<45){
                        title += " ";
                    }
                    currentBookInfo.add(title);
                    currentBookInfo.add(rset.getString("price"));
                    currentBookInfo.add(rset.getString("quantity"));
                    isbnsInCart.add(rset.getString("ISBN"));
                    booksInCart.add(currentBookInfo);
                }

                if(!(booksInCart.size()>0)){
                    System.out.println("Your cart is empty!");
                    return;
                }

                System.out.println("=== " + username + "'s Cart ===");
                System.out.println("x: Title                                        \tPrice\tQuantity");
                for(int i=0; i<booksInCart.size(); i++){
                    System.out.print(i + ": ");
                    for(int j=0; j<booksInCart.get(i).size(); j++){
                        System.out.print(booksInCart.get(i).get(j) + "\t");
                    }
                    System.out.println();
                }
                System.out.println("Subtotal: $" + df.format(subtotal));
                double tax = subtotal * 0.13;
                System.out.println("Tax: $" + df.format(tax));
                total = tax + subtotal;
                System.out.println("Total: $" + df.format(total)); 

                System.out.println("Would you like to remove anything? (y/n)");
                boolean wantToRemove = false;
                while(booksInCart.size()>0){
                    String selection = scan.nextLine();
                    if(selection.equals("y")){
                        wantToRemove = true;
                        break;
                    }else if(selection.equals("n")){
                        break;
                    }else{
                        System.out.println("Just enter y or n");
                    }
                }
                if(wantToRemove) System.out.println("Enter the index you want to remove: ('end' to stop)");
                ArrayList<String> removeIsbns = new ArrayList<String>();
                while(wantToRemove){
                    System.out.print("Item: ");
                    String item = scan.nextLine();

                    if(item.equals("end")) break;

                    if(!isNumeric(item) || Integer.parseInt(item)>=isbnsInCart.size()){
                        System.out.println("Just the index");
                        continue;
                    }
                    
                    System.out.print("Quantity: ");
                    String quant = scan.nextLine();
                    if(quant.equals("end")) break;
                    if(!isNumeric(quant)){
                        System.out.println("Just the number you want to remove");
                        continue;
                    }
                    

                    for(int i=0; i<Integer.parseInt(quant); i++){
                        removeIsbns.add(isbnsInCart.get(Integer.parseInt(item)));
                    }

                }

                if(removeIsbns.size()>0) removeFromCart(scan, cartId, removeIsbns);
                else break;
            }

            System.out.println("Are you ready to checkout? (y/n)");
            boolean successfulPurchase = false;
            while(true){
                String selection = scan.nextLine();
                if(selection.equals("y")){
                    successfulPurchase = checkout(scan, cartId, username, total);
                    break;
                }else if(selection.equals("n")){
                    break;
                }else{
                    System.out.println("Just enter y or n");
                }
            }

            if(successfulPurchase){
                for(int i=0; i<isbnsInCart.size(); i++){
                    checkStock(isbnsInCart.get(i));
                }
            }

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static void checkStock(String isbn){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "select stock, publisher_name from book where ISBN=" + isbn + ";";
            ResultSet rset = stmt.executeQuery(query);
            int stock = 0;
            String publisher = "";
            while(rset.next()){
                stock = Integer.parseInt(rset.getString("stock"));
                publisher = rset.getString("publisher_name");
            }
            if(stock < 10){
                String dateTime = LocalDateTime.now().toString();
                query = "insert into stock_refill_order values('" + dateTime + "', '" + publisher + "', " + isbn + ");";
                stmt.executeUpdate(query);
                query = "update book set stock=stock+" + 10 + " where ISBN=" + isbn + ";";
                stmt.executeUpdate(query);
            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static boolean checkout(Scanner scan, String cartId, String username, double total){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query;
            
            System.out.println("Would you like to use the credit card and billing information from your profile? (y/n)");
            boolean useProfileCardAndBilling = false;
            while(true){
                String selection = scan.nextLine();
                if(selection.equals("y")){
                    useProfileCardAndBilling = true;
                    break;
                }else if(selection.equals("n")){
                    break;
                }else{
                    System.out.println("Just answer y or n");
                }
            }

            System.out.println("Would you like to use the shipping address from your profile? (y/n)");
            boolean useProfileShipping = false;
            while(true){
                String selection = scan.nextLine();
                if(selection.equals("y")){
                    useProfileShipping = true;
                    break;
                }else if(selection.equals("n")){
                    break;
                }else{
                    System.out.println("Just answer y or n");
                }
            }

            String shipping = "";
            String billing = "";
            String creditCard = "";

            if(!useProfileCardAndBilling){
                System.out.print("Credit Card:");
                while(true){
                    creditCard = scan.nextLine();
                    if(!isNumeric(creditCard)){
                        System.out.println("Error: Not numeric! Please try again!");
                        continue;
                    }
                    break;
                }
                System.out.print("Billing Address: ");
                while(true){
                    billing = scan.nextLine();
                    if(billing == null || billing.equals("")){
                        System.out.println("Error: Please try again!");
                        continue;
                    }
                    break;
                }
            }
            if(!useProfileShipping){
               System.out.print("Shipping Address: ");
                while(true){
                    shipping = scan.nextLine();
                    if(shipping == null || shipping.equals("")){
                        System.out.println("Error: Please try again!");
                        continue;
                    }
                    break;
                } 
            }

            if(useProfileCardAndBilling && useProfileShipping){
                query = "select credit_card, address from customer where customer_username='" + username + "';";
                ResultSet rset = stmt.executeQuery(query);
                while(rset.next()){
                    creditCard = rset.getString("credit_card");
                    billing = shipping = rset.getString("address");
                }   
            }else if(useProfileCardAndBilling){
                query = "select credit_card, address from customer where customer_username='" + username + "';";
                ResultSet rset = stmt.executeQuery(query); 
                while(rset.next()){
                    creditCard = rset.getString("credit_card");
                    billing = rset.getString("address");
                }  
            }else if(useProfileShipping){
                query = "select credit_card, address from customer where customer_username='" + username + "';";
                ResultSet rset = stmt.executeQuery(query);  
                while(rset.next()){
                    shipping = rset.getString("address");
                }  
            }
            String dateTime = LocalDateTime.now().toString();

            query = "select count(*) from tracking;";
            ResultSet rset = stmt.executeQuery(query);
            int trackingNumber = 0;
            while(rset.next()){
                trackingNumber = Integer.parseInt(rset.getString("count")) + 1;
            }

            query = "insert into tracking values(" + trackingNumber + ", '" + dateTime + "', '" + shipping + "', '" + billing + "', " + creditCard + ", " + total + ", 'In-Transit', " + cartId +", '" + username + "');";
            stmt.executeUpdate(query);

            System.out.println("Your Tracking Number: " + trackingNumber);
            System.out.println("Thank You for your Purchase! :)");

            createCart(username);
            return true;

            


        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return false;
        }
    }

    public static void removeFromCart(Scanner scan, String cartId, ArrayList<String> removeIsbns){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "select ISBN, quantity from in_cart where shopping_cart_id=" + cartId + ";";
            ResultSet rset = stmt.executeQuery(query);
            ArrayList<String> isbnsInCart = new ArrayList<String>();
            while(rset.next()){
                int currentQuantity = Integer.parseInt(rset.getString("quantity"));
                String currentIsbn = rset.getString("ISBN");
                for(int i=0; i<currentQuantity; i++){
                    isbnsInCart.add(currentIsbn);
                }
            }
            
            for(int i=0; i<removeIsbns.size(); i++){
                for(int j=0; j<isbnsInCart.size(); j++){
                    if(isbnsInCart.get(j).equals(removeIsbns.get(i))){
                        query = "update book set stock=stock+1 where ISBN=" + isbnsInCart.get(j) + ";";
                        stmt.executeUpdate(query);

                        isbnsInCart.remove(j);
                        break;
                    }
                }
                
            }
            query = "delete from in_cart where shopping_cart_id=" + cartId + ";";
            stmt.executeUpdate(query);

            if(isbnsInCart.size()>0) addToCart(cartId, isbnsInCart);


        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static void trackOrders(Scanner scan, String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String tracking_number = null;
            while(true){
                System.out.print("Enter Tracking Number (just enter for all orders on your account): ");
                String selection = scan.nextLine();
                if(selection.equals("")){
                    tracking_number = null;
                    break;
                }else if(!isNumeric(selection)){
                    System.out.println("Answer not numeric, please try again");
                }else{
                    tracking_number = selection;
                    break;
                }
            }
            String query = "";
            if(tracking_number == null){
                System.out.println("=== " + username + "'s Orders ===");
                query = "select * from tracking natural join shopping_cart natural join in_cart natural join book where customer_username='" + username + "' order by date_time_of_purchase;";
            }else{
                System.out.println("=== Order Number: " + tracking_number + " ===");
                query = "select * from tracking natural join shopping_cart natural join in_cart natural join book where tracking_number=" + tracking_number + ";";
            }
            ResultSet rset = stmt.executeQuery(query);

            String prev = "";
            while(rset.next()){
                String currTrackNum = rset.getString("tracking_number");
                if(!(currTrackNum.equals(prev))){
                    System.out.println("===============================================");
                    System.out.println("Order Purchased: " + rset.getString("date_time_of_purchase"));
                    System.out.println("Ordered By: " + rset.getString("customer_username"));
                    System.out.println("Amount Paid: $" + rset.getString("amount_paid"));
                    System.out.println("Tracking Number: " + rset.getString("tracking_number"));
                    System.out.println("Tracking Status: " + rset.getString("tracking_status"));
                    System.out.println("Shipping to: " + rset.getString("shipping_address"));
                    System.out.println("Contents:");
                }
                System.out.println("- " + rset.getString("title") + " x" + rset.getString("quantity"));
                prev = currTrackNum;
            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static void customerLoop(Scanner scan){
        //Login
        System.out.println("Connecting to Database...");
        String username = null;
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");
            while(true){
                System.out.println("Please enter your username: (or 'back' to return to main menu)");
                username = scan.nextLine();
                if(username.equals("back") || username.equals("")) return;


                String query = "select count(*) from customer where customer_username='" + username + "';";
                ResultSet rset = stmt.executeQuery(query);
                if(rset.next() && rset.getString("count").equals("1")){
                    System.out.println("Login successful");
                    break;
                }else{
                    System.out.println("Login unsuccessful... Please Try Again");
                }
            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
        


        //Loop
        while(true){
            System.out.println();
            System.out.println();
            System.out.println("Welcome " + username + "!");
            System.out.println("=== Cutomer Menu ===");
            System.out.println("1 - Search for Books");
            System.out.println("2 - View Shopping Cart");
            System.out.println("3 - Track Orders");
            System.out.println("q - Return to Main Menu");

            String selection = scan.nextLine();
            if(selection.equals("1")){
                searchForBooks(scan, username);
            }else if(selection.equals("2")){
                viewCart(scan, username);
            }else if(selection.equals("3")){
                trackOrders(scan, username);
            }else if(selection.equals("q")){
                break;
            }else{
                System.out.println("Invalid Option");
                System.out.println("Please select only 1-4 or q");
            }
        }
    }

    public static boolean createCart(String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");
            String query = "select count(*) from shopping_cart;";
            ResultSet rset = stmt.executeQuery(query);
            int newCartId = 0;
            while(rset.next()){
                newCartId = Integer.parseInt(rset.getString("count"));
            }
            newCartId++;

            query = "insert into shopping_cart values(" + newCartId + ", '" + username + "');";
            stmt.executeUpdate(query);
            return true;

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return false;
        }
    }

    public static void registerCustomer(Scanner scan){ // TODO improve by making it so it checks if username is taken already
        System.out.println("Connecting to Database...");
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");
            boolean flag = true;
            while(flag){ // Loop until user successfully created
                try{
                    System.out.println("Please enter the following information (on new lines)");
                    System.out.println("Username");
                    System.out.println("Name");
                    System.out.println("Address");
                    System.out.println("Credit Card");
                    System.out.println("Email");
                    System.out.println("Phone Number");


                    String[] newUserInfo = new String[6];
                    for(int i=0; i<newUserInfo.length; i++){
                        newUserInfo[i] = scan.nextLine();
                        if(newUserInfo[i].equals("")){
                            System.out.println("You didn't enter anything. Please enter the value again:");
                            i--;
                        }
                    }

                    String query = "insert into customer values ('" + newUserInfo[0] + "'";
                    for(int i=1; i<newUserInfo.length; i++){
                        query += ",'" + newUserInfo[i] + "'";
                    }
                    query += ");";
                    System.out.println("Registering...");
                    stmt.executeUpdate(query);

                    flag = !createCart(newUserInfo[0]);

                }catch (Exception e){
                    System.out.println("Exception: " + e);
                    System.out.println("Please try again");
                }
            }
            System.out.println("User Registered!");
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
        }

    }

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        System.out.println("==========================");
        System.out.println("Welcome to Look Inna Book!");
        System.out.println("==========================");
        System.out.println();
        System.out.println();
        while(true){
            System.out.println("=== Main Menu ===");
            System.out.println("Who are you?");
            System.out.println("1 - Owner");
            System.out.println("2 - Returning Customer");
            System.out.println("3 - New Customer");
            System.out.println("q - Quit");


            String selection = scan.nextLine();
            if(selection.equals("1")){
                ownerLoop(scan);
            }else if(selection.equals("2")){
                customerLoop(scan);
            }else if(selection.equals("3")){
                registerCustomer(scan);
            }else if(selection.equals("q")){
                break;
            }else{
                System.out.println("Invalid Option");
                System.out.println("Please select only 1, 2, 3, or q");
            }
        }
        System.out.println("See you next time!");
        
    }
}