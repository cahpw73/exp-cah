
window.onload = activeCategoryBrandTab;

function activeCategoryBrandTab(){
    console.log("changing project tabs ....");
    $( "#headerTab" ).removeClass( "active" );
    $( "#supplierTab" ).removeClass( "active" );
    $( "#itemTab" ).removeClass( "active" );
    $( "#textTab" ).removeClass( "active" );
    $( "#deliverableTab" ).removeClass( "active" );
    $( "#cashflowTab" ).removeClass( "active" );
    $( "#documentTab" ).addClass( "active" );

    $( "#headerPo" ).removeClass( "active" );
    $( "#supplierPo" ).removeClass( "active" );
    $( "#itemPo" ).removeClass( "active" );
    $( "#textPo" ).removeClass( "active" );
    $( "#deliverablesPo" ).removeClass( "active" );
    $( "#cashflowPo" ).removeClass( "active" );
    $( "#documentPo" ).addClass( "active" );

}

