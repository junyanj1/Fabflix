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

    url: "http://128.195.6.93:5179/api/g/movies/genre",
    // success: handleResult, // Bind event handler as a success callback
    success: handleSearchResult
});

function handleSearchResult(res,status,xhr) {
    // console.log(xhr.getResponseHeader("sessionID"));
    sessionid = xhr.getResponseHeader("sessionID");
    transactionid = xhr.getResponseHeader("transactionid");
    email = xhr.getResponseHeader("email");
    delay = xhr.getResponseHeader("delay");
    debugger;
    if (status === 'nocontent') {
        getReport();
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
                    if (res.resultCode == 219)
                        createMenu(resp);
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

function createMenu(res) {
    let genreDom = $('.genres');
    genreDom.empty();

    let genreList = res.genres;
    let dropdown = "<form> <p>List of Genres</p> <br> <select id='genreList'>";
    for (let i=0; i<genreList.length; ++i) {
        let item = genreList[i]["name"];
        dropdown += "<option value=\""+item+"\">"+item+"</option>";
    }
    dropdown += "</select><br><br><input type='submit'></form>";
    debugger;
    genreDom.append(dropdown);
    Cookies.set("email",email);
    Cookies.set("sessionid",sessionid);
}
//
// $("form").submit(function (event) {
//     event.preventDefault();
//     let selected = $("#genreList").val();
//
//     Cookies.set("movieid",id);
//     Cookies.set("sessionid",sessionid);
//     Cookies.set("delay",delay);
//     let son = window.location.replace("movieListPage.html");
//     son.document.getElementById("genre").value = selected;
// });

$(function() {
    $(document).on('submit', 'form', function(event) {
        event.preventDefault();
        let selected = $("#genreList").val();

        Cookies.set("sessionid",sessionid);
        Cookies.set("delay",delay);
        Cookies.set("genre",selected);
        window.location.replace("movieListPage.html");
    });
});
