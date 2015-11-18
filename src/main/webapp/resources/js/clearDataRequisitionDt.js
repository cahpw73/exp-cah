/**
 * Created by Christian on 17/11/15.
 */

function clearRtfNo() {
    console.log("clear rtf no");
    var originator = document.getElementsByClassName('originatorClass')[0].value;
    if(originator != null && originator != ""){
        document.getElementsByClassName('rTFNoClass')[0].value = "";
    }
}

function clearOriginator() {
    console.log("clear originator");
    var rtf = document.getElementsByClassName('rTFNoClass')[0].value;
    if(rtf != null && rtf != "") {
        document.getElementsByClassName('originatorClass')[0].value = "";
    }
}