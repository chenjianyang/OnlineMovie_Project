let addStar = $("#addStar");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(data) {
    if(data){

        alert("new generated id is  "+data);

    }else{
        alert("add star fail");
    }
    window.location.replace("employee.html");
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addStar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addStar.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
addStar.submit(submitLoginForm);











