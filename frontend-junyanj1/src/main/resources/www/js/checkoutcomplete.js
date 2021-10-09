let email = Cookies.get("email");
let transactionid = 0;
let sessionid = Cookies.get("sessionid");
let delay = 0;


function completeTheMission() {
    const urlParams = new URLSearchParams(window.location.search);
    let paymentid = urlParams.get('paymentId');
    let token = urlParams.get("token");
    let payerid = urlParams.get("PayerID");
    let d = {};
    if (paymentid!=null)
        d["paymentId"] = paymentid;
    if (token!=null)
        d["token"] = token;
    if (payerid!=null)
        d["PayerID"] = payerid;
    $.ajax({
        method: "GET", // Declare request type
        headers: {
            "email": email,
            "sessionId":sessionid,
        },
        data: d,
        url: "http://128.195.6.93:6540/api/billing/order/complete",
        // success: handleResult, // Bind event handler as a success callback
        success: function (res,status,xhr) {
            // console.log(xhr.getResponseHeader("sessionID"));
            sessionid = xhr.getResponseHeader("sessionID");
            transactionid = xhr.getResponseHeader("transactionid");
            email = xhr.getResponseHeader("email");
            delay = xhr.getResponseHeader("delay");
            debugger;
            if (res.resultCode==3420) {
                window.alert(res.message);
                Cookies.set("email",email);
                Cookies.set("sessionid",sessionid);
                window.location.replace("../index.html");
            } else {
                window.alert(res.message);
                Cookies.set("email",email);
                Cookies.set("sessionid",sessionid);
            }
        },
        failure: function (res) {
            window.alert("Internal server error");
        }
    });
}