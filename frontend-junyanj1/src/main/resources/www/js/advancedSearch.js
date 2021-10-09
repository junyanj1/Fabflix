let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = 0;
let title = null;
let genre = null;
let year = 0;
let director = null;

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

function getReport() {
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
                        $.ajax({
                            method: "GET", // Declare request type
                            headers: {
                                "email": email,
                                "sessionId":sessionid,
                            },
                            url: "http://128.195.6.93:5179/api/g/movies/get/"+res.movies[0].movieId,
                            // success: handleResult, // Bind event handler as a success callback
                            success: getReport2,
                        });
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

function getReport2(res,status,xhr) {
    transactionid = xhr.getResponseHeader("transactionid");
    debugger;
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
                        movieTable(resp);
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

function movieTable(res) {
    debugger;
    let movieDom = $('.movie');
    movieDom.empty(); // Clear the previous results
    // Manually build the HTML Table with the response
    result = res.movie;
    let rowHTML = "<table border=\"1\"><tr><th>Movie ID</th><td>"+result.movieId+"</td></tr>";
    rowHTML += "<tr><th>Title</th><td>"+result.title+"</td></tr>";
    rowHTML += "<tr><th>Director</th><td>"+result.director+"</td></tr>";
    rowHTML += "<tr><th>Year</th><td>"+result.year+"</td></tr>";
    rowHTML += "<tr><th>Budget</th><td>"+result.budget+"</td></tr>";
    rowHTML += "<tr><th>Overview</th><td>"+result.overview+"</td></tr>";
    rowHTML += "<tr><th>Poster</th><td><img src='https://image.tmdb.org/t/p/original"+result["poster_path"]+"'></td></tr>";
    rowHTML += "<tr><th>Revenue</th><td>"+result.revenue+"</td></tr>";
    rowHTML += "<tr><th>Rating</th><td>"+result.rating+"</td></tr>";
    rowHTML += "<tr><th>NumVotes</th><td>"+result.numVotes+"</td></tr>";
    // rowHTML += "<tr rowspan='"+result.genres.length+"'><th>Genres</th><td>"+result.genres+"</td></tr>";
    // rowHTML += "<tr rowspan='"+result.stars.length+"'><th>Stars</th><td>"+result.stars+"</td></tr>";
    rowHTML += "<tr rowspan='" + result.genres.length+"'><th>Genres</th>"
    for (let i=0;i<result.genres.length;i++) {
        rowHTML += "<td>"+result.genres[i].name+"</td>";
    }
    rowHTML += "</tr>";

    rowHTML += "<tr rowspan='" + result.stars.length+"'><th>Stars</th>"
    for (let i=0;i<result.stars.length;i++) {
        rowHTML += "<td>"+result.stars[i].name+"</td>";
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