let addMovie = $("#addMovie");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleAddMovie(newId) {

    if(newId){

        alert(newId);
        alert("add movie success")
        //alert("new generated id is  "+data);

    }else{
        alert("add movie fail");
    }
    // window.location.replace("employee.html");
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddMovie(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addMovie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addMovie.serialize(),
            success: handleAddMovie
        }
    );
}

// Bind the submit action of the form to a handler function
addMovie.submit(submitAddMovie);