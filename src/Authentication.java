import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//--------------------------Class to handle the authentication------------------
public class Authentication {

    final String DB_URL = "";
    final String USER = "";
    final String PASS = "";
    Connection conn;

    public Authentication() {
        try 
        {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } 
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
    }

    //-------------------------- Function to register new user into the account -------------------------
    public String register(String name, String email, String password, String phone_number) {

        if(name=="" || email == "" || password=="" || phone_number=="")
        {
            return "Enter complete details";
        }
        String sql = "INSERT INTO users(name, email, password, phone_number, coins) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3,password);
            pstmt.setString(4, phone_number);
            pstmt.setInt(5, 0);
            pstmt.executeUpdate();
            return "Registration Successfull! Now you may login";
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "Error: Please try again";
        }
    }

    //-------------------------- Function to log user in  -------------------------
    public boolean login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                System.out.println(rs.getString("name")+" has been logged in successfully\n");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}