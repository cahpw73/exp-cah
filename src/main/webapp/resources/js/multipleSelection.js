/**
 * Created by Christian on 9/3/14.
 */

$(document).ready(function () {
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
    function addSelectedValue(elementId){
        selectedValues[selectedValues.length] = elementId;
    }
});


