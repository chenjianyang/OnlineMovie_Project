import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieList", urlPatterns = "/api/movie")
public class MovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
     //   System.out.println("checkpoint1 ");
        long startTime = System.currentTimeMillis();
        long JDBCtime=0;
        long query1time=0;
        long query2time=0;
        long query3time=0;




        response.setContentType("application/json"); // Response mime type

        String mode = request.getParameter("mode");
        String temp_browse = request.getParameter("browse");
        String temp_sort = request.getParameter("sort");
        String page = request.getParameter("page");
        String temp_num = request.getParameter("num");




        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);
        //String testtitle= (String) session.getAttribute("title");
       // System.out.println(testtitle);
        try {
            // Get a connection from dataSource
            long startTimeJDBC = System.currentTimeMillis();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            //Connection dbcon = d.getConnection();
         //   System.out.println("checknumber");
            // Declare our statement
            Statement statement = dbcon.createStatement();
            Statement statement2 = dbcon.createStatement();
            Statement statement3 = dbcon.createStatement();
            long endTimeJDBC = System.currentTimeMillis();
            JDBCtime=endTimeJDBC-startTimeJDBC;
            //get parameters
            ResultSet rs = null;
            ResultSet rs2 = null;
            ResultSet rs3 = null;

            JsonArray jsonArray = new JsonArray();
            Integer check = (Integer)session.getAttribute("check");
            if(check == null)
            {
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
            }

            // init total page count
            session.setAttribute("TotalPage", null);

            //save parameter to global
            //detect mode
            String temp_Mode = (String)session.getAttribute("mode");
           // System.out.println("movie checkpoint1");
            //a new action
            if(!mode.equals("3"))
                session.setAttribute("mode", mode);
            if (mode.equals("3"))
                mode = temp_Mode;

            if(!temp_sort.equals("null"))
                session.setAttribute("sort", temp_sort);
            if(!temp_num.equals("null"))
                session.setAttribute("num", temp_num);
            if(temp_browse!=null)
                session.setAttribute("browse", temp_browse);
            //if(!temp_browse.equals("null"))
            //assign value
            String sort = (String)session.getAttribute("sort");
            String num1 = (String)session.getAttribute("num");
            String num=num1.replaceAll("\"","");



            String browse = (String)session.getAttribute("browse");



            //search
            if(mode.compareTo("1") == 0)
            {

                String title = "";
                String year = "";
                String director = "";
                String star = "";

                //get search result
                title = (String)session.getAttribute("title");
                year = (String)session.getAttribute("year");
                director = (String)session.getAttribute("director");
                star = (String)session.getAttribute("star");


                session.setAttribute("browse", null);

//暂时set
                String query = null;
                //test
                if(title == null && year == null && director == null && star == null)
                {
                    query ="select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                            "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                            "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                            ")as newa \n" +
                            "group by newa.id ";
                }
                // has title but not year
                else if((title != null || director != null) && year == null)
                {
                    if(director == null) {
                        director = "";

                        if (star == null) {
                            /////////////////////////////////////////
                            // change the topmovies

                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where  " +
                                    "match(title) against ('";
                            query = query + title + "' in boolean mode) AND newa.director like '%" + director + "%'";
                            query = query + " group by newa.id ";


                        } else {
                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where " +
                                    "match(title) against('" + title + "' in boolean mode)\n" +
                                    " and newa.director like'%" + director + "%' and newa.id=stars_in_movies.movieId and stars_in_movies.starId = stars.id" +
                                    "and stars.name like '%" + star + "%'   group by newa.id ";


                        }
                    }
                    if(title == null) {
                        if (star == null) {
                            /////////////////////////////////////////
                            // change the topmovies

                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where  " +
                                    "newa.director like '%" + director + "%'";
                            query = query + " group by newa.id ";


                        } else {
                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where " +
                                    "newa.director like'%" + director + "%' and newa.id=stars_in_movies.movieId and stars_in_movies.starId = stars.id" +
                                    "and stars.name like '%" + star + "%'   group by newa.id ";


                        }
                    }

                }

                //include year
                else if(year != null)
                {
                    if(director == null) {
                        director = "";


                        //without stars
                        if (star == null) {

                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where match(title) against ('";
                            query = query + title + "' in boolean mode) AND newa.director like '%" + director + "%'" + "and year='" + year + "'";
                            query = query + " group by newa.id";


                        }
                        //with year and stars
                        else {
                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where match(title) against ('";
                            query = query + title + "' in boolean mode) AND newa.director like '%" + director + "%' AND year='" + year + "' and newa.id=stars_in_movies.movieId and stars_in_movies.starId=stars.id and stars.name like'%" + star + "%'";
                            query = query + " group by newa.id";


                        }
                    }
                    if(title == null) {
                        if (star == null) {

                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where newa.director like '%" + director + "%'" + "and year='" + year + "'";
                            query = query + " group by newa.id";


                        }
                        //with year and stars
                        else {
                            query = "select newa.price,newa.id, newa.title, newa.year, newa.director, substring_index(group_concat(distinct newa.name SEPARATOR ','), ',', 3) as genres, newa.rating\n" +
                                    "from (select movies.id,title,year,director,price,rating,genreId,genres.name from (movies  left join ratings on movies.id=movieId) left join genres_in_movies\n" +
                                    "on movies.id=genres_in_movies.movieId left join genres on genres.id=genreId\n" +
                                    ")as newa " +
                                    "where newa.director like '%" + director + "%' AND year='" + year + "' and newa.id=stars_in_movies.movieId and stars_in_movies.starId=stars.id and stars.name like'%" + star + "%'";
                            query = query + " group by newa.id";


                        }
                    }
                }

                //calculate total result and total page number
                String resultCount = "select count(*) as count from (" + query + ") as t";
                long query1times = System.currentTimeMillis();
                rs3 = statement3.executeQuery(resultCount);
                long query1timee = System.currentTimeMillis();
                query1time = query1timee-query1times;
                String tempRN = "";
                while (rs3.next())
                {
                    tempRN = rs3.getString("count");
                }

                int num_result = Integer.valueOf(tempRN);
                int Single_Page = Integer.valueOf(num);

                int Total_page = num_result / Single_Page;
                if( (num_result % Single_Page) != 0)
                    Total_page++;

                //save it to global
                session.setAttribute("TotalPage", Total_page);


                //--------------------------------------
                if(sort.compareTo("1") == 0)
                {
                    query = query + " order by rating DESC, title DESC";
                }
                else if(sort.compareTo("2") == 0)
                {
                    query = query + " order by rating ASC, title ASC";
                }
                else if(sort.compareTo("3") == 0)
                {
                    query = query + " order by title DESC, rating DESC";
                }
                else if(sort.compareTo("4") == 0)
                {
                    query = query + " order by title ASC, rating ASC";
                }



                if(num.compareTo("10") == 0)
                {
                    query = query + " limit 10 ";
                }
                else if(num.compareTo("20") == 0 )
                {
                    query = query + " limit 20 ";
                }
                else if(num.compareTo("50") == 0)
                {
                    query = query + " limit 50 ";
                }
                else if(num.compareTo("100") == 0)
                {
                    query = query + " limit 100 ";
                }








                //edit offset
                    Integer currentPage = (Integer)session.getAttribute("pageNumber");
                    //calculate correct current page
                    //prev
                    if(page.compareTo("1") == 0)
                    {
                        currentPage = currentPage - 1;
                        if (currentPage < 1)
                            currentPage = 1;
                    }
                    //next
                    else if(page.compareTo("2") == 0)
                    {
                        currentPage = currentPage + 1;
                        if(currentPage > Total_page)
                            currentPage = Total_page;
                    }

                    //update page number
                    session.setAttribute("pageNumber", currentPage);

                    //----------------

                    int offset = 0;
                    if(currentPage == 1)
                    {

                    }
                    else if(currentPage <= Total_page)
                    {
                        offset = (currentPage - 1) * Single_Page;
                        query = query + " offset " + offset;
                    }
                    else if(currentPage > Total_page)
                    {
                        offset = (Total_page - 1) * Single_Page;
                        query = query + " offset " + offset;
                    }


                // Perform the query
              //  System.out.println("final query is: "+ query);
                rs = null;
                long query2times = System.currentTimeMillis();
                rs = statement.executeQuery(query);
                long query2timee = System.currentTimeMillis();
                query2time=query2timee-query2times;
                rs2 = null;


                // Iterate through each row of rs
                while (rs.next()) {
                    rs2 = null;
                    ArrayList list1 = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    ArrayList list3 = new ArrayList();

                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genres = rs.getString("genres");
                    String movie_rating = rs.getString("rating");
                    String price = rs.getString("price");



                    //assign string
                    String star1 = "";
                    String star2 = "";
                    String star3 = "";
                    String ID1 = "";
                    String ID2 = "";
                    String ID3 = "";
                    String gen1 = "";
                    String gen2 = "";
                    String gen3 = "";

                    //split gen
                    String tempGen = movie_genres;
                    if(tempGen!=null) {
                        String[] split = tempGen.split(",");
                        for (String tempGen2 : split) {
                            list3.add(tempGen2);
                        }
                    }
                    else
                    {
                        list3.add("null");
                    }
                        //insert gens
                        if (list3.size() == 0) {
                        } else if (list3.size() == 1) {
                            gen1 = gen1 + list3.get(0);
                        } else if (list3.size() == 2) {
                            gen1 = gen1 + list3.get(0);
                            gen2 = gen2 + list3.get(1);
                        } else if (list3.size() == 3) {
                            gen1 = gen1 + list3.get(0);
                            gen2 = gen2 + list3.get(1);
                            gen3 = gen3 + list3.get(2);
                        }


                    //All 3 stars
                    String query2 = "select count(*) as count, stars.id, stars.name from stars_in_movies, stars where stars.id = stars_in_movies.starId AND (stars.id = ANY(select stars.id from movies, stars, stars_in_movies where stars.id = stars_in_movies.starId AND stars_in_movies.movieId = movies.id AND movies.id = '";
                    query2 = query2 + movie_id + "')) group by stars.id order by count DESC,name ASC limit 3";
                    long query3times = System.currentTimeMillis();
                    rs2 = statement2.executeQuery(query2);
                    long query3timeend = System.currentTimeMillis();
                    query3time=query3timeend-query3times;
                    // insert all stars to list
                    while (rs2.next())
                    {
                        String tempN = rs2.getString("name");
                        String tempID = rs2.getString("id");
                        list1.add(tempN);
                        list2.add(tempID);
                    }
                    //rs2 = null;

                    if(list2.size() == 0)
                    {
                        star1="null";
                    }
                    else if(list2.size() == 1)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                    }
                    else if(list2.size() == 2)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        star1 = star1 + ", ";
                    }
                    else if(list2.size() == 3)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        ID3 = ID3 + list2.get(2);
                        star3 = star3 + list1.get(2);
                        star1 = star1 + ", ";
                        star2 = star2 + ", ";
                    }

                    //-------------------
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    //jsonObject.addProperty("movie_genres", movie_genres);
                    jsonObject.addProperty("gen1", gen1);
                    jsonObject.addProperty("gen2", gen2);
                    jsonObject.addProperty("gen3", gen3);
                    jsonObject.addProperty("star1", star1);
                    jsonObject.addProperty("star2", star2);
                    jsonObject.addProperty("star3", star3);
                    jsonObject.addProperty("ID1", ID1);
                    jsonObject.addProperty("ID2", ID2);
                    jsonObject.addProperty("ID3", ID3);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("price", price);
                    jsonObject.addProperty("quantity", 1);
                    jsonArray.add(jsonObject);
                }
            }
            //browse

            if(mode.compareTo("2") == 0)
            {

                String query = null;
                //if a-z

                if(browse.length() == 1)
                {

                    query = "select * from (select movies.price,movies.id, movies.title, movies.year, movies.director, substring_index(group_concat(distinct genres.name SEPARATOR ','), ',', 3) as genres, ratings.rating \n" +
                            " from ratings, movies , genres, genres_in_movies\n" +
                            " where (movies.id = ratings.movieId) AND (ratings.movieId = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) \n" +
                            " group by movies.id) as newa where title like '" + browse + "%'";

                }
                //if *
                else if(browse.compareTo("test") == 0)
                {
                    query = "select * from (select movies.price,movies.id, movies.title, movies.year, movies.director, substring_index(group_concat(distinct genres.name SEPARATOR ','), ',', 3) as genres, ratings.rating \n" +
                            " from ratings, movies , genres, genres_in_movies\n" +
                            " where (movies.id = ratings.movieId) AND (ratings.movieId = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) \n" +
                            " group by movies.id) as newa where title regexp '^[^a-z0-9]'";
                }
                // by genres

                else if(browse.compareTo("Music") == 0)
                {
                    query="(select * from (select movies.price,movies.id, movies.title, movies.year, movies.director, substring_index(group_concat(distinct genres.name SEPARATOR ','), ',', 3) as genres, ratings.rating \n" +
                            " from ratings, movies , genres, genres_in_movies\n" +
                            " where (movies.id = ratings.movieId) AND (ratings.movieId = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) \n" +
                            " group by movies.id) as newa where newa.genres like '%music%' and newa.genres not like '%musical%') UNION ALL (select * from newa where newa.genres like '%music,musical%')";
                }
                else
                {

                    query = "select * from (select movies.price,movies.id, movies.title, movies.year, movies.director, substring_index(group_concat(distinct genres.name SEPARATOR ','), ',', 3) as genres, ratings.rating \n" +
                            " from ratings, movies , genres, genres_in_movies\n" +
                            " where (movies.id = ratings.movieId) AND (ratings.movieId = genres_in_movies.movieId) AND (genres_in_movies.genreId = genres.id) \n" +
                            " group by movies.id) as newa where newa.genres like '%" + browse + "%'";
                }

                //calculate total result and total page number
                String resultCount = "select count(*) as count from (" + query + ") as t";

                rs3 = statement3.executeQuery(resultCount);

                String tempRN = "";
                while (rs3.next())
                {
                    tempRN = rs3.getString("count");
                }


                int num_result = Integer.valueOf(tempRN);
              //  System.out.println("checkpoint11 ");
                int Single_Page = Integer.valueOf(num);

                int Total_page = num_result / Single_Page;
                if( (num_result % Single_Page) != 0)
                    Total_page++;
             //   System.out.println("checkpoint2");



                //--------------------------------------
                if(sort.compareTo("1") == 0)
                {
                    query = query + " order by newa.rating DESC, newa.title DESC";
                }
                else if(sort.compareTo("2") == 0)
                {
                    query = query + " order by newa.rating ASC, newa.title ASC";
                }
                else if(sort.compareTo("3") == 0)
                {
                    query = query + " order by newa.title DESC, newa.rating DESC";
                }
                else if(sort.compareTo("4") == 0)
                {
                    query = query + " order by newa.title ASC, newa.rating ASC";
                }

                if(num.compareTo("10") == 0)
                {
                    query = query + " limit 10";
                }
                else if(num.compareTo("20") == 0)
                {
                    query = query + " limit 20";
                }
                else if(num.compareTo("50") == 0)
                {
                    query = query + " limit 50";
                }
                else if(num.compareTo("100") == 0)
                {
                    query = query + " limit 100";
                }

                //edit offset
                Integer currentPage = (Integer)session.getAttribute("pageNumber");
                //calculate correct current page
                //prev
                if(page.compareTo("1") == 0)
                {
                    currentPage = currentPage - 1;
                    if (currentPage < 1)
                        currentPage = 1;
                }
                //next
                else if(page.compareTo("2") == 0)
                {
                    currentPage = currentPage + 1;
                    if(currentPage > Total_page)
                        currentPage = Total_page;
                }

                //update page number
                session.setAttribute("pageNumber", currentPage);

                //----------------
                int offset = 0;
                if(currentPage == 1)
                {

                }
                else if(currentPage <= Total_page)
                {
                    offset = (currentPage - 1) * Single_Page;
                    query = query + " offset " + offset;
                }
                else if(currentPage > Total_page)
                {
                    offset = (Total_page - 1) * Single_Page;
                    query = query + " offset " + offset;
                }


                //System.out.println(query);
                rs = statement.executeQuery(query);
                rs2 = null;

                while (rs.next())
                {
                    rs2 = null;
                    ArrayList list1 = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    ArrayList list3 = new ArrayList();

                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genres = rs.getString("genres");
                    String movie_rating = rs.getString("rating");
                    String price = rs.getString("price");



                    //assign string
                    String star1 = "";
                    String star2 = "";
                    String star3 = "";
                    String ID1 = "";
                    String ID2 = "";
                    String ID3 = "";
                    String gen1 = "";
                    String gen2 = "";
                    String gen3 = "";

                    //split gen
                    String tempGen = movie_genres.substring(0, movie_genres.length());
                    String[] split = tempGen.split(",");
                    for (String tempGen2 : split) {
                        list3.add(tempGen2);
                    }

                    //insert gens
                    if(list3.size() == 0)
                    {
                    }
                    else if(list3.size() == 1)
                    {
                        gen1 = gen1 + list3.get(0);
                    }
                    else if(list3.size() == 2)
                    {
                        gen1 = gen1 + list3.get(0);
                        gen2 = gen2 + list3.get(1);
                    }
                    else if(list3.size() == 3)
                    {
                        gen1 = gen1 + list3.get(0);
                        gen2 = gen2 + list3.get(1);
                        gen3 = gen3 + list3.get(2);
                    }

                    //All 3 stars
                    String query2 = "select count(*) as count, stars.id, stars.name from stars_in_movies, stars where stars.id = stars_in_movies.starId AND (stars.id = ANY(select stars.id from movies, stars, stars_in_movies where stars.id = stars_in_movies.starId AND stars_in_movies.movieId = movies.id AND movies.id = '";
                    query2 = query2 + movie_id + "')) group by stars.id order by count DESC,name ASC limit 3";

                    rs2 = statement2.executeQuery(query2);

                    // insert all stars to list
                    while (rs2.next())
                    {
                        String tempN = rs2.getString("name");
                        String tempID = rs2.getString("id");
                        list1.add(tempN);
                        list2.add(tempID);
                    }
                    //rs2 = null;

                    if(list2.size() == 0)
                    {
                    }
                    else if(list2.size() == 1)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                    }
                    else if(list2.size() == 2)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        star1 = star1 + ", ";
                    }
                    else if(list2.size() == 3)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        ID3 = ID3 + list2.get(2);
                        star3 = star3 + list1.get(2);
                        star1 = star1 + ", ";
                        star2 = star2 + ", ";
                    }

                    //-------------------
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    //jsonObject.addProperty("movie_genres", movie_genres);
                    jsonObject.addProperty("gen1", gen1);
                    jsonObject.addProperty("gen2", gen2);
                    jsonObject.addProperty("gen3", gen3);
                    jsonObject.addProperty("star1", star1);
                    jsonObject.addProperty("star2", star2);
                    jsonObject.addProperty("star3", star3);
                    jsonObject.addProperty("ID1", ID1);
                    jsonObject.addProperty("ID2", ID2);
                    jsonObject.addProperty("ID3", ID3);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("price",Integer.valueOf(price));
                    jsonObject.addProperty("quantity",1);
                    jsonArray.add(jsonObject);
                }


            }


            session.setAttribute("movieList",jsonArray);
            // write JSON string to output
          //  System.out.println("print result");
          //  System.out.println(jsonArray.toString());
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);


            dbcon.close();



            //response.setHeader("refresh", "3");
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
            e.printStackTrace();
        }
        //out.close();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime; // elapsed time in nano seconds. Note: print the values in nano seconds
        long TStime=JDBCtime+query1time+query2time+query3time;
        String timefortj=String.valueOf(TStime);
        File file1 =new File("/home/ubuntu/case1tj.txt");
        Writer outFile1 =new FileWriter(file1,true);
        outFile1.write(timefortj);
        outFile1.write("\n");
        outFile1.close();
        System.out.println("print time"+elapsedTime);
      //  File file =new File("D:/githubproject/project5/time.txt");
       // FileWriter fileWritter = new FileWriter(file.getName(),true);
        String time=String.valueOf(elapsedTime);
       //fileWritter.write(time);
       // fileWritter.close();

        File file =new File("/home/ubuntu/case1ts.txt");
        Writer outFile =new FileWriter(file,true);
        outFile.write(time);
        outFile.write("\n");
        outFile.close();


    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
       // System.out.println("post");
        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);
        //System.out.println("in the forsearch page");
        JsonObject responseJsonObject = new JsonObject();
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
           // System.out.println(title);
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
          //  System.out.println(title);
            String [] tokens=title.split(" ");
            String newString="";
            for (String s :tokens)
            {
                newString+="+"+s+"* ";
            }
           // System.out.println(newString);


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
         //   System.out.println("checkpoint forsearch");
//
//            //request.getRequestDispatcher("/index.html").forward(request,response);
//           HttpServletResponse httpResponse = (HttpServletResponse) response;
//            httpResponse.sendRedirect("index.html?mode=1&sort=1&num=25");
            // response.sendRedirect("index.html?mode=1&sort=1&num=25");
            responseJsonObject.addProperty("status", "success");
            out.write(responseJsonObject.toString());

        } catch (Exception ex) {

            // Output Error Massage to html
            ex.printStackTrace();
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }

        //out.close();
    }


}