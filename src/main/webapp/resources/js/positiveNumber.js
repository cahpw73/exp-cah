/**
 * Created by Alvaro
 */


function isNumberKey(evt)
{
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

    return true;
}

function isNumberFormat(evt)
{

    var charCode = (evt.which) ? evt.which : event.keyCode
    console.log("keycode: " + charCode);
    if(charCode == 46) {
        return true;
    }else if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        return false;
    }
    return true;
}