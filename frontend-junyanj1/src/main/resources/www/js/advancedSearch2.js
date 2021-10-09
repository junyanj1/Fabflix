let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = 0;
let limit = 0;
let title = null;
let genre = null;
let year = 0;
let director = null;
let offset = 0;
let orderby = null;
let direction = null;
let page = 0;

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
                console.log(res,status);
                debugger;
                st = status;
                resp = res;
                sessionid = xhr.getResponseHeader("sessionID");
                if (status !== "nocontent") {
                    clearInterval(timerid);
                    debugger;
                    if (res.resultCode == 210)
                        f(resp);
                    else
                        window.alert(resp.message);
                }
            },
            failure: function (res) {
                window.alert("Internal server error");
            }
        });
    },delay)
}

function details(id) {
    Cookies.set("movieid",id);
    Cookies.set("sessionid",sessionid);
    Cookies.set("delay",delay);
    window.location.replace("movieDetails.html");
}

function movieTable(res) {
    let movieDom = $('.movie');
    movieDom.empty(); // Clear the previous results

    // Manually build the HTML Table with the response
    let rowHTML = "<table border=\"1\"><tr><td>Movie ID</td><td>Title</td><td>Director</td><td>Year</td><td>Rating</td><td>NumVotes</td>";
    let movieList = res.movies;

    for (let i = 0; i < movieList.length; ++i) {
        rowHTML += "<tr>";
        let movieObject = movieList[i];

        rowHTML += "<td><form><button type='button' onclick='details(\""+movieObject["movieId"]+"\")'>"+movieObject["movieId"]+"</button></form></td>";
        rowHTML += "<td>" + movieObject["title"] + "</td>";
        rowHTML += "<td>" + movieObject["director"] + "</td>";
        rowHTML += "<td>" + movieObject["year"] + "</td>";
        rowHTML += "<td>" + movieObject["rating"] + "</td>";
        rowHTML += "<td>" + movieObject["numVotes"] + "</td>";
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

        title = $(".title").val();// Extract data from search input box to be the title argument
        genre = $(".genre").val();
        year = $(".year").val();
        director = $(".director").val();

        let queryP = {
            "title":title,
            "genre":genre,
            "year":year,
            "director":director,
        };

        function isEmpty(value){
            return value == null || value == "";
        }

        for(key in queryP)
            if(isEmpty(queryP[key]))
                delete queryP[key];
        debugger;
        // console.log("Search title: " + title);

        $.ajax({
            method: "GET", // Declare request type
            headers: {
                "email": email,
                "sessionId":sessionid,
            },
            data: queryP,
            url: "http://128.195.6.93:5179/api/g/movies/search",
            // success: handleResult, // Bind event handler as a success callback
            success: handleSearchResult
        });
        }
);