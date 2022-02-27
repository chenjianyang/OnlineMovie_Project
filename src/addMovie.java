

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @author jianyang
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-24
 */

@WebServlet(name = "addMovie",urlPatterns = "/api/addMovie")
public class addMovie extends HttpServlet {


    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        System.out.println("add a Movie");
        String starName = request.getParameter("starname");
        String moviename = request.getParameter("moviename");
        String Genres=request.getParameter("Genres");
        String Director=request.getParameter("Director");
        String Year=request.getParameter("Year");


        String message="";

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedbmaster");
           // Connection dbcon = ds.getConnection();
            Connection moviedb = ds.getConnection();
        // get starsId
            ////////////////////////////


            int movieyear=Integer.valueOf(Year);



                String lastQuery = "call addMovie2(?,?,?,?,?)";
                PreparedStatement lastStatement = moviedb.prepareStatement(lastQuery);
            lastStatement.setString(1, moviename);
            lastStatement.setString(2, Genres);
            lastStatement.setString(3, starName);

            lastStatement.setString(4, Director);
            lastStatement.setInt(5, movieyear);


                ResultSet result = lastStatement.executeQuery();

                while (result.next())
                {
                    message=result.getString("message");
                }

            System.out.println("addMovie success");
        }catch(SQLException | NamingException throwables){
                throwables.printStackTrace();

            }

        System.out.println(message);
                out.write(message);
                out.close();
    }

}
