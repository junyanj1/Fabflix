function getRegisterReport(transactionid,delay,username) {
    let st = "nocontent";
    let resp = null;
    let timerid = 0;

    timerid = setInterval(function () {
        $.ajax({
            method: "GET", // Declare request type
            headers: {
                // "email": "existing1@existing.com",
                // "sessionId":"94275b25c8ded70cab5e1c648a1574e9ad602be0b3b7bfe384f85760b9578a82805aa801ce214003e6eb9a1fda885dee11364339c55f8142c73865d9857ed6f1",
                "transactionid":transactionid,
            },
            url: "http://128.195.6.93:5179/api/g/report",
            // success: handleResult, // Bind event handler as a success callback
            complete: function (res,status,xhr) {
                console.log(res,status)
                debugger
                st = status;
                resp = res;
                // sessionid = xhr.getResponseHeader("sessionID");
                if (status !== "nocontent") {
                    debugger;
                    clearInterval(timerid);
                    if (status === "success" && res.responseJSON.resultCode==120) {
                        window.alert(res.responseJSON.message);
                        Cookies.set("email",username);
                        Cookies.set("sessionid",res.responseJSON.sessionID);
                        window.location.replace("placeholder.html");
                    } else {
                        window.alert(res.responseJSON.message);
                    }
                }
            }
        });
    },delay)
}

$("form").submit(function (event) {
    event.preventDefault(); // Prevent the default form submit event, using ajax instead

    let username = $(".username").val();
    let password = $(".pw").val();

    debugger;
    // console.log("Search title: " + title);

    $.ajax({
        method: "POST", // Declare request type
        data: JSON.stringify({"email":username,"password":password}),
        contentType:"application/json",
        dataType:"json",
        url: "http://128.195.6.93:5179/api/g/idm/login",

        success: function (res,status,xhr) {
            console.log(status,xhr);
            let transactionid = xhr.getResponseHeader("transactionid");
            let delay = xhr.getResponseHeader("delay");
            debugger;
            getRegisterReport(transactionid,delay,username)
        },
        failure: function (res) {
            window.alert(res.responseJSON.message);
        }
    });
});