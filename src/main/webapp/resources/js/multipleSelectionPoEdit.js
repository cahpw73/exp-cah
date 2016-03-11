/**
 * Created by Alvaro on 24/4/15.
 */
selectedValues = [];
$(document).ready(function () {
    initializeMultiselection();
});

function initializeMultiselection(idsSelected) {
    var array = [];
    console.log('selected first print ' + idsSelected);
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
                console.log("val before  " + selectedValues);
                $('#poStatusesHidenId').val(selectedValues.toString());

            },
            numberDisplayed: 5,
            buttonClass: 'form-control multiselect-button-po'
        }
    );
    if (idsSelected) {
        array = JSON.parse("[" + idsSelected + "]");
        console.log('second print selected ' + array);
        $('#poStatuses').multiselect('select', array);
    }
}

function verifyExcludeValues(optionSelected) {
    console.log("verifyExcludeValues")
    console.log("optionSelected= " + optionSelected);
    deleted=12;
    completed=5;
    cancelled=1;
    if (optionSelected == completed || optionSelected == deleted || optionSelected == cancelled) {
        console.log("has some to evaluation")
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
    console.log("deletedExcludeValues");
    console.log("status1= " + status1);
    console.log("status2= " + status2);
    console.log("selectedValues = " + selectedValues)
    index1 = selectedValues.indexOf(status1.toString());
    index2 = selectedValues.indexOf(status2.toString());
    console.log("index1= " + index1);
    console.log("index2= " + index2);
    if(index1>-1){
        $('#poStatuses').multiselect('deselect', [status1.toString()]);
        selectedValues.splice(index1,1);
    }
    if(index2>-1){
        $('#poStatuses').multiselect('deselect', [status2.toString()]);
        selectedValues.splice(index2,1);
    }

}


