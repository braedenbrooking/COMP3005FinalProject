# COMP3005FinalProject

## Running Instructions

 - Ensure you have a version of java installed (Java 17 or later recommended)
 - Compile with "javac COMP3005FinalProject/LookInnaBook.java" from the parent directory
 - Ensure you have the proper driver downloaded (Likely "postgresql-42.2.24.jar" or similar)
 - If you were able to properly install the driver you should be able to run it with "java COMP3005FinalProject/LookInnaBook.java"
 - If you were unable to properly install the driver (like me) you can run it with "java -cp .;"\<path to driver>" COMP3005FinalProject/LookInnaBook

## Brief Usage Instructions
*More detailed instructions can be found in report*
### As Customer
When you boot up the program you'll be greeted with a main menu screen. From here you can register a customer account for using the system with option 3. Once registered you can select option 2 and give your username to login. From there, you'll be given all of the customer options. If you click 1, you'll be given the option to search the database for books. You can enter all sorts of criteria for the search (number of pages, author, price, etc.) Then you'll be given the list of books which match your search criteria and you can add any number of them to your shopping cart. Back on the customer menu, you can select 2 to view what is in your shopping cart. If you then wish to remove items from your cart you may. You can also check out, and give shipping, credit card, and billing information, or alternatively you can use the information associated with your customer profile. Finally, you can track your orders with option 3. Either enter a specific tracking number or enter no number to see all previous orders associated with your cart. Then when you're ready to leave, press q to exit to main menu and q from there to exit the program.
### As Owner
When you boot up the program you'll be greeted with a main menu screen, just like for the customer. Then, you can select 1 to go to the owner menu. From here you can add or remove books from the system with options 1 and 2 respectively. You can view information about a specific publisher (or all publishers) with option 3. With option 4, you can view sales reports of either a certain attribute or just general. Finally, you can view the orders the bookstore has made to the publishers to restock books which have had their stocks fall below 10. Then when you're ready to leave, press q to exit to main menu and q from there to exit the program, just like with the customer.
