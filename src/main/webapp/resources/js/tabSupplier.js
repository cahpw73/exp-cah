/**
 * Created by Christian on 9/3/14.
 */


function activeCategoryBrandTab(){
    console.log("changing tabs ....");
    $( "#tabSupplier" ).removeClass( "active" );
    $( "#tabCategory" ).removeClass( "active" );
    $( "#tabCategory" ).addClass( "active" );
    $( "#supplier" ).removeClass( "active" );
    $( "#contacts" ).removeClass( "active" );
    $( "#categories" ).addClass( "active" );
}
