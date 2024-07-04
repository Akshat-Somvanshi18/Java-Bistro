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

public class Cafe{

   //----------------------------------------- Global variables -------------------------------------
   static final String DB_URL = "jdbc:mysql://localhost/java_project";
   static final String USER = "root";
   static final String PASS = "Ak$hat1816";
   static Connection conn;
   static HashMap<String,String> current_orders = new HashMap<>();
   static HashMap<String,String> completed_orders = new HashMap<>();


   //----------------------------------------- Main Function -------------------------------------
   public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);

      try {
         conn = DriverManager.getConnection(DB_URL, USER, PASS);
         fetchCurrentOrders();
         fetchAllOrders();
         System.out.println(current_orders);
         System.out.println(completed_orders);

         while(true)
         {
            System.out.println("\n------------------------------Welcome to the Cafe 18-----------------------------");
            System.out.println("Press ->  (1): Show current orders  (2): Complete order  (3): Show order history");
            System.out.println("-----------------------------------------------------------------------------------");

            int choice = sc.nextInt();

            if(choice == 1)
            {
                displayOrders(current_orders);
            }
            else if(choice == 2)
            {
                displayOrders(current_orders);
                completeOrder();
            }
            else if(choice == 3)
            {
                displayOrders(completed_orders);
            }
            else
            {
                System.out.println("Invalid choice selected! Try Again");
            }
         }
         

         

      } catch (SQLException e) {
         e.printStackTrace();
      }
    

   }
      //-------------------------- Function to retrieve all current orders recieved from database-------------------------
      public static void fetchCurrentOrders() {
        try {
            current_orders.clear();
           conn = DriverManager.getConnection(DB_URL, USER, PASS);
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT * FROM order_recieved where order_status = 'Pending'");
           while(rs.next())
           {
             String email = rs.getString("user_email");
             int id = rs.getInt("order_id");
             String date = rs.getString("order_date");
             String sql = "SELECT name from users where email = ?";
             PreparedStatement pstmt = conn.prepareStatement(sql);
             pstmt.setString(1,email);
             ResultSet res = pstmt.executeQuery();
             String name="";
             if(res.next())
             {
                name = res.getString("name");
                name = Integer.toString(id)+") "+name+" "+date;
             }
             String order = rs.getString("order_items");
             current_orders.put(name,order);
           }
          
           
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
     }

//-------------------------- Function to retrieve all completed orders from database-------------------------
     public static void fetchAllOrders() {
        try {
            completed_orders.clear();
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM order_recieved where order_status = 'Completed'");
            while(rs.next())
            {
              String email = rs.getString("user_email");
              int id = rs.getInt("order_id");
              String date = rs.getString("order_date");
              String sql = "SELECT name from users where email = ?";
              PreparedStatement pstmt = conn.prepareStatement(sql);
              pstmt.setString(1,email);
              ResultSet res = pstmt.executeQuery();
              String name="";
              if(res.next())
              {
                 name = res.getString("name");
                 name = Integer.toString(id)+") "+name+" "+date;
              }
              String order = rs.getString("order_items");
              completed_orders.put(name,order);
            }
           
            
         } catch (SQLException e) {
            System.out.println(e.getMessage());
         }
     }


//-------------------------- Function to display all orders from database-------------------------
     public static void displayOrders(HashMap<String,String> map)
     {
        if(map.isEmpty())
        {
            System.out.println("\nNo orders found!\n");
        }
        else
        {
            System.out.println("\n------------------------------------Order List---------------------------------------");
            for(String s : map.keySet())
            {
                System.out.println(s+"  "+map.get(s));
            }
            System.out.println("-------------------------------------------------------------------------------------\n");
        }
     }

//-------------------------- Function to complete the order and update the database-------------------------
     public static boolean completeOrder()
     {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter the id of the order to be completed: ");
        int id = sc.nextInt();
        System.out.println("");
        try {

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = "update order_recieved set order_status = 'Completed' where order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            fetchCurrentOrders();
            fetchAllOrders();
            System.out.println("Order Completed!");
            return true;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
     }
}


