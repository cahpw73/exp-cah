window.onload = function () {
    renderBodyButtons();
    var body = document.body
    var html = document.documentElement;
    var url = window.location.href;
    index = url.indexOf("anchorsp=");
    if (index > -1) {
        index2=url.substr(index+9).indexOf("&");
        if(index2>-1){
            scroll=url.substr(index+9,index2);
        }else{
            scroll=url.substr(index+9);
        }
        if(body.scrollTop == 0 && html.scrollTop == 0) {
            body.scrollTop += scroll;
            html.scrollTop += scroll;
        }
    }
};

$(window).scroll(function (event) {
    var scroll = $(window).scrollTop();
    document.getElementById("scrollTopId").value = scroll;
});

