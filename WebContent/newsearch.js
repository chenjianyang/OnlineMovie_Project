/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleListResult(resultData) {

    console.log("handleListResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");


    for (let i = 0; i < Math.max(10, resultData.length); i=i+3) {
        var g=resultData[i]['movie_genres'];
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"
       //     + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>'+ "</th>";
   + '<a href=index.html?mode=2&browse=' + resultData[i]['movie_genres'] +'&sort=1&num=20'+'">'+ g + '</a>'+ "</th>";
        rowHTML += "<th>"
            //     + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>'+ "</th>";
            + '<a href=index.html?mode=2&browse=' + resultData[i+1]['movie_genres'] +'&sort=1&num=20'+'">'+ resultData[i+1]['movie_genres'] + '</a>'+ "</th>";
        rowHTML += "<th>"
            //     + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>'+ "</th>";
            + '<a href=index.html?mode=2&browse=' + resultData[i+2]['movie_genres'] +'&sort=1&num=20'+'">'+ resultData[i+2]['movie_genres'] + '</a>'+ "</th>";
       // rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/Showsearch", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});