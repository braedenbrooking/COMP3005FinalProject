package COMP3005FinalProject;

//Author: Braeden Brooking
//Student #: 101107870
//COMP 3005 - F21 - Final Project

//Reminder to myself for how to run this
//java -cp .;"C:\Users\braed\Downloads\postgresql-42.2.24.jar" COMP3005FinalProject/LookInnaBook


import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;


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
            Integer.parseInt(str);
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

                String query = "select title, name, price, pages, genre, ISBN from book natural join wrote natural join author where ";
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
                System.out.println(query); //DEBUG
                ResultSet rset = stmt.executeQuery(query);
                System.out.println("=== Results ===");
                System.out.println("Title                                   \tAuthor             \tPrice\tPages\tGenre           \tISBN");
                while(rset.next()){
                    String title = rset.getString("title");
                    while(title.length()<40){
                        title += " ";
                    }
                    String name = rset.getString("name");
                    while(name.length()<20){
                        name += " ";
                    }
                    String genre = rset.getString("genre");
                    while(genre.length()<16){
                        genre += " ";
                    }

                    System.out.println(title + "\t" + name + "\t" + rset.getString("price") + "\t" + rset.getString("pages") + "\t" + genre + "\t" + rset.getString("ISBN"));
                }

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
            System.out.println("1 - See All Books");
            System.out.println("2 - Search for Books");
            System.out.println("3 - View Shopping Cart");
            System.out.println("4 - Checkout");
            System.out.println("5 - Track Orders");
            System.out.println("q - Return to Main Menu");

            String selection = scan.nextLine();
            if(selection.equals("1")){
                //Function
            }else if(selection.equals("2")){
                searchForBooks(scan, username);
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
                System.out.println("Please select only 1-3 or q");
            }
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
                    System.out.println("Email");
                    System.out.println("Phone Number");
                    System.out.println("Credit Card");


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
                    stmt.executeQuery(query);

                    System.out.println("User Registered!");
                    flag = false;
                }catch (Exception e){
                    System.out.println("Exception: " + e);
                    System.out.println("Please try again");
                }
            }
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