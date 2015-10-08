function calculateTotalCost() {
    console.log("calculateTotalPrice()")
    var quantity = document.getElementsByClassName('form-control edQtyItems')[0].value;
    var unitPrice = document.getElementsByClassName('form-control edUniCostItems')[0].value;
    var totalPrice;
    var language = (navigator.userLanguage || navigator.browserLanguage || navigator.language);
    try {
        if (!isNaN(parseInt(quantity)) && unitPrice != "") {
            var unitPriceCalc = unitPrice.replace(",", "");
            console.log("calculating....")
            totalPrice = parseFloat(quantity) * parseFloat(unitPriceCalc);
            var totalPriceFixed = parseFloat(totalPrice.toFixed(2));
            // var totalPriceFixedStr = totalPriceFixed.toString().replace(".", ",");
            document.getElementsByClassName('form-control edTotalCostItems')[0].value = accounting.toFixed(totalPriceFixed, 2);
        } else {
            document.getElementsByClassName('form-control edTotalCostItems')[0].value = ""
        }
    } catch (ex) {
        console.log("error");
    }
}

function calculateUnitCost() {
    console.log("calculateTotalPrice()")
    var quantity = document.getElementsByClassName('form-control edQtyItems')[0].value;
    var totalCost = document.getElementsByClassName('form-control edTotalCostItems')[0].value;
    var unitCost;
    try {
        if (!isNaN(parseInt(quantity)) && !isNaN(parseInt(totalCost))) {
            var totalCostCalc = totalCost.replace(",", "");
            console.log("calculating....")
            unitCost = parseFloat(totalCostCalc) / parseFloat(quantity);
            var unitPriceFixed = parseFloat(unitCost.toFixed(2));
            document.getElementsByClassName('form-control edUniCostItems')[0].value = accounting.toFixed(unitPriceFixed, 2);
        } else {
            document.getElementsByClassName('form-control edUniCostItems')[0].value = ""
        }
    } catch (ex) {
        console.log("error");
    }
}