/**
 * Created by Alvaro on 24/4/15.
 */
selectedValues = [];
$(document).ready(function () {
    initializeMultiselection();
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
});

$(window).scroll(function (event) {
    var scroll = $(window).scrollTop();
    document.getElementById("scrollTopId").value = scroll;
});

function initializeMultiselection(idsSelected) {
    var array = [];
    selectedValues = [];
    if (idsSelected) {
        selectedValues = idsSelected.split(',')
    }
    console.log('selected values ' + selectedValues);
    $('#poStatuses').multiselect(
        {
            onChange: function (option, checked, select) {
                console.log("value affected " + option.val());
                if (checked) {
                    verifyExcludeValues(option.val());
                    selectedValues[selectedValues.length] = option.val();
                } else {
                    index = selectedValues.indexOf(option.val());
                    selectedValues.splice(index, 1);
                }
                $('#poStatusesHidenId').val(selectedValues.toString());

            },
            numberDisplayed: 5,
            buttonClass: 'form-control multiselect-button-po'
        }
    );
    if (idsSelected) {
        array = JSON.parse("[" + idsSelected + "]");
        $('#poStatuses').multiselect('select', array);
    }
}

function verifyExcludeValues(optionSelected) {
    deleted=12;
    completed=5;
    cancelled=1;
    if (optionSelected == completed || optionSelected == deleted || optionSelected == cancelled) {
        if(optionSelected==completed){
            deletedExcludeValues(deleted,cancelled);
        }else if(optionSelected==deleted){
            deletedExcludeValues(completed,cancelled);
        }else if(optionSelected==cancelled){
            deletedExcludeValues(completed,deleted);
        }
    }
}

function deletedExcludeValues(status1, status2){
    index1 = selectedValues.indexOf(status1.toString());
    index2 = selectedValues.indexOf(status2.toString());
    if(index1>-1){
        $('#poStatuses').multiselect('deselect', [status1.toString()]);
        selectedValues.splice(index1,1);
    }
    if(index2>-1){
        $('#poStatuses').multiselect('deselect', [status2.toString()]);
        selectedValues.splice(index2,1);
    }

}


