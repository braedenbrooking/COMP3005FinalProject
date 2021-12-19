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


    //Owner Functions

    /**
    * This function is responsible for adding books to the database. It is called by the owner loop and loops until the user no longer wants
    * to add more books. If the book added is from a publisher or author which is not in the database already, addPublisher or addAuthor is called
    * @param scan This is used to get information from the user
    * @return void
    */
    public static void addBook(Scanner scan){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            while(true){ // Loop until the user no longer wants to add books
                System.out.print("ISBN: ");
                String newBookIsbn = scan.nextLine();
                System.out.print("Title: ");
                String newBookTitle = scan.nextLine();
                System.out.print("Genre: ");
                String newBookGenre = scan.nextLine();
                String newBookPages = "";
                while(true){ // Loop until # of pages entered is numeric
                    System.out.print("Pages: ");
                    newBookPages = scan.nextLine();
                    if(!isNumeric(newBookPages)){
                        System.out.println("Error: Must be numeric");
                    }
                    break;
                }
                String newBookPrice = "";
                while(true){ // Loop until price entered is numeric
                    System.out.print("Price: ");
                    newBookPrice = scan.nextLine();
                    if(!isNumeric(newBookPrice)){
                        System.out.println("Error: Must be numeric");
                    }
                    break;
                }

                System.out.print("Publisher: ");
                String newBookPublisher = scan.nextLine();
                //Check if publisher is in the database already
                String query = "select count(*) from publisher where publisher_name='" + newBookPublisher + "';";
                ResultSet rset = stmt.executeQuery(query);
                int count=0;
                while(rset.next()){ 
                    count = Integer.parseInt(rset.getString("count"));
                }

                if(count == 0){
                    System.out.println(newBookPublisher + " is a new publisher. Please fill out their information: ");
                    if(!addPublisher(scan, newBookPublisher)) throw new Exception("Error: Cannot add publisher");
                }

                String newBookStock = "";
                while(true){ // Loop until # of copies to order entered is > 0 and numeric
                    System.out.print("# of " + newBookTitle + " to Purchase from Publisher: ");
                    newBookStock = scan.nextLine();
                    if(!isNumeric(newBookStock)){
                        System.out.println("Error: Must be numeric");
                        continue;
                    }else if(Integer.parseInt(newBookStock) <= 0){
                        System.out.println("Must be greater than 0");
                        continue;
                    }
                    break;

                }

                String newBookPercentage = "";
                while(true){ // Loop until price entered is numeric
                    System.out.print("% of sales to send Publisher: ");
                    newBookPercentage = scan.nextLine();
                    if(!isNumeric(newBookPercentage)){
                        System.out.println("Error: Must be numeric");
                    }
                    break;
                }

                query = "insert into book values(" + newBookIsbn + ", '" + newBookTitle + "', " + newBookPrice + ", " + newBookPercentage + ", " + newBookPages + ", " + newBookStock + ", '" + newBookGenre + "', '" + newBookPublisher + "');";
                stmt.executeUpdate(query); // Insert book into db
                String dateTime = LocalDateTime.now().toString();
                query = "insert into stock_order values('" + dateTime + "', " + newBookIsbn + ", " + newBookStock + ");";
                stmt.executeUpdate(query); // Order book from publisher

                int index = 1;
                while(true){ //Add as many authors as necessary
                    System.out.print("Author #" + index + ": ");
                    String newBookAuthorName = scan.nextLine();
                    if(newBookAuthorName.equals("") && index>1){
                        break;
                    }else if(newBookAuthorName.equals("") && index==1){
                        System.out.println("Must have at least 1 author");
                        continue;
                    }
                    //Check if author is in the database. This allows for multiple authors of the same name
                    query = "select count(name) from author where name='" + newBookAuthorName + "';";
                    rset = stmt.executeQuery(query);
                    count = 0;
                    int newBookAuthorId = -1;
                    while(rset.next()){
                        count = Integer.parseInt(rset.getString("count"));
                        if(count==0){ // Author's name is not in the database
                            newBookAuthorId = addAuthor(newBookAuthorName);
                            break;
                        }else{ // Author's name is in the database
                            query = "select title, author_id from author natural join wrote natural join book where name='" + newBookAuthorName + "' order by author_id;";
                            ResultSet rset2 = stmt.executeQuery(query); // Find all books by authors with that name
                            int prev = -1;
                            int finalAuthorId = -1;
                            while(rset2.next()){
                                int currAuthorId = Integer.parseInt(rset2.getString("author_id"));
                                if(currAuthorId == prev){ // Only need to check one book per author_id
                                    continue;
                                }
                                System.out.println("Is this the same author that wrote " + rset2.getString("title") + "? (y/n)");
                                if(yesOrNo(scan)) finalAuthorId = currAuthorId; //Yes means they are the same author
                                
                                if(finalAuthorId != -1){
                                    break;
                                }else{
                                    prev = currAuthorId;
                                }
                            }
                            if(finalAuthorId == -1){ // This means that the author is a new author so we add them
                                newBookAuthorId = addAuthor(newBookAuthorName);
                                break;
                            }else{
                                newBookAuthorId = finalAuthorId;
                                break;
                            }
                        }
                    }

                    if(newBookAuthorId == -1) throw new Exception("Error: Author ID invalid"); // This should theoretically never happen

                    query = "insert into wrote values(" + newBookAuthorId + ", " + newBookIsbn + ");";
                    stmt.executeUpdate(query); // Add new relationship between the book and author

                    System.out.println("Add another author?(y/n)");
                    if(!yesOrNo(scan)){
                        break;
                    }
                    index++;
                }
                System.out.println("Book Added!");
                System.out.println("Add another book? (y/n)");
                if(!yesOrNo(scan)){
                    break;
                }

            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * Adds new authors to the database given the name of the author and assigns the new author an id.
    * @param authorName The name of the author to be added, gotten from the user in addBooks()
    * @return int This is the assigned author_id
    */
    public static int addAuthor(String authorName){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "select count(*) from author;";
            ResultSet rset = stmt.executeQuery(query);
            int nextId = 0;
            while(rset.next()){
                nextId = Integer.parseInt(rset.getString("count")) + 1; // New id will just be the number of authors in the database +1
            }
            query = "insert into author values(" + nextId + ", '" + authorName + "');";
            stmt.executeUpdate(query);
            return nextId;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return -1;
        }
    }

    /**
    * Adds new publishers to the database by prompting the user for information
    * @param scan This is used to get information from the user
    * @param publisherName This is the name of the publisher, gotten from the user in addBooks()
    * @return boolean true if succesfull; false otherwise
    */
    public static boolean addPublisher(Scanner scan, String publisherName){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String newPublisherBank = "";
            while(true){
                System.out.print("Bank Account: ");
                newPublisherBank = scan.nextLine();
                if(!isNumeric(newPublisherBank)){
                    System.out.println("Error: Must be numeric");
                }
                break;
            }
            System.out.print("Address: ");
            String newPublisherAddress = scan.nextLine();
            System.out.print("Email: ");
            String newPublisherEmail = scan.nextLine();
            String newPublisherPhone = "";
            while(true){
                System.out.print("Phone Number: ");
                newPublisherPhone = scan.nextLine();
                if(!isNumeric(newPublisherPhone)){
                    System.out.println("Error: Must be numeric");
                }
                break;
            }
            String query = "insert into publisher values('" + publisherName + "', " + newPublisherBank + ", '" + newPublisherAddress + "', '" + newPublisherEmail + "', " + newPublisherPhone + ");";
            stmt.executeUpdate(query);
            return true;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return false;
        }   
    }

    /**
    * This function is responsible for removing books from the database. It is called by the owner loop and loops until the user no longer wants
    * to remove books. If the book removed is only from a publisher or author, removePublisher or removeAuthor is called
    * @param scan This is used to get information from the user
    * @return void
    */
    public static void removeBook(Scanner scan){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            while(true){
                System.out.print("ISBN of the book you are removing (or 'end' when done): ");
                String isbn = scan.nextLine();
                if(isbn.equals("end")){
                    break;
                }else if(!isNumeric(isbn)){
                    System.out.println("Error: Not numeric please try again");
                    continue;
                }
                String query = "select count(*), author_id from wrote where author_id in (select author_id from wrote where ISBN=" + isbn + ") group by author_id;";
                ResultSet rset = stmt.executeQuery(query);
                boolean bookDoesNotExistFlag = true;;
                while(rset.next()){
                    int count = Integer.parseInt(rset.getString("count"));
                    if(count == 0){
                        break;
                    }else if(count == 1){
                        bookDoesNotExistFlag = false;
                        removeAuthor(rset.getString("author_id"));
                    }
                }

                if(bookDoesNotExistFlag){
                    System.out.println("That book does not exist in the database");
                    continue;
                }

                query = "select count(*), publisher_name from book where publisher_name in (select publisher_name from book where ISBN=" + isbn + ") group by publisher_name;";
                rset = stmt.executeQuery(query);
                boolean deletedPublisher = false;
                while(rset.next()){
                    int count = Integer.parseInt(rset.getString("count"));
                    if(count == 0){
                        throw new Exception("What");
                    }else if(count == 1){
                        deletedPublisher = removePublisher(rset.getString("publisher_name"));
                        break;
                    }
                }
                if(deletedPublisher) continue;

                query = "delete from book where ISBN=" + isbn + ";";
                stmt.executeUpdate(query); 
                System.out.println(isbn + " was succesfully deleted");
            }


        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function is responsible for removing authors given an author id
    * @param authorId This is the id of the author that will be removed, gotten from the user in removeBooks
    * @return boolean true if succesfull; false otherwise
    */
    public static boolean removeAuthor(String authorId){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){

            String query = "delete from author where author_id='" + authorId + "';";
            stmt.executeUpdate(query);
            System.out.println("Last book from author with id=" + authorId + " was removed so author is removed aswell");
            return true;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return false;
        }
    }

    /**
    * This function is responsible for removing publishers given a name
    * @param authorId This is the name of the publisher that will be removed, gotten from the user in removeBooks
    * @return boolean true if succesfull; false otherwise
    */
    public static boolean removePublisher(String publisherName){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "delete from publisher where publisher_name='" + publisherName + "';";
            stmt.executeUpdate(query);
            System.out.println("Last book from publisher " + publisherName + " was removed so publisher is removed aswell");
            return true;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return false;
        }
    }

    /**
    * This function is for viewing information about any/all publishers
    * @param scan This is used to get information from the user
    * @return void
    */
    public static void viewPublisher(Scanner scan){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "";
            System.out.print("Publisher's Name (Just enter for all): ");
            String selection = scan.nextLine();
            if(selection.equals("")){
                query = "select * from publisher;";
            }else{
                query = "select * from publisher where publisher_name='" + selection + "';";
            }
            ResultSet rset = stmt.executeQuery(query);
            while(rset.next()){
                System.out.println("=================");
                String publisherName = rset.getString("publisher_name");
                System.out.println("Publisher: " + publisherName);
                System.out.println("Bank Account: " + rset.getString("bank_account"));
                System.out.println("Address: " + rset.getString("address"));
                System.out.println("Email: " + rset.getString("email"));
                System.out.println("Phone: " + rset.getString("phone_number"));
                System.out.println("Publishes: ");
                viewBooksByPublisher(publisherName);
            }

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function prints all books published by a specific publisher
    * @param publisherName The name of the publisher that published the books
    * @return void
    */
    public static void viewBooksByPublisher(String publisherName){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "select title from book natural join publisher where publisher_name='" + publisherName + "' order by title;";
            ResultSet rset = stmt.executeQuery(query);
            while(rset.next()){
                System.out.println("\t- " + rset.getString("title"));
            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function lists all orders made by the book store to 
    * @param publisherName The name of the publisher that published the books
    * @return void
    */
    public static void viewOrders(Scanner scan){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String query = "";
            System.out.print("Publisher's Name (Just enter for all): ");
            String selection = scan.nextLine();
            if(selection.equals("")){
                query = "select * from stock_order natural join book order by publisher_name, date_time, title;";
            }else{
                query = "select * from stock_order natural join book where publisher_name='" + selection + "' order by date_time;";
            }
            ResultSet rset = stmt.executeQuery(query);
            String prevPublisher = "";
            while(rset.next()){
                String currPublisher = rset.getString("publisher_name");
                if(!currPublisher.equals(prevPublisher)) System.out.println("===" + currPublisher + "===");
                System.out.println("Order to: " + currPublisher + " placed at: " + rset.getString("date_time") +" for " + rset.getString("title") + " / " + rset.getString("isbn") + " x" + rset.getString("number_purchased"));
                prevPublisher = currPublisher;
            }
            
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function calculates the gross sales, expenses, and net sales for the whole bookstore or can be grouped by on of the 
    * collumns of purchase, book, or author
    * @param scan This is used to get information from the user
    * @return void
    */
    public static void viewSalesReport(Scanner scan){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            System.out.print("Would you like to just view sales vs expenditures (y/n):");
            boolean salesVsExpenditures = yesOrNo(scan);
            String salesBy = null;
            if(!salesVsExpenditures){
                System.out.print("View Sales by (genre, author, etc.): ");
                salesBy = scan.nextLine().toLowerCase();
                if(salesBy.equals("author")) salesBy = "name";
            }

            String query = null;
            if(salesVsExpenditures){
                query = "select price, percentage_to_publisher, quantity from purchase natural join shopping_cart natural join in_cart natural join book;";
            }else{
                query = "select price, percentage_to_publisher, quantity, " + salesBy + " from purchase natural join shopping_cart natural join in_cart natural join book natural join wrote natural join author order by " + salesBy + ";";
            }

            ResultSet rset = stmt.executeQuery(query);
            ArrayList<Double> grossTotal = new ArrayList<Double>(); // Each element of these lists correspond to an element of the category
            ArrayList<Double> expenses = new ArrayList<Double>();
            ArrayList<Double> netTotal = new ArrayList<Double>();
            ArrayList<String> elementsOfCategory = new ArrayList<String>();
            String prev = "";
            int index = -1;
            if(salesBy == null) index = 0;
            while(rset.next()){
                String category = null;
                if(salesBy != null) category = rset.getString(salesBy);
                else{
                    grossTotal.add(0.0);
                    expenses.add(0.0);
                    netTotal.add(0.0);
                }
                if(salesBy != null && !category.equals(prev)){
                    index++;
                    grossTotal.add(0.0);
                    expenses.add(0.0);
                    netTotal.add(0.0);
                    elementsOfCategory.add(category);
                }
                double currPrice = Double.parseDouble(rset.getString("price")) * Integer.parseInt(rset.getString("quantity"));
                grossTotal.set(index, grossTotal.get(index)+ currPrice);
                double currExpense = (Double.parseDouble(rset.getString("percentage_to_publisher"))/100)*currPrice;
                expenses.set(index, expenses.get(index)+currExpense);
                netTotal.set(index, netTotal.get(index)+(currPrice - currExpense));
                prev = category;
            }

            if(salesBy == null) System.out.println("=== Sales Report ===");
            else System.out.println("=== Sales Report by " + salesBy + " ===");
            
            for(int i=0; i<index+1; i++){
                if(salesBy != null) System.out.println(salesBy + " = " + elementsOfCategory.get(i));
                System.out.println("Gross Sales: $" + df.format(grossTotal.get(i)));
                System.out.println("Expenses: $-" + df.format(expenses.get(i)));
                System.out.println("Net Total: $" + df.format(netTotal.get(i)));
                System.out.println("===============================");
            }

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function acts as a menu for the owner interface
    * @param scan This is used to get information from the user
    * @return void
    */
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
                addBook(scan);
            }else if(selection.equals("2")){
                removeBook(scan);
            }else if(selection.equals("3")){
                viewPublisher(scan);
            }else if(selection.equals("4")){
                viewSalesReport(scan);
            }else if(selection.equals("5")){
                viewOrders(scan);
            }else if(selection.equals("q")){
                break;
            }else{
                System.out.println("Invalid Option");
                System.out.println("Please select only 1-5 or q");
            }
        }
    }


    //Helper Functions
    /**
    * This is a helper function for asking the user to enter y for yes or n for no
    * @param scan This is used to get information from the user
    * @return boolean returns true on 'y' and false on 'n'
    */
    public static boolean yesOrNo(Scanner scan){
        while(true){
            String selection = scan.nextLine();
            if(selection.equals("y")){
                return true;
            }else if(selection.equals("n")){
                return false;
            }else{
                System.out.println("Just y or n");
            }
        }
    }

    /**
    * This is a helper function for ensuring a string is numeric
    * @param str The string which is being checked if its numeric
    * @return boolean returns true if its numeric and false otherwise
    */
    public static boolean isNumeric(String str){
        try{
            Double.parseDouble(str);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }


    // Customer Functions
    /**
    * This function searches for books based on search criteria given by the user. Then, the user is prompted if they
    * want to add to their cart
    * @param scan This is used to get information from the user
    * @param username This is the user's username to identify them in the customer table
    * @return none
    */
    public static void searchForBooks(Scanner scan, String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            
            
            while(true){
                System.out.println("Book Search: ");
                System.out.println("Enter the following: (or just enter if not applicable)");


                String[] prompts = {"Title", "Author", "Publisher", "Genre", "More than __ Pages", "Less than __ Pages", "More than $__", "Less than $__"}; // Prompts for the search criteria
                String[] selections = new String[8];
                String[] columns = {"title", "name", "publisher", "genre", "pages", "pages", "price", "price"}; // Collumns of the database table
                for(int i=0; i<selections.length; i++){ // Gets the criteria of the user
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
                for(int i=0; i<selections.length; i++){ // Checks if all fields were left blank
                    if (selections[i] != null){
                        query += "where ";
                        break;
                    }
                }

                
                String and = "";
                for(int i=0; i<selections.length; i++){ // Adds any search criteria to the query
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
                while(rset.next()){ // Collects data retrieved by the query
                    ArrayList<String> bookinfo = new ArrayList<String>();
                    String title = rset.getString("title");
                    while(title.length()<45){ // Padding to make the output look nice
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
                        if(bookList.get(i).contains(isbn)){ // If the book is already in the database it means the book has multiple authors
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
                //Now that the user can see the books, they can start adding to their cart
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
                    int maximumQuantity = Math.min(quantityAsInt, Integer.parseInt(bookList.get(itemAsInt).get(5))); // Only can add a max of how many are in stock at the moment
                        
                    for(int i=0; i<maximumQuantity; i++)
                        addToCartItems.add(bookList.get(itemAsInt).get(bookList.get(itemAsInt).size()-1));

                    query = "update book set stock=" + (Integer.parseInt(bookList.get(itemAsInt).get(5))-maximumQuantity) + " where ISBN='" +  bookList.get(itemAsInt).get(bookList.get(itemAsInt).size()-1) + "';";
                    stmt.executeUpdate(query);
                    
                }
                String cartId = getCurrentCartId(username);
                addToCart(cartId, addToCartItems);


                System.out.println("Would you like to search again? (y/n)");
                if(!yesOrNo(scan)) break;

            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function adds books to the cart corresponding to the id given
    * @param cartId The id of the cart we are adding items to
    * @param isbns List of ISBNs corresponding to books in the database that need to be added. The number of duplicates is the quantity to be added
    * @return none
    */
    public static void addToCart(String cartId, ArrayList<String> isbns){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            if(cartId == null){
                System.out.println("Error No Shopping Cart");
                return;
            }

            String query = "select isbn, quantity from in_cart where shopping_cart_id='" + cartId + "';";
            ResultSet rset = stmt.executeQuery(query);

            while(rset.next()){ // We need to add the items already in the cart to the list of isbns
                String curISBN = rset.getString("ISBN");
                
                for(int i=0; i<Integer.parseInt(rset.getString("quantity")); i++){
                    isbns.add(curISBN);
                }
            }
            query = "delete from in_cart where shopping_cart_id=" + cartId + ";";
            stmt.executeUpdate(query); // Clear the cart
            Collections.sort(isbns);
            String prev = "";
            query = "";
            for(int i=0; i<isbns.size(); i++){ // Add all the items in the list to the cart
                if(isbns.get(i).equals(prev)){
                    continue;
                }
                int quant = 0;
                for(int j=i; j<isbns.size(); j++){ // Count the number of duplicates in the list to get the quantity
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

    /**
    * This function finds the active cart for the username since the user can have multiple carts which have already been checked out
    * @param username The username of the user whom we are finding the active cart
    * @return String The id of the current cart
    */
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
            while(rset.next()){ // The current cart is always the one with the largest id value associated with that user (see createCart)
                cartId = rset.getString("shopping_cart_id");
            }
            return cartId;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return null;
        }
    }

    /**
    * This function lists the items in the user's cart and then prompts the user to remove and/or checkout
    * @param scan This is used to get information from the user
    * @param username This is the user's username to identify them in the customer table
    * @return none
    */
    public static void viewCart(Scanner scan, String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            
            String cartId = getCurrentCartId(username);
            double total = 0.0;
            ArrayList<String> isbnsInCart = new ArrayList<String>(); // Kept Separately to help with future functions 
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

                if(!(booksInCart.size()>0)){ //If cart is empty, no need to ask about removing or checking out so we just return to the menu
                    System.out.println("Your cart is empty!");
                    return;
                }

                // Now we can list the items
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
                boolean wantToRemove = yesOrNo(scan);
                if(wantToRemove) System.out.println("Enter the index you want to remove: ('end' to stop)");
                ArrayList<String> removeIsbns = new ArrayList<String>(); // Keep track of all ISBNs of books we want to remove
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

                isbnsInCart = new ArrayList<String>();
                booksInCart = new ArrayList<ArrayList<String>>();
            }

            System.out.println("Are you ready to checkout? (y/n)");
            boolean successfulPurchase = false;
            if(yesOrNo(scan)) successfulPurchase = checkout(scan, cartId, username, total);

            if(successfulPurchase){ // If the purchase was successfull we need to make sure the stock of all books purchased did not drop below the threshold
                for(int i=0; i<isbnsInCart.size(); i++){
                    checkStock(isbnsInCart.get(i));
                }
            }

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function checks that the stock of a given book has not fallen below the threshold (10)
    * @param isbn ISBN of book whose stock needs checking
    * @return none
    */
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
                query = "insert into stock_order values('" + dateTime + "', " + isbn + ", " + 10 +");";
                stmt.executeUpdate(query); // Just adds 10 to the stock
                query = "update book set stock=stock+" + 10 + " where ISBN=" + isbn + ";";
                stmt.executeUpdate(query); // Orders 10 more books from the publisher of the book
            }
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function checks out the user using their active cart. The user has the option to use the credit card, billing, and/or shipping information
    * from their profile or they can provide new information
    * @param scan This is used to get information from the user
    * @param cartId The active shopping cart of the user
    * @param username This is the user's username to identify them in the customer table
    * @param total The total amount the user will need to pay
    * @return boolean true on successful purchase; false otherwise
    */
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
            boolean useProfileCardAndBilling = yesOrNo(scan);

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
            System.out.println("Would you like to use the shipping address from your profile? (y/n)");
            boolean useProfileShipping = yesOrNo(scan);
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

            if(useProfileCardAndBilling && useProfileShipping){ // In the cases where they want to use their profile info, we must retrieve it
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
            String dateTime = LocalDateTime.now().toString(); // Timestamp for the purchase record

            query = "select count(*) from purchase;";
            ResultSet rset = stmt.executeQuery(query);
            int trackingNumber = 0;
            while(rset.next()){ // Calculate tracking number
                trackingNumber = Integer.parseInt(rset.getString("count")) + 1;
            }

            query = "insert into purchase values(" + trackingNumber + ", '" + dateTime + "', '" + shipping + "', '" + billing + "', " + creditCard + ", " + total + ", 'In-Transit', " + cartId +");";
            stmt.executeUpdate(query);

            System.out.println("Your Tracking Number: " + trackingNumber);
            System.out.println("Thank You for your Purchase! :)");

            createCart(username); //Must create a new cart since the relation between carts and purchases is 1-to-1
            return true;
        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return false;
        }
    }

    /**
    * This function removes 1-many books from a given shopping cart
    * @param scan This is used to get information from the user
    * @param cartId The active shopping cart of the user
    * @param removeIsbns The list of books to remove from the cart
    * @return none
    */
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
            while(rset.next()){ // Get all items currently in the cart 
                int currentQuantity = Integer.parseInt(rset.getString("quantity"));
                String currentIsbn = rset.getString("ISBN");
                for(int i=0; i<currentQuantity; i++){
                    isbnsInCart.add(currentIsbn);
                }
            }
            
            for(int i=0; i<removeIsbns.size(); i++){ // Loop over the lists and remove the elements of removeIsbns from isbnsInCart
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
            stmt.executeUpdate(query); // Clear the cart
            if(isbnsInCart.size()>0) addToCart(cartId, isbnsInCart); // Add the items still in the cart back into the cart using addToCart

        }catch (Exception sqle){
            System.out.println("Exception: " + sqle);
            return;
        }
    }

    /**
    * This function lists the purchases of a particular user or just one purchase of the user's choice
    * @param scan This is used to get information from the user
    * @param username This is the user's username to identify them in the customer table
    * @return none
    */
    public static void trackOrders(Scanner scan, String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            String tracking_number = null;
            while(true){ // Allow the user to view all of their purchases
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
                query = "select * from purchase natural join shopping_cart natural join in_cart natural join book where customer_username='" + username + "' order by date_time_of_purchase;";
            }else{
                System.out.println("=== Order Number: " + tracking_number + " ===");
                query = "select * from purchase natural join shopping_cart natural join in_cart natural join book where tracking_number=" + tracking_number + ";";
            }
            ResultSet rset = stmt.executeQuery(query);

            String prev = "";
            while(rset.next()){ 
                String currTrackNum = rset.getString("tracking_number");
                if(!(currTrackNum.equals(prev))){ //There will be duplicates of all this information for each different item in purchase so this ensure its only printed once per purchase
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

    /**
    * This function acts as a menu for the customer interface
    * @param scan This is used to get information from the user
    * @return void
    */
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
            while(true){ // Gets username from customer to log them in
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

        //Customer menu
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
    
    /**
    * This function creates a new cart for the user of the given username
    * @param username This is the user's username to identify them in the customer table
    * @return boolean true if successful; false otherwise
    */
    public static boolean createCart(String username){
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            
            String query = "select count(*) from shopping_cart;";
            ResultSet rset = stmt.executeQuery(query);
            int newCartId = 0;
            while(rset.next()){ // Next cart id is always just the number of carts in the database + 1
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

    /**
    * This function creates a new customer account for the user and prompts for all the necessary information
    * @param scan This is used to get information from the user
    * @return boolean true if successful; false otherwise
    */
    public static void registerCustomer(Scanner scan){ 
        System.out.println("Connecting to Database...");
        try(
            Connection conn = DriverManager.getConnection(
                DB_HOST + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
            
            boolean flag = true;
            while(flag){ // Loop until user successfully created
                try{
                    System.out.println("Please enter the following information");

                    final String[] prompts = {"Username: ", "Name: ", "Address: ", "Credit Card: ", "Email: ", "Phone Number: "};

                    String[] newUserInfo = new String[6];
                    for(int i=0; i<newUserInfo.length; i++){ // Prompts and collects information
                        System.out.print(prompts[i]);
                        newUserInfo[i] = scan.nextLine();
                        if(newUserInfo[i].equals("")){
                            System.out.println("You didn't enter anything. Please enter the value again:");
                            i--;
                        }
                    }
                    while(true){ // Checks if the username is already taken 
                        String query = "select count(*) from customer where customer_username='" + newUserInfo[0] + "';";
                        ResultSet rset = stmt.executeQuery(query);
                        boolean usernameExists = false;
                        while(rset.next()) if(Integer.parseInt(rset.getString("count"))>0) usernameExists = true;
                        if(!usernameExists && !newUserInfo[0].equals("")) break;
                        System.out.println("That username already exists!");
                        System.out.print("New Username: ");
                        newUserInfo[0] = scan.nextLine();
                    }
                    String query = "insert into customer values ('" + newUserInfo[0] + "'";
                    for(int i=1; i<newUserInfo.length; i++){
                        query += ",'" + newUserInfo[i] + "'";
                    }
                    query += ");";
                    System.out.println("Registering...");
                    stmt.executeUpdate(query);

                    flag = !createCart(newUserInfo[0]); // If the cart is succesfully created the user is fully registered

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

    /**
    * This function acts as a main menu 
    * @param args Command line arguments
    * @return void
    */
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        System.out.println("==========================");
        System.out.println("Welcome to Look Inna Book!");
        System.out.println("==========================");
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