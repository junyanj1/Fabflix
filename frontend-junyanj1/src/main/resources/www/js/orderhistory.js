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
    url: "http://128.195.6.93:5179/api/g/billing/order/retrieve",
    // success: handleResult, // Bind event handler as a success callback
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
                    if (res.resultCode == 3410)
                        orderTable(res);
                    else
                        window.alert(res.message);
                }
            },
            failure: function (res) {
                clearInterval(timerid);
                window.alert("Internal server error");
            }
        });
    },delay)
}

function orderTable(res) {
    debugger;
    let itemsDom = $('.items');
    itemsDom.empty();

    let rowHTML = "<table border=\"1\"><tr><th>Transaction ID</th><th>State</th><th colspan=\"2\">amount</th><th colspan=\"2\">Transaction fee</th><th>create time</th><th>update time</th><th>email</th><th>movieId</th><th>quantity</th><th>unit price</th><th>discount</th><th>saleDate</th>";
    result = res.transactions;
    for (let i=0; i<result.length;++i) {
        rowHTML += "<tr>";
        let item = result[i];
        let height = item.items.length+1;

        rowHTML += "<td rowspan='"+height+"'>"+item.transactionId+"</td>";
        rowHTML += "<td rowspan='"+height+"'>"+item.state+"</td>";
        rowHTML += "<td rowspan='"+height+"'>total: "+item.amount.total+"</td><td rowspan='"+height+"'>currency: "+item.amount.currency+"</td>";
        rowHTML += "<td rowspan='"+height+"'>value: "+item.transaction_fee.value+"</td><td rowspan='"+height+"'>currency: "+item.transaction_fee.currency+"</td>";
        rowHTML += "<td rowspan='"+height+"'>"+item.create_time+"</td>";
        rowHTML += "<td rowspan='"+height+"'>"+item.update_time+"</td>";

        for (let j=0;j<item.items.length;++j) {
            rowHTML += "<tr><td>"+item.items[j]["email"]+"</td>";
            rowHTML += "<td>"+item.items[j]["movieId"]+"</td>";
            rowHTML += "<td>"+item.items[j]["quantity"]+"</td>";
            rowHTML += "<td>"+item.items[j]["unit_price"]+"</td>";
            rowHTML += "<td>"+item.items[j]["discount"]+"</td>";
            rowHTML += "<td>"+item.items[j]["saleDate"]+"</td></tr>";
        }
        rowHTML += "</tr>";
    }
    rowHTML += "</table>";
    itemsDom.append(rowHTML);
    Cookies.set("email",email);
    Cookies.set("sessionid",sessionid);
}


