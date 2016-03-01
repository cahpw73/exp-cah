
window.onload = activeCategoryBrandTab;

function activeCategoryBrandTab(){
    console.log("changing project tabs ....");
    $( "#tabProject" ).removeClass( "active" );
    $( "#tabStandarText" ).removeClass( "active" );
    $( "#tabCurrency" ).removeClass( "active" );
    $( "#tabDocument" ).addClass( "active" );
    $( "#projectDetail" ).removeClass( "active" );
    $( "#standardText" ).removeClass( "active" );
    $( "#currency" ).removeClass( "active" );
    $( "#documents" ).addClass( "active" );
}

