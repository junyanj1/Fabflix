let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = 0;


// Event handler, the callback function to handle the API response
function handleSearchResult(res,status,xhr) {
    console.log(xhr.getResponseHeader("sessionID"));
    sessionid = xhr.getResponseHeader("sessionID");
    transactionid = xhr.getResponseHeader("transactionid");
    email = xhr.getResponseHeader("email");
    delay = xhr.getResponseHeader("delay");
    debugger;
    if (status === 'nocontent') {
        getReport(movieTable)
    } else {
        window.alert(res.message);
        window.location.replace("login.html");
    }
}

function getReport(f) {
    let st = "nocontent";
    let resp = null;
    let timerid = 0;

    timerid = setInterval(function () {
        $.ajax({
            method: "GET", // Declare request type
            headers: {
                "email": email,
                "sessionId":sessionid,
                "transactionid":transactionid,
            },
            url: "http://128.195.6.93:5179/api/g/report",
            // success: handleResult, // Bind event handler as a success callback
            success: function (res,status,xhr) {
                console.log(res,status)
                debugger
                st = status;
                resp = res;
                sessionid = xhr.getResponseHeader("sessionID");
                if (status !== "nocontent") {
                    clearInterval(timerid);
                    debugger;
                    f(resp);
                }
            },
            failure: function (res) {
                window.alert("Internal server error");
            }
        });
    },delay)
}

function movieTable(res) {
    let movieDom = $('.movies');
    movieDom.empty(); // Clear the previous results

    // Manually build the HTML Table with the response
    let rowHTML = "<table border=\"1\"><tr><td>Movie ID</td><td>Title</td><td>Director</td>";
    let movieList = res.movies;

    for (let i = 0; i < movieList.length; ++i) {
        rowHTML += "<tr>";
        let movieObject = movieList[i];

        rowHTML += "<td>" + movieObject["movieId"] + "</td>";
        rowHTML += "<td>" + movieObject["title"] + "</td>";
        rowHTML += "<td>" + movieObject["director"] + "</td>";
        rowHTML += "</tr>";
    }
    rowHTML += "</table>";
    debugger;
    movieDom.append(rowHTML);
    Cookies.set("email",email);
    Cookies.set("sessionid",sessionid);
}

// Overwrite the default submit behaviour of the HTML Form
$("form").submit(function (event) {
        event.preventDefault(); // Prevent the default form submit event, using ajax instead

        let title = $(".title").val() // Extract data from search input box to be the title argument
        debugger;
        // console.log("Search title: " + title);

        $.ajax({
            method: "GET", // Declare request type
            headers: {
                "email": email,
                "sessionId":sessionid,
            },
            url: "http://128.195.6.93:5179/api/g/movies/search?limit=15&offset=0&title=" + title,
            // success: handleResult, // Bind event handler as a success callback
            success: handleSearchResult
        });
    }
);
