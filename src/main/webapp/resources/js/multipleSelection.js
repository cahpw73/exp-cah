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
        $('#poStatusesHidenId').val(selectedValues.toString());
    }
    console.log('selected values ' + selectedValues);
    $('#poStatuses').multiselect(
        {
            onChange: function (option, checked, select) {
                console.log("value affected " + option.val());
                if (checked) {
                    console.log("selectedValues Length= " + selectedValues.length);
                    selectedValues[selectedValues.length] = option.val();
                } else {
                    index = selectedValues.indexOf(option.val());
                    selectedValues.splice(index, 1);
                }
                $('#poStatusesHidenId').val(selectedValues.toString());
            },
            numberDisplayed: 2,
            buttonClass: 'form-control multiselect-button-po'
        }
    );
    if (idsSelected) {
        array = JSON.parse("[" + idsSelected + "]");
        console.log('second print selected ' + array);
        $('#poStatuses').multiselect('select', array);
    }
}
