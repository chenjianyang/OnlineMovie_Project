import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called SearchPage, which maps to url "/form"
@WebServlet(name = "SearchPage", urlPatterns = "/api/newSearch")
public class SearchPage extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
   // @Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("in the search page server");
        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);


        try {

            // Create a new connection to database
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbCon = ds.getConnection();
            System.out.println("check1");
            // Declare a new statement
            Statement statement = dbCon.createStatement();
            JsonArray jsonArray = new JsonArray();
            String query="select name as gname from genres ";
            ResultSet result = statement.executeQuery(query);
            while(result.next())
            {
                 String movie_genres=result.getString("gname");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonArray.add(jsonObject);
            }
            System.out.println("check2");
            System.out.println(jsonArray.toString());
            out.write(jsonArray.toString());






            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html



//
//            //request.getRequestDispatcher("/index.html").forward(request,response);
            //response.sendRedirect("index.html?mode=1&sort=1&num=25");


        } catch (Exception ex) {

            // Output Error Massage to html
            ex.printStackTrace();
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }

        //out.close();
    }

}
