import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author jianyang
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-24
 */

@WebServlet(name = "addStar",urlPatterns = "/api/addStar")
public class addStar extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        System.out.println("add a star");
        String starName = request.getParameter("starname");
        String birthYear = request.getParameter("birthyear");
        String newId="";
        boolean noBirth=false;
        if (birthYear=="")
        {
            noBirth=true;
            System.out.println("no birthyear");
        }
        System.out.println(starName);
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedbmaster");
            // Connection dbcon = ds.getConnection();
            Connection moviedb = ds.getConnection();


            Statement statement = moviedb.createStatement();
            String query = "Select max(id) as maxId from stars";
            ResultSet result = statement.executeQuery(query);
            String maxId="";
            while (result.next())
            {
                maxId=result.getString("maxId");
            }

            String s = maxId.replaceAll("\\d+", "");
            String numOfs = maxId.replaceAll("\\D+", "");
            int toInt=Integer.valueOf(numOfs);
            int newInt=toInt+1;
            String toString=String.valueOf(newInt);
             newId=s+toString;
            if(noBirth==true)
            {
                String newQuery = "insert into stars(id,name) values(?,?)";
                PreparedStatement statement1 = moviedb.prepareStatement(newQuery);
                statement1.setString(1, newId);
                statement1.setString(2, starName);
                int num = statement1.executeUpdate();
            }
            else {
                String newQuery = "insert into stars(id,name,birthYear) values(?,?,?)";
                PreparedStatement statement1 = moviedb.prepareStatement(newQuery);
                statement1.setString(1, newId);
                statement1.setString(2, starName);
                statement1.setString(3, birthYear);
                int num = statement1.executeUpdate();
            }
            System.out.println("addStar success");
        }catch(SQLException | NamingException throwables){
                throwables.printStackTrace();

            }


                out.write(newId);
                out.close();
    }

}
