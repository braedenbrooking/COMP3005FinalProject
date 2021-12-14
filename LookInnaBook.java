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
            }else if(selection.equals("q")){
                break;
            }else{
                System.out.println("Invalid Option");
                System.out.println("Please select only 1-4 or q");
            }
        }
    }

    public static void customerLoop(Scanner scan){
        System.out.println("Please enter your username:");
        String username = scan.nextLine();
        while(true){
            System.out.println();
            System.out.println();
            System.out.println("Welcome " + username + "!");
            System.out.println("=== Cutomer Menu ===");
            System.out.println("1 - Search for Books");
            System.out.println("2 - View Checkout Basket");
            System.out.println("3 - Track Orders");
            System.out.println("q - Return to Main Menu");

            String selection = scan.nextLine();
            if(selection.equals("1")){
                //Function
            }else if(selection.equals("2")){
                //Function
            }else if(selection.equals("3")){
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
        try(
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432" + DB_PATH,
                DB_USER, DB_PASS
            );
            Statement stmt = conn.createStatement();
        ){
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