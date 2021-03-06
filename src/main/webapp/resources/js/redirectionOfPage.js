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

/****Redirection to project*****/
//Quitar el segundo boton porque ya no se lo necesita
$("#projectListLinkId").click(function(e){
    verifyToLeaveProjectEdit(e)
})

$("#cancelProjectBtn1").click(function(e){
    verifyToLeaveProjectEdit(e)
})

function verifyToLeaveProjectEdit(e){
    for (var i = 0; i < navigator.urls.length; i++) {
        if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
            leavePageToProjectList(e,navigator.urls[i].level);
            break;
        }
    }
}

function leavePageToProjectList(e,level){
    target=level+"procurement/project/list.jsf";
    leavingPage(e,target,"confCancelPurchaseList");
}

/*****Redirection to PO List*******/
function verifyToLeavePOEdit(e){
    if(hasChanges){
        if(e.preventDefault) e.preventDefault();
        for (var i = 0; i < navigator.urls.length; i++) {
            if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
                leavePageToPOList(e,navigator.urls[i].level);
                return false;
            }
        }
    }else{
        if(e.preventDefault) e.preventDefault();
        console.log("hasChanges: " + hasChanges)
        document.getElementById("editPoForm:cancelPOCreateBtn1").click();
    }
    return false;
}
function leavePageToPOList(level){
    target=level+"procurement/project/purchase-order/list.jsf";
    console.log("hasChanges true");
    PF('confGotoPoListDlg1').show();
}

/****Redirection to Supplier List ***/
function verifyToLeaveSupplierEdit(e){
    if(hasChanges){
        if(e.preventDefault) e.preventDefault();
        for (var i = 0; i < navigator.urls.length; i++) {
            if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
                PF('confGoToSupplierListDlg').show();
                return false;
            }
        }
    }
    return false;
}
/***Redirection to User List***/
function verifyToLeaveUserEdit(e){
    if(hasChanges){
        if(e.preventDefault) e.preventDefault();
        for (var i = 0; i < navigator.urls.length; i++) {
            if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
                PF('confGoToUserListDlg').show();
                return false;
            }
        }
    }
    return false;
}

/***Redirection to Logo List**/
function verifyToLeaveLogoEdit(e){
    if(hasChanges){
        if(e.preventDefault) e.preventDefault();
        for (var i = 0; i < navigator.urls.length; i++) {
            if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
                PF('confGoToLogoListDlg').show();
                return false;
            }
        }
    }
    return false;
}

/**Redirection to Client List**/
function verifyToLeaveClientEdit(e){
    if(hasChanges){
        if(e.preventDefault) e.preventDefault();
        for (var i = 0; i < navigator.urls.length; i++) {
            if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
                PF('confGoToClientListDlg').show();
                return false;
            }
        }
    }
    return false;
}
/***Redirection to profile ProjectList de Profile***/
function verifyToLeaveProfileProcurementEdit(e){
    if(hasChanges){
        if(e.preventDefault) e.preventDefault();
        for (var i = 0; i < navigator.urls.length; i++) {
            if(window.location.href.indexOf(navigator.urls[i].url)!=-1){
                PF('confGoToProjectListFromProfileDlg').show();
                return false;
            }
        }
    }
    return false;
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
    leavingPage(e,target,"confCancelReportList");
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
    leavingPage(e,target,"confCancelAdminList");
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
    leavingPage(e,target,"confCancelProfile");
}
function leavingPage(e,target,dialog){
    e.preventDefault();
    if(hasChanges){
        console.log("hasChanges true");
        PF(dialog).show();
    }else{
        window.location = target;
    }
}
