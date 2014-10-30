/**
 * Created by Christian on 9/3/14.
 */

function getLanguage(){
    return (navigator.userLanguage||navigator.browserLanguage||navigator.language);
}

jQuery(document).ready(function() {
    document.getElementById("loginform:localeLanguage").value = getLanguage();
    console.log("Locale Language" + document.getElementById("loginform:localeLanguage").value);
});