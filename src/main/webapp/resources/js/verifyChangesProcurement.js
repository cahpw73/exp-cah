/**
 * Created by Christian on 9/3/14.
 */

var hasChanges = false;

$('#editForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from procurement");
    hasChanges=true;
    console.log("hasChanges: " + hasChanges);
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

$('#supplierId input').on( 'input change keyup paste', function() {
    console.log("Into create or edit from procurement");
    hasChanges=true;
    console.log("hasChanges: " + hasChanges);
});

$('#supplierId textarea').on("change keyup paste", function() {
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
function restartChanges(){
    console.log("reseting changes.");
    hasChanges=false;
}

function printDraft(){
    $("#editPoForm\\:printDraftPO" ).click();
}