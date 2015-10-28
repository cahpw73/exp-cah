window.onload = function () {
    renderBodyButtons();
    var body = document.body
    var html = document.documentElement;
    var url = window.location.href;
    index = url.indexOf("#");
    console.log("index: "+ index);
    if (index > -1) {
        console.log("subtStr: " + url.substr(index+1))
        body.scrollTop += url.substr(index+1);
        html.scrollTop += url.substr(index+1);
    }
};

$(window).scroll(function (event) {
    var scroll = $(window).scrollTop();
    document.getElementById("scrollTopId").value = scroll;
    console.log("scrollTop: " + scroll);
});

