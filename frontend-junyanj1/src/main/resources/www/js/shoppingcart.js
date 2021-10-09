let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = 0;

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
    }),
    url: "http://128.195.6.93:5179/api/g/billing/cart/retrieve",
    // success: handleResult, // Bind event handler as a success callback
    success: function (res,status,xhr) {
        // console.log(xhr.getResponseHeader("sessionID"));
        debugger;
        if (status === 'nocontent') {
            sessionid = xhr.getResponseHeader("sessionID");
            transactionid = xhr.getResponseHeader("transactionid");
            email = xhr.getResponseHeader("email");
            delay = xhr.getResponseHeader("delay");
            getReport()
        } else {
            window.alert(res.message);
            window.location.replace("login.html");
        }
    }
});

function getReport(res,status,xhr) {
    // transactionid = xhr.getResponseHeader("transactionid");
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
                sessionid = xhr.getResponseHeader("sessionID");
                if (status !== "nocontent") {
                    clearInterval(timerid);
                    debugger;
                    if (res.resultCode == 3130)
                        cartTable(res);
                    else {
                        window.alert(res.message);
                        Cookies.set("email",email);
                        Cookies.set("sessionid",sessionid);
                    }
                }
            },
            failure: function (res) {
                window.alert("Internal server error");
            }
        });
    },delay)
}

function getReport2() {
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
                    Cookies.set("email",email);
                    Cookies.set("sessionid",sessionid);
                    window.location.replace("shoppingcart.html");
                }
            },
            failure: function (res) {
                window.alert("Internal server error");
            }
        });
    },delay)
}

function deleteItem(id) {
    let deleteid = id;

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
            "movieId":deleteid,
        }),
        url: "http://128.195.6.93:5179/api/g/billing/cart/delete",
        // success: handleResult, // Bind event handler as a success callback
        success: function (res,status,xhr) {
            // console.log(xhr.getResponseHeader("sessionID"));
            sessionid = xhr.getResponseHeader("sessionID");
            transactionid = xhr.getResponseHeader("transactionid");
            email = xhr.getResponseHeader("email");
            delay = xhr.getResponseHeader("delay");
            debugger;
            if (status === 'nocontent') {
                getReport2()
            } else {
                window.alert(res.message);
                window.location.replace("login.html");
            }
        }
    });
}


function updateItem(id) {
    let updateid = id;
    let q = document.getElementById("q").value;

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
            "movieId":updateid,
            "quantity":q,
        }),
        url: "http://128.195.6.93:5179/api/g/billing/cart/update",
        // success: handleResult, // Bind event handler as a success callback
        success: function (res,status,xhr) {
            // console.log(xhr.getResponseHeader("sessionID"));
            sessionid = xhr.getResponseHeader("sessionID");
            transactionid = xhr.getResponseHeader("transactionid");
            email = xhr.getResponseHeader("email");
            delay = xhr.getResponseHeader("delay");
            debugger;
            if (status === 'nocontent') {
                getReport2()
            } else {
                window.alert(res.message);
                window.location.replace("login.html");
            }
        }
    });
}

function deleteAll() {
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
        }),
        url: "http://128.195.6.93:5179/api/g/billing/cart/clear",
        // success: handleResult, // Bind event handler as a success callback
        success: function (res,status,xhr) {
            // console.log(xhr.getResponseHeader("sessionID"));
            sessionid = xhr.getResponseHeader("sessionID");
            transactionid = xhr.getResponseHeader("transactionid");
            email = xhr.getResponseHeader("email");
            delay = xhr.getResponseHeader("delay");
            debugger;
            if (status === 'nocontent') {
                getReport2()
            } else {
                window.alert(res.message);
                window.location.replace("login.html");
            }
        }
    });
}

function toCheckOut() {
    Cookies.set("email",email);
    Cookies.set("sessionid",sessionid);
    window.location.replace("checkout.html");
}

function cartTable(res) {
    debugger;
    let itemsDom = $('.items');
    itemsDom.empty();

    let rowHTML = "<table border=\"1\"><tr><th>Movie ID</th><th>Quantity</th><th>Update?</th><th>Delete?</th>";
    result = res.items;
    for (let i=0; i<result.length;++i) {
        rowHTML += "<tr>";
        let item = result[i];

        rowHTML += "<td>"+item["movieId"]+"</td>";
        rowHTML += "<td><form><input id='q' type='number' value='"+item["quantity"]+"' min='1'></form>";
        rowHTML += "<td><form><button type='button' onclick='updateItem(\""+item["movieId"]+"\")'>Update</button></form></td>";
        rowHTML += "<td><form><button type='button' onclick='deleteItem(\""+item["movieId"]+"\")'>Delete</button></form></td>";
    }
    rowHTML += "</table>";
    itemsDom.append(rowHTML);
    Cookies.set("email",email);
    Cookies.set("sessionid",sessionid);
}
