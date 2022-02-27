import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Resource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

// server endpoint URL
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/*
	 * populate the Super hero hash map.
	 * Key is hero ID. Value is hero name.
	 */


    public MovieSuggestion() {
        super();
    }

    /*
     * 
     * Match the query against superheroes and return a JSON response.
     * 
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     * 
     * The format is like this because it can be directly used by the 
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *   
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     * 
     * 
     */
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			// setup the response json arrray
			System.out.println("autocomplete");
			JsonArray jsonArray = new JsonArray();
			
			// get the query string from parameter
			String aQuery = request.getParameter("query");
			System.out.println(aQuery);

			
			// return the empty json array if query is null or empty
			if (aQuery == null || aQuery.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
			Connection moviedb = ds.getConnection();

			String [] tokens=aQuery.split(" ");
			String newString="";
			for (String s :tokens)
			{
				newString+="+"+s+"* ";
			}
			System.out.println(newString);
			String newQuery="select title,id from movies where match(title) against (? in boolean mode)";
			PreparedStatement statement = moviedb.prepareStatement(newQuery);
			statement.setString(1,newString);

			ResultSet result = statement.executeQuery();
			int counter=0;
			while (result.next())
			{
				String title=result.getString("title");
				String movieId=result.getString("id");
				jsonArray.add(generateJsonObject(movieId,title));

				counter++;
				if(counter==10)
				{
					break;
				}

			}
			String newQuery2="select name,id from stars where match(name) against (? in boolean mode)";
			PreparedStatement statement2 = moviedb.prepareStatement(newQuery2);
			statement2.setString(1,newString);
			ResultSet result2 = statement2.executeQuery();
			int counter2=0;
			while(result2.next())
			{
				if(counter2==10-counter)
				{
					break;
				}
				String starName=result2.getString("name");
				String starId=result2.getString("id");
				jsonArray.add(generateJsonObject(starId,starName));


				counter2++;

			}



			// search on superheroes and add the results to JSON Array
			// this example only does a substring match
			// TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

			
			response.getWriter().write(jsonArray.toString());
			return;
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
	
	/*
	 * Generate the JSON Object from hero to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "heroID": 11 }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String movieId, String title) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", title);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("movieId", movieId);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}



}
