let movieid = Cookies.get("movieid");
let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = Cookies.get("delay");


$.ajax({
    method: "GET", // Declare request type
    headers: {
        "email": email,
        "sessionId":sessionid,
    },
    url: "http://128.195.6.93:5179/api/g/movies/get/"+movieid,
    // success: handleResult, // Bind event handler as a success callback
    success: getReport2,
});

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
    rowHTML += "</tr>";
    rowHTML += "</table>";
    debugger;
    movieDom.append(rowHTML);
    Cookies.set("email",email);
    Cookies.set("sessionid",sessionid);
}

function handleRateResult(res,status,xhr) {
    console.log(xhr.getResponseHeader("sessionID"));
    sessionid = xhr.getResponseHeader("sessionID");
    transactionid = xhr.getResponseHeader("transactionid");
    email = xhr.getResponseHeader("email");
    delay = xhr.getResponseHeader("delay");
    debugger;
    if (status === 'nocontent') {
        getReport()
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
                    window.alert(resp.message);
                }
            },
            failure: function (res) {
                window.alert("Internal server error");
            }
        });
    },delay)
}

$("#ratings").submit(function (event) {
    event.preventDefault();
    let rating = $(".rate").val();
    debugger;
    $.ajax({
        method: "POST", // Declare request type
        headers: {
            "email": email,
            "sessionId":sessionid,
        },
        contentType:"application/json",
        dataType:"json",
        data: JSON.stringify({"id":movieid,"rating":rating}),
        url: "http://128.195.6.93:5179/api/g/movies/rating",
        success: handleRateResult
    });
});

$("#ShoppingCart").submit(function (event) {
    event.preventDefault();
    let quantity = $(".quantity").val();
    debugger;
    $.ajax({
        method: "POST", // Declare request type
        headers: {
            "email": email,
            "sessionId":sessionid,
        },
        contentType:"application/json",
        dataType:"json",
        data: JSON.stringify({
            "email":email,
            "movieId":movieid,
            "quantity":quantity,
        }),
        url: "http://128.195.6.93:5179/api/g/billing/cart/insert",
        success: function (res,status,xhr) {
            // console.log(xhr.getResponseHeader("sessionID"));
            sessionid = xhr.getResponseHeader("sessionID");
            transactionid = xhr.getResponseHeader("transactionid");
            email = xhr.getResponseHeader("email");
            delay = xhr.getResponseHeader("delay");
            debugger;
            if (status === 'nocontent') {
                getReport()
            } else {
                window.alert(res.message);
                window.location.replace("login.html");
            }
        }
    });
});
