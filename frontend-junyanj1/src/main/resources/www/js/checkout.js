let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = 0;
let token = null;

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
        "firstName":"my First Name",
        "lastName": "my Last Name",
        "ccId":"9999888877776666123",
        "address": "1234, Campus Dr., Irvine, CA, 92697"
    }),
    url: "http://128.195.6.93:5179/api/g/billing/customer/insert",
    // success: handleResult, // Bind event handler as a success callback
    success: function (res,status,xhr) {
        // console.log(xhr.getResponseHeader("sessionID"));
        sessionid = xhr.getResponseHeader("sessionID");
        transactionid = xhr.getResponseHeader("transactionid");
        email = xhr.getResponseHeader("email");
        delay = xhr.getResponseHeader("delay");
        debugger;
        if (status === 'nocontent') {
            getReport1()
        } else {
            window.alert(res.message);
            window.location.replace("login.html");
        }
    }
});

function getReport1(res,status,xhr) {
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
                    if (res.resultCode == 333 || res.resultCode==3300)
                        toPaypal(res);
                    else {
                        window.alert(res.message);
                        window.location.replace("shoppingcart.html");
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

function getReport2(res,status,xhr) {
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
                    if (res.resultCode == 3400) {
                        token = res.token;
                        Cookies.set("email",email);
                        Cookies.set("sessionid",sessionid);
                        window.open(res.redirectURL);
                    }

                    else {
                        window.alert(res.message);
                        Cookies.set("email",email);
                        Cookies.set("sessionid",sessionid);
                        window.location.replace("shoppingcart.html");
                    }
                }
            },
            failure: function (res) {
                window.alert("Internal server error");
            }
        });
    },delay)
}

function toPaypal(res){
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
        }),
        url: "http://128.195.6.93:5179/api/g/billing/order/place",
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

function completeTransaction() {
    $.ajax({
        method: "GET", // Declare request type
        headers: {
            "email": email,
            "sessionId":sessionid,
        },
        contentType:"application/json",
        dataType:"json",
        data: JSON.stringify({
            "email":email,
        }),
        url: "http://128.195.6.93:6540/api/billing/order/complete",
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
