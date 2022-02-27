import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called SearchPage, which maps to url "/form"
@WebServlet(name = "SearchPage", urlPatterns = "/api/employee")
public class employee extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);


        try {




            // Create a new connection to database
            //Connection dbCon = dataSource.getConnection();






//
//            //request.getRequestDispatcher("/index.html").forward(request,response);
        HttpServletResponse httpResponse = (HttpServletResponse) response;
           httpResponse.sendRedirect("loginE.html");
           // response.sendRedirect("index.html?mode=1&sort=1&num=25");


        } catch (Exception ex) {

            // Output Error Massage to html
            ex.printStackTrace();
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }

        //out.close();
    }

}
