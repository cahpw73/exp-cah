window.onload = function () {
    var body = document.body
    var html = document.documentElement;
    var url = window.location.href;
    index = url.indexOf("anchorsp=");
    console.log("index: "+ index);
    if (index > -1) {
        index2=url.substr(index+9).indexOf("&");
        console.log("index2 = " + url.substr(index+9).indexOf("&"));
        if(index2>-1){
            console.log("index2>-1 = " + url.substr(index+9,index2));
            scroll=url.substr(index+9,index2);
        }else{
            console.log("index2<=-1 = " + url.substr(index+9));
            scroll=url.substr(index+9);
            console.log("scroll after index+9 = " + scroll);
        }
        console.log("subtStr: " + scroll)
        console.log("1 body.scrollTop = "+body.scrollTop);
        console.log("1 html.scrollTop = " +html.scrollTop);
        if(body.scrollTop == 0 && html.scrollTop == 0) {
            body.scrollTop += scroll;
            html.scrollTop += scroll;
        }
        console.log("2 body.scrollTop = "+body.scrollTop);
        console.log("2 html.scrollTop = " +html.scrollTop);
    }
};

$(window).scroll(function (event) {
    console.log("window.scroll on scrollScopeSupply")
    var scroll = $(window).scrollTop();
    console.log("scrollTop: " + scroll);
    document.getElementById("scrollTopId").value = scroll;

});

