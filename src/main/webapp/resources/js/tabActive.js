/**
 * Created by Alvaro on 9/3/15.
 */

var tabHeader=1;
var tabSupplier=2;
var tabItem=3;
var tabText=4;
var tabDeliverable=5;
var tabCashflow=6;
var tabActive=tabHeader;

function activeTab(tab){
    tabActive=tab;
}

function isActive(tab){
    return tab==tabActive;
}

function activeCategoryBrandTab(){
    console.log("changing tabs ....");
    $( "#tabSupplier" ).removeClass( "active" );
    $( "#tabCategory" ).removeClass( "active" );
    $( "#tabCategory" ).addClass( "active" );
    $( "#supplier" ).removeClass( "active" );
    $( "#contacts" ).removeClass( "active" );
    $( "#categories" ).addClass( "active" );
}

