/**
 * Created by Alvaro on 7/7/15.
 */

$(function () {
    $("#loginform input").keypress(function (e) {
        console.log("pressing keypress");
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            console.log("clicking....");
            $("#loginform\\:loginBtn").click();
            return false;
        } else {
            return true;
        }
    });
});