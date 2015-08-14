
/*$(document).ready(function() {
    calculateTotalPrice();
    copyCurrencyLabel();
});*/

function calculateTotalCost(){
    console.log("calculateTotalPrice()")
    var quantity = document.getElementsByClassName('form-control edQtyItems')[0].value;
    var unitPrice = document.getElementsByClassName('form-control edUniCostItems')[0].value;
    var totalPrice;
    var language =  (navigator.userLanguage||navigator.browserLanguage||navigator.language);
    console.log("quantity: " + parseInt(quantity));
    console.log("unitPrice: " + unitPrice);
    var unitPriceCalc = unitPrice.replace(",", "");
    try{
    if(isNumericPositive(quantity) && parseInt(quantity) >= 0 && parseFloat(unitPriceCalc) >= -100000000){
        console.log("calculating....")
        totalPrice = parseFloat(quantity) * parseFloat(unitPriceCalc);
        var totalPriceFixed = parseFloat(totalPrice.toFixed(2));
        document.getElementsByClassName('form-control edTotalCostItems')[0].value = totalPriceFixed.toLocaleString(language);
    }else{
        document.getElementsByClassName('form-control edTotalCostItems')[0].value = "0.00"
    }
    }catch (ex){
        console.log("error");
    }
}

function isNumericPositive(obj) {
    var result = !jQuery.isArray( obj ) && (obj - parseFloat( obj ) + 1) >= 0;
    console.log("RESULT IS NUMERIC POSITVE: " + result);
    return result;
}
function isNumeric(obj) {
    var result = !jQuery.isArray( obj ) && (obj - parseFloat( obj ) + 1)>=-10000000;
    console.log("RESULT IS NUMERIC: " + result);
    return result;
}