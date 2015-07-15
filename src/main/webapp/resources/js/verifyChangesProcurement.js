/**
 * Created by Christian on 9/3/14.
 */

var hasChanges = false;

$('#editForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from procurement");
    hasChanges=true;
});

$('#editForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

$('#editPoForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from PO");
    hasChanges=true;
});

$('#editPoForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});
$('#currencyForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from PO");
    hasChanges=true;
});

$('#currencyForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});
$('#brandorm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from PO");
    hasChanges=true;
});

$('#brandForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});
$('#previewForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from PO");
    hasChanges=true;
});

$('#previewForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

$('#editUserForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from PO");
    hasChanges=true;
});

$('#editUserForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

function calendarChangesProc(){
    console.log("calendar create or edit");
    hasChanges=true;
}
function doChangeProc(){
    console.log('something was changed!');
    hasChanges=true;
}