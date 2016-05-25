window.onload = function () {
    var body = document.body
    var html = document.documentElement;
    var url = window.location.href;
    index = url.indexOf("anchor=");
    console.log("index: "+ index);
    if (index > -1) {

        index2=url.substr(index+7).indexOf("&");
        if(index2>-1){
            scroll=url.substr(index+7,index2);
        }else{
            scroll=url.substr(index+7);
        }
        console.log("subtStr: " + scroll);
        console.log("body.scrollTop 1: " + body.scrollTop);
        body.scrollTop = 0;
        html.scrollTop = 0;
        body.scrollTop += scroll;
        html.scrollTop += scroll;
        console.log("body.scrollTop 2: " + body.scrollTop);
        console.log("html.scrollTop: " + html.scrollTop);
    }
};

$(window).scroll(function (event) {
    var scroll = $(window).scrollTop();
    console.log("window.scrollTop= " + scroll);
    document.getElementById("scrollTopId").value = scroll;
    console.log("function scroll");
    console.log("scrollTop: " + scroll);
});

