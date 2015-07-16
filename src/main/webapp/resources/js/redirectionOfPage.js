/**
 * Created by Christian on 14/7/15.
 */

navigator ={};
navigator.urls =new Array ();
navigator.urls[0]={"url":"project/edit.jsf","level":"../../"};
navigator.urls[1]={"url":"/purchase-order/edit","level":"../../../"};
navigator.urls[2]={"url":"/client/edit","level":"../../../"};
navigator.urls[3]={"url":"/logo/preview","level":"../../../"};
navigator.urls[4]={"url":"/textSnippet/textSnippet","level":"../../../"};
navigator.urls[5]={"url":"/category/category","level":"../../../"};
navigator.urls[6]={"url":"/brands/brands","level":"../../../"};
navigator.urls[7]={"url":"/currency/currency","level":"../../../"};
navigator.urls[8]={"url":"/user/edit","level":"../../../"};
navigator.urls[9]={"url":"/supplier/edit","level":"../../../"};

$("#projectListLinkId").click(function(e){
    for (var i = 0; i < navigator.urls.length; i++) {
        if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
            leavePageToProjectList(e,navigator.urls[i].level);
            break;
        }
    }
})

function leavePageToProjectList(e,level){
    target=level+"procurement/project/list.jsf";
    leavingPage(e,target);
}

$("#reportListLinkId").click(function(e){
    console.log("click report list.... "+window.location.href);
    for (var i = 0; i < navigator.urls.length; i++) {
        if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
            leavePageToReportList(e,navigator.urls[i].level);
            break;
        }
    }

})

function leavePageToReportList(e,level){
    target=level+"procurement/report/report.jsf";
    leavingPage(e,target);
}

$("#adminListLinkId").click(function(e){
    console.log("click admin list....")
    for (var i = 0; i < navigator.urls.length; i++) {
        if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
            leavePageToAdminList(e,navigator.urls[i].level);
            break;
        }
    }
})

function leavePageToAdminList(e,level){
    target=level+"procurement/admin/admin.jsf";
    leavingPage(e,target);
}

$("#profileLinkId").click(function(e){
    console.log("click profile....");
    for (var i = 0; i < navigator.urls.length; i++) {
        if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
            leavePageToProfileList(e,navigator.urls[i].level);
            break;
        }
    }
})

function leavePageToProfileList(e,level){
    target=level+"procurement/profile/profile.jsf";
    leavingPage(e,target);
}
function leavingPage(e,target){
    e.preventDefault();
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelProfile').show();
    }else{
        window.location = target;
    }
}

