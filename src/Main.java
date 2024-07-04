import java.util.*;
import javax.naming.InsufficientResourcesException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

   //----------------------------------------- Global variables -------------------------------------
   static final String DB_URL = ""; //Database url
   static final String USER = ""; //username
   static final String PASS = ""; //password
   static Connection conn;
   static boolean logged_in = false;
   static String current_user = "";
   static ArrayList<Product> all_products = new ArrayList<>();
   static ArrayList<Item> my_cart = new ArrayList<>();

   //----------------------------------------- Main Function -------------------------------------
   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         Authentication auth = new Authentication();
         fetchAllProducts();

         while (true && !logged_in) {
            System.out.println("\n--------------------Welcome to the Cafe 18--------------------");
            System.out.println("Press ->  (1): Register(New Customer)  (2): Login");
            System.out.println("--------------------------------------------------------------");
            int choice = sc.nextInt();

            if (choice == 1) {
               System.out.print("Enter your name : ");
               String name = sc.next();
               System.out.println("");
               System.out.print("Enter your email : ");
               String email = sc.next();
               System.out.println("");
               System.out.print("Enter your password : ");
               String password = sc.next();
               System.out.println("");
               System.out.print("Enter your phone number : ");
               String phone_number = sc.next();
               System.out.println("");
               System.out.println(auth.register(name, email, password, phone_number));
            } else if (choice == 2) {
               System.out.print("Enter your email : ");
               String email = sc.next();
               System.out.println("");
               System.out.print("Enter your password : ");
               String password = sc.next();
               System.out.println("");
               if (auth.login(email, password)) {
                  logged_in = true;
                  current_user = email;
               } else {
                  System.out.println("Invalid credentials! Try again");
               }
            } else {
               System.out.println("Invalid selection! Try Again");
            }

            if (logged_in) {

               while (true) {
                  System.out.println("\n---------------------Choose an operation---------------------");
                  System.out.println("(1): Add Product");
                  System.out.println("(2): Modify Cart");
                  System.out.println("(3): View Cart");
                  System.out.println("(4): Checkout");
                  System.out.println("(5): View Order History");
                  System.out.println("(6): Logout");
                  System.out.println("-------------------------------------------------------------");
                  int op_choice = sc.nextInt();
                  if (op_choice == 1) {
                     displayAllProducts();
                     Item choosedItem = chooseProduct();
                     if (choosedItem != null) {
                        my_cart.add(choosedItem);
                        System.out.println(choosedItem.product.name + " X " + choosedItem.quantity + " added to cart");
                     }
                  } else if (op_choice == 2) {
                     System.out.println(modifyCart());
                  } else if (op_choice == 3) {
                     displayMyCart();
                  } else if (op_choice == 4) {
                     if (checkOut()) {
                        my_cart.clear();
                        logOut();
                        break;
                     } else {
                        displayMyCart();
                     }
                  } else if (op_choice == 5) {
                     displayOrderHistory();
                  } else if (op_choice == 6) {
                     logOut();
                     break;
                  }else{
                     System.out.println("Invalid selection! Try Again");
                  }
               }

            }
         }

      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   //-------------------------- Function to retrieve all products from database-------------------------
   public static void fetchAllProducts() {
      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM products");
         while (rs.next()) {
            Product product = new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"),
                  rs.getString("description"), rs.getInt("stock"));
            all_products.add(product);
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }
   
   //-------------------------- Function to display all products -------------------------
   public static void displayAllProducts() {
      if (all_products.isEmpty())
         System.out.println("No products to show");
      else {
         for (Product p : all_products) {
            System.out.println(p.displayDetail());
         }
      }
      System.out.println("--------------------------------------------------------------");
      System.out.println("");
   }

   //------------------------ Function to display all products in user's cart-------------------------
   public static void displayMyCart() {
      if (my_cart.isEmpty())
         System.out.println("No products added");
      else {
         System.out.println("\n--------------------Products in my cart----------------------");
         for (Item i : my_cart) {
            System.out.println(i.getItemInfo());
         }
         System.out.println("Total Price = Rs." + getTotalPrice());
      }
      System.out.println("--------------------------------------------------------------");
      System.out.println("");
   }

   //--------------------- Function to  all choose a product and add it in cart ---------------------
   public static Item chooseProduct() {
      Scanner sc = new Scanner(System.in);

      System.out.print("Enter the product ID to select: ");
      int productId = sc.nextInt();
      System.out.println("");
      if(!isValidIdProducts(productId))
      {
         System.out.println("Invalid product ID entered!");
         return null;
      }
      System.out.print("Enter the quantity: ");
      int quantity = sc.nextInt();
      System.out.println("");

      for (Product product : all_products) {
         if (product.id == productId) {
            if (product.isAvailable(quantity)) {
               return new Item(product, quantity);
            } else {
               System.out.println("Sorry! Not available");
               return null;
            }
         }
      }
      System.out.println("Invalid product ID entered!");
      return null;
   }

   //-------------------------- Function modify the cart -------------------------
   public static String modifyCart() {
      Scanner sc = new Scanner(System.in);
      if (my_cart.isEmpty()) {
         return "Cart is Empty";
      } else {
         displayMyCart();
         System.out.print("Enter the id of product to be modified: ");
         int deleteId = sc.nextInt();
         System.out.println("");
         if(!isValidIdCart(deleteId))
         {
            return "Invalid ID entered! Try again";
         }
         System.out.println("----------------------------------------------------------------");
         System.out.println("Press -> (1): Remove product  (2): Modify quantity  (3): Cancel");
         System.out.println("----------------------------------------------------------------\n");
         int choice = sc.nextInt();
         if (choice == 3) {
            return "Back to main menu";
         }

         for (int i = 0; i < my_cart.size(); i++) {
            if (my_cart.get(i).product.id == deleteId) {
               if (choice == 1) {
                  my_cart.remove(i);
                  return "Product removed successfully from the cart";
               } else {
                  System.out.print("Enter the new quantity to be ordered: ");
                  int q = sc.nextInt();
                  System.out.println("");
                  if (my_cart.get(i).product.isAvailable(q)) {
                     my_cart.get(i).quantity = q;
                     return my_cart.get(i).product.name+"'s quantity modified successfully";
                  } else {
                     return Integer.toString(q)+my_cart.get(i).product.name+" not available, only "+my_cart.get(i).product.stock+" left";
                  }
               }
            }
         }
         return "Invalid ID entered! Try again";
      }
   }

   //-------------------------- Function to calculate total price of the cart -------------------------
   public static double getTotalPrice() {
      if (my_cart.isEmpty())
         return 0.0;
      else {
         double total = 0.0d;
         for (Item i : my_cart) {
            total += i.product.price * i.quantity;
         }
         return total;
      }
   }

   //-------------------------- Function to check if user input valid id of product -------------------------
   public static boolean isValidIdProducts(int id)
   {
      for(Product p : all_products)
      {
         if(p.id == id)
           return true;
      }

      return false;
   }

   //-------------------- Function to check if user input valid id of product from cart ---------------------
   public static boolean isValidIdCart(int id)
   {
      for(Item i : my_cart)
      {
         if(i.product.id == id)
           return true;
      }

      return false;
   }

   //---------------------- Function to checkout and do perform final actions before ordering -------------
   public static boolean checkOut() {
      Scanner sc = new Scanner(System.in);
      if (my_cart.isEmpty())
         return false;
      else {
         double total_price = getTotalPrice();
         displayMyCart();
         int coins_available = fetchCoins();
         if (coins_available == -1) {
            System.out.println("Unexpected error occured");
            return false;
         } else if (coins_available > 0) {
            System.out.println("You have " + coins_available + " coins available in your account");
            System.out.println("-------------------Do you want to redeem?----------------------");
            System.out.println("Press ->  (1) Yes  (2) No");
            System.out.println("--------------------------------------------------------------");
            int redeem = sc.nextInt();
            System.out.println("");
            if (redeem == 1) {
               while (true) {
                  System.out.print("Enter the number of coins to be redeemed: ");
                  int redeem_amt = sc.nextInt();
                  System.out.println("");
                  if (redeem_amt <= total_price) {
                     System.out.println("Yay! "+Math.round(((redeem_amt / total_price) * 100)) + "% discount applied");
                     total_price -= redeem_amt;
                     System.out.println("Total price to be paid after redeem : Rs." + (total_price));
                     break;
                  } else {
                     System.out.println("Insufficient coins : Enter valid number of coins");
                  }
               }
            }
         }
         System.out.println("Press -> (1): Proceed to payment  (2): Cancel");
         int choice = sc.nextInt();
         if (choice == 2)
            return false;
         else {
            System.out.println("\n--------------------------------------------------------------");
            System.out.println("Payment Successfull");
            System.out.println("--------------------------------------------------------------");
            if (saveCartHistory() && updateCoins(total_price) && placeOrder()) {
               for(Item i : my_cart)
               {
                  for(Product p : all_products)
                  {
                     if(i.product.id == p.id)
                     {
                        p.stock-=i.quantity;
                     }
                  }
               }
               return true;
            } else {
               return false;
            }
         }

      }

   }

   //----------------- Function to retrieve the coins stored in the DB of the user --------------------
   public static int fetchCoins() {
      try {
         Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         String sql = "SELECT * from users where email = ?";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, current_user);
         ResultSet res = pstmt.executeQuery();
         int coins_available = 0;

         if (res.next())
            coins_available = res.getInt("coins");

         return coins_available;
      } catch (SQLException e) {
         System.out.println(e.getMessage());
         return -1;
      }

   }

   //--------------- Function to credit coins in user's account on successfull order place -------------
   public static boolean updateCoins(double total_price) {
      try {
         Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         int coins = ((int) total_price / 100) * 5;
         String sql = "UPDATE users set coins = coins + " + Integer.toString(coins) + " where email = '" + current_user
               + "';";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.executeUpdate();
         System.out.println("--------------------------------------------------------------");
         System.out.println(coins + " coins have been credited to your account");
         System.out.println("--------------------------------------------------------------\n");
         return true;
      } catch (SQLException e) {
         System.out.println(e.getMessage());
         return false;
      }
   }

   //-------------------------- Function to placed order by user in the DB -------------------------
   public static boolean saveCartHistory() {
      try {

         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         String date = getDate();

         for (int i = 0; i < my_cart.size(); i++) {
            String sql = "INSERT INTO order_history(user_email, order_item, order_date) VALUES(?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, current_user);
            if (i != my_cart.size() - 1)
               pstmt.setString(2, my_cart.get(i).product.name + " X " + my_cart.get(i).quantity);
            else
               pstmt.setString(2, my_cart.get(i).product.name + " X " + my_cart.get(i).quantity + ".");
            pstmt.setString(3, date);
            pstmt.executeUpdate();

         }
         return true;
      } catch (SQLException e) {
         System.out.println(e.getMessage());
         return false;
      }

   }

   //-------------------------- Function to display the order history of the user -------------------------
   public static void displayOrderHistory() {
      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         String sql = "select order_date, group_concat(order_item) as 'order_item' from order_history where user_email = ? group by order_date";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, current_user);
         ResultSet res = pstmt.executeQuery();
         HashMap<String, String> history = new HashMap<>();
         while (res.next()) {
            history.put(res.getString("order_date"), res.getString("order_item"));
         }
         for (String key : history.keySet()) {
            System.out.println("-----------" + key + "---------------");
            String temp[] = history.get(key).split(",");
            for (String s : temp) {
               System.out.println(s);
               if (s.charAt(s.length() - 1) == '.')
                  System.out.println("-----------------------------");
            }
         }

      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }

   public static boolean placeOrder()
   {
      try {
         Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         String sql = "INSERT INTO order_recieved (user_email, order_items, order_date, order_status) VALUES (?, ?, ?, ?)";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, current_user);
         pstmt.setString(2, getMyCartItemsAsString());
         pstmt.setString(3, getDate());
         pstmt.setString(4, "Pending");
         pstmt.executeUpdate();
         System.out.println("--------------------------------------------------------------");
         System.out.println("Order has been placed succesfully!");
         System.out.println("--------------------------------------------------------------");
         return true;
      } catch (Exception e) {
         System.out.println(e.getMessage());
         return false;
      }

   }

   public static String getDate()
   {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDateTime now = LocalDateTime.now();
      String date = dtf.format(now);
      return date;
   }

   public static String getMyCartItemsAsString()
   {
      String s="";
      for(int i=0;i<my_cart.size();i++)
      {
         if(i!=my_cart.size()-1)
         s = s+my_cart.get(i).product.name+" X "+my_cart.get(i).quantity+",";
         else
         s = s+my_cart.get(i).product.name+" X "+my_cart.get(i).quantity;
      }
      return s;
   }

   //--------------------------- Function to log user out -----------------------------
   public static void logOut() {
      logged_in = false;
      current_user = "";
   }
}