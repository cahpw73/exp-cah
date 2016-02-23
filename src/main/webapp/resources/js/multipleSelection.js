/**
 * Created by Alvaro on 24/4/15.
 */
$(document).ready(function () {
    initializeMultiselection("");
});

function initializeMultiselection(idsSelected) {
    console.log("initialize MultiSelection")
    console.log("selected first print= " + idsSelected);
    var array = [];
    selectedValues = [];
    if (idsSelected) {
        selectedValues = idsSelected.split(',')
        $('#poStatusesHidenId').val(selectedValues.toString());
        console.log("selected values 1 = " + selectedValues.toString());
    }
    $('#poStatuses').multiselect(
        {
            onChange: function (option, checked, select) {
                console.log("value affected " + option.val());
                if (checked) {
                    selectedValues[selectedValues.length] = option.val();
                } else {
                    index = selectedValues.indexOf(option.val());
                    selectedValues.splice(index, 1);
                }
                $('#poStatusesHidenId').val(selectedValues.toString());
                console.log("selected values 2 onChange = " + selectedValues.toString());
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
