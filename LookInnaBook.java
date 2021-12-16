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


public class LookInnaBook{

    public static final String DB_USER = "postgres";
    public static final String DB_PASS = "brooking";
    public static final String DB_PATH = "/project";

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
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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

                String query = "select title, name, price, pages, genre, ISBN from book natural join wrote natural join author ";
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
                query += ";";
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

                    String isbn = rset.getString("ISBN");

                    bookinfo.add(title);
                    bookinfo.add(name);
                    bookinfo.add(rset.getString("price"));
                    bookinfo.add(rset.getString("pages"));
                    bookinfo.add(genre);
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
                System.out.println("x: Title                                        \tAuthor(s)                          \tPrice\tPages\tGenre           \tISBN");
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
                        
                    for(int i=0; i<Integer.parseInt(quantity); i++)
                        addToCartItems.add(bookList.get(Integer.parseInt(item)).get(bookList.get(Integer.parseInt(item)).size()-1));
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
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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
                "jdbc:postgresql://localhost:5432" + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.println("Connected!");
            String cartId = getCurrentCartId(username);
            
            while(true){
                String query = "select title, price, quantity, ISBN from shopping_cart natural join in_cart natural join book where shopping_cart_id=" + cartId + " order by title;";

                ResultSet rset = stmt.executeQuery(query);
                ArrayList<String> isbnsInCart = new ArrayList<String>();
                ArrayList<ArrayList<String>> booksInCart = new ArrayList<ArrayList<String>>();
                while(rset.next()){
                    ArrayList<String> currentBookInfo = new ArrayList<String>();
                    String title = rset.getString("title");
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







        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    public static void removeFromCart(Scanner scan, String cartId, ArrayList<String> removeIsbns){
        try(
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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

    public static void customerLoop(Scanner scan){
        //Login
        System.out.println("Connecting to Database...");
        String username = null;
        try(
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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
                //Function
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
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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
                "jdbc:postgresql://localhost:5432" + DB_PATH,
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