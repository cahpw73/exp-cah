
$(document).ready(function() {
    calculateTotalPrice();
    copyCurrencyLabel();
});

function calculateTotalPrice(){
    console.log("calculateTotalPrice()")
    var quantity = document.getElementsByClassName('scopeSupplyQuantity')[0].value;//document.getElementById("scopeSupplyQuantity").value;
    var unitPrice = document.getElementsByClassName('scopeSupplyCost')[0].value;
    var totalPrice;
    var language =  (navigator.userLanguage||navigator.browserLanguage||navigator.language);
    console.log("quantity: " + parseInt(quantity));
    console.log("unitPrice: " + unitPrice);
    var unitPriceCalc = unitPrice.replace(",", "");
    try{
    if(isNumeric(quantity) && parseInt(quantity) >= 0 && parseFloat(unitPriceCalc) >= 0){
        console.log("calculating....")
        totalPrice = parseFloat(quantity) * parseFloat(unitPriceCalc);
        var totalPriceFixed = parseFloat(totalPrice.toFixed(2));
        var currency = document.getElementsByClassName('currencyPrice')[0].value;
        document.getElementsByClassName('totalPriceTxt')[0].value = totalPriceFixed.toLocaleString(language) +" "+ currency;
    }else{
        document.getElementsByClassName('totalPriceTxt')[0].value = "-"
    }
    }catch (ex){
        console.log("error");
    }
}

function isNumeric(obj) {
    var result = !jQuery.isArray( obj ) && (obj - parseFloat( obj ) + 1) >= 0;
    console.log("RESULT IS NUMERIC: " + result);
    return result;
}

function verifyQuantityValid(){
    var quantity = document.getElementsByClassName('scopeSupplyQuantity')[0].value;
    if(parseInt(quantity)<0){
        document.getElementsByClassName('scopeSupplyQuantity')[0].value = 0;
    }else if(quantity == ""){
        document.getElementsByClassName('totalPriceTxt')[0].value = "";
    }
}

function verifyUnitPriceValid(){
    var priceUnit = document.getElementsByClassName('scopeSupplyCost')[0].value;
    if(parseInt(priceUnit)<0){
        document.getElementsByClassName('scopeSupplyCost')[0].value = 0;
    }else if(priceUnit == ""){
        document.getElementsByClassName('totalPriceTxt')[0].value = "";
    }
}

function copyCurrencyLabel(){
    calculateTotalPrice();
}