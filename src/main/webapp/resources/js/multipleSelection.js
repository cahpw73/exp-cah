/**
 * Created by Alvaro on 24/4/15.
 */
selectedValues = [];
$(document).ready(function () {
    initializeMultiselection();
});

function initializeMultiselection(){
    selectedValues = [];
    $('#poStatuses').multiselect(
        {
            onChange: function (option, checked, select) {
                console.log("value affected " + option.val());
                if (checked) {
                    selectedValues[selectedValues.length] = option.val();
                } else {
                    index=selectedValues.indexOf(option.val());
                    selectedValues.splice(index,1);
                }
                $('#poStatusesHidenId').val(selectedValues.toString());
            }

        }
    );
}


