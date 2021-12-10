package COMP3005FinalProject;

import java.util.Scanner;


public class LookInnaBook{

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
            System.out.println("2 - Customer");
            System.out.println("q - Quit");


            String selection = scan.nextLine();
            if(selection.equals("1")){
                ownerLoop(scan);
            }else if(selection.equals("2")){
                customerLoop(scan);
            }else if(selection.equals("q")){
                break;
            }else{
                System.out.println("Invalid Option");
                System.out.println("Please select only 1, 2, or q");
            }
        }
        System.out.println("See you next time!");
        
    }
}