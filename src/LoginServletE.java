import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginEServlet", urlPatterns = "/api/loginE")
public class LoginServletE extends HttpServlet {


    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("do post11");
        JsonObject responseJsonObject = new JsonObject();

        PrintWriter out = response.getWriter();
        String username = null;
        String password = null;
        String FinalUsername = null;
        String encryptedPassword = null;
      /*  String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {

            responseJsonObject.addProperty("message", "recaptcha verification failed: gRecaptchaResponse is null or empty");
            out.write(responseJsonObject.toString());
            out.close();
            return;
        }*/

        response.setContentType("text/html"); // Response mime type
        try
        {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();
            Statement statement = dbcon.createStatement();
            Statement statement2 = dbcon.createStatement();
            ResultSet rs = null;
            ResultSet rs2 = null;
            boolean success = false;
            username = request.getParameter("username");
            password = request.getParameter("password");

            if(username.equals("") || password.equals(""))
            {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Please enter your Username/Password");
            }
            else
            {
                String query1 = "select employees.* from employees where email = '" + username + "'";
                //String query2 = "select customers.* from customers where email = '" + username + "' AND password = '" + password + "'";

                //check username
                rs = statement.executeQuery(query1);
                if (rs.next())
                {
                    FinalUsername = rs.getString("email");
                }

                if(FinalUsername == null)
                {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exists");
                }
                //username is valid
                else
                {
                    //check password

                        encryptedPassword = rs.getString("password");
                        success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);


                    if(success == false)
                    {
                        // Login fail
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "Your password is incorrect");
                    }
                    //success!
                    else
                    {
                        // Login success:
                        // set this user into the session
                        request.getSession().setAttribute("newUser", new User(username));

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");
                    }

                }
            }



            rs.close();
            rs2.close();
            statement.close();
            statement2.close();
            dbcon.close();
        } catch (Exception e) {

        }







        out.write(responseJsonObject.toString());
        out.close();
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */







    }
}
