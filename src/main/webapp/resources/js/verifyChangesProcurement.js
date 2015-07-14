/**
 * Created by Christian on 9/3/14.
 */

var hasChanges = false;

$('#editForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from procurement");
    hasChanges=true;
});

$('#editPoForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from PO");
    hasChanges=true;
});

function calendarChanges(){
    console.log("calendar create or edit");
    hasChanges=true;
}
function doChange(){
    console.log('something was changed!');
    hasChanges=true;
}