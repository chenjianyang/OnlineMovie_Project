

import com.google.gson.JsonArray;
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
import java.sql.*;
import java.util.ArrayList;

/**
 * @author jianyang
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-24
 */

@WebServlet(name = "showMeta",urlPatterns = "/api/showMeta")
public class showmeta extends HttpServlet {


   // @Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;
    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection moviedb = ds.getConnection();
        // get starsId
            ////////////////////////////
            DatabaseMetaData metaData =  moviedb.getMetaData();
            //Retrieving the columns in the database
            ArrayList<String> tableNames = new ArrayList<>();
            String [] types = {"table"};
            ResultSet tables = metaData.getTables(null,null,"%",types);
            while (tables.next()){
                String tableName = tables.getString("TABLE_NAME");
                //String typeName = tables.getString("TYPE_NAME");
                System.out.println(tableName);
               tableNames.add(tableName);

            }
            //JsonArray tableArray =new JsonArray();
            JsonObject tableJson = new JsonObject();
            for (String tableName:tableNames) {
                JsonArray jsonArray =new JsonArray();
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                //Printing the column name and size
                while (columns.next()){
                    String colName = columns.getString("COLUMN_NAME");
                    String typeName = columns.getString("TYPE_NAME");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(colName,typeName);
                    jsonArray.add(jsonObject);
                }
               tableJson.add(tableName,jsonArray);
            }

            System.out.println(tableJson.toString());
            out.write(tableJson.toString());
            }catch(SQLException | NamingException throwables){
                throwables.printStackTrace();
            }
    }

    public void  doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request,response);
    }

}
