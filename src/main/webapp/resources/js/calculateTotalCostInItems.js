
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

    try{
    if(!isNaN(parseInt(quantity)) && unitPrice != ""){
        var unitPriceCalc = unitPrice.replace(",", "");
        console.log("calculating....")
        totalPrice = parseFloat(quantity) * parseFloat(unitPriceCalc);
        var totalPriceFixed = parseFloat(totalPrice.toFixed(2));
       // var totalPriceFixedStr = totalPriceFixed.toString().replace(".", ",");
        document.getElementsByClassName('form-control edTotalCostItems')[0].value = accounting.toFixed(totalPriceFixed,2);
    }else{
        document.getElementsByClassName('form-control edTotalCostItems')[0].value = ""
    }
    }catch (ex){
        console.log("error");
    }
}

function calculatePaymentValue(){
    console.log("calculatePaymentValue()")
    var percentage = document.getElementsByClassName('form-control percentageCashflow')[0].value;
    var projectValue = document.getElementsByClassName('form-control projectValueCashflow')[0].value;
    var totalValue;
    var language =  (navigator.userLanguage||navigator.browserLanguage||navigator.language);
    console.log("percentage: " + parseFloat(percentage));
    console.log("projectValue: " + parseFloat(projectValue));
    try{
        if(!isNaN(parseFloat(percentage)) && !isNaN(parseFloat(projectValue))){
            projectValue = projectValue.toString().replace(",",".");
            console.log("calculating....")
            totalValue = (parseFloat(percentage) * parseFloat(projectValue))/100;
            var totalPriceFixed = parseFloat(totalValue.toFixed(2));
            document.getElementsByClassName('form-control paymentValueCf')[0].value = totalPriceFixed.toLocaleString(language);
        }else{
            document.getElementsByClassName('form-control paymentValueCf')[0].value = ""
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
function printDraft(){
    $("#editPoForm\\:printDraftPO").click();
}
