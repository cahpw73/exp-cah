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
                values = ''
                if (option) {
                    values = option.val();
                } else {
                    if (checked) {
                        values = '0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17'
                    }
                }
                if (checked) {
                    if (values.length > 1) {
                        val = values.split(',')
                        for (i = 0; i < val.length; i++) {
                            selectedValues[selectedValues.length] = val[i];
                        }
                    } else {
                        selectedValues[selectedValues.length] = values;
                    }
                } else {
                    if (option) {
                        index = selectedValues.indexOf(values);
                        selectedValues.splice(index, 1);
                    } else {
                        selectedValues = '';
                    }
                }
                $('#poStatusesHidenId').val(selectedValues.toString());
                console.log("selected values 2 onChange = " + selectedValues.toString());
            },
            includeSelectAllOption: true,
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
