/**
 * Created by Christian on 9/3/14.
 */

function getLanguage(){
    return (navigator.userLanguage||navigator.browserLanguage||navigator.language);
}

jQuery(document).ready(function() {
    document.getElementById("loginform:localeLanguage").value = getLanguage();
    var tz = jstz.determine(); // Determines the time zone of the browser client
    document.getElementById("loginform:timeZone").value =tz.name();
    console.log("Locale Language" + document.getElementById("loginform:localeLanguage").value);
});