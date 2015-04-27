/**
 * Created by Alvaro on 24/4/15.
 */

$(document).ready(function () {
    selectedValues = [];
    $('#poStatuses').multiselect({
            numberDisplayed: 2
        }
    );
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


