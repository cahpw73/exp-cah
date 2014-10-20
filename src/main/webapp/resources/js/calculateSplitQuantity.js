/**
 * Created by Christian on 9/3/14.
 */
var originalQuantity;
var arrivedQuantity;
var newQuantity;

    $(document).ready(function() {
        hasChanges=true;
        originalQuantity = document.getElementById("arrivedQuantity").value;
        console.log("original quantity: " + originalQuantity);
    });

    function calculateNewScopeSupplyQuantity(){
        arrivedQuantity = document.getElementById("arrivedQuantity").value;
        console.log("originalQuantity: " + originalQuantity);
        console.log("arrivedQuantity: " + arrivedQuantity);
        if((arrivedQuantity >= 0) && (arrivedQuantity <= originalQuantity)){
            newQuantity  = originalQuantity - arrivedQuantity;
            console.log("newQuantity: " + newQuantity);
            if(newQuantity > 0){
                document.getElementById("newQuantitySupplySplit").value = newQuantity;
            }else{
                document.getElementById("arrivedQuantity").value = originalQuantity;
                document.getElementById("newQuantitySupplySplit").value = 0;
            }
        }else{
            document.getElementById("arrivedQuantity").value = originalQuantity;
            document.getElementById("newQuantitySupplySplit").value = 0;
        }

    }

    function calculateOldScopeSupplyQuantity(){
        newQuantity = document.getElementById("newQuantitySupplySplit").value;
        if((newQuantity >= 0) && (newQuantity <= originalQuantity)){
            arrivedQuantity = originalQuantity - newQuantity;
            console.log("arrivedQuantity: " + arrivedQuantity);
            if(arrivedQuantity > 0){
                document.getElementById("arrivedQuantity").value = arrivedQuantity;
            }else{
                document.getElementById("arrivedQuantity").value = originalQuantity;
                document.getElementById("newQuantitySupplySplit").value = 0;
            }
        }else{
            document.getElementById("arrivedQuantity").value = originalQuantity;
            document.getElementById("newQuantitySupplySplit").value = 0;
        }
    }

    function verifyArrivedQuantity(){
        console.log("verifyArrivedQuantity");
        arrivedQuantity = document.getElementById("arrivedQuantity").value;
        console.log("arrivedQuantity: " + arrivedQuantity);
        if(arrivedQuantity == ""){
            document.getElementById("arrivedQuantity").value = 0;
        }
    }

    function verifyNewQuantity(){
        console.log("verifyNewQuantity");
        newQuantity = document.getElementById("newQuantitySupplySplit").value;
        console.log("newQuantitySupplySplit: " + arrivedQuantity);
        if(newQuantity == ""){
            document.getElementById("newQuantitySupplySplit").value = 0;
        }
    }