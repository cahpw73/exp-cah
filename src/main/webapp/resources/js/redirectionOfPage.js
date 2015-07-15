/**
 * Created by Christian on 14/7/15.
 */

$("#projectListLinkId").click(function(e){
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit project..");
        leavePageToProjectList(e,"../../");
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/client/edit") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/logo/preview") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/textSnippet/textSnippet") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/category/category") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/brands/brands") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/currency/currency") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/user/edit") != -1){
        leavePageToProjectList(e,"../../../");
    }else if(window.location.href.indexOf("/supplier/edit") != -1){
        leavePageToProjectList(e,"../../../");
    }
})

function leavePageToProjectList(e,level){
    e.preventDefault();
    console.log("toLeavePageFromModal");
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelPurchaseList').show();
    }else{
        window.location = level+"procurement/project/list.jsf"
    }
}

$("#reportListLinkId").click(function(e){
    console.log("click report list.... "+window.location.href);
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit..");
        leavePageToReportList(e,"../../");
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/client/edit") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/logo/preview") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/textSnippet/textSnippet") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/category/category") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/brands/brands") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/currency/currency") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/user/edit") != -1){
        leavePageToReportList(e,"../../../");
    }else if(window.location.href.indexOf("/supplier/edit") != -1){
        leavePageToReportList(e,"../../../");
    }

})

function leavePageToReportList(e,level){
    e.preventDefault();
    console.log("toLeavePageFromModal");
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelReportList').show();
    }else{
        window.location = level+"procurement/report/report.jsf";
    }
}

$("#adminListLinkId").click(function(e){
    console.log("click admin list....")
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit..");
        leavePageToAdminList(e,"../../");
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/client/edit") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/logo/preview") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/textSnippet/textSnippet") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/category/category") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/brands/brands") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/currency/currency") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/user/edit") != -1){
        leavePageToAdminList(e,"../../../");
    }else if(window.location.href.indexOf("/supplier/edit") != -1){
        leavePageToAdminList(e,"../../../");
    }
})

function leavePageToAdminList(e,level){
    e.preventDefault();
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelAdminList').show();
    }else{
        window.location = level+"procurement/admin/admin.jsf"
    }
}

$("#profileLinkId").click(function(e){
    console.log("click profile....")
    if(window.location.href.indexOf("project/edit.jsf")  != -1) {
        console.log("is create or edit..");
        leavePageToProfileList(e,"../../");
    }else if(window.location.href.indexOf("/purchase-order/edit") != -1){
        console.log("is create or edit PO..");
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/client/edit") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/logo/preview") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/textSnippet/textSnippet") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/category/category") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/brands/brands") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/currency/currency") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/user/edit") != -1){
        leavePageToProfileList(e,"../../../");
    }else if(window.location.href.indexOf("/supplier/edit") != -1){
        leavePageToProfileList(e,"../../../");
    }
})

function leavePageToProfileList(e,level){
    e.preventDefault();
    if(hasChanges){
        console.log("hasChanges true");
        PF('confCancelProfile').show();
    }else{
        window.location = level+"procurement/profile/profile.jsf"
    }
}

