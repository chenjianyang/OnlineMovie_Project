import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
@WebServlet(name = "SearchPage", urlPatterns = "/api/forSearch")
public class Forsearch extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);
        System.out.println("in the forsearch page");

        try {


            session.setAttribute("title", null);
            session.setAttribute("year", null);
            session.setAttribute("director", null);
            session.setAttribute("star", null);

            session.setAttribute("mode", null);
            session.setAttribute("browse", null);
            session.setAttribute("sort", null);
            session.setAttribute("pageNumber", 1);
            session.setAttribute("num", null);
            session.setAttribute("TotalPage", null);
            session.setAttribute("query", null);
            session.setAttribute("check", 1);

            // Create a new connection to database
            //Connection dbCon = dataSource.getConnection();




            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            System.out.println(title);
            String [] tokens=title.split(" ");
            String newString="";
            for (String s :tokens)
            {
                newString+="+"+s+"* ";
            }
            System.out.println(newString);


            if(title!=null&& title.compareTo("")!= 0)
            {
                session.setAttribute("title", newString);

            }
            if(year!=null&&year.compareTo("")!= 0)
            {
                session.setAttribute("year", year);

            }
            if(director!=null&&  director.compareTo("")!= 0)
            {
                session.setAttribute("director", director);

            }
            if(star!=null&& star.compareTo("")!= 0)
            {
                session.setAttribute("star", star);

            }
            System.out.println("checkpoint forsearch");
//
//            //request.getRequestDispatcher("/index.html").forward(request,response);
//           HttpServletResponse httpResponse = (HttpServletResponse) response;
//            httpResponse.sendRedirect("index.html?mode=1&sort=1&num=25");
           // response.sendRedirect("index.html?mode=1&sort=1&num=25");
            out.write("true");

        } catch (Exception ex) {

            // Output Error Massage to html
            ex.printStackTrace();
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }

        //out.close();
    }

}
