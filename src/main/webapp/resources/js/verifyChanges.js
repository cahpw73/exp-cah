/**
 * Created by Christian on 9/3/14.
 */

var hasChanges = false;

$('#empForm input').on( 'input change keyup paste', function() {
    console.log("Into create or edit");
    hasChanges=true;
});

$('#scopeSupplyFormId input').on( 'input change keyup paste', function() {
    hasChanges=true;
});

$('#scopeSupplyEditFormId input').on( 'input change keyup paste', function() {
    hasChanges=true;
});
$('#scopeSupplySplitFormId input').on( 'input change keyup paste', function() {
    hasChanges=true;
});

$('#empForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

$('#empForm textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

$('#scopeSupplyFormId textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

$('#scopeSupplyEditFormId textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
    hasChanges=true;
});

$('#scopeSupplySplitFormId textarea').on("change keyup paste", function() {
    console.log("textArea create or edit");
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