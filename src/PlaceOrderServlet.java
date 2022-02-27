import com.google.gson.JsonObject;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author jianyang chen
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-26
 */

@WebServlet(name = "PlaceOrderServlet",urlPatterns = "/api/PlaceOrder")
public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String CreditCard = request.getParameter("CreditCard");
        String Expiration = request.getParameter("Expiration");
        String FirstName = request.getParameter("FirstName");
        String LastName = request.getParameter("LastName");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        JsonObject responseJsonObject = new JsonObject();

        /*try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {

            responseJsonObject.addProperty("message", "recaptcha verification failed: gRecaptchaResponse is null or empty");
            out.write(responseJsonObject.toString());
            out.close();
            return;
        }*/

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedbmaster");
            Connection dbcon = ds.getConnection();
          //  Connection dbcon = dataSource.getConnection();


            if (CreditCard.equals("") || Expiration.equals("")||FirstName.equals("")||LastName.equals("")) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "any input can not be empty");
            } else {
                String query = "select id from creditcards as c where c.id= ? and c.expiration=?";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, CreditCard);
                statement.setString(2, Expiration);
                ResultSet result = statement.executeQuery();
                if (result.next()) //which means the username exist and the password match
                {


                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect CreditCard or incorrect Expiration");
                }
            }
            out.write(responseJsonObject.toString());
        } catch (SQLException | NamingException throwables) {
            throwables.printStackTrace();
        }
        out.close();
    }

}

