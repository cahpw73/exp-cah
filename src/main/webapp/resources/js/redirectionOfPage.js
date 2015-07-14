/**
 * Created by Christian on 14/7/15.
 */

$("#projectListLinkId").click(function(e){
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit project..");
        leavePageToProjectList(e);
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToProjectList(e);
    }
})

function leavePageToProjectList(e){
    e.preventDefault();
    console.log("toLeavePageFromModal");
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelPurchaseList').show();
    }else{
        window.location = "../../procurement/project/list.jsf"
    }
}

$("#reportListLinkId").click(function(e){
    console.log("click report list....")
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit..");
        leavePageToReportList(e);
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToReportList(e);
    }
})

function leavePageToReportList(e){
    e.preventDefault();
    console.log("toLeavePageFromModal");
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelReportList').show();
    }else{
        window.location = "../../procurement/report/report.jsf"
    }
}

$("#adminListLinkId").click(function(e){
    console.log("click admin list....")
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit..");
        leavePageToAdminList(e);
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToAdminList(e);
    }
})

function leavePageToAdminList(e){
    e.preventDefault();
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelReportList').show();
    }else{
        window.location = "../../procurement/admin/admin.jsf"
    }
}

$("#profileLinkId").click(function(e){
    console.log("click profile....")
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit..");
        leavePageToProfileList(e);
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToProfileList(e);
    }
})

function leavePageToProfileList(e){
    e.preventDefault();
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelReportList').show();
    }else{
        window.location = "../../procurement/profile/profile.jsf"
    }
}

