window.onload = function () {
    var body = document.body
    var html = document.documentElement;
    var url = window.location.href;
    index = url.indexOf("anchorScope=");
    console.log("indexScope: "+ index);
    if (index > -1) {

        index2=url.substr(index+7).indexOf("&");
        if(index2>-1){
            scroll=url.substr(index+7,index2);
        }else{
            scroll=url.substr(index+7);
        }
        console.log("subtStrScope: " + scroll)
        body.scrollTopScope += scroll;
        html.scrollTopScope += scroll;
    }
};

$(window).scroll(function (event) {
    var scroll = $(window).scrollTopScope();
    document.getElementById("scrollTopScopeId").value = scroll;
    console.log("scrollTopScope: " + scroll);
});

