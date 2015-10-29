/**
 * Created by Alvaro on 7/7/15.
 */

$(function () {
    $("#searchForm input").keypress(function (e) {
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            $("#srch").click();
            return false;
        } else {
            return true;
        }
    });
});

$(function () {
    $("#reportForm input").keypress(function (e) {
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            $("#searchBtnId").click();
            return false;
        } else {
            return true;
        }
    });
});