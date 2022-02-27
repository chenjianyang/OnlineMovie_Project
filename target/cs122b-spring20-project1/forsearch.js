let searchForm = $("#searchForm");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    //let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");

    window.location.replace("index.html?mode=1&sort=1&num=20");


}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitsearchForm(formSubmitEvent) {
    console.log("submit searchForm");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: searchForm.serialize(),
            success: handleLoginResult
        }
    );




}

// Bind the submit action of the form to a handler function
searchForm.submit(submitsearchForm);





